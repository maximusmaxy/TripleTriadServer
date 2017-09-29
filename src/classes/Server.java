/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import frames.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocol.*;

/**
 *
 * @author Max
 */
public class Server extends Thread {

    private ServerFrame frame;
    private PlayerHandler player1;
    private PlayerHandler player2;

    public Server(ServerFrame frame) {
        this.frame = frame;
    }

    @Override
    public void run() {
        frame.writeLine("Running");
        int portNumber = 6969;
        Message message;
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            //host
            String hostName = serverSocket.getInetAddress().getLocalHost().getHostName();
            frame.writeLine("Host Name: " + hostName);
            String hostAddress = serverSocket.getInetAddress().getLocalHost().getHostAddress();
            frame.writeLine("Host Address: " + hostAddress);
            
            //player1
            frame.writeLine("Waiting for player 1.");
            Socket player1Socket = serverSocket.accept();
            ObjectInputStream in1 = new ObjectInputStream(player1Socket.getInputStream());
            ObjectOutputStream out1 = new ObjectOutputStream(player1Socket.getOutputStream());
            sendMessage(out1, new Message("You are Player 1. Waiting on Player 2.", Message.MESSAGE, null));
            frame.writeLine("Player 1 connected.");
            
            //player2
            frame.writeLine("Waiting for player 2.");
            Socket player2Socket = serverSocket.accept();
            ObjectInputStream in2 = new ObjectInputStream(player2Socket.getInputStream());
            ObjectOutputStream out2 = new ObjectOutputStream(player2Socket.getOutputStream());
            sendMessage(out1, new Message("Player 1 goes first.", Message.TURN, true));
            sendMessage(out2, new Message("Player 2 goes second. Waiting on player 1.", Message.TURN, false));
            frame.writeLine("Player 2 connected.");
            
            //connection
            player1 = new PlayerHandler(frame, true, in1, out2);
            player2 = new PlayerHandler(frame, false, in2, out1);
            frame.writeLine("Starting Match.");
            player1.start();
            player2.start();
            
            //game loop
            while (true) {
                sleep(100);
                if (player1.isExit() || player2.isExit())
                    break;
            }
        } catch (Exception e) {
            frame.writeLine(e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void sendMessage(ObjectOutputStream out, Message message) throws IOException {
        out.writeObject(message);
        out.flush();
    }
}
