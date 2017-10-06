package main;

import chatClient.ClientHandler;
import chatServer.Server;
import java.io.IOException;


public class MainServer {

    public static void main(String[] args) {
        try {
            Server server = new Server(8081);
            while (true) {
                new Thread(new ClientHandler(server.getServerSocket().accept(), server)).start();
            }
        } catch (IOException ex) {
            System.out.println("Couldn`t create server at port: 8081");
        }

    }
}
