package com.example.koresuniku.kisosvet;


import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UDPClient {
    private static final String LOG_TAG = UDPClient.class.getSimpleName();
    private static long beginTime;
    private static final int TIMEOUT = 500;

    static byte[] send_data = new byte[256];
    static byte[] receiveData = new byte[256];
    static String modifiedSentence;

  //  static {
  public static int corePoolSize = 60;
    public static  int maximumPoolSize = 80;
    public static int keepAliveTime = 10;
    public static  BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
        public static Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

  //  }

    public static boolean isProcessing = false;

    public UDPClient() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static ArrayList<String> clienNeedToReceiveData(String str) {
        try {
            DatagramSocket client_socket = new DatagramSocket(5000);
            InetAddress IPAddress = InetAddress.getByName("192.168.1.105");

            Log.i(LOG_TAG, "using first method");
            send_data = str.getBytes("ASCII");
            DatagramPacket send_packet = new DatagramPacket(send_data, str.length(), IPAddress, 5000);
            client_socket.setSoTimeout(500);
            client_socket.send(send_packet);
            ArrayList<String> unformattedStrings = new ArrayList<>();
            beginTime = System.currentTimeMillis();
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    client_socket.receive(receivePacket);
                } catch (SocketTimeoutException e) {
                    byte[] bytes = receivePacket.getData();
                    String newSentence = new String(bytes, "UTF-8") + "\n";
                    unformattedStrings.add(newSentence);
                    //modifiedSentence += newSentence;
                    //Log.i(LOG_TAG, String.valueOf(System.currentTimeMillis() <= beginTime + TIMEOUT));
                    break;
                }
                byte[] bytes = receivePacket.getData();
                String newSentence = new String(bytes, "UTF-8") + "\n";
                unformattedStrings.add(newSentence);
                //modifiedSentence += newSentence;
                //Log.i(LOG_TAG, String.valueOf(System.currentTimeMillis() <= beginTime + TIMEOUT));
            }
            //Log.i(LOG_TAG, "finish");
            ArrayList<String> formattedStrings = formatReceivedData(unformattedStrings);

            //Log.i(LOG_TAG, String.valueOf(formattedStrings));


            client_socket.close();
            return unformattedStrings;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void client(final String str) {

        if (isProcessing) return;

        isProcessing = true;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                DatagramSocket client_socket = null;
                try {
                    client_socket = new DatagramSocket(5000);
                    InetAddress IPAddress = InetAddress.getByName("192.168.1.105");

                    Log.i(LOG_TAG, "using second method: " + str);

                    send_data = str.getBytes("ASCII");
                    DatagramPacket send_packet = new DatagramPacket(send_data, str.length(), IPAddress, 5000);
                    client_socket.send(send_packet);
//                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//                    client_socket.receive(receivePacket);
//                    modifiedSentence = new String(receivePacket.getData());
//
//                    modifiedSentence = null;
                   // client_socket.close();
                 //   isProcessing = false;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (client_socket != null) {
                        client_socket.close();
                    }
                    isProcessing = false;
                }
                return null;
            }
        }.executeOnExecutor(threadPoolExecutor);
    }



    private static ArrayList<String> formatReceivedData(ArrayList<String> byteStrings) {
        ArrayList<String> result = new ArrayList<>();
        for (CharSequence byteString : byteStrings) {
            for (int i = 0; i < byteString.length(); i++) {
                Character c = byteString.charAt(i);
                if (Character.isLetterOrDigit(c)
                        || Character.isWhitespace(c)
                        || c.equals("\\")
                        || c.equals(":")) {
                    continue;
                } else {
                    String resultItem = (String) byteString.subSequence(0, i);
                    result.add(resultItem);
                    break;
                }
            }
        }

        return result;
    }
}

