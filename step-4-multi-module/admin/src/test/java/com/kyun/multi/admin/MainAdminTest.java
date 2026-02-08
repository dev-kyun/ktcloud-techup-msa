package com.kyun.multi.admin;

import com.kyun.multi.core.UserService;
import com.kyun.multi.util.TextFormatter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainAdminTest {
    @Test
    void formatsAdminOutput() {
        UserService userService = new UserService();
        TextFormatter formatter = new TextFormatter();
        String output = "[ADMIN] " + formatter.decorate("관리자: " + userService.findName());
        assertEquals("[ADMIN] *** 관리자: 홍길동 ***", output);
    }
}
