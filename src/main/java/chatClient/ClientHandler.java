package chatClient;

import chatServer.Server;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private String clientName;
    private final Socket socket;
    private final Scanner scan;
    private final PrintWriter pw;
    private final Server server;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.scan = new Scanner(socket.getInputStream());
        this.pw = new PrintWriter(socket.getOutputStream(), true);
        this.server = server;
        this.clientName = "unknown";
        System.out.println("ChatClient connected!");
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and setters">
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    public Socket getSocket() {
        return socket;
    }

    public Scanner getScan() {
        return scan;
    }

    public PrintWriter getPw() {
        return pw;
    }

    public Server getServer() {
        return server;
    }
//</editor-fold>

    @Override
    public void run() {
        logIn(); //First action from the user should be logging in
        if (!clientName.equals("unknown")) {
            listenForInput(); //Waiting for input from the user
        }
    }

    private void logIn() {
        String input = scan.nextLine(); //Take the input from the client
        if (input.startsWith("LOGIN:")) { //If the input starts with LOGIN:
            server.addClientToChat(this, input);
        } else { //If he input doesn`t follow protocol
            server.disconnectClient(this);
        }
    }

    private void listenForInput() {
        String input = scan.nextLine(); //Take the input from the client
        while (!input.startsWith("LOGOUT:")) {
            server.interpretInput(input, this);
            try {
                input = scan.nextLine();
            } catch (Exception e) {
                break;
            }
        }
        if (input.startsWith("LOGOUT:")) {
            server.disconnectClient(this);
        }
        server.printAllClients();
    }

}
