package store;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProfileTable implements Serializable {
    private final Map<Integer, Profile> profileMap;
    static int nextID;

    ProfileTable() {
        profileMap = new HashMap<>();
    }

    Profile add(String name) {
        int id = ProfileTable.nextID++;
        Profile profile = new Profile(id, name);
        this.profileMap.put(id, profile);

        return profile;
    }

    Profile get(int id) {
        return profileMap.get(id);
    }

    void remove(int id) { profileMap.remove(id); }

    Collection<Profile> allProfiles() {
        return profileMap.values();
    }
}
