package br.luizlabs.flexiblefiledatatransfer;

import java.io.*;
import java.net.*;

public class SocketManager {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ServerSocket serverSocket;  // Para uso do lado do servidor

    public static record ReceivedMessage(String dataType, String completeMessage) {}

    // Construtor para o Cliente (inicializa a conexão com o servidor)
    public SocketManager(String serverIp, int serverPort) {
        initializeClientConnection(serverIp, serverPort);
    }

    // Construtor para o Servidor (abre a conexão na porta e espera aceitar um cliente)
    public SocketManager(int serverPort) {
        initializeServerConnection(serverPort);
    }

    // Inicializa a conexão como Cliente
    private void initializeClientConnection(String serverIp, int serverPort) {
        try {
            socket = new Socket(serverIp, serverPort);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inicializa a conexão como Servidor
    private void initializeServerConnection(int serverPort) {
        try {
            serverSocket = new ServerSocket(serverPort);
            System.out.println("Aguardando conexão na porta " + serverPort + "...");
            socket = serverSocket.accept();  // Aceita a conexão do cliente
            System.out.println("Cliente conectado!");
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Envia mensagem do Cliente ou Servidor
    public void sendMessage(String message, String messageType) {
        String formattedMessage = "StartOfMessage " + messageType + "\n" + message + "\nEndOfMessage";
        writer.println(formattedMessage);
        writer.flush();
    }

    // Recebe a mensagem do Cliente ou Servidor
    public ReceivedMessage receiveMessage() {
        try {
            StringBuilder messageBuilder = new StringBuilder();
            String dataType = null;
            String line;
            boolean insideMessage = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("StartOfMessage ")) {
                    dataType = line.substring("StartOfMessage ".length());
                    insideMessage = true;
                } else if (line.equals("EndOfMessage")) {
                    break;
                } else if (insideMessage) {
                    messageBuilder.append(line).append("\n");
                }
            }

            return new ReceivedMessage(dataType, messageBuilder.toString().trim());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Fecha a conexão
    public void closeConnection() {
        try {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}