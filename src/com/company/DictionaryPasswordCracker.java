package com.company;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class DictionaryPasswordCracker {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {

        //please change the password filename here...don't include directory.
        String pwdFilename = "spw1.hex";


        //change algorithm because I discovered that the passwords were hashed with different
        //algorithms.
        String hashingAlgorithm = "SHA-256";

        Path dictionary = Paths.get("PasswordCracking/dic-0294.txt");
        Path pw = Paths.get("PasswordCracking/" + pwdFilename);
        Path salt = Paths.get("PasswordCracking/salt.hex");
        FileWriter fileWriter = new FileWriter(new File("results.txt"));
        String lookupHash = Files.lines(pw).findFirst().get();
        String salt1 = Files.lines(salt).findFirst().get();
        if (pwdFilename.startsWith("s")) {
            System.out.println("Testing with salted hash ");
            testSaltedHash(dictionary, fileWriter, lookupHash, salt1,hashingAlgorithm);
        } else{
            testHash(dictionary, fileWriter, lookupHash,hashingAlgorithm);
        }



    }

    private static void testHash(Path dictionary, FileWriter fileWriter, String lookupHash, String hashingAlgorithm) throws IOException {
        Map<String, String> map = new HashMap<>();
        for (String text : Files.readAllLines(dictionary)) {
            map.put(getSecurePassword(null, text,hashingAlgorithm), text);
        }
        if (map.containsKey(lookupHash)) {
            System.out.println("THE UNHASHED IS => " + map.get(lookupHash));
        } else{
            System.out.println("That hash cannot be found...maybe switch your algorithm!!");
        }

        fileWriter.write(map + "\n");
        fileWriter.flush();
        fileWriter.close();
    }

    private static void testSaltedHash(Path dictionary, FileWriter fileWriter, String lookupHash, String salt1, String hashingAlgorithm) throws IOException {
        Map<String, String> map = new HashMap<>();
        for (String text : Files.readAllLines(dictionary)) {
            map.put(getSecurePassword(salt1, text,hashingAlgorithm), text);
        }
        if (map.containsKey(lookupHash)) {
            System.out.println("THE UNHASHED IS => " + map.get(lookupHash));
        }

        fileWriter.write(map + "\n");
        fileWriter.flush();
        fileWriter.close();
    }

    //borrowed from stackoverflow
    public static byte[] hexStringToByteArray(String text) {
        byte[] textBytes = new byte[text.length() / 2];
        IntStream.range(0, textBytes.length).forEach(i -> textBytes[i] = (byte) Integer.parseInt(text.substring(2 * i, 2 * i + 2), 16));
        return textBytes;

    }

    private static String getSecurePassword(String salt, String passwordToHash, String algorithm) {
        String password = null;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            if (salt != null) {
                md.update(hexStringToByteArray(salt));
            }

            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            password = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return password;
    }

}