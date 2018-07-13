package il.ac.bgu.cs.bp.leaderfollower;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Michal Pasternak
 */
public class SocketCommunicator {

    private BufferedReader in;
    private PrintWriter out = null;
    private Socket socket = null;

    public SocketCommunicator() {
    }

    public void connectToServer(String ip, int port) throws IOException {

        // Make connection and initialize streams
        socket = new Socket(ip, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("connected to " + ip + ":" + port);
    }

    // Send command for sending a message through the socket and returning the reply
    public String send(String message) {
        String reply = null;
        if (out == null) {
            System.out.println("fail - socket error");
        }
        out.println(message);
        try {
            reply = in.readLine();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return reply;
    }

    // expecting no reply.
    public void noReply(String message) {
        out.println(message);
    }

    // Closes the connection on the socket
    public void close() {
        try {
            socket.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }
}
