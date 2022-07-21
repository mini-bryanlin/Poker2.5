
package Poker;
import java.io.*;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(222);

        while(true){
            try{
                socket = serverSocket.accept();
                inputStreamReader = new InputStreamReader(socket.getInputStream());
                outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                bufferedWriter = new BufferedWriter(outputStreamWriter);
                while (true){
                    String fromClient = bufferedReader.readLine();
                    System.out.println("Client: " + fromClient);
                    bufferedWriter.write("Seen");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    if (fromClient.equalsIgnoreCase("end")){
                        break;
                    }


                }
                socket.close();
                inputStreamReader.close();
                outputStreamWriter.close();
                bufferedReader.close();
                bufferedWriter.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

    }

}


