package com.kaanakduman;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLDecoder;
import java.util.HashSet;

public class Person {
    HashSet<Person> children = new HashSet<>();
    HashSet<Person> parents = new HashSet<>();
    String name;
    String link;
    int x = 0;
    int y = 0;

    public Person(String link) {
        if (Main.people.size() >= Main.PEOPLE_SIZE) return;
        link = fixedLink(link);
        this.link = link;
        name = parseName(link);
        if (name == null) return;
        if (Main.people.containsKey(link)) return;
        Main.people.put(link, this);
        String notifyMessage = Main.people.size() + " " + name + " " + link;
        System.out.println(notifyMessage);
        Document doc;
        try {
            doc = Jsoup.connect(link).get();
            Element table = getTable(doc);
            if (table != null && children.size() < Main.PEOPLE_SIZE) {
                createFamily(table);
            }
        } catch (Exception e) {
            System.out.println("Ignoring exception");
            e.printStackTrace();
        }
    }

    /**
     * Sometimes uncommon characters in the URL get encoded. This method converts the entire URL to ASCII
     * @param link The partially-encoded URL
     * @return An ASCII URL
     */
    public static String fixedLink(String link) {
        if (link.contains("%")) {
            System.err.println("ERROR BAD LINK " + link);
            link = URLDecoder.decode(link);
            System.out.println("LINK IS NOW " + link);

        }
        return link;
    }

    /**
     * Tells you if there's an issue with a wikipedia link
     * @param link The link of the wikipedia profile to search
     * @return If the link won't lead to a real person's wikipedia profile
     */
    public static boolean isLinkMalformed(String link) {
        if (link.contains(";redlink=1")) return true;
        if (link.contains("en.wikipedia.orghttps")) return true;
        if (link.contains("Elchingen_Abbey")) return true;
        return false;
    }

    /**
     * Parses the name from a link
     * @param link The link to parse from
     * @return The name of the person at that link or null if it's a bad link
     */
    public static String parseName(String link) {
        if (isLinkMalformed(link)) return null;
        String[] arr = link.split("wiki/");
        String name = arr[arr.length - 1].replace("_", " ").trim();
        if (name.length() <= 1) return null;
        if (name.startsWith("https://")) return null;
        if (name.startsWith("(")) return null;
        return name;
    }

    /**
     * Calls createParent and createChildren on rows it can identify as being relevant
     * @param table The table containing basic bio about a person
     */
    public void createFamily(Element table) {
        Elements rows = table.select("tr");
        for (int i = 1; i < rows.size(); i++) {
            Element row = rows.get(i);
            Element header = getHeader(row);
            if (header != null && (header.text().equals("Father") || header.text().equals("Mother"))) {
                createParent(row);
            } else if (header != null && (header.text().equals("Issue Detail") || header.text().equals("Issue") || header.text().equals("Children"))) {
                createChildren(row);
            }
        }
    }

    /**
     * Returns a person node from their link
     * @param link The link to a person
     * @return The person we've found with the link
     */
    public Person getPerson(String link) {
        if (Main.people.containsKey(link)) {
            return Main.people.get(link);
        }
        return null;
    }

    /**
     * Adds a node's child to our graph
     * @param string The name of a child which only sometimes actually leads to a real wikipedia profile
     */
    public void createChild(String string) {
        Person child;
        if (string.contains("<a href=\"/wiki/")) {
            string = string.split("<a href=\"")[1].split("\"")[0];
            if (string.contains("redlink=1")) return;
            String link = "https://en.wikipedia.org" + string;
            child = getPerson(link);
            if (child == null) {
                child = new Person(link);
            }
            if (child.name != null) {
                this.children.add(child);
                child.parents.add(this);
            }
        }
    }

    /**
     * Adds a node's children to our graph
     * @param row A row in a table containing the name of the children
     */
    public void createChildren(Element row) {
        Element data = row.getElementsByClass("infobox-data").first();
        if (data == null) return;
        String[] stringData = data.toString().split("<br>");
        for (String string : stringData) {
            createChild(string);
        }
    }

    /**
     * Gets the header of a row in a table
     * @param row the row
     * @return the header
     */
    public Element getHeader(Element row) {
        return row.getElementsByClass("infobox-label").first();
    }

    /**
     * Adds a node's parents to our graph
     * @param row A row in a table containing the name of the parent
     */
    public void createParent(Element row) {
        Element data = row.getElementsByClass("infobox-data").first();
        if (data == null) return;
        Element member;
        if (data.getAllElements().size() > 0) {
            member = data.getAllElements().get(data.getAllElements().size() - 1);
        } else {
            member = data;
        }
        String href = member.attr("href");
        String link = "https://en.wikipedia.org" + href;
        Person parent = getPerson(link);
        if (parent == null) {
            parent = new Person(link);
        }
        if (parent.name != null) {
            parent.children.add(this);
            this.parents.add(parent);
        }

    }

    /**
     * Get the box containing parent and child info from a wikipedia page
     * @param doc The webpage to investigate
     * @return The first table in the wikipedia page
     */
    public Element getTable(Document doc) {
        Elements infoboxes = doc.getElementsByClass("infobox vcard");
        if (infoboxes.size() == 0) return null;
        return infoboxes.get(0).select("table").get(0);
    }

    @Override
    public String toString() {
        StringBuilder childrenNames = new StringBuilder();
        for (Person child : children) {
            childrenNames.append(" ").append("\n\t> ").append(child.name);
        }
        return "+ " + name + " (" + link + ")" + childrenNames;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Person) {
            Person p = (Person) o;
            if (p.name.equals(this.name)) {
                return true;
            }
        }
        return false;
    }
}