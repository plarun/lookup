package store;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException {
        UIController ui = new UIController();
        ui.initData();

        boolean isRunning = true;

        try {
            while (isRunning) {
                System.out.println("[1] Create profile");
                System.out.println("[2] Update Profile");
                System.out.println("[3] Search Profile");
                System.out.println("[4] Filter by tags");
                System.out.println("[5] List Profiles");
                System.out.println("[6] Delete Profile");
                System.out.println("[?] Exit");

                int option = ui.readInt("Option: ");

                if (option == 1) {
                    isRunning = ui.createProfile();
                } else if (option == 2) {
                    isRunning = ui.updateProfile();
                } else if (option == 3) {
                    isRunning = ui.searchProfile();
                } else if (option == 4) {
                    isRunning = ui.filterByTag();
                } else if (option == 5) {
                    isRunning = ui.viewProfiles();
                } else if (option == 6) {
                    isRunning = ui.deleteProfile();
                } else {
                    isRunning = false;
                }
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            ui.saveData();
        }
    }
}
