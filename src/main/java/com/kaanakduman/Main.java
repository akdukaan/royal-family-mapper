package com.kaanakduman;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    // PEOPLE_SIZE - The number of vertices in the graph
    // 50 is good for an initial test, 2000 for a stronger test, 10000 for our final test
    final public static int PEOPLE_SIZE = 10000;

    // NUM_PAIRINGS - The number of random pairings to create in testing the efficiency of our algorithm
    // 10000 is good for an initial test, 1000000 is good for our final test
    final public static int NUM_PAIRINGS = 10000000;

    // WRITE_TO_JSON - Whether we should overwrite the results in output.yml.
    // Typically, leave as false unless you fixed something and need a new json.
    final public static boolean WRITE_TO_JSON = true;

    // Stores a list of people with their link as the key
    public static HashMap<String, Person> people = new HashMap<>();

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        new Person("https://en.wikipedia.org/wiki/Elizabeth_II");
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000000;
        printBreak();
        for (Person p : people.values()) {
            System.out.println(p);
        }
        printBreak();
        System.out.println("Time to scrape wikipedia: " + duration + "s");

        for (Person p : people.values()) {
            if (p.parents.size() > 2) {
                System.err.println("ERROR: " + p.name + " has three parents: " + p.parents);
            }
        }

        // Set the x values of the people by topologicalSorting
        HashSet<Person> visited = new HashSet<>();
        Stack<Person> workingStack = new Stack<>();
        Stack<Person> solutionStack = new Stack<>();

        startTime = System.nanoTime();
        for (Person p : people.values()) {
            if (!visited.contains(p)) {
                workingStack.add(p);
                while (!workingStack.isEmpty()) {
                    Person working = workingStack.peek();
                    if (visited.contains(working)) {
                        solutionStack.add(workingStack.pop());
                    } else {
                        visited.add(working);
                        for (Person c : working.children) {
                            if (!visited.contains(c)) {
                                workingStack.add(c);
                            }
                        }
                    }
                }
            }
        }
        int i = 1;
        while (!solutionStack.isEmpty()) {
            solutionStack.pop().x = i;
            i++;
        }

        // Assign Y values using Kahn's algorithm
        for (Person p : people.values()) {
            p.indegree = p.parents.size();
        }
        Queue<Person> queue = new LinkedList<>();
        for (Person p : people.values()) {
            if (p.indegree == 0) {
                queue.add(p);
            }
        }
        int y = 1;
        while (!queue.isEmpty()) {
            Person next = queue.remove();
            next.y = y;
            y++;
            for (Person child : next.children) {
                child.indegree--;
                if (child.indegree == 0) {
                    queue.add(child);
                }
            }
        }

        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;
        System.out.println("Time to assign (x,y) coordinates: " + duration + "ms");

        for (Person person : people.values()) {
            for (Person child : person.children) {
                if (child.y <= person.y) {
                    System.err.println("ERROR: " + child + " is a child of but is positioned to the BOTTOM of " + person);
                }
                if (child.x <= person.x) {
                    System.err.println("ERROR: " + child + " is a child of but is positioned to the LEFT of " + person);
                }
            }
        }

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
        duration = (endTime - startTime) / 1000000;
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
        duration = (endTime - startTime) / 1000000;
        System.out.println("Time for basic DFS with feline: " + duration + "ms");

        // Run basic DFS without feline
        for (Pairing pairing : pairings) {
            Person person1 = pairing.getPerson1();
            Person person2 = pairing.getPerson2();
            boolean pathExists = depthFirstSearch(person1, person2);
            //System.out.println("Does the path exist between " + person1.name + " and " + person2.name + "? " + pathExists);
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;
        System.out.println("Time for basic DFS without feline: " + duration + "ms");

        try {
            writeJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeJson() throws IOException {
        if (!WRITE_TO_JSON) return;
        FileWriter file = new FileWriter("output.json");
        JSONObject root = new JSONObject();
        JSONObject obj;
        for (Person p : people.values()) {
            if (p.y > 0) {
                obj = new JSONObject();
                obj.put("name", p.name);
                obj.put("x", p.x);
                obj.put("y", p.y);

                JSONArray children = new JSONArray();
                for (Person c : p.children) {
                    if (c.y > 0) {
                        JSONObject childrenCoords = new JSONObject();
                        childrenCoords.put("x", c.x);
                        childrenCoords.put("y", c.y);
                        childrenCoords.put("name", c.name);
                        children.add(childrenCoords);
                    }
                }
                obj.put("children", children);
                root.put(p.name, obj);
            }
        }
        file.write(root.toJSONString());

        file.close();

    }

    /**
     * The DFS we all know
     * @param start The node to start at
     * @param goal The node to end at
     * @return If it is possible to reach goal from start
     */
    public static boolean depthFirstSearch(Person start, Person goal) {
        HashSet<Person> visited = new HashSet<>();
        Stack<Person> stack = new Stack<>();
        stack.add(start);
        while (!stack.isEmpty()) {
            Person p = stack.pop();
            if (p.equals(goal)) return true;
            if (!visited.contains(p)) {
                visited.add(p);
                stack.addAll(p.children);
            }
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
        HashSet<Person> visited = new HashSet<>();
        Stack<Person> stack = new Stack<>();
        stack.add(start);
        while (!stack.isEmpty()) {
            Person p = stack.pop();
            if (p.equals(goal)) return true;
            if (!visited.contains(p)) {
                visited.add(p);
                if (p.x < goal.x && p.y < goal.y) {
                    stack.addAll(p.children);
                }
            }
        }
        return false;
    }

    /**
     * Prints line break to pretty the console output
     */
    public static void printBreak() {
        System.out.println();
        System.out.println("====================================================================================");
        System.out.println();
    }
}
