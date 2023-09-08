package svc.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import svc.dto.ConsumeQuotaRequest
import svc.dto.CreateQuotaRequest
import svc.dto.SimQuotaType
import svc.exception.NotFoundException
import svc.service.QuotaService
import svc.service.SimService

import java.time.OffsetDateTime

import static org.mockito.BDDMockito.given
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Тесты на валидацию запросов, это требует поднятия контекста.
 */
@WebMvcTest(SimServiceController.class)
class SimServiceControllerTest {

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    private MockMvc mvc

    @MockBean
    private QuotaService quotaService

    @MockBean
    private SimService simService // required

    @Test
    void 'activateSim, fails if param `enabled` is incorrect'() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/sims/111/activate")
                .param("enabled", "incorrect")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
    }

    @Test
    void 'addQuota, incorrect type'() throws Exception {
        String reqBody = '{"type":"badType","amount":20,"endDate":"2023-09-06T15:45:43.681Z"}'
        mvc.perform(MockMvcRequestBuilders
                .post("/sims/{id}/quota/add", 2)
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
    }

    @Test
    void 'addQuota, zero amount'() throws Exception {
        var request = new CreateQuotaRequest(
                amount: 0,
                type: SimQuotaType.VOICE,
                endDate: OffsetDateTime.now())
        mvc.perform(MockMvcRequestBuilders
                .post("/sims/{id}/quota/add", 2)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
    }

    @Test
    void 'addQuota, negative amount'() throws Exception {
        var request = new CreateQuotaRequest(
                amount: -1,
                type: SimQuotaType.VOICE,
                endDate: OffsetDateTime.now())
        mvc.perform(MockMvcRequestBuilders
                .post("/sims/{id}/quota/add", 2)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
    }

    @Test
    void 'consumeQuota, zero amount'() throws Exception {
        var request = new ConsumeQuotaRequest(
                amount: 0,
                type: SimQuotaType.VOICE)
        mvc.perform(MockMvcRequestBuilders
                .post("/sims/{id}/quota/consume", 2)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
    }

    @Test
    void 'consumeQuota, incorrect type'() throws Exception {
        String reqBody = '{"type":"badType","amount":1}'
        mvc.perform(MockMvcRequestBuilders
                .post("/sims/{id}/quota/consume", 2)
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
    }

    @Test
    void 'getQuotaAvailable, nonexistent simId leads to 404'() throws Exception {
        given(quotaService.getQuotaAvailable(999999))
                .willThrow(NotFoundException.class)
        mvc.perform(MockMvcRequestBuilders
                .get("/sims/999999/quota/available")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""))
    }
}
