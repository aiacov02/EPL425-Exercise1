package com.tcp.server;

import com.sun.deploy.util.StringUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedTCPServer {

    private static class TCPWorker implements Runnable {

        private Socket client;
        private String clientbuffer;

        public TCPWorker(Socket client) {
            this.client = client;
            this.clientbuffer = "";
        }

        @Override
        public void run() {

            try {
                System.out.println("Client connected with: " + this.client.getInetAddress());
		
		
		

		while(true){
			DataOutputStream output = new DataOutputStream(client.getOutputStream());
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(this.client.getInputStream())
                        );

			
                	this.clientbuffer = reader.readLine();
			if(this.clientbuffer.equals("CLOSE")){
				output.writeBytes("Connection Terminated\n");
				client.close();
				break;
			}
                System.out.println("[" + new Date() + "] Received: " + this.clientbuffer);

                String[] tokens = this.clientbuffer.split(" ");
                for (String token : tokens)
                {
                    System.out.println(token);
                }
                Random rand = new Random(System.currentTimeMillis());
                int irand = rand.nextInt(1701);
                int finalRand = 300 + irand;
                //String payload="";
                int sizefoo = finalRand*1024;



                char[] payload = new char[sizefoo];
                Arrays.fill(payload,'a');
                String p = new String(payload);

                //System.out.println("this random is " + finalRand + " size: " + p);
                output.writeBytes("Welcome " + tokens[3] + p + System.lineSeparator());
		}
                
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
}

    public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(3000);

    public static void main(String args[]) {
        try {
            ServerSocket socket = new ServerSocket(80);

            System.out.println("Server listening to: " + socket.getInetAddress() + ":" + socket.getLocalPort());

            while (true) {
                Socket client = socket.accept();

                TCP_WORKER_SERVICE.submit(
                        new TCPWorker(client)
                );

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

