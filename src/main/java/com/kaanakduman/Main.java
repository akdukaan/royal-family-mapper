package com.kaanakduman;

import java.util.*;

public class Main {
    public static HashMap<String, Person> people = new HashMap<>();
    public static int maxSize = 9;
    static HashSet<String> visited;
    static Stack<Person> stack;

    public static void main(String[] args) {
        new Person("https://en.wikipedia.org/wiki/Elizabeth_II");
        System.out.println();
        System.out.println("====================================================================================");
        System.out.println();
        for (Person p : people.values()) {
            System.out.println(p);
        }
        System.out.println();
        System.out.println("====================================================================================");
        System.out.println();

        // Set the x values of the people by topologicalSorting
        visited = new HashSet<>();
        stack = new Stack<>();
        for (Person p : people.values()) {
            topologicalSort(p);
        }
        int i = 1;
        while (!stack.isEmpty()) {
            stack.pop().x = i;
            i++;
        }

        // TODO Set the Y values of the people


        Scanner scanner = new Scanner(System.in);
        System.out.println("Link of person 1:");
        String link1 = scanner.nextLine();
        if (!people.containsKey(link1)) {
            System.out.println("Error: Person not found");
            return;
        }
        Person person1 = people.get(link1);
        System.out.println("Link of person 2:");
        String link2 = scanner.nextLine();
        if (!people.containsKey(link2)) {
            System.out.println("Error: Person not found");
            return;
        }
        Person person2 = people.get(link2);

        System.out.println("Searching for a path between " + Person.fixedName(link1) + " and " + Person.fixedName(link2) + "...");
        if (person1.x > person2.x) {
            System.out.println("Feline says that no path exists.");
            System.out.println(person1.x);
            System.out.println(person2.x);
            return;
        }
        System.out.println("Feline says path may exist. Performing DFS...");
        // TODO DFS
    }

    static public void topologicalSort(Person p) {
        if (visited.contains(p.name)) return;
        for (Person c : p.children) {
            topologicalSort(c);
        }
        stack.add(p);
        visited.add(p.name);
    }
}
