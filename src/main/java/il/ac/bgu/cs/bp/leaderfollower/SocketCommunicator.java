package il.ac.bgu.cs.bp.leaderfollower;

// Author: Michal Pasternak
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketCommunicator {

    private BufferedReader in;
    private PrintWriter out = null;
    private Socket socket = null;

    public SocketCommunicator() {
    }

    public void connectToServer(String IP, int port) throws IOException {

        // Make connection and initialize streams
            socket = new Socket(IP, port);
            in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("connected");
    }

    // Send command for sending a message through the socket and returning the reply
    public String send(String message) {
        String reply = null;
        if (out == null){
          System.err.println("fail - socket error");
        }
        out.println(message);
        try {
            reply = getMessage();
        }
        catch(java.io.IOException e){ e.printStackTrace();}
        return reply;
    }
    // expecting no reply.
    public void noReply(String message){
      out.println(message);
    }

    public String getMessage() throws IOException {
        return in.readLine();
    }

    // Closes the connection on the socket
    public void close(){
        try {
            socket.close();
        }
        catch(java.io.IOException e){ e.printStackTrace();}
    }
}
