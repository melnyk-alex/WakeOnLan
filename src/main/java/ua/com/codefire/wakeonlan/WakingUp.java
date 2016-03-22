/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.wakeonlan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author human
 */
public class WakingUp extends Thread {

    private static final Logger LOG = Logger.getLogger(WakingUp.class.getName());
    private Map<String, int[]> packets;

    private WakingUp() {
        this.packets = new HashMap<>();
    }

    public WakingUp(Map<String, int[]> packets) {
        this.packets = packets;
    }

    public WakingUp(int[] mac) {
        this();
        packets.put("COMPUTER", mac);
    }

    public WakingUp(String mac) {
        this();

        packets.put("COMPUTER", parseMAC(mac));
    }

    @Override
    public void run() {
        for (String comp : packets.keySet()) {
            send(createMagicPacket(packets.get(comp)));
        }
    }

    private byte[] createMagicPacket(int[] mac) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 0xFF (x6)
        for (int i = 0; i < 6; i++) {
            baos.write(0xFF);
        }

        // MAC (x16)
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < mac.length; j++) {
                baos.write(mac[j]);
            }
        }

        return baos.toByteArray();
    }

    private void send(byte[] magicPacket) {
        try {
            try (MulticastSocket multicastSocket = new MulticastSocket()) {
                DatagramPacket dp = new DatagramPacket(magicPacket, magicPacket.length,
                        InetAddress.getByName("255.255.255.255"), 9);
                multicastSocket.send(dp);
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public static int[] parseMAC(String mac) {
        String[] splitMAC = mac.split(":");
        int[] MAC = new int[6];

        for (int i = 0; i < splitMAC.length; i++) {
            MAC[i] = Integer.valueOf(splitMAC[i], 16);
        }

        return MAC;
    }
}
