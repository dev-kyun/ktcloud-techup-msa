package com.kyun.common;

public class MessageFormatter {
    public String format(String role, String name) {
        return "[" + role + "] " + name;
    }
}
