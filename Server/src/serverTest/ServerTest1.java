package serverTest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTest1 {
    private static final int SHORT_CONNECTION_PORT = 6000;
    private static final int LONG_CONNECTION_PORT = 6001;

    public static void main(String[] args) {
        ExecutorService shortConnectionExecutor = Executors.newFixedThreadPool(10);
        ExecutorService longConnectionExecutor = Executors.newFixedThreadPool(10);
        try (ServerSocket shortConnectionServerSocket = new ServerSocket(SHORT_CONNECTION_PORT);
             ServerSocket longConnectionServerSocket = new ServerSocket(LONG_CONNECTION_PORT)) {

            System.out.println("服务器启动，等待客户端连接...");

            //将处理连接的任务提交给相应的线程池
            Thread shortConnectionThread = new Thread(() -> {
                while (true) {
                    try {
                        Socket shortConnectionClientSocket = shortConnectionServerSocket.accept();
                        System.out.println("短连接客户端: " + shortConnectionClientSocket);
                        shortConnectionExecutor.execute(new ServerHandler(shortConnectionClientSocket));
                        System.out.println("短连接客户端已连接");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread longConnectionThread = new Thread(() -> {
                while (true) {
                    try {
                        Socket longConnectionClientSocket = longConnectionServerSocket.accept();
                        System.out.println("长连接客户端: " + longConnectionClientSocket);
                        longConnectionExecutor.execute(new ServerHandlerLongConnection(longConnectionClientSocket));
                        System.out.println("长连接客户端已连接");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            shortConnectionThread.start();
            longConnectionThread.start();

            shortConnectionThread.join();
            longConnectionThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            shortConnectionExecutor.shutdown();
            longConnectionExecutor.shutdown();
        }
    }
}

