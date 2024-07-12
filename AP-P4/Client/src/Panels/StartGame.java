package Panels;

import Game.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class StartGame extends JFrame implements ActionListener, Serializable {
    JPanel buttonPanel;
    JTextField userNameField;
    static JButton hostButton;
    JButton LoadServerButton;
    JButton LoadClientButton;

    JButton joinButton;

    public StartGame() {
        setTitle("Chess Crusader");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(400, 400);

        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("assets/OIP.jfif"));
        JLabel backgroundLabel = new JLabel(imageIcon);
        backgroundLabel.setLayout(new GridBagLayout());


        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 15, 10));
        buttonPanel.setOpaque(false);

        userNameField=new JTextField("Name");
        userNameField.setForeground(Color.black);
        userNameField.setBackground(Color.white);
        userNameField.setFont(new Font("Tahoma", Font.PLAIN, 15));
        buttonPanel.add(userNameField);

        hostButton = new JButton("     Host     ");
        hostButton.setOpaque(true);
        hostButton.setForeground(Color.GREEN);
        hostButton.setBackground(Color.BLACK);
        hostButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        hostButton.setFocusable(false);
        hostButton.addActionListener(this);


        buttonPanel.add(hostButton);
        joinButton = new JButton("Join");
        joinButton.setOpaque(true);
        joinButton.setForeground(Color.GREEN);
        joinButton.setBackground(Color.BLACK);
        joinButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        joinButton.setFocusable(false);
        joinButton.addActionListener(this);
        buttonPanel.add(joinButton);

        LoadServerButton = new JButton("Load Server");
        LoadServerButton.setOpaque(true);
        LoadServerButton.setForeground(Color.GREEN);
        LoadServerButton.setBackground(Color.BLACK);
        LoadServerButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        LoadServerButton.setFocusable(false);
        LoadServerButton.addActionListener(this);
        buttonPanel.add(LoadServerButton);

        LoadClientButton = new JButton("Load Client");
        LoadClientButton.setOpaque(true);
        LoadClientButton.setForeground(Color.GREEN);
        LoadClientButton.setBackground(Color.BLACK);
        LoadClientButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        LoadClientButton.setFocusable(false);
        LoadClientButton.addActionListener(this);
        buttonPanel.add(LoadClientButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundLabel.add(buttonPanel, gbc);

        setContentPane(backgroundLabel);
    }

    private void host() {

            try {
                Board.saveName(userNameField.getText(),0);
                ServerSocket serverSocket = new ServerSocket(8080);
                Socket socket = serverSocket.accept();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                dispose();
                new Server(socket, objectInputStream, objectOutputStream, userNameField.getText(), true);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        }

    private void join() {
                    try {
                        Board.saveName(userNameField.getText(),1);
                        Socket socket = new Socket("localhost", 8080);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                        java.awt.Point location =getLocation();
                        dispose();
                        new Client(socket, objectInputStream, objectOutputStream, userNameField.getText(),true);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
    }
    private void loadServer() {

        try {
            userNameField.setText(Board.loadName(0));
            ServerSocket serverSocket = new ServerSocket(8080);
            Socket socket = serverSocket.accept();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            dispose();
            new Server(socket, objectInputStream, objectOutputStream, userNameField.getText(), false);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void loadClient() {
        try {
            userNameField.setText(Board.loadName(1));
            Socket socket = new Socket("localhost", 8080);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            dispose();
            new Client(socket, objectInputStream, objectOutputStream, userNameField.getText(),false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == hostButton) {
            host();
        } else if (e.getSource() == joinButton) {
            join();
        } else if (e.getSource() == LoadServerButton) {
            if (Board.ifEmpty()){
                JOptionPane.showMessageDialog(null, "last game was ended");
            return;
        }
            loadServer();
        }
        else if (e.getSource() == LoadClientButton) {
            if (Board.ifEmpty()){
                JOptionPane.showMessageDialog(null, "last game was ended");
                return;
            }
            loadClient();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StartGame().setVisible(true));
    }
}
