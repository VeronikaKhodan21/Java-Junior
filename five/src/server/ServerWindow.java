package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerWindow {
    private static final int PORT = 8080;
    private static final String LOG_PART = "src/server/log.txt";

    private final List<ClientHandler> clients = new ArrayList<>();
    private boolean running = true;
    private ServerSocket serverSocket;

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Сервер работает по порту = " + PORT);

        // Запуск потока для чтения команд с консоли
        Thread consoleThread = new Thread(this::readConsoleInput);
        consoleThread.start();

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Подключен новый клиент");
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                synchronized (clients) {
                    clients.add(clientHandler);
                }
                new Thread(clientHandler).start();
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readConsoleInput() {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String inputLine;
            while (running && (inputLine = consoleReader.readLine()) != null) {
                if ("stop server".equalsIgnoreCase(inputLine)) {
                    broadcastMessage("Сервер остановлен.");
                    stopServer();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastMessage(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    private void stopServer() {
        running = false;
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.stopClient();
            }
            clients.clear();
        }
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private boolean connected = true;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                broadcastMessage(readLog());
            } catch (IOException e) {
                e.printStackTrace();
                closeConnections();
            }
        }

        private String readLog() {
            StringBuilder sb = new StringBuilder();
            try (FileReader readr = new FileReader(LOG_PART);) {
                int c;
                while ((c = readr.read()) != -1) {
                    sb.append((char) c);
                }
                sb.delete(sb.length() - 1, sb.length());
                return sb.toString();
            } catch (Exception e) {
                System.out.println("Лох. история не читается");
                return null;
            }
        }

        @Override
        public void run() {
            try {
                String inputLine;
                while (connected && (inputLine = in.readLine()) != null) {
                    System.out.println("Получено от клиента" + inputLine);
                    saveInLog(inputLine);
                    if ("exit".equalsIgnoreCase(inputLine)) {
                        connected = false;
                        break;
                    }
                    broadcastMessage(inputLine);
                }
            } catch (IOException e) {
                if (connected) {
                    e.printStackTrace();
                }
            } finally {
                closeConnections();
            }
        }

        private void saveInLog(String text) {
            try (FileWriter writer = new FileWriter(LOG_PART, true)) {
                writer.write(text);
                writer.write("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            if (connected) {
                out.println(message);
            }
        }

        public void stopClient() {
            connected = false;
            closeConnections();
        }

        private void closeConnections() {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            synchronized (clients) {
                clients.remove(this);
            }
        }
    }
}
