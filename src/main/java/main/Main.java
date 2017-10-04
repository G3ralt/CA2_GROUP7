package main;

import chatClient.ChatClient;
import chatServer.ChatServer;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            ChatServer server = new ChatServer(8081);
            while (true) {
                new Thread(new ChatClient(server.getServerSocket().accept(), server.getClients(), server)).start();
            }
        } catch (IOException ex) {
            System.out.println("Couldn`t create server at port: 8081");
        }

    }
}
