package store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TrieNode implements Serializable{
    Map<Character, TrieNode> children;
    char character;
    boolean isLeaf;
    int profileID;

    TrieNode(char ch) {
        children = new HashMap<>();
        character = ch;
        isLeaf = false;
        profileID = -1;
    }
}

class Trie implements Serializable {
    TrieNode root;

    Trie() {
        root = new TrieNode('*');
    }

    void add(String value, int profileID) {
        if (value == null || value.length() == 0)
            return;

        TrieNode curr = this.root;

        for (char ch : value.toCharArray()) {
            TrieNode next = curr.children.get(ch);
            if (next == null) {
                next = new TrieNode(ch);
                curr.children.put(ch, next);
            }
            curr = next;
        }
        curr.isLeaf = true;
        curr.profileID = profileID;
    }

    void suggest(TrieNode node, List<Integer> list, StringBuffer buf) {
        if (node.isLeaf)
            list.add(node.profileID);
        if (node.children == null || node.children.isEmpty())
            return;

        for (TrieNode next : node.children.values()) {
            suggest(next, list, buf.append(next.character));
            buf.setLength(buf.length()-1);
        }
    }

    List<Integer> search(String prefix) {
        List<Integer> list = new ArrayList<>();
        TrieNode curr = this.root;
        StringBuffer buf = new StringBuffer();

        for (char ch : prefix.toCharArray()) {
            curr = curr.children.get(ch);
            if (curr == null)
                return list;
            buf.append(ch);
        }

        suggest(curr, list, buf);
        return list;
    }
}
