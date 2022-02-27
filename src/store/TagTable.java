package store;

import java.io.Serializable;
import java.util.*;

public class TagTable implements Serializable {
    private final Map<String, List<Integer>> tagMapping;

    TagTable() {
        tagMapping = new HashMap<>();
    }

    void tag(String key, int id) {
        this.tagMapping.computeIfAbsent(key, k -> new LinkedList<>());
        this.tagMapping.get(key).add(id);
    }

    void tagAll(String[] keys, int id) {
        for (String key : keys) {
            this.tag(key, id);
        }
    }

    void unTag(String key, int id) {
        this.tagMapping.computeIfPresent(key, (k, list) -> {
            list.remove(id);
            return list;
        });
    }

    void unTagAll(String[] keys, int id) {
        for (String key : keys) {
            this.unTag(key, id);
        }
    }

    private List<Integer> get(String tag) {
        return this.tagMapping.get(tag);
    }

    List<Integer> get(String[] tagList) {
        Map<Integer, Integer> lookup = new HashMap<>();
        int level = -1;

        for (String tag : tagList) {
            ++level;
            List<Integer> profiles = this.get(tag);
            if (profiles == null) {
                return new ArrayList<>();
            }
            for (int profileID : profiles) {
                if (lookup.get(profileID) == null)
                    lookup.put(profileID, 0);
                else
                    lookup.put(profileID, lookup.get(profileID) + 1);
            }
        }

        List<Integer> filteredProfileIDs = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : lookup.entrySet()) {
            if (entry.getValue() == level)
                filteredProfileIDs.add(entry.getKey());
        }

        return filteredProfileIDs;
    }
}
