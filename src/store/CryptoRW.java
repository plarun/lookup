package store;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CryptoRW {
    private static final String filename = "data.txt";
    private static final String algo = "AES";
    private static final String trans = "AES/CBC/PKCS5Padding";
    private static final byte[] key = "qp1z8&nM*Ax4Oo0!".getBytes();

    boolean fileExists() {
        File file = new File(filename);
        return file.exists() && !file.isDirectory();
    }

    void cryptoWrite(ProfileTable profileMap, TagTable tagMap, Trie trie, int id) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, IOException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, algo);
        Cipher cipher = Cipher.getInstance(trans);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));

        SealedObject sealedProfileMap = new SealedObject(profileMap, cipher);
        SealedObject sealedTagMap = new SealedObject(tagMap, cipher);
        SealedObject sealedTrie = new SealedObject(trie, cipher);
        SealedObject sealedID = new SealedObject(id, cipher);

        FileOutputStream fileOut = new FileOutputStream(filename);
        CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
        ObjectOutputStream objOut = new ObjectOutputStream(cipherOut);

        objOut.writeObject(sealedProfileMap);
        objOut.writeObject(sealedTagMap);
        objOut.writeObject(sealedTrie);
        objOut.writeObject(sealedID);

        objOut.flush();
        objOut.close();
    }

    void cryptoRead(UIController uiController) throws IOException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, algo);
        Cipher cipher = Cipher.getInstance(trans);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));

        FileInputStream fileIn = new FileInputStream(filename);
        CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
        ObjectInputStream objIn = new ObjectInputStream(cipherIn);

        SealedObject sealedProfileMap = (SealedObject) objIn.readObject();
        SealedObject sealedTagMap = (SealedObject) objIn.readObject();
        SealedObject sealedTrie = (SealedObject) objIn.readObject();
        SealedObject sealedID = (SealedObject) objIn.readObject();

        uiController.setProfileMap((ProfileTable) sealedProfileMap.getObject(cipher));
        uiController.setTagMap((TagTable) sealedTagMap.getObject(cipher));
        uiController.setTrie((Trie) sealedTrie.getObject(cipher));
        ProfileTable.nextID = (Integer) sealedID.getObject(cipher);

        objIn.close();
    }
}
