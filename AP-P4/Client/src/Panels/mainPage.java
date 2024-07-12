package Panels;

import Blocks.*;
import Game.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Objects;

import static java.lang.System.exit;

public class mainPage extends JFrame implements Serializable {
    static ObjectOutputStream out;
    static int Turn=0;
    static java.lang.reflect.Type blockArrayType = new TypeToken<blocks[][]>() {}.getType();
    String winner;
    Gson gson=new Gson();
    Socket socket;
    JPanel logPanel;
    JTextField turnPanel;
    static ObjectInputStream in;
    String LOGTEXT="Game Log\n";
    String ChatText="Chat Box\n";
    static String username;
    Boolean IsSever;
    static JLayeredPane layeredPane=new JLayeredPane();
    private JTextArea messageToSend;
    private JButton saveButton;
    private JTextArea logArea;
    private JTextArea messages;
    private blocks clickedPiece=null;
    private static String ServerName="";
    private static String ClientName="";
    private int xClicked;
    private int yClicked;
    private Boolean[][] circlePut=new Boolean[8][8];

    public mainPage(String name,Socket socket,ObjectOutputStream objectOutputStream,ObjectInputStream objectInputStream,Boolean isServer,Boolean newGame){
        this.socket=socket;
        username=name;
        in = objectInputStream;
        out = objectOutputStream;
        IsSever=isServer;
        if (isServer)
            ServerName=name;
        else {
            ServerName=Board.loadName(0);
            ClientName = name;
        }

        if(!newGame){
            this.Turn=Integer.parseInt(Board.load(2));
            LOGTEXT=Board.load(0)+"\n";
            ChatText=Board.load(1)+"\n";
        }
        Board.arrangePower();
        initializeFrame();
        initializeChatPanel();
        initializeBoardPanel();
        setVisible(true);
        addAsset();
    }

    public void initializeBoardPanel(){
        setResizable(false);
        getContentPane().setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(80,80,640,640);
        layeredPane.setOpaque(true);
        layeredPane.setVisible(true);
        add(layeredPane);
        String str;
        if(Turn%2==0)
            str="    "+username+"\t"+"Turn: "+ServerName;
        else
            str="    "+username+"\t"+"Turn: "+ClientName;
        turnPanel=new JTextField(str);
        turnPanel.setForeground(Color.red);
        turnPanel.setBackground(Color.black);
        turnPanel.setEditable(false);
        turnPanel.setFont(new Font("Tahoma",Font.BOLD,15));
        turnPanel.setBorder(BorderFactory.createLineBorder(Color.green));
        turnPanel.setBounds(650,10,250,40);
        add(turnPanel);

        // Fill screen with tiles
        int x = 0;
        int y = 0;
        int width = 80;
        int height = 80;
        int numberOfTiles = 640 * 640 / (width * height);
        for (int i = 0; i < numberOfTiles; i++) {
//            System.out.println("[" + (y / 80) + "][" + (x / 80) + "]");
//            System.out.println("X: " + x + " | Y: " + y);

            // Add ground tiles
            int row = y / height;
            int col = x / width;
            //System.out.println("Row: " + row + " | Col: " + col);
            if (row == 7) {

                String assetindex = switch (col) {
                    case 0 -> "02";
                    case 1 -> "10";
                    case 2 -> "11";
                    case 3 -> "12";
                    case 4 -> "015";
                    case 5 -> "016";
                    case 6 -> "018";
                    case 7 -> "017";
                    default -> throw new IllegalStateException("Unexpected value: " + col);
                };
                ImageIcon tileImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/" + assetindex + "_Chess Crusader.png")));
                tileImage.setImage(tileImage.getImage().getScaledInstance(80, 80, 0));
                JLabel tileLabel = new JLabel(tileImage);
                tileLabel.setBounds(x, y, width, height);
                // Add image to tile by left click
                int finalX = x;
                int finalY = y;

                layeredPane.add(tileLabel, 0);
            } else if (col == 0) {
                ImageIcon tileImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/" + "0" + String.valueOf(9 - row) + "_Chess Crusader.png")));
                tileImage.setImage(tileImage.getImage().getScaledInstance(80, 80, 0));
                JLabel tileLabel = new JLabel(tileImage);
                tileLabel.setBounds(x, y, width, height);
                // Add image to tile by left click
                int finalX = x;
                int finalY = y;

                layeredPane.add(tileLabel, 0);
            } else {
                String assetindex;
                if ((row % 2 == 0) == (col % 2 == 0)) {
                    assetindex = "01";
                } else {
                    assetindex = "00";
                }
                ImageIcon tileImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/" + assetindex + "_Chess Crusader.png")));
                tileImage.setImage(tileImage.getImage().getScaledInstance(80, 80, 0));

                JLabel tile = new JLabel(tileImage);
                tile.setBounds(x, y, width, height);
                // Add image to tile by left click
                int finalX = x;
                int finalY = y;

                layeredPane.add(tile,Integer.valueOf(1));
            }
            layeredPane.repaint(new Rectangle(x, y, width, height));

            x += width;
            if (x >= 640) {
                x = 0;
                y += height;
            }
        }

        layeredPane.repaint();
        add(layeredPane);

    }

