import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class ChatServer {
    private static final int PORT = 9001;

    //to get unique name list
    private static HashSet<String> names = new HashSet<String>();

    //massege broadcast to clients
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    public static void main(String args[]) throws Exception{

        System.out.println("The char server is running");
        ServerSocket listner = new ServerSocket(PORT);

        //untill response from client
        try{
            while(true){
                Socket socket = listner.accept();
                Thread handlerThread = new Thread(new Handler(socket));
                handlerThread.start();
            }
        }finally{
            listner.close();
        }
    }

    //to handle the masseges between single client
    private static class Handler implements Runnable{
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {

            try{
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(),true);

                //iterate untill get unique name
                while (true){
                    out.println("SUBMITNAME");
                    name = in.readLine();

                    if(name == null){
                        return;
                    }
                    if(!names.contains(names)){
                        names.add(name);
                        break;
                    }
                }

                out.println("NAMEACCEPTED!");
                writers.add(out);

                //Accepted client msg and broadcast
                while(true){
                    String input = in.readLine();
                    if (input == null){
                        return;
                    }
                    //broadcast only availbilities
                    for (PrintWriter writer: writers){
                        writer.println("MESSAGE "+ name +": "+input);
                    }
                }

            }catch (IOException e){
                System.out.println(e);
            }finally {
             //all the things clear when client out the app
             if (names != null){
                 names.remove(name);
             }
             if (out != null){
                 writers.remove(out);
             }
             try{
                 socket.close();
                }catch(IOException e){
                 System.out.println(e);
             }
            }
        }
    }
}
