package com.jyr.system.util;

public class SequenceGenerator {

    private SequenceGenerator() {}

    public static String generateNext(String lastNumber, String prefix) {
        if (lastNumber == null || lastNumber.isEmpty()) {
            return prefix + "00001";
        }
        String numericPart = lastNumber.replaceAll("[^0-9]", "");
        long next = Long.parseLong(numericPart) + 1;
        return prefix + String.format("%05d", next);
    }
}
