package com.kyun.multi.admin;

import com.kyun.multi.core.UserService;
import com.kyun.multi.util.TextFormatter;

public class MainAdmin {
    public static void main(String[] args) {
        UserService userService = new UserService();
        TextFormatter formatter = new TextFormatter();
        String name = userService.findName();
        System.out.println("[ADMIN] " + formatter.decorate("관리자: " + name));
    }
}
