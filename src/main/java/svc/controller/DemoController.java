package svc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Displays some helpful info.
 */
@Controller
public class DemoController {

    @Value("classpath:banner.txt")
    Resource bannerFile;

    @Autowired
    ResourceLoader resourceLoader;

    @ResponseBody
    @RequestMapping(value = "/", produces = "text/plain; charset=utf-8")
    String showHelp() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:banner.txt");
        return new String(loadResource(resource), StandardCharsets.UTF_8);
    }

    static byte[] loadResource(Resource resource) throws IOException {
        try(InputStream inputStream = resource.getInputStream()) {
            return FileCopyUtils.copyToByteArray(inputStream);
        }
    }
}
