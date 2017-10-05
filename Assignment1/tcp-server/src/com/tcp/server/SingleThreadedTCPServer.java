package com.tcp.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class SingleThreadedTCPServer {

    public static void main(String args[]) {
        try {
            String clientbuffer = "";
            ServerSocket socket = new ServerSocket(80);

            System.out.println("Server listening to: " + socket.getInetAddress() + ":" + socket.getLocalPort());

            while (true) {
                Socket client = socket.accept();

                System.out.println("Client connected with: " + client.getInetAddress());

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream())
                );
                DataOutputStream output = new DataOutputStream(client.getOutputStream());

                clientbuffer = reader.readLine() + System.lineSeparator();

                System.out.println("[" + new Date() + "] Received: " + clientbuffer);

                output.writeBytes(clientbuffer.toUpperCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
