package com.example.koresuniku.kisosvet;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient {
    static byte[] send_data = new byte[1024];
    static byte[] receiveData = new byte[1024];
    static String modifiedSentence;


    public static void client(String str) {
        try {
            DatagramSocket client_socket = new DatagramSocket(5000);
            InetAddress IPAddress = InetAddress.getByName("192.168.1.105");

            //while (true)
            // {
            send_data = str.getBytes("ASCII");
            //System.out.println("Type Something (q or Q to quit): ");

            DatagramPacket send_packet = new DatagramPacket(send_data, str.length(), IPAddress, 5000);
            client_socket.send(send_packet);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            client_socket.receive(receivePacket);
            modifiedSentence = new String(receivePacket.getData());

            modifiedSentence = null;
            client_socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // }

    }
}

