package com.kaanakduman;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;

public class Person {
    HashSet<Person> children = new HashSet<>();
    HashSet<Person> parents = new HashSet<>();
    String name;
    String link;
    int x = 0;
    int y = 0;
    int indegree;

    public Person(String link) {
        if (Main.people.size() >= Main.PEOPLE_SIZE) return;
        this.link = link;
        if (isLinkMalformed(link)) return;
        Document doc;
        try {
            doc = Jsoup.connect(link).get();
            name = doc.title().split(" - Wikipedia")[0];
            if (name.contains("#")) {
                name = null;
            }
            if (Main.people.containsKey(name)) {
                name = null;
            }
            if (name == null) return;
            Main.people.put(name, this);
            String notifyMessage = Main.people.size() + " " + name + " " + link;
            System.out.println(notifyMessage);
            Element table = getTable(doc);
            if (table != null && children.size() < Main.PEOPLE_SIZE) {
                createFamily(table);
            }
        } catch (Exception e) {
            System.err.println("Error: " + link);
        }
    }

    /**
     * Tells you if there's an issue with a wikipedia link
     * @param link The link of the wikipedia profile to search
     * @return If the link won't lead to a real person's wikipedia profile
     */
    public static boolean isLinkMalformed(String link) {
        if (link.contains("redlink=1")) return true;
        if (link.contains("#")) return true;
        if (link.contains("en.wikipedia.orghttps")) return true;
        if (link.contains("TemplateStyles")) return true;
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
        try {
            Document doc = Jsoup.connect(link).get();
            return doc.title().split(" - Wikipedia")[0];
        } catch (IOException e) {
            System.err.println("ERROR: Exception parsing name for " + link);
        }
        return null;
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
     * @param key The link to a person
     * @return The person we've found with the link
     */
    public Person getPerson(String key) {
        if (Main.people.containsKey(key)) {
            return Main.people.get(key);
        }
        return null;
    }

    /**
     * Adds a node's child to our graph
     * @param string The name of a child which only sometimes actually leads to a real wikipedia profile
     */
    public void createChild(String string) {
        Person child;
        if (hasBlackText(string)) return;
        if (string.contains("<a href=\"/wiki/")) {
            string = string.split("<a href=\"")[1].split("\"")[0];
            if (string.contains("redlink=1")) return;
            String link = "https://en.wikipedia.org" + string;
            child = getPerson(parseName(link));
            if (child == null) {
                child = new Person(link);
            }
            if (child.name != null && !child.name.equals(this.name)) {
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
        String dataString = data.toString();
        if (dataString.contains("<ul>")) {
            String[] stringData = data.toString().split("<ul>")[1].split("</ul>")[0].split("<li>");
            for (String string : stringData) {
                createChild(string.replace("</li>", "").replace("</span>", "").replace("<span class=\"nowrap\">", "").replace("<td class=\"infobox-data\">", "").trim());
            }

        } else if (dataString.contains("<br>")) {
            String[] stringData = data.toString().split("<br>");
            for (String string : stringData) {
                createChild(string.replace("</span>", "").replace("<span class=\"nowrap\">", "").replace("<td class=\"infobox-data\">", "").trim());
            }
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
     * Determines if there's any text that isn't hyperlinked within a line
     * @param string the line to check
     * @return if there is nonhyperlinked text
     */
    // Ex. Issue with https://en.wikipedia.org/wiki/Maria_of_Swabia

    public boolean hasBlackText(String string) {
        if (!string.startsWith("<a href") || !string.endsWith("</a>")) {
            return true;
        }
        else {
            return false;
        }
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
        if (hasBlackText(member.toString())) return;
        String href = member.attr("href");
        String link = "https://en.wikipedia.org" + href;
        Person parent = getPerson(parseName(link));
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
        return name + " (" + link + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Person p) {
            if (p.name.equals(this.name)) {
                return true;
            }
        }
        return false;
    }
}