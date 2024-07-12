package Game;

import Panels.mainPage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;

import static java.lang.System.*;

public class Client implements Serializable {

    static ObjectInputStream in;
    static ObjectOutputStream out;
    static Socket socket;
    JFrame clientFrame;

     public  Client(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, String username,Boolean newGame){

            this.socket = socket;
            this.in = objectInputStream;
            this.out = objectOutputStream;
            if (newGame)
                Board.firstMapFixer();
            else
                Board.loadBoard();
            clientFrame=new mainPage(username,socket,out,in,false,newGame);
    }
}
