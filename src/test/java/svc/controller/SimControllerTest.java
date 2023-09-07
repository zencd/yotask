package svc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import svc.dto.ConsumeQuotaRequest;
import svc.dto.CreateQuotaRequest;
import svc.dto.SimQuota;
import svc.dto.SimQuotaAvailable;
import svc.dto.SimQuotaStatus;
import svc.dto.SimQuotaType;
import svc.exception.NotFoundException;
import svc.repository.SimCardRepository;
import svc.service.SimCardService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SimController.class)
public class SimControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SimCardService simCardService;

    @MockBean
    private SimCardRepository simCardRepository;

    private static final Matcher<Object> NULL = IsNull.nullValue();

    @Test
    public void activateSim_enabling_ok() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/sims/111/activate")
                .param("enabled", "true")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(simCardService).activateSim(111, true);
    }

    @Test
    public void activateSim_disabling_ok() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/sims/111/activate")
                .param("enabled", "false")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(simCardService).activateSim(111, false);
    }

    @Test
    public void activateSim_failsIfParamEnabledIsIncorrect() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/sims/111/activate")
                .param("enabled", "incorrect")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void addQuota_mainSuccess() throws Exception {
        given(simCardService.createQuota(any())).willReturn(SimQuota.builder()
                .id(500L)
                .balance(BigDecimal.valueOf(123))
                .type(SimQuotaType.VOICE)
                .status(SimQuotaStatus.ENABLED)
                .build());

        var request = CreateQuotaRequest.builder()
                .amount(new BigDecimal(20))
                .type(SimQuotaType.VOICE)
                .endDate(OffsetDateTime.now())
                .build();

        mvc.perform(MockMvcRequestBuilders
                        .post("/sims/{id}/quota/add", 2)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(500)))
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.type", is("voice")))
                .andExpect(jsonPath("$.balance", is(123)))
                .andExpect(jsonPath("$.endDate", is(NULL)))
                .andExpect(jsonPath("$.dateCreated", is(NULL)))
                .andExpect(jsonPath("$.lastUpdated", is(NULL)));
    }

    @Test
    public void addQuota_badType() throws Exception {
        String reqBody = "{\"type\":\"badType\",\"amount\":20,\"endDate\":\"2023-09-06T15:45:43.681Z\"}";
        mvc.perform(MockMvcRequestBuilders
                .post("/sims/{id}/quota/add", 2)
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void addQuota_badAmountZero() throws Exception {
        var request = CreateQuotaRequest.builder()
                .amount(new BigDecimal(0))
                .type(SimQuotaType.VOICE)
                .endDate(OffsetDateTime.now())
                .build();
        mvc.perform(MockMvcRequestBuilders
                .post("/sims/{id}/quota/add", 2)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
        ;
    }

    @Test
    public void addQuota_badAmountNegative() throws Exception {
        var request = CreateQuotaRequest.builder()
                .amount(new BigDecimal(-1))
                .type(SimQuotaType.VOICE)
                .endDate(OffsetDateTime.now())
                .build();
        mvc.perform(MockMvcRequestBuilders
                .post("/sims/{id}/quota/add", 2)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
        ;
    }

    @Test
    public void consumeQuota_mainSuccess() throws Exception {
        var request = ConsumeQuotaRequest.builder()
                .amount(new BigDecimal(20))
                .type(SimQuotaType.VOICE)
                .build();
        String body = objectMapper.writeValueAsString(request);
        mvc.perform(MockMvcRequestBuilders
                .post("/sims/{id}/quota/consume", 2)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        List<Invocation> invocations = (List<Invocation>) Mockito.mockingDetails(simCardService).getInvocations();
        assertEquals(1, invocations.size());
    }

    @Test
    public void consumeQuota_badAmount() throws Exception {
        var request = ConsumeQuotaRequest.builder()
                .amount(new BigDecimal(-1))
                .type(SimQuotaType.VOICE)
                .build();
        mvc.perform(MockMvcRequestBuilders
                .post("/sims/{id}/quota/consume", 2)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void consumeQuota_badType() throws Exception {
        String reqBody = "{\"type\":\"badType\",\"amount\":1}";
        mvc.perform(MockMvcRequestBuilders
                .post("/sims/{id}/quota/consume", 2)
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getQuotaAvailable_mainSuccess() throws Exception {
        SimQuotaAvailable response = new SimQuotaAvailable(new BigDecimal(200), new BigDecimal(300));

        given(simCardService.getQuotaAvailable(2L))
                .willReturn(response);

        mvc.perform(MockMvcRequestBuilders
                .get("/sims/{id}/quota/available", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minutes", is(200)))
                .andExpect(jsonPath("$.megabytes", is(300)));
    }

    @Test
    public void getQuotaAvailable_inexistentSimIdLeadsTo404() throws Exception {
        given(simCardService.getQuotaAvailable(999999))
                .willThrow(NotFoundException.class);
        mvc.perform(MockMvcRequestBuilders
                .get("/sims/999999/quota/available")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

}