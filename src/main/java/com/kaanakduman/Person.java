package com.kaanakduman;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;

public class Person {
    HashSet<Person> children = new HashSet<>();
    String name;
    String link;
    boolean isReal = false;

    public boolean isLinkMalformed(String link) {
        if (link.contains(";redlink=1")) return true;
        if (link.contains("en.wikipedia.orghttps")) return true;
        if (link.contains("Elchingen_Abbey")) return true;
        return false;
    }

    public String fixedName(String link) {
        if (isLinkMalformed(link)) return null;
        String[] arr = link.split("wiki/");
        String name = arr[arr.length - 1].replace("_", " ").trim();
        if (name.length() <= 1) return null;
        if (name.startsWith("https://")) return null;
        if (name.startsWith("(")) return null;
        return name;
    }

    public Person(String link) {
        if (Main.people.size() >= Main.maxSize) return;
        this.link = link;
        name = fixedName(link);
        if (name == null) return;
        isReal = true;
        if (Main.people.containsKey(link)) return;
        Main.people.put(link, this);
        String notifyMessage = name + " " + link;
        System.out.println(notifyMessage);
        Document doc;
        try {
            doc = Jsoup.connect(link).get();
            Element table = getTable(doc);
            if (table != null && children.size() < Main.maxSize) {
                createFamily(table);
            }
        } catch (Exception e) {
            System.out.println("Ignoring exception");
            e.printStackTrace();
        }
    }

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

    public Person getPerson(String link) {
        if (Main.people.containsKey(link)) {
            return Main.people.get(link);
        }
        return null;
    }

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
            if (child.name != null)
                this.children.add(child);
        }
    }

    public void createChildren(Element row) {
        Element data = row.getElementsByClass("infobox-data").first();
        if (data == null) return;
        String[] stringData = data.toString().split("<br>");
        for (String string : stringData) {
            createChild(string);
        }
    }

    public Element getHeader(Element row) {
        return row.getElementsByClass("infobox-label").first();
    }

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
        if (parent.name != null)
            parent.children.add(this);

    }

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
            if (p.toString().equals(o.toString())) {
                return true;
            }
        }
        return false;
    }
}