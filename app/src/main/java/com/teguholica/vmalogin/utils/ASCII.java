package com.teguholica.vmalogin.utils;

public class ASCII {

    public static String convert(String name) {
        String sOctal = name;

        Integer iOctal = Integer.parseInt(sOctal, 8);

        char cOctal = (char)iOctal.intValue();
        return String.valueOf(cOctal);
    }

}
