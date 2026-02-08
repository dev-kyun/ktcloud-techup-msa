package com.kyun.single;

public class MessageFormatter {
    public String format(String role, String name) {
        return "[" + role + "] " + name;
    }
}
