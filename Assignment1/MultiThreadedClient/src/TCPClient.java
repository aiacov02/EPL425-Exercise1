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
    public static int sumlatency=0;
    public static float avglatency=0;
    public static int AllRequests=0;
    public static final int REQUESTSPERCLIENT=300;
    public static final int CLIENTS=10;

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

                //System.out.println("started thread " + this.counter);
                String message="", response="";
                Socket socket = new Socket(this.host, port);
                //System.out.println("Checkpoint 2");


                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                BufferedReader server = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                //System.out.println("Checkpoint 3");
//                String request;
//                BufferedReader reader = new BufferedReader(
//                        new InputStreamReader(System.in)
//                );



                //message = reader.readLine() + System.lineSeparator();
                message= message.concat("Hello " + "Client IP: "+socket.getInetAddress() + " Client Random Local Port: " + socket.getLocalPort() + " Client ID: " + counter + "\n");
                boolean stopsending = false;
                int i=0;
                while(i<REQUESTSPERCLIENT){
                    int req;
                    output.writeBytes(message);
                    req= (int) System.currentTimeMillis();

                    response = server.readLine();

                    int resp= (int) System.currentTimeMillis();
                    System.out.println("[" + new Date() + "] Client: " + counter + " received response for request: " + (i+1));
                    if(response.equals("STOP")){
                        stopsending=true;
                        break;
                    }
                    AllRequests++;

                    sumlatency+=(resp-req);


                    i++;

                }
                if(!stopsending){
                    output.writeBytes("CLOSE\n");
                    response = server.readLine();
                }


                ThreadsFinished++;
                socket.close();
                System.out.println("Client " + counter + " finished with " + i + " requests served and message: " + response);
                if(ThreadsFinished==CLIENTS){avglatency= (float) ((float)sumlatency/AllRequests); System.out.println("Latency: " + avglatency + " milliseconds");System.exit(1);}

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(CLIENTS);


    public static void main(String args[]) {
        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        try{
            int counter=1;
            while (counter<=CLIENTS) {


                TCP_WORKER_SERVICE.submit(
                        new TCPWorker(ip,counter,port)
                );
                counter++;
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
