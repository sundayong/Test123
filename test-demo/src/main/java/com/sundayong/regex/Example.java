package com.sundayong.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Example {
    public static void main(String[] args) {
        final String regex = "^ETB-2022-\\d{1,}";
        final String string = "ETB-2022-01\n"
	 + "ETB-2022-02\n"
	 + "ETB-2022-03\n"
	 + "E-ETB-2022-01\n"
	 + "E-ETB-2022-02";
        
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(string);
        
        while (matcher.find()) {
            System.out.println("Full match: " + matcher.group(0));
            
            for (int i = 1; i <= matcher.groupCount(); i++) {
                System.out.println("Group " + i + ": " + matcher.group(i));
            }
        }
    }
}
