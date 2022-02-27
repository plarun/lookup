package store;

import javax.crypto.*;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class UIController {
    private TagTable tagMap;
    private ProfileTable profileMap;
    private final Scanner scan;
    private Trie trie;
    private final CryptoRW cryptoRW;

    private final static String profileHeader = "ID        | Name                | Tags";

    UIController() {
        this.tagMap = new TagTable();
        this.profileMap = new ProfileTable();
        this.scan = new Scanner(System.in);
        this.trie = new Trie();
        this.cryptoRW = new CryptoRW();
    }

    void setProfileMap(ProfileTable profileMap) { this.profileMap = profileMap; }

    void setTagMap(TagTable tagMap) { this.tagMap = tagMap; }

    void setTrie(Trie trie) { this.trie = trie; }

    int readInt(String prompt) {
        System.out.print(prompt);
        int id = scan.nextInt();
        scan.nextLine();
        return id;
    }

    String readString(String prompt) {
        System.out.print(prompt);
        return scan.nextLine();
    }

    String[] readCSVStrings(String prompt) {
        System.out.print(prompt);
        String tags = scan.nextLine();
        return tags.split(",");
    }

    boolean createProfile() {
        String name = readString("Name: ");
        if (name.length() == 0) {
            System.out.println("Empty name");
            return true;
        }
        String[] tagList = readCSVStrings("Tags (csv): ");

        Profile profile = profileMap.add(name);
        for (String tag : tagList) {
            profile.addTag(tag);
        }

        tagMap.tagAll(tagList, profile.getID());
        trie.add(name, profile.getID());

        return true;
    }

    boolean updateProfile() {
        int id = readInt("ID: ");

        Profile currProfile = this.profileMap.get(id);
        if (currProfile == null) {
            System.out.println("Profile not found");
            return true;
        }

        boolean isRunning = true;
        while (isRunning) {
            System.out.println(profileHeader);
            printProfile(currProfile);

            System.out.println("[1] Add tags");
            System.out.println("[2] Remove tags");
            System.out.println("[3] Clear tags");
            System.out.println("[?] Back");

            int option = readInt("Option: ");

            if (option == 1) {
                String[] tags = readCSVStrings("Tags to add (csv): ");
                currProfile.addTags(tags);
                tagMap.tagAll(tags, currProfile.getID());
            } else if (option == 2) {
                String[] tags = readCSVStrings("Tags to remove (csv): ");
                currProfile.removeTags(tags);
                tagMap.unTagAll(tags, currProfile.getID());
            } else if (option == 3) {
                currProfile.clearTags();
                tagMap.unTagAll(currProfile.getTags().toArray(new String[0]), currProfile.getID());
            } else {
                isRunning = false;
            }
        }

        return true;
    }

    boolean searchProfile() {
        String name = readString("Search Name: ");
        if (name.length() == 0)
            return false;

        List<Integer> profileIDs = trie.search(name);
        printProfile(profileIDs);

        return true;
    }

    boolean filterByTag() {
        String[] tags = readCSVStrings("Tags (csv): ");
        if (tags.length != 0) {
            printProfile(tagMap.get(tags));
        }

        return true;
    }

    boolean viewProfiles() {
        Collection<Profile> profiles = this.profileMap.allProfiles();
        if (profiles.size() == 0) {
            System.out.println("No Profiles");
        } else {
            System.out.println(profileHeader);
            for (Profile profile : profiles)
                printProfile(profile);
        }

        return true;
    }

    boolean deleteProfile() {
        int id = readInt("Delete ID: ");
        Profile profile = profileMap.get(id);
        if (profile == null) {
            System.out.println("Profile not found");
        } else {
            tagMap.unTagAll(profile.getTags().toArray(new String[0]), id);
            profileMap.remove(id);
        }

        return true;
    }

    private void printProfile(Collection<Integer> profileIDs) {
        if (profileIDs.size() == 0) {
            return;
        }

        System.out.println(profileHeader);
        for (int profileID : profileIDs) {
            Profile profile = profileMap.get(profileID);
            printProfile(profile);
        }
    }

    private void printProfile(Profile profile) {
        System.out.printf("%-10s| ", profile.getID());
        System.out.printf("%-20s| ", profile.getName());

        boolean isFirst = true;
        String delimiter = "";
        for (String tag : profile.getTags()) {
            System.out.print(delimiter + tag);
            if (isFirst) {
                delimiter = ",";
                isFirst = false;
            }
        }
        System.out.println();
    }

    void saveData() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        cryptoRW.cryptoWrite(this.profileMap, this.tagMap, this.trie, ProfileTable.nextID);
        System.out.println("Data stored");
    }

    void initData() throws IOException, ClassNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        if (cryptoRW.fileExists()) {
            cryptoRW.cryptoRead(this);
            System.out.println("Data initiated");
        } else {
            ProfileTable.nextID = 0;
        }
    }
}
