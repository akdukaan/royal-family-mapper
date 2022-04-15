package com.kaanakduman;

import java.util.*;

public class Main {
    public static HashMap<String, Person> people = new HashMap<>();
    public static int maxSize = 9;
    public static HashSet<String> visited;
    public static Stack<Person> stack;

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

        // TODO Set the Y values of the people using FELINE


        // TODO Create a large (100+) set of (person1, person2) combinations
        // TODO Run FELINE on it once using depthFirstSearch and once using prunedDepthFirstSearch
        // TODO And compare the times it took for the two different methods
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

        System.out.println("Searching for a path between " + Person.parseName(link1) + " and " + Person.parseName(link2) + "...");
        if (person1.x > person2.x || person1.y > person2.y) {
            System.out.println("Feline says that no path exists.");
            return;
        }
        System.out.println("Feline says path may exist. Performing DFS...");

        // Perform DFS
        boolean pathExists = depthFirstSearch(person1, person2);
        System.out.println("Does the path exist? " + pathExists);
    }

    /**
     * The DFS we all know
     * @param start The node to start at
     * @param goal The node to end at
     * @return If it is possible to reach goal from start
     */
    static public boolean depthFirstSearch(Person start, Person goal) {
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
    static public boolean prunedDepthFirstSearch(Person start, Person goal) {
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

    /**
     * Does a topological sort on p and all of its children
     * @param p The person to sort the children of
     */
    static public void topologicalSort(Person p) {
        if (visited.contains(p.name)) return;
        for (Person c : p.children) {
            topologicalSort(c);
        }
        stack.add(p);
        visited.add(p.name);
    }
}
