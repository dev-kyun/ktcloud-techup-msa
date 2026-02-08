package com.kyun.appservice;

import com.kyun.common.Greeting;
import com.kyun.common.MessageFormatter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private final MessageFormatter formatter = new MessageFormatter();

    @GetMapping("/app/hello")
    public Greeting hello() {
        String message = formatter.format("APP", "홍길동");
        return new Greeting(message);
    }
}
