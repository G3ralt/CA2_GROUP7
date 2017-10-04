package chatServer;

import chatClient.ChatClient;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

public class ChatServer {

    private final HashMap<String, ChatClient> clients;
    private final ServerSocket serverSocket;

    public ChatServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.clients = new HashMap();
        System.out.println("ChatServer created!");
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and setters">
    public HashMap<String, ChatClient> getClients() {
        return clients;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
//</editor-fold>
    //Prints the list of all connected clients in each client`s GUI

    public void printAllClients() {
        if (!clients.isEmpty()) { //Check if we have clients to print to
            String names = ""; 
            for (String key : clients.keySet()) { // Iterate all the names and add them to a String
                names += key + ",";
            }

            names = names.substring(0, names.length() - 1);
            for (ChatClient client : clients.values()) { //Print the list of names to all clients
                client.getPw().println("CLIENTLIST:" + names);
            }
        }
    }

    public boolean logInProtocol(String input, ChatClient client) {
        String name = input.split(":")[1]; //Get the input after "LOGIN:"
        if (name.matches("[a-zA-Z0-9]*")) { //If the name is only letters and numbers
            if (!clients.containsKey(name)) { //if we the name is unique for the server
                client.setClientName(name);
                clients.put(name, client);
                printAllClients();
                return true;
            } else { //If the client name is already existing
                client.getPw().println("Name: <" + name + "> is not unique. Please try again with a different name.");
            }
        } else { //If the name has invalid characters
            client.getPw().println("Invalid name. Characters from a-z, A-Z, 0-9 expected.");
        }
        return false;
    }

    public void sendMessage(String receivers, String msg, String senderName) {
        if (receivers.contains("*") && receivers.length() == 1) { //If the input is according to protocol
            for (ChatClient client : clients.values()) { //Send the message to all
                client.getPw().println("MSGRES:" + senderName + ":" + msg);
            }
        } else if (receivers.contains("*") && receivers.length() != 1) { //If the input is not according to protocol
            clients.get(senderName).getPw().println("Invalid input. MSG:*:<message> expected");
        } else if (receivers.contains(",")) { //If we have more than 1 receiver
            String[] names = receivers.split(","); // Collection of all the names
            for (String name : names) { //Print the message for everyone
                if (clients.containsKey(name)) { //Check if the user is existing
                    clients.get(name).getPw().println("MSGRES:" + senderName + ":" + msg);
                } else {
                    clients.get(senderName).getPw().println("No such client: " + name);
                }
            }
        } else { //We have only one receiver
            if (clients.containsKey(receivers)) { //Check if the user is existing
                clients.get(receivers).getPw().println("MSGRES:" + senderName + ":" + msg);
            } else {
                clients.get(senderName).getPw().println("No such client: " + receivers);
            }
        }
    }
}
