package com.kyun.multi.app;

import com.kyun.multi.core.UserService;
import com.kyun.multi.util.TextFormatter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainAppTest {
    @Test
    void formatsAppOutput() {
        UserService userService = new UserService();
        TextFormatter formatter = new TextFormatter();
        String output = "[APP] " + formatter.decorate(userService.findName());
        assertEquals("[APP] *** 홍길동 ***", output);
    }
}
