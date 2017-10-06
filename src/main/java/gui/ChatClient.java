package gui;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class ChatClient implements Runnable {
    
    private String name;
    private final Socket socket;
    private final PrintWriter output;
    
    public ChatClient(String ip, int port, String name) throws IOException {
        this.socket = new Socket(ip, port);
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        logIn();
    }
    
    private void logIn() {
        output.println("LOGIN:" + name);
    }

    public void sendMessage(String message) {
        output.println("MSG:" + message);
    }
    
    public void logOut(){
        output.println("LOGOUT:");
    }
}
