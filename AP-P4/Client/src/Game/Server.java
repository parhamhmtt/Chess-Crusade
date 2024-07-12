package Game;

import Panels.mainPage;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Server implements Board,Serializable {
    static ObjectOutputStream out;
    static ObjectInputStream in;
    static Socket clientSocket;
    JFrame serverFrame;
    public Server(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream,String Server,Boolean newGame) {
        clientSocket = socket;
        in = objectInputStream;
        out = objectOutputStream;
        if(newGame)
            Board.firstMapFixer();
        else
            Board.loadBoard();
        serverFrame=new mainPage(Server,socket,out,in,true,newGame);
    }
}