package chatServer;

import chatClient.ChatClient;
import chatClient.ClientHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;


public class Server {
    private final HashMap<String, ClientHandler> clients;
    private final ServerSocket serverSocket;
    
    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.clients = new HashMap();
        System.out.println("ChatServer created!");
    }
    
    //<editor-fold defaultstate="collapsed" desc="Getters and setters">
    public HashMap<String, ClientHandler> getClients() {
        return clients;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
//</editor-fold>

    public void printAllClients() {
        if (!clients.isEmpty()) { //Check if we have clients to print to
            String names = ""; 
            for (String key : clients.keySet()) { // Iterate all the names and add them to a String
                names += key + ",";
            }
            names = names.substring(0, names.length() - 1); //Loose the last ,
            for (ClientHandler client : clients.values()) { //Print the list of names to all clients
                client.getPw().println("CLIENTLIST:" + names);
            }
        }
    }
    
    public void addClientToChat(ClientHandler client, String input) {
        String name = input.substring(6);
        if(clients.containsKey(name)) {
            disconnectClient(client);
        } else {
            client.setClientName(name);
            clients.put(name, client);
            printAllClients();
        }
    }
    
    public void disconnectClient(ClientHandler client) {
        try {
            System.out.println("Disconnecting client: " + client.getClientName());
            clients.remove(client.getClientName());
            client.getPw().println("Disconnecting from server ...");
            client.getPw().close();
            client.getScan().close();
            client.getSocket().close();
        } catch (IOException ex) {
            System.out.println("Couldn`t close socket: " + client.getSocket().toString());
        }
    }

    public void interpretInput(String input, ClientHandler sender) {
        if(input.startsWith("MSG:")) {
            input = input.substring(4);
            sendMessage(input, sender);
        } else {
            disconnectClient(sender);
        }
    }

    private void sendMessage(String input, ClientHandler sender) {
        String[] array = input.split(":");
        String msgToSend = input.substring(input.indexOf(":")+1);
        if(input.startsWith("*:")) {
            for (ClientHandler client : clients.values()) { //Send the message to all
                client.getPw().println("MSGRES:" + sender.getClientName() + ":" + msgToSend);
            }
        } else if(!array[0].contains(",")) {
            if(clients.containsKey(array[0])) {
                clients.get(array[0]).getPw().println("MSGRES:" + sender.getClientName() + ":" + msgToSend);
            } else {
                disconnectClient(sender);
            }
        } else if(array[0].contains(",")) {
            array = array[0].split(",");
            for(String name : array) {
                clients.get(name).getPw().println("MSGRES:" + sender.getClientName() + ":" + msgToSend);
            }
        } else {
            disconnectClient(sender);
        }
    }
}