    private void initializeChatPanel() {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(null);
        chatPanel.setBackground(new Color(30,30,30));
        chatPanel.setBounds(935, 420, 580, 550);

        if (IsSever){
            saveButton=new JButton("Save Game");
            saveButton.setForeground(Color.red);
            saveButton.setBackground(Color.black);
            saveButton.setFont(new Font("Tahoma",Font.BOLD,15));
            saveButton.setBorder(BorderFactory.createLineBorder(Color.green));
            saveButton.setBounds(750,750,150,40);
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String log="gameSaved by "+ServerName;
                    logArea.setText(logArea.getText() + log + "\n");
                    logPanel.repaint();
                    Board.save(logArea.getText(),messages.getText(),Turn);
                    try {
                        out.writeUTF("updateMap");
                    out.flush();
                    String str;
                    if (Turn % 2 == 0)
                        str = "    " + username + "\t" + "Turn: "+ServerName;
                    else
                        str = "    " + username + "\t" + "Turn: "+ClientName;
                    turnPanel.setText(str);
                    turnPanel.repaint();
                    out.writeInt(Turn);
                    out.flush();
                    out.writeUTF(log);
                    out.flush();

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            add(saveButton);
        }



         logPanel = new JPanel();
        logPanel.setLayout(null);
        logPanel.setBounds(935, 5, 580, 410);

        logArea = new JTextArea(LOGTEXT);
        logArea.setEditable(false);
        logArea.setBorder(BorderFactory.createLineBorder(Color.green, 1));
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.green);
        logArea.setFont(new Font("Tahoma", Font.PLAIN, 15));
        JScrollPane messageScrollPane2 = new JScrollPane(logArea);
        messageScrollPane2.setBounds(0, 0, 580, 410);
        logPanel.add(messageScrollPane2);

        add(logPanel);

        messages = new JTextArea(ChatText);
        messages.setEditable(false);
        messages.setBorder(BorderFactory.createLineBorder(Color.green,1));
        messages.setBackground(Color.BLACK);
        messages.setForeground(Color.green);
        messages.setFont(new Font("Tahoma", Font.PLAIN, 15));
        JScrollPane messageScrollPane = new JScrollPane(messages);
        messageScrollPane.setBounds(0, 0, 580, 340);
        chatPanel.add(messageScrollPane);

        messageToSend = new JTextArea();
        messageToSend.setBorder(BorderFactory.createLineBorder(Color.green,1));
        messageToSend.setBackground(Color.BLACK);
        messageToSend.setForeground(Color.green);
        messageToSend.setFont(new Font("Tahoma", Font.PLAIN, 15));
        JScrollPane scrollMessageToSend = new JScrollPane(messageToSend);
        scrollMessageToSend.setBounds(0, 341, 500, 50);
        chatPanel.add(scrollMessageToSend);

        JButton sendButton = new JButton("Send");
        sendButton.setBorder(BorderFactory.createLineBorder(Color.green,1));
        sendButton.setBackground(Color.BLACK);
        sendButton.setForeground(Color.green);
        sendButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        sendButton.setBounds( 500, 341, 79, 50);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == sendButton) {
                    try {
                        String message = messageToSend.getText().trim();
                        if (!message.isBlank()) {
                            out.writeUTF("updateChat");
                            out.flush();
                            out.writeUTF(username + ": " + message);
                            out.flush();
                            messages.setText(messages.getText() + username + ": " + message + "\n");
                            messageToSend.setText("");
                            chatPanel.repaint();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        chatPanel.add(sendButton);


        add(chatPanel);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response;
                while (socket.isConnected()) {
                    try {
                        switch (response=in.readUTF()) {
                            case "updateChat":
                                String message = in.readUTF();
                                 if (!message.isBlank()) {
                                messages.setText(messages.getText() + message + "\n");
                                chatPanel.repaint();
                                 }
                            break;
                            case "updateMap":
                                Turn=in.readInt();
                                String log = in.readUTF();

                                    logArea.setText(logArea.getText() + log + "\n");
                                    logPanel.repaint();

                                String str;
                                if(Turn%2==0)
                                    str="    "+username+"\t"+"Turn: "+ServerName;
                                else
                                    str="    "+username+"\t"+"Turn: "+ClientName;
                                turnPanel.setText(str);
                                turnPanel.repaint();
                               Board.loadBoard();
                                reload();
                                break;

                            case "gameEnd":
                                winner=in.readUTF();
                                JOptionPane.showMessageDialog(null,"Congrats...,Winner is\n***** "+winner+" *****");
                                Board.ClearFiles();
                                exit(0);
                            default:
                                JOptionPane.showMessageDialog(null,response);
                        }
                    } catch (EOFException ex) {
                        ex.printStackTrace();
                        exit(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                        exit(0);
                    }

                }
            }
        }).start();
    }



