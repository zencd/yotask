package svc

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matcher
import org.hamcrest.core.IsNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import svc.dto.ConsumeQuotaRequest
import svc.dto.CreateQuotaRequest
import svc.dto.SimQuotaType
import svc.entity.SimCardEntity
import svc.entity.SimCardStatus
import svc.entity.SimQuotaEntity
import svc.entity.SimQuotaStatus
import svc.repository.SimCardRepository
import svc.repository.SimQuotaRepository

import java.time.OffsetDateTime
import java.time.ZoneOffset

import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Тест с поднятием контекста и БД, без моков.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ComponentTest {

	private static final Matcher<Object> NULL = IsNull.nullValue()
	private static final OffsetDateTime FAR_DATE = OffsetDateTime.of(2099, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)

	@Autowired
	private MockMvc mvc

	@Autowired
	SimCardRepository simCardRepository

	@Autowired
	SimQuotaRepository simQuotaRepository

	@Autowired
	ObjectMapper objectMapper

	@Test
	void contextLoads() {
	}

	@Test
	void 'addQuota, main success'() throws Exception {
		SimCardEntity sim = new SimCardEntity(msisdn: "79990000001")
		simCardRepository.save(sim)

		var request = new CreateQuotaRequest(amount: 20, type: SimQuotaType.VOICE, endDate: OffsetDateTime.now())

		mvc.perform(MockMvcRequestBuilders
				.post("/sims/{id}/quota/add", sim.id)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.id', is(1)))
				.andExpect(jsonPath('$.status', is(1)))
				.andExpect(jsonPath('$.type', is("voice")))
				.andExpect(jsonPath('$.balance', is(20)))
				.andExpect(jsonPath('$.endDate', is(notNullValue())))
				.andExpect(jsonPath('$.dateCreated', is(notNullValue())))
				.andExpect(jsonPath('$.lastUpdated', is(notNullValue())))
	}

	@Test
	void 'consumeQuota, main success'() throws Exception {
		var now = OffsetDateTime.now()

		var sim = new SimCardEntity(msisdn: '79990000001')
		simCardRepository.save(sim)

		var quota = new SimQuotaEntity(
				simCard: sim,
				type: SimQuotaType.VOICE,
				balance: new BigDecimal(100),
				status: SimQuotaStatus.ENABLED,
				lastUpdated: now,
				dateCreated: now,
				endDate: FAR_DATE)
		simQuotaRepository.save(quota)

		var request = new ConsumeQuotaRequest(simId: sim.id, type: SimQuotaType.VOICE, amount: 10)
		mvc.perform(MockMvcRequestBuilders
				.post("/sims/{id}/quota/consume", sim.id)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(""))

		quota = simQuotaRepository.findById(quota.id).orElseThrow()
		assertEquals(new BigDecimal("90.00"), quota.balance)
	}

	@Test
	void 'getQuotaAvailable, main success'() throws Exception {
		var now = OffsetDateTime.now()

		var sim = new SimCardEntity(msisdn: '79990000001')
		simCardRepository.save(sim)

		var quotaVoice = new SimQuotaEntity(
				simCard: sim,
				type: SimQuotaType.VOICE,
				balance: new BigDecimal(100),
				status: SimQuotaStatus.ENABLED,
				lastUpdated: now,
				dateCreated: now,
				endDate: FAR_DATE)
		simQuotaRepository.save(quotaVoice)

		var quotaTraffic = new SimQuotaEntity(
				simCard: sim,
				type: SimQuotaType.TRAFFIC,
				balance: new BigDecimal(200),
				status: SimQuotaStatus.ENABLED,
				lastUpdated: now,
				dateCreated: now,
				endDate: FAR_DATE)
		simQuotaRepository.save(quotaTraffic)

		mvc.perform(MockMvcRequestBuilders
				.get("/sims/{id}/quota/available", sim.id)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.voice', is(100d)))
				.andExpect(jsonPath('$.traffic', is(200d)))
	}

	@Test
	void 'activateSim, disable'() throws Exception {
		var sim = new SimCardEntity(status: SimCardStatus.ENABLED, msisdn: '79990000001')
		simCardRepository.save(sim)

		mvc.perform(MockMvcRequestBuilders
				.post("/sims/{id}/activate", sim.id)
				.param("enabled", "false")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(""))

		sim = simCardRepository.findById(sim.id).orElseThrow()
		assertEquals(SimCardStatus.DISABLED, sim.status)
	}

	@Test
	void 'activateSim, enable'() throws Exception {
		var sim = new SimCardEntity(status: SimCardStatus.DISABLED, msisdn: "79990000001")
		simCardRepository.save(sim)

		mvc.perform(MockMvcRequestBuilders
				.post("/sims/{id}/activate", sim.id)
				.param("enabled", "true")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(""))

		sim = simCardRepository.findById(sim.id).orElseThrow()
		assertEquals(SimCardStatus.ENABLED, sim.status)
	}
}
