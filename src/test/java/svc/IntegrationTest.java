package svc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import svc.controller.SimController;
import svc.repository.SimCardRepository;
import svc.service.SimCardService;
import svc.service.SimCardServiceImpl;
import svc.service.SimServiceValidator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {
        SimController.class,
        SimCardRepository.class,
        SimCardServiceImpl.class,
        SimServiceValidator.class
})
@EnableJpaRepositories(basePackages="svc.repository")
@EnableAutoConfiguration
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SimCardRepository simCardRepository;

    @Autowired
    SimCardService simCardService;

    @Test
    public void test1() throws Exception {

        mockMvc.perform(get("/sims")).andExpect(status().is2xxSuccessful());
        mockMvc.perform(get("/xxx")).andExpect(status().isNotFound());

        //mockMvc.perform(post("/xxx", 42L)
        //        .contentType()
        //        .andExpect(status().isOk());
        //
    }

}