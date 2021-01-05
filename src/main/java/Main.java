import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Main {
    public static void main(String[] args) {
        if (args.length != 1){
            System.out.println("Please specify a file path");
            System.out.println("java -jar WSFTPPasswordRecovery.jar C:\\path\\to\\file.ini");
            return;
        }
        HashMap<String, HashMap<String, String>> fileContent = parseFile(args[0]);
        printAllInfo(fileContent);
    }

    public static void printAllInfo(HashMap<String, HashMap<String, String>> file){

        for (String provider : file.keySet()) {
            System.out.println(provider);
            System.out.println("-Server: "+file.get(provider).get("host"));
            System.out.println("-Username: "+file.get(provider).get("uid"));
            try {
                if (file.get(provider).get("pwd") != null){
                    System.out.println("-Password: "+decrypt(file.get(provider).get("pwd")).split("\u0000")[0]);
                }
            } catch (Exception e) {
                System.out.println("ERROR, COULDN'T DECRYPT PASSWORD: "+file.get(provider).get("pwd"));
                e.printStackTrace();
            }
            System.out.println("----------------------");
        }

    }

    public static HashMap<String, HashMap<String, String>> parseFile(String filePath){
        BufferedReader reader;
        HashMap<String, HashMap<String, String>> file = new HashMap<>();
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            String current = "";
            while (line != null) {

                if (line.startsWith("[")) {
                    current = line.replace("[", "").replace("]", "");
                    file.put(current, new HashMap<>());
                    line = reader.readLine();
                    continue;
                }
                if (current.isEmpty())
                    throw new IOException("File must start with [");
                HashMap<String, String> stringStringHashMap = file.get(current);
                String[] split = line.replace("\"", "").split("=", 2);
                if (split.length == 2) {
                    if (split[0].toLowerCase().equals("pwd")){
                        split[1] = split[1].replace("_", ""); //base64 cant contain _
                    }
                    stringStringHashMap.put(split[0].toLowerCase(), split[1]);
                    file.put(current, stringStringHashMap);
                } else {
                    System.out.println("");
                }

                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String decrypt(String base64Password) throws Exception {

        byte[] keybytes = hexStringToByteArray("E1F0C3D2A5B4879669784B5A2D3C0F1E34127856ab90efcd");
        byte[] ivbytes = hexStringToByteArray("34127856ab90efcd");
        byte[] msg = Base64.getDecoder().decode(base64Password);

        SecretKey secretKey = new SecretKeySpec(keybytes, "DESede");

        Cipher decipher = Cipher.getInstance("DESede/CBC/NoPadding");
        IvParameterSpec iv = new IvParameterSpec(ivbytes);
        decipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] plainText = decipher.doFinal(msg);

        return new String(plainText, "UTF-8");
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
