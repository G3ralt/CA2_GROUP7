package chatClient;

import java.io.*;
import java.net.Socket;
import java.util.*;
import chatServer.ChatServer;

public class ChatClient implements Runnable {

    private String clientName;
    private final Socket socket;
    private final Scanner scan;
    private final PrintWriter pw;
    private final HashMap clients;
    private final ChatServer server;

    public ChatClient(Socket socket, HashMap clients, ChatServer server) throws IOException {
        this.socket = socket;
        this.clients = clients;
        this.scan = new Scanner(socket.getInputStream());
        this.pw = new PrintWriter(socket.getOutputStream(), true);
        this.server = server;
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

    public HashMap getClients() {
        return clients;
    }

    public ChatServer getServer() {
        return server;
    }
//</editor-fold>

    @Override
    public void run() {
        logIn(); //First action from the user should be logging in
        listenForInput(); //Waiting for input from the user
    }

    private void logIn() {
        String input = scan.nextLine(); //Take the input from the client
        while (input != null) {
            if (input.startsWith("LOGIN:")) { //If the input starts with LOGIN: *protocol*
                boolean loggedIn = server.logInProtocol(input, this); // Check the rest of the input
                if (loggedIn) {
                    break;
                }
            } else { //If he input doesn`t follow protocol
                pw.println("Invalid input. LOGIN:<name> expected");
            }
            input = scan.nextLine(); //Try with new input
        }
    }

    private void listenForInput() {
        String input = scan.nextLine(); //Take the input from the client
        while (!input.startsWith("LOGOUT:")) {
            interpretInput(input);
            input = scan.nextLine();
        }
        logOut();
        server.printAllClients();
    }

    private void interpretInput(String input) {
        String[] array = input.split(":");
        if(input.startsWith("MSG:") && array.length == 3) {  //Check if the input follows the protocol
            server.sendMessage(array[1], array[2], this.clientName);
        } else {
            pw.println("Invalid command. MSG:<receivers>:<message> expected");
        }
    }
    
    private void logOut() {
        try {
            System.out.println("Logging out " + this.clientName);
            pw.println("Goodbye! Disconnected from Chat");
            clients.remove(this.clientName);
            pw.close();
            scan.close();
            socket.close();
            System.out.println("Logged out!");
        } catch (IOException ex) {
            System.out.println("Coulnd`t close socket!");
        }
    }
}