    private void initializeFrame(){
        setLayout(null);
        setResizable(false);
        getContentPane().setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        getContentPane().setBackground(new Color(30,30,30));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public void addAsset() {
        Board.arrangePower();
        String str = "";
        int x,y,width,height;

        for (int i = 0; i <8 ; i++) {
            for (int j = 0; j <8 ; j++) {
                switch (Board.map[i][j].tag){
                    case "c":
                        if (Board.map[i][j] instanceof knight)
                            str="17";
                        else if (Board.map[i][j] instanceof  castle)
                            str="23";
                        else if (Board.map[i][j] instanceof  archer)
                            str="21";
                        else if (Board.map[i][j] instanceof  solider)
                            str="19";
                        else if (Board.map[i][j] instanceof  hashashin)
                            str="15";
                        break;
                    case "m":
                        if (Board.map[i][j] instanceof knight)
                            str="18";
                        else if (Board.map[i][j] instanceof  castle)
                            str="24";
                        else if (Board.map[i][j] instanceof  archer)
                            str="22";
                        else if (Board.map[i][j] instanceof  solider)
                            str="20";
                        else if (Board.map[i][j] instanceof  hashashin)
                            str="16";
                        break;
                    case "e":
                        str="";
                        break;

                }
                if (!str.equals("")) {
                        ImageIcon tileImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/" + str + "_Chess Crusader.png")));
                    tileImage.setImage(tileImage.getImage().getScaledInstance(80, 80, 0));
                    JLabel tileLabel = new JLabel(tileImage);
                    tileLabel.setBounds(j * 80, i * 80, 80, 80);
                    int finalI = i;
                    int finalJ = j;
                    if((username.equalsIgnoreCase(ServerName)&& Integer.valueOf(str)%2==0) ||(username.equalsIgnoreCase(ClientName)&& Integer.valueOf(str)%2==1) ) {
                        tileLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mousePressed(MouseEvent e) {
                                if(Turn<=2){
                                    ClientName=Board.loadName(1);
                                }
                                thisAsset(finalI,finalJ);
                                if (e.getButton() == MouseEvent.BUTTON1) {
                                    if (clickedPiece == null) {
                                        clickedPiece = Board.map[finalI][finalJ];
                                        mouseIsOn(finalI, finalJ);
                                        yClicked = finalJ;
                                        xClicked = finalI;
                                    } else {
                                        System.out.println(circlePut[finalI][finalJ]);
                                        if (circlePut[finalI][finalJ]) {


                                        } else if (finalI == xClicked && finalJ == yClicked) {
                                            removeCircle();
                                        }
                                        removeCircle();
                                        clickedPiece = null;
                                    }
                                }
                                ;

                            }
                        });
                    }
                    layeredPane.add(tileLabel, Integer.valueOf(2));
                    str = String.valueOf(Board.map[i][j].power + 26);
                    tileImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/" + str + "_Chess Crusader.png")));
                    tileImage.setImage(tileImage.getImage().getScaledInstance(80, 80, 0));
                    tileLabel = new JLabel(tileImage);
                    tileLabel.setBounds(j * 80, i * 80, 80, 80);
                    layeredPane.add(tileLabel, Integer.valueOf(5));
                    revalidate();
                    repaint();

                }

            }
        }
    }

    private void thisAsset(int finalI, int finalJ) {
        ImageIcon tileImage;
        tileImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/13_Chess Crusader.png")));
        tileImage.setImage(tileImage.getImage().getScaledInstance(80, 80, 0));
        JLabel tileLabel = new JLabel(tileImage);
        tileLabel.setBounds(finalJ * 80, finalI * 80, 80, 80);
        layeredPane.add(tileLabel, Integer.valueOf(9));
        revalidate();
        repaint();
    }

    private void removeCircle() {
      for (Component component: layeredPane.getComponentsInLayer(Integer.valueOf(9)))
          layeredPane.remove(component);
      revalidate();
      repaint();
    }

    public void mouseIsOn(int i,int j) {
            circlePut=Board.availableStep(i,j);
            for (int k = 0; k < 8; k++) {
                for (int l = 0; l < 8; l++) {
                    if(circlePut[k][l])
                        addCircle(k,l);
                }
            }
        }

    private void addCircle(int i, int j) {
        ImageIcon tileImage;
        tileImage = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/14_Chess Crusader.png")));
        tileImage.setImage(tileImage.getImage().getScaledInstance(40, 40, 0));
        JLabel tileLabel = new JLabel(tileImage);
        tileLabel.setBounds(j * 80, i * 80, 80, 80);
        tileLabel.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    clickedPiece = null;
                    removeCircle();
                    if (((Turn % 2 == 0) && username.equals(ServerName)) || ((Turn % 2 == 1) && username.equals(ClientName))) {
                        if (Board.map[xClicked][yClicked].power >= Board.map[i][j].power &&( !(Board.map[xClicked][yClicked] instanceof castle) || Board.map[i][j] instanceof emp)) {
                            Board.move(xClicked, yClicked, i, j);
                            String log = movedLog(username, xClicked, yClicked, i, j);
                            try {
                                Board.saveBoard();
                                out.writeUTF("updateMap");
                                out.flush();
                                Turn += 1;
                                String str;
                                if (Turn % 2 == 0)
                                    str = "    " + username + "\t" + "Turn: "+ServerName;
                                else
                                    str = "    " + username + "\t" + "Turn: "+ClientName;
                                turnPanel.setText(str);
                                turnPanel.repaint();

                                out.writeInt(Turn);
                                out.flush();
                                out.writeUTF(log);
                                out.flush();
                                if(!IfGameEnd().equals("")){
                                    out.writeUTF("gameEnd");
                                    out.flush();
                                    out.writeUTF(winner);
                                    out.flush();
                                    JOptionPane.showMessageDialog(null,"Congrats...,Winner is\n***** "+winner+" *****");
                                    exit(0);
                                }

                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            reload();

                        }
                        else
                        JOptionPane.showMessageDialog(null,"Cant move because of less Power","illegal move",JOptionPane.ERROR_MESSAGE);

                    }
                    else {
                        JOptionPane.showMessageDialog(null,"Wait.... ,Its not Your Turn ","Error",JOptionPane.ERROR_MESSAGE);
                    }

                }

            }
        });
        layeredPane.add(tileLabel, Integer.valueOf(9));
        revalidate();
        repaint();
    }


    private String IfGameEnd(){
        winner="";
        Boolean serverLose=true;
        Boolean clientLose=true;
        for (int i = 0; i <8; i++) {
            for (int j = 0; j <8; j++) {
                    if(Board.map[i][j] instanceof castle){
                        if(Board.map[i][j].tag.equals("c"))
                            serverLose=false;
                        if(Board.map[i][j].tag.equals("m"))
                            clientLose=false;
                    }
            }
        }
        if(serverLose || clientLose){
            if (serverLose)
                winner=ClientName;
            if (clientLose)
                winner=ServerName;
        }

        return winner;
    }

    private String movedLog(String username, int xClicked, int yClicked, int i, int j) {
        xClicked-=8;
        xClicked*=(-1);
        i-=8;
        i*=(-1);
        String str= username+": Moved a piece from "+"[" + (xClicked) + "][" + (char)(yClicked+65) + "]"+" to "+"[" + (i) + "][" + (char)(j+65) + "]";
        logArea.setText(logArea.getText() + str + "\n");
        logPanel.repaint();
            return str;
    }

    private void reload() {
        for (Component component: layeredPane.getComponentsInLayer(Integer.valueOf(5)))
            layeredPane.remove(component);
        for (Component component: layeredPane.getComponentsInLayer(Integer.valueOf(2)))
            layeredPane.remove(component);
        addAsset();
        revalidate();
        repaint();
    }
}
