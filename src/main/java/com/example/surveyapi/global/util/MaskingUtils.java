package com.example.surveyapi.global.util;

public class MaskingUtils {

    public static String maskName(String name) {

        if (name.length() < 2) {
            return "";
        }

        int mid = name.length() / 2;

        return name.substring(0, mid) + "*" + name.substring(mid + 1);
    }

    public static String maskEmail(String email, Long userId) {
        int atIndex = email.indexOf("@");
        if (atIndex == -1) {
            return email;
        }

        String prefix = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        String maskPrefix =
            prefix.length() < 3 ?
                "*".repeat(prefix.length()) :
                prefix.substring(0, 3) + "*".repeat(prefix.length() - 3);
        return maskPrefix + "+" + userId  + domain;
    }

    public static String maskPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("(\\d{3})-\\d{4}-(\\d{2})(\\d{2})", "$1-****-**$3");
    }

    public static String maskDistrict(String district) {
        return "*".repeat(district.length());
    }

    public static String maskDetailAddress(String address) {
        return "*".repeat(address.length());
    }

    public static String maskPostalCode(String postalCode) {
        if (postalCode.length() < 2) {
            return "*".repeat(postalCode.length());
        }

        return postalCode.substring(0, 2) + "*".repeat(postalCode.length() - 2);
    }
}
