/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatudpclient;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Bettaieb Ayoub
 */
public class GUIChat extends JFrame implements ActionListener {

    JPanel panel;
    JTextField message;
    JButton send;
    JTextArea chat;
    JScrollPane scroll;
    DatagramSocket socket;
    JMenuBar menu;
    JMenu inserisci;
    JMenuItem login;

    private String messaggio;
    private String indirizzoIP = "127.0.0.1";
    String username;

    public GUIChat() throws SocketException {
        pack();
        panel = new JPanel();
        message = new JTextField();
        send = new JButton("Invia");
        chat = new JTextArea();
        scroll = new JScrollPane(chat);
        socket = new DatagramSocket();
        menu = new JMenuBar();
        inserisci = new JMenu("Inserisci");
        login = new JMenuItem("USERNAME");

        this.setVisible(true);
        this.setSize(500, 500);
        this.setTitle("Chat tra client");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(3, 2));
        this.setJMenuBar(menu);
        menu.add(inserisci);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));
        message.setBorder(new EmptyBorder(10, 100, 10, 100));
        panel.add(message);

        panel.add(send);
        inserisci.add(login);
        chat.setBorder(new EmptyBorder(22, 22, 22, 22));
        chat.setEditable(false);

        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scroll);
        this.add(panel);

        message.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                message.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (username == null) {
                    JOptionPane.showMessageDialog(null, "USERNAME NON INSERITO, INSERISCI UN USERNAME PER POTER COMINCIARE LA COMUNICAZIONE!");
                } else {
                    try {
                        invio(message.getText(), username);
                    } catch (IOException ex) {
                        Logger.getLogger(GUIChat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    message.setText("");
                }
            }
        });
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                username = JOptionPane.showInputDialog("USERNAME: ");
                if (username != null) {
                    chat.append("Username: " + username);
                    chat.append("\n");
                }
            }
        });
        Thread inascolto = new Thread() {
            public void run() {
                try {
                    ricezione();
                } catch (IOException ex) {
                    Logger.getLogger(GUIChat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        inascolto.start();
    }

    public void invio(String messaggio, String username) throws IOException {
        DatagramPacket datagramma;

        byte[] buffer;
        buffer = messaggio.getBytes("UTF-8");
        datagramma = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(indirizzoIP), 1077);
        socket.send(datagramma);
        JOptionPane.showMessageDialog(panel, "Ho inviato il messaggio", "Send", JOptionPane.ERROR_MESSAGE);
    }

    public void ricezione() throws IOException {
        DatagramPacket datagramma;
        String ricezione;
        byte[] buffer = new byte[100];

        datagramma = new DatagramPacket(buffer, buffer.length);
        while (!Thread.interrupted()) {
            socket.receive(datagramma);
            ricezione = new String(datagramma.getData(), 0, datagramma.getLength(), "ISO-8859-1");
            chat.append(username + " > " + ricezione + "\n");

        }
        socket.close();
    }

    public static void main(String[] args) throws SocketException {

        new GUIChat();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
