package model;

import java.util.Random;

public class Util {

    public static String genarateCode() {
        double r = Math.random();
        int x = (int) (r * 1000000);
        return String.format("%06d", x);
    }

    public static boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }

    public static boolean isPasswordValid(String password) {
        return password.matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$");
    }

    public static boolean isCodeValid(String code) {
        return code.matches("^\\d{4,5}$");
    }

    public static boolean isIntegers(String value) {
        return value.matches("^\\d+$");
    }

    public static boolean isDoubles(String value) {
        return value.matches("^\\d+(\\.\\d{2})?$");
    }

    public static boolean isMobileValid(String mobile){
        return mobile.matches("^07[0145678][0-9]{7}$");
    }
    
    public static String generateOrderId() {
        Random rand = new Random();
        int randomNumber = 1000 + rand.nextInt(9000);
        return "#OR" + randomNumber;
    }
}
