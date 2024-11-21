package org.portico.lrc;

import java.util.*;

public class Profile {
    private String name;
    private Map<String, Set<String>> accessRights = new HashMap<>();

    public Profile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addAccess(String topic, String[] operations) {
        accessRights.put(topic, new HashSet<>(Arrays.asList(operations)));
    }

    public boolean hasAccess(String topic, String operation) {
        return accessRights.containsKey(topic) && accessRights.get(topic).contains(operation);
    }
}
