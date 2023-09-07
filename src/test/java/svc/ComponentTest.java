package svc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import svc.dto.ConsumeQuotaRequest;
import svc.dto.CreateQuotaRequest;
import svc.dto.SimQuotaType;
import svc.entity.SimCardEntity;
import svc.entity.SimCardStatus;
import svc.entity.SimQuotaEntity;
import svc.entity.SimQuotaStatus;
import svc.repository.SimCardRepository;
import svc.repository.SimQuotaRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тест с поднятием контекста и БД.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ComponentTest {

	private static final Matcher<Object> NULL = IsNull.nullValue();
	private static final OffsetDateTime FAR_DATE = OffsetDateTime.of(2099, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

	@Autowired
	private MockMvc mvc;

	@Autowired
	SimCardRepository simCardRepository;

	@Autowired
	SimQuotaRepository simQuotaRepository;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	public void contextLoads() {
	}

	@Test
	public void addQuota_mainSuccess() throws Exception {
		SimCardEntity sim = SimCardEntity.builder().msisdn("79990000001").build();
		simCardRepository.save(sim);

		var request = CreateQuotaRequest.builder()
				.amount(new BigDecimal(20))
				.type(SimQuotaType.VOICE)
				.endDate(OffsetDateTime.now())
				.build();

		mvc.perform(MockMvcRequestBuilders
						.post("/sims/{id}/quota/add", sim.getId())
						.content(objectMapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.status", is(1)))
				.andExpect(jsonPath("$.type", is("voice")))
				.andExpect(jsonPath("$.balance", is(20)))
				.andExpect(jsonPath("$.endDate", is(notNullValue())))
				.andExpect(jsonPath("$.dateCreated", is(notNullValue())))
				.andExpect(jsonPath("$.lastUpdated", is(notNullValue())))
		;
	}

	@Test
	public void consumeQuota_mainSuccess() throws Exception {
		var now = OffsetDateTime.now();

		var sim = SimCardEntity.builder().msisdn("79990000001").build();
		simCardRepository.save(sim);

		var quota = SimQuotaEntity.builder()
				.simCard(sim)
				.type(SimQuotaType.VOICE)
				.balance(new BigDecimal(100))
				.status(SimQuotaStatus.ENABLED)
				.lastUpdated(now)
				.dateCreated(now)
				.endDate(FAR_DATE)
				.build();
		simQuotaRepository.save(quota);

		var request = ConsumeQuotaRequest.builder()
				.simId(sim.getId())
				.type(SimQuotaType.VOICE)
				.amount(new BigDecimal(10))
				.build();
		mvc.perform(MockMvcRequestBuilders
						.post("/sims/{id}/quota/consume", sim.getId())
						.content(objectMapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(""));

		quota = simQuotaRepository.findById(quota.getId()).orElseThrow();
		assertEquals(new BigDecimal("90.00"), quota.getBalance());
	}

	@Test
	public void getQuotaAvailable_mainSuccess() throws Exception {
		var now = OffsetDateTime.now();

		var sim = SimCardEntity.builder().msisdn("79990000001").build();
		simCardRepository.save(sim);

		var quotaVoice = SimQuotaEntity.builder()
				.simCard(sim)
				.type(SimQuotaType.VOICE)
				.balance(new BigDecimal(100))
				.status(SimQuotaStatus.ENABLED)
				.lastUpdated(now)
				.dateCreated(now)
				.endDate(FAR_DATE)
				.build();
		simQuotaRepository.save(quotaVoice);

		var quotaTraffic = SimQuotaEntity.builder()
				.simCard(sim)
				.type(SimQuotaType.TRAFFIC)
				.balance(new BigDecimal(200))
				.status(SimQuotaStatus.ENABLED)
				.lastUpdated(now)
				.dateCreated(now)
				.endDate(FAR_DATE)
				.build();
		simQuotaRepository.save(quotaTraffic);

		mvc.perform(MockMvcRequestBuilders
						.get("/sims/{id}/quota/available", sim.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.minutes", is(100.0)))
				.andExpect(jsonPath("$.megabytes", is(200.0)))
		;
	}

	@Test
	public void activateSim() throws Exception {
		var sim = SimCardEntity.builder().status(SimCardStatus.ENABLED).msisdn("79990000001").build();
		simCardRepository.save(sim);

		{
			// disable it
			mvc.perform(MockMvcRequestBuilders
							.post("/sims/{id}/activate", sim.getId())
							.param("enabled", "false")
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().string(""));

			sim = simCardRepository.findById(sim.getId()).orElseThrow();
			assertEquals(SimCardStatus.DISABLED, sim.getStatus());
		}

		{
			// enable it again
			mvc.perform(MockMvcRequestBuilders
							.post("/sims/{id}/activate", sim.getId())
							.param("enabled", "true")
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().string(""));

			sim = simCardRepository.findById(sim.getId()).orElseThrow();
			assertEquals(SimCardStatus.ENABLED, sim.getStatus());
		}
	}

}
