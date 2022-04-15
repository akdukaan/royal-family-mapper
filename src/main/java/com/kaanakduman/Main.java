package com.kaanakduman;

import java.util.*;

public class Main {
    final public static int PEOPLE_SIZE = 200;
    final public static int NUM_PAIRINGS = 1000000;

    public static HashMap<String, Person> people = new HashMap<>();
    public static HashSet<String> visited;
    public static Stack<Person> stack;

    public static void main(String[] args) {
        new Person("https://en.wikipedia.org/wiki/Elizabeth_II");
        printBreak();
        for (Person p : people.values()) {
            System.out.println(p);
        }
        printBreak();

        // Set the x values of the people by topologicalSorting
        long startTime = System.nanoTime();
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

        // TODO Set the Y values of the people using FELINE


        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;
        System.out.println("Time to assign (x,y) coordinates: " + duration + "ms");

        // Create a large set of (person1, person2) combinations
        Person[] peopleArray = people.values().toArray(new Person[0]);
        HashSet<Pairing> pairings = new HashSet<>();
        for (i = 0; i < NUM_PAIRINGS; i++) {
            int index1 = new Random().nextInt(peopleArray.length);
            int index2 = new Random().nextInt(peopleArray.length);
            pairings.add(new Pairing(peopleArray[index1], peopleArray[index2]));
        }

        // Run pruned DFS with feline
        startTime = System.nanoTime();
        for (Pairing pairing : pairings) {
            Person person1 = pairing.getPerson1();
            Person person2 = pairing.getPerson2();
            boolean pathExists;
            if (person1.x > person2.x || person1.y > person2.y) {
                pathExists = false;
            } else {
                pathExists = prunedDepthFirstSearch(person1, person2);
            }
            //System.out.println("Does the path exist between " + person1.name + " and " + person2.name + "? " + pathExists);
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime)/1000000;
        System.out.println("Time for pruned DFS with feline: " + duration + "ms");

        // Run basic DFS with feline
        for (Pairing pairing : pairings) {
            Person person1 = pairing.getPerson1();
            Person person2 = pairing.getPerson2();
            boolean pathExists;
            if (person1.x > person2.x || person1.y > person2.y) {
                pathExists = false;
            } else {
                pathExists = depthFirstSearch(person1, person2);
            }
            //System.out.println("Does the path exist between " + person1.name + " and " + person2.name + "? " + pathExists);
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime)/1000000;
        System.out.println("Time for basic DFS with feline: " + duration + "ms");

        // Run basic DFS without feline
        for (Pairing pairing : pairings) {
            Person person1 = pairing.getPerson1();
            Person person2 = pairing.getPerson2();
            boolean pathExists = depthFirstSearch(person1, person2);
            //System.out.println("Does the path exist between " + person1.name + " and " + person2.name + "? " + pathExists);
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime)/1000000;
        System.out.println("Time for basic DFS without feline: " + duration + "ms");
    }

    /**
     * The DFS we all know
     * @param start The node to start at
     * @param goal The node to end at
     * @return If it is possible to reach goal from start
     */
    public static boolean depthFirstSearch(Person start, Person goal) {
        visited = new HashSet<>();
        stack = new Stack<>();
        stack.add(start);
        while (!stack.isEmpty()) {
            Person p = stack.pop();
            if (p.equals(goal)) return true;
            stack.addAll(p.children);
        }
        return false;
    }

    /**
     * This is similar to DFS but will terminate some branches early if FELINE determines a path isn't possible
     * @param start The node to start at
     * @param goal The node to end at
     * @return If it is possible to reach goal from start
     */
    public static boolean prunedDepthFirstSearch(Person start, Person goal) {
        visited = new HashSet<>();
        stack = new Stack<>();
        stack.add(start);
        while (!stack.isEmpty()) {
            Person p = stack.pop();
            if (p.equals(goal)) return true;
            if (p.x < goal.x && p.y < goal.y) {
                stack.addAll(p.children);
            }
        }
        return false;
    }

    // TODO Switch topologicalSort to be iterative instead of recursive
    /**
     * Does a topological sort on p and all of its children
     * @param p The person to sort the children of
     */
    public static void topologicalSort(Person p) {
        if (visited.contains(p.name)) return;
        for (Person c : p.children) {
            topologicalSort(c);
        }
        stack.add(p);
        visited.add(p.name);
    }

    public static void printBreak() {
        System.out.println();
        System.out.println("====================================================================================");
        System.out.println();
    }
}
