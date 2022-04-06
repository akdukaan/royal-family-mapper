package com.kaanakduman;

import java.util.*;

public class Main {
    public static HashMap<String, Person> people = new HashMap<>();
    public static int maxSize = 500;

    public static void main(String[] args) {
        new Person("https://en.wikipedia.org/wiki/Elizabeth_II");
        System.out.println();
        System.out.println("====================================================================================");
        System.out.println();
        for (Person p : people.values()) {
            System.out.println(p);
        }
    }
}
