package serverTest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest2 {
    private static final int SHORT_CONNECTION_PORT = 6000;
    private static final int LONG_CONNECTION_PORT = 6001;
    public static void main(String[] args) {
        try (ServerSocket shortConnectionServerSocket = new ServerSocket(SHORT_CONNECTION_PORT);
             ServerSocket longConnectionServerSocket = new ServerSocket(LONG_CONNECTION_PORT)) {
            System.out.println("服务器启动，等待客户端连接...");
            // 创建两个线程分别处理短连接和长连接的请求
            Thread shortConnectionThread = new Thread(() -> {
                while (true) {
                    try {
                        Socket clientSocket = shortConnectionServerSocket.accept();
                        System.out.println("短连接客户端: " + clientSocket);
                        ServerHandler serverHandler = new ServerHandler(clientSocket);
                        Thread thread = new Thread(serverHandler);
                        thread.start();
                        System.out.println("短连接客户端已连接");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread longConnectionThread = new Thread(() -> {
                while (true) {
                    try {
                        Socket clientSocket = longConnectionServerSocket.accept();
                        System.out.println("长连接客户端: " + clientSocket);
                        ServerHandlerLongConnection serverHandlerLongConnection = new ServerHandlerLongConnection(clientSocket);
                        Thread thread = new Thread(serverHandlerLongConnection);
                        thread.start();
                        System.out.println("长连接客户端已连接");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


            // 启动两个线程
            shortConnectionThread.start();
            longConnectionThread.start();

            //这两行代码的目的是为了让主线程（main 方法所在的线程）等待 shortConnectionThread 和 longConnectionThread 这两个线程执行结束，
            //这样可以确保所有线程都执行完毕后再退出程序。
            // 等待两个线程结束
            shortConnectionThread.join();
            longConnectionThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
