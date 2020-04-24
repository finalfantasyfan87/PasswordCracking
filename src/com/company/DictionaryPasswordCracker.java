package com.company;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DictionaryPasswordCracker {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        Path path = Paths.get("PasswordCracking/dic-0294.txt");
        Path pw = Paths.get("PasswordCracking/pw2.hex");
        FileWriter fw = new FileWriter(new File("results.txt"));
        Optional<String> password1 = Files.lines(pw).findFirst();

        Map<String, String> map = new HashMap<>();
        for (String s : Files.readAllLines(path)) {
            map.put(s, getSecurePassword(s));
            if(map.containsValue(password1)){
                System.out.println(map.get(password1));
            }
        }
        fw.write(String.valueOf(map));

        fw.flush();
        fw.close();

    }

    private static String getSecurePassword(String passwordToHash) {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("SHA");
            //Get the hash's bytes
            byte[] bytes = md.digest(passwordToHash.getBytes());
            String sb = IntStream.range(0, bytes.length).mapToObj(i -> Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1)).collect(Collectors.joining());
            generatedPassword = sb;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

}