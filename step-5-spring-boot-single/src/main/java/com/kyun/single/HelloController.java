package com.kyun.single;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private final MessageFormatter formatter = new MessageFormatter();

    @GetMapping("/app/hello")
    public Greeting hello() {
        return new Greeting(formatter.format("APP", "홍길동"));
    }
}
