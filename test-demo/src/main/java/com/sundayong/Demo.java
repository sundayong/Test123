package com.sundayong;

import java.util.ArrayList;
import java.util.Collection;
//:Demo
public class Demo {

    public static int a;
    public static char b;
    public static boolean c;

    public static void print(int num) {
        for (int i = 31; i >= 0; i--) {
            System.out.print((num & (1 << i)) == 0 ? "0" : "1");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        //System.getProperties().list(System.out);
        //System.out.println(System.getProperty("user.name"));
        //System.out.println(System.getProperty("java.library.path"));

        //System.out.println("a:" + a);
        //System.out.println("b:" + b);
        //System.out.println("c:" + c);

        //byte a = 1;
        //System.out.println((+a));

        String str = "abc";
        char[] str1 = {'a','b','c'};
        System.out.println(str.equals(str1));
    }///:~
}
