package com.kyun.multi.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextFormatterTest {
    @Test
    void decoratesValueWithStars() {
        TextFormatter formatter = new TextFormatter();
        assertEquals("*** hello ***", formatter.decorate("hello"));
    }
}
