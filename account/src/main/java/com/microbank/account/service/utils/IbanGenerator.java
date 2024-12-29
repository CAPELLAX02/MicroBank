package com.microbank.account.service.utils;

public class IbanGenerator {
    public static String generateIBAN() {
        return "MB" + (100000000000L + (long) (Math.random() * 899999999999L));
    }
}
