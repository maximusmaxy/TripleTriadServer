/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import frames.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocol.*;

/**
 *
 * @author Max
 */
public class PlayerHandler extends Thread {

    private ServerFrame frame;
    private boolean player1;
    private ObjectInputStream playerIn;
    private ObjectOutputStream opponentOut;
    private boolean exit;

    public PlayerHandler(ServerFrame frame, boolean player1, ObjectInputStream playerIn,
            ObjectOutputStream opponentOut) {
        this.frame = frame;
        this.player1 = player1;
        this.playerIn = playerIn;
        this.opponentOut = opponentOut;
        exit = false;
    }

    @Override
    public void run() {
        Message message;
        try {
            while (true) {
                message = (Message) playerIn.readObject();
                opponentOut.writeObject(message);
                opponentOut.flush();
                if (message.getType() == Message.EXIT) {
                    frame.writeLine((player1 ? "Player 2" : "Player 1") + " exited the game.");
                    exit = true;
                    break;
                }
            }
        } catch (Exception e) {
            frame.writeLine("Error reading " + (player1 ? "Player 1" : "Player 2"));
            e.printStackTrace();
        }
    }

    public boolean isExit() {
        return exit;
    }
}
