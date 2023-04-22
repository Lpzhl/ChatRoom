package serverTest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static DatabaseConnection dbConnection;

    public static void main(String[] args) {
        dbConnection = new DatabaseConnection();
        try (ServerSocket serverSocket = new ServerSocket(6000)) {
            System.out.println("服务器启动，等待客户端连接...");
            while (true) {
                Socket clientSocket = null;
                try {
                    clientSocket = serverSocket.accept();
                    System.out.println("客户端"+clientSocket);
                    ServerHandler serverHandler = new ServerHandler(clientSocket);
                    Thread thread = new Thread(serverHandler);
                    thread.start();
                    System.out.println("客户端已连接");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
