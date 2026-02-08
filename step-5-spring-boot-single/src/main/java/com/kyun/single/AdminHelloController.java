package com.kyun.single;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminHelloController {
    private final MessageFormatter formatter = new MessageFormatter();

    @GetMapping("/admin/hello")
    public Greeting hello() {
        return new Greeting(formatter.format("ADMIN", "홍길동"));
    }
}
