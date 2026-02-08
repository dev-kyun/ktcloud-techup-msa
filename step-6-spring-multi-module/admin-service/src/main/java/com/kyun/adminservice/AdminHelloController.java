package com.kyun.adminservice;

import com.kyun.common.Greeting;
import com.kyun.common.MessageFormatter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminHelloController {
    private final MessageFormatter formatter = new MessageFormatter();

    @GetMapping("/admin/hello")
    public Greeting hello() {
        String message = formatter.format("ADMIN", "홍길동");
        return new Greeting(message);
    }
}
