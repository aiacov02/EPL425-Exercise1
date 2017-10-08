package com.tcp.server;

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
    public static long start_time;
    public static double throughput;
    public static int c;
    public static int Repetitions;
    public static int MaxRepetitions;

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
            Repetitions++;
			DataOutputStream output = new DataOutputStream(client.getOutputStream());
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(this.client.getInputStream())
                        );

			
            this.clientbuffer = reader.readLine();
            if(c==0){
                start_time=System.currentTimeMillis();
            }

			if(this.clientbuffer.equals("CLOSE")){
				output.writeBytes("Connection Terminated\n");
				client.close();
                Repetitions--;
				break;
			}

            if(Repetitions>MaxRepetitions){
                output.writeBytes("STOP\n");

                break;
            }
                System.out.println("[" + new Date() + "] Received: " + this.clientbuffer);

                String[] tokens = this.clientbuffer.split(" ");

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
                throughput=(double)(c*1000)/((System.currentTimeMillis()-start_time));
                System.out.println("throughput of server: "+ throughput + " Requests per Second");

                c++;
		}
                
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
}

    public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(10);

    public static void main(String args[]) {
        MaxRepetitions = Integer.parseInt(args[1]);
        int port = Integer.parseInt(args[0]);
        try {
            ServerSocket socket = new ServerSocket(port);

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

