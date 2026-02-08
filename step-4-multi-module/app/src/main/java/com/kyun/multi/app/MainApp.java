package com.kyun.multi.app;

import com.kyun.multi.core.UserService;
import com.kyun.multi.util.TextFormatter;

public class MainApp {
    public static void main(String[] args) {
        UserService userService = new UserService();
        TextFormatter formatter = new TextFormatter();
        String name = userService.findName();
        System.out.println("[APP] " + formatter.decorate(name));
    }
}
