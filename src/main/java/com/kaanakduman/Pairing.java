package com.kaanakduman;

public class Pairing {
    public Person person1;
    public Person person2;

    public Pairing(Person p1, Person p2) {
        person1 = p1;
        person2 = p2;
    }

    public Person getPerson1() {
        return person1;
    }

    public Person getPerson2() {
        return person2;
    }
}
