package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.*;

import server.ServerWindow;

public class ClientGUI extends JFrame {
    private static final int HEIGHT = 300;
    private static final int WIDTH = 400;

    private static Socket socket;

    private PrintWriter out;
    private static BufferedReader in;

    private Boolean connected;
    private String name;
    private int port;

    private JTextArea log;
    private JTextField tfMessage;
    private JButton btnSend, btnExit;
    private JPanel messagePanel;

    public ClientGUI() {

        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setTitle("Chat client");
        setLocation(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createPanel();
        getNamePort();
        setVisible(true);

        connectToServer();
    }

    /* 
    public void answer(String text) {
        appendLog(text);
    }*/

    private void connectToServer() {
        try {
            socket = new Socket("localhost", port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Thread readerThread = new Thread(this::messageRead);
            readerThread.start();
            messagePanel.setVisible(true);
            connected = true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Не удалось подключиться к серверу.", "Ошибка подключения",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

    }

    private int strToint(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void appendLog(String text) {
        log.append(text + "\n");
    }

    private void createPanel() {
        add(createLog());
        add(createFooter(), BorderLayout.NORTH);
        add(createExitPanel(), BorderLayout.SOUTH);
    }

    private void getNamePort() {
        port = strToint(JOptionPane.showInputDialog(this, "Введите порт соединения"));
        name = JOptionPane.showInputDialog(this, "Введите свой никнейм");
    }

    private Component createLog() {
        log = new JTextArea();
        log.setEditable(false);
        return new JScrollPane(log);
    }

    private Component createFooter() {
        messagePanel = new JPanel(new BorderLayout());
        tfMessage = new JTextField();
        tfMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    writeMesssage();
                }
            }
        });
        btnSend = new JButton("send");
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writeMesssage();
            }
        });
        messagePanel.add(tfMessage);
        messagePanel.add(btnSend, BorderLayout.EAST);
        messagePanel.setVisible(true);
        return messagePanel;
    }

    private void writeMesssage() {
        String message = tfMessage.getText().trim();
        out.println(name + ": " + message);
        tfMessage.setText("");
    }

    private void writeMesssage(String text) {
        out.println(name + ": " + text);
        tfMessage.setText("");
    }

    private Component createExitPanel() {
        Panel panel = new Panel();
        btnExit = new JButton("EXIT");
        btnExit.setForeground(Color.RED);
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnectFromServer();
                System.exit(0);
            }
        });
        panel.add(btnExit);
        return panel;
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            disconnectFromServer();
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

    public void disconnectFromServer() {
        try {
            if (connected) {
                writeMesssage("отключился от сервера");
                socket.close();
                in.close();
                out.close();
                connected = false;
                
            } else {
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }
        } catch (Exception e) {
            appendLog("Какая-то лажа");
        }
    }

    public void messageRead() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                log.append(line + "\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
