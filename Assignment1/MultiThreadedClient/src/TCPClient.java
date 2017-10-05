import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClient {

    public static int ThreadsFinished = 0;

    private static class TCPWorker implements Runnable{


        String host;
        int counter;
        int port;

        public TCPWorker(String host,int counter, int port){
            this.host = host; this.counter = counter; this.port = port;
        }

        @Override
        public void run(){
            try {

                System.out.println("started thread " + this.counter);
                String message="", response;
                Socket socket = new Socket(this.host, port);
                System.out.println("Checkpoint 2");


                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                BufferedReader server = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                System.out.println("Checkpoint 3");
//                String request;
//                BufferedReader reader = new BufferedReader(
//                        new InputStreamReader(System.in)
//                );



                //message = reader.readLine() + System.lineSeparator();
                message= message.concat("Hello + " + socket.getInetAddress() + " " + socket.getLocalPort() + " " + counter + "\n");

                int i=0;
                while(i<10){

                    output.writeBytes(message);

                    System.out.println(System.currentTimeMillis());

//                    System.out.println(System.currentTimeMillis());
//                    response = server.readLine();


                    System.out.println("Client: "+i);
                    response = server.readLine();
                    System.out.println("[" + new Date() + "] Received: " + response + " from request: " + i);

//                    System.out.println("[" + new Date() + "] Received: " + response + " from request: " + i);
                    i++;

                }
                output.writeBytes("CLOSE\n");
                response = server.readLine();
                System.out.println(response);


                ThreadsFinished++;
                System.out.println("Thread number: " + Thread.currentThread().getId() + " finished");
                socket.close();
                if(ThreadsFinished==10) System.exit(1);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(10);


    public static void main(String args[]) {

        try{
            int counter=1;
            while (counter<=10) {


                TCP_WORKER_SERVICE.submit(
                        new TCPWorker("34.208.155.204",counter,80)
                );
                counter++;
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
