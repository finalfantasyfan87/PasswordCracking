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
        Path dictionary = Paths.get("PasswordCracking/dic-0294.txt");

        Path salt = Paths.get("PasswordCracking/salt.hex");
        FileWriter fileWriter = new FileWriter(new File("results.txt"));
        String givenSalt = Files.lines(salt).findFirst().get();

for(int x =0; x<=6; x++){
            for (int i = 1; i <= 3; i++) {
                Path passwordToCheck = Paths.get("PasswordCracking/pw" + i + ".hex");
                String pwToValidate = Files.lines(passwordToCheck).findFirst().get();
                testHash(dictionary, fileWriter, pwToValidate);

            }
            for (int i = 1; i <= 3; i++) {
                Path passwordToCheck = Paths.get("PasswordCracking/spw" + i + ".hex");
                String pwToValidate = Files.lines(passwordToCheck).findFirst().get();
                testSaltHash(dictionary, fileWriter, givenSalt, pwToValidate);
            }
        }


    }

    private static void testSaltHash(Path dictionary, FileWriter fw, String salt1, String hashToCompare) throws IOException {
        Map<String, String> map = new HashMap<>();
        for (String text : Files.readAllLines(dictionary)) {
            map.put(getSecurePassword(salt1, text), text);

        }
        if (map.containsKey(hashToCompare)) {
            System.out.println("FOUND :: " + map.get(hashToCompare));
        }
        writeMapToFile(map, fw);
        fw.flush();
        fw.close();
    }

    private static void testHash(Path dictionary, FileWriter fw, String hashToCompare) throws IOException {
        Map<String, String> map = new HashMap<>();
        for (String text : Files.readAllLines(dictionary)) {
            map.put(getSecurePassword(null, text), text);

        }
        if (map.containsKey(hashToCompare)) {
            System.out.println("FOUND :: " + map.get(hashToCompare));
        }
        writeMapToFile(map, fw);

    }

    private static void writeMapToFile(Map<String, String> map, FileWriter fw) throws IOException {
        fw.write(map + "\n");
        fw.flush();
        fw.close();

    }

    //borrowed from online resources
    public static byte[] hexStringToByteArray(String text) {
        byte[] textBytes = new byte[text.length() / 2];
        IntStream.range(0, textBytes.length).forEach(i -> textBytes[i] = (byte) Integer.parseInt(text.substring(2 * i, 2 * i + 2), 16));
        return textBytes;

    }

    private static String getSecurePassword(String salt, String passwordToHash) {
        String password = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
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