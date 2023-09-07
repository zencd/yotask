package svc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import svc.entity.SimCardEntity;
import svc.repository.SimCardRepository;

import java.io.IOException;
import java.util.List;

/**
 * For demo only.
 */
@Profile("demo")
@RestController
public class DemoController {

    @Value("classpath:banner.txt")
    Resource bannerFile;

    @Autowired
    SimCardRepository simCardRepository;

    @ResponseBody
    @GetMapping(value = "/", produces = "text/html; charset=utf-8")
    Resource index() throws IOException {
        return bannerFile;
    }

    @GetMapping("/sims")
    @Operation(summary = "For development only.")
    public List<SimCardEntity> allSims() {
        return simCardRepository.findAll();
    }
}
