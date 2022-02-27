package store;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Profile implements Serializable {
    private final int id;
    private final String name;
    private final Set<String> tags;

    Profile(int id, String name) {
        this.id = id;
        this.name = name;
        this.tags = new HashSet<>();
    }

    int getID() { return id; }

    String getName() {
        return name;
    }

    void addTag(String tag) {
        this.tags.add(tag);
    }

    void addTags(String[] tags) {
        for (String tag : tags)
            addTag(tag);
    }

    void removeTag(String tag) {
        this.tags.remove(tag);
    }

    void removeTags(String[] tags) {
        for (String tag : tags)
            removeTag(tag);
    }

    Set<String> getTags() {
        return new HashSet<>(this.tags);
    }

    void clearTags() {
        this.tags.clear();
    }
}
