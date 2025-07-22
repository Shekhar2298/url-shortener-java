package com.example.core;

import java.util.Random;

public class URLShortener {
//    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//    private static final int LENGTH = 6;
//    private static final Random random = new Random();
//
//    public static String generateShortCode() {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < LENGTH; i++) {
//            sb.append(BASE62.charAt(random.nextInt(BASE62.length())));
//        }
//        return sb.toString();
//    }
    public static String generateRandomCode(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

}
