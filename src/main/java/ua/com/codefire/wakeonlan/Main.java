/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.wakeonlan;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author human
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        Properties basic = new Properties();
        String propFile = loadArguments(args, basic);

        if (propFile != null) {
            basic = loadProperties(basic, propFile);
        }

        if (!basic.isEmpty()) {
            Map<String, int[]> macs = convertToMap(basic);

            for (Entry entry : basic.entrySet()) {
                System.out.printf("%s: [%s]\n", entry.getKey(), entry.getValue());
            }

            System.out.print("Run this computers? (y/n): ");
            if (Character.toLowerCase((char) System.in.read()) == 'y') {
                new WakingUp(macs).start();
            }
        }
    }

    private static String loadArguments(String[] args, Properties prop) {
        String propFile = null;

        for (String arg : args) {
            if (arg.startsWith("-p")) {
                propFile = arg.substring(2, arg.length());
            } else if (arg.startsWith("-m")) {
                prop.put("COMPUTER", arg.substring(2, arg.length()));
            }
        }

        return propFile;
    }

    private static Properties loadProperties(Properties props, String propFile) {
        Properties prop = new Properties(props);
        try {
            prop.load(new FileInputStream(propFile));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return prop;
    }

    private static Map<String, int[]> convertToMap(Properties basic) throws NumberFormatException {
        Map<String, int[]> macs = new HashMap<>();

        for (Entry entry : basic.entrySet()) {
            macs.put((String) entry.getKey(), WakingUp.parseMAC((String) entry.getValue()));
        }

        return macs;
    }
}
