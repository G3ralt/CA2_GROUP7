package gui;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JTextArea;


public class Observer implements Runnable {
    
    private boolean isOn;
    private final JTextArea frame;
    private final Scanner scan;
    
    public Observer(Socket socket, JTextArea frame) throws IOException {
        this.scan = new Scanner(socket.getInputStream());
        this.frame = frame;
        this.isOn = true;
    }
    
    public void closeObserver() {
        this.isOn = false;
    }
    
    @Override
    public void run() {
        while(isOn) {
            String input = scan.nextLine(); //BLOCKING call
            if(input.startsWith("MSGRES:")) {
                input = input.substring(7);
            }
            frame.append(input + "\n");
        }
    }
    
    
}
