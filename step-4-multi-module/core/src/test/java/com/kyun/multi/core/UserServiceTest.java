package com.kyun.multi.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceTest {
    @Test
    void returnsExpectedName() {
        UserService userService = new UserService();
        assertEquals("홍길동", userService.findName());
    }
}
