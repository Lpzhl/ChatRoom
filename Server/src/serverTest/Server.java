package serverTest;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server { // 定义一个名为Server的公共类


    public static void main(String[] args) { // 程序的主入口
        try (ServerSocket serverSocket = new ServerSocket(6000)) { // 使用try-with-resources语句创建一个在端口6000上监听的ServerSocket对象
            System.out.println("服务器启动，等待客户端连接..."); // 打印服务器启动信息
            while (true) { // 使用无限循环，使服务器持续监听客户端连接
                Socket clientSocket = null; // 定义一个Socket对象，用于表示客户端连接
                try {
                    clientSocket = serverSocket.accept(); // 当有客户端连接时，调用accept()方法接受客户端连接，返回一个表示客户端的Socket对象
                    System.out.println("客户端: " + clientSocket); // 打印客户端连接的信息
                    ServerHandler serverHandler = new ServerHandler(clientSocket); // 创建一个ServerHandler对象，传入客户端Socket作为参数
                    Thread thread = new Thread(serverHandler); // 使用ServerHandler对象创建一个新线程
                    thread.start(); // 启动线程，处理客户端连接
                    System.out.println("客户端已连接"); // 打



                    // 印客户端连接成功的信息
                } catch (IOException e) {
                    e.printStackTrace(); // 当捕获到IOException时，打印异常堆栈信息
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // 当捕获到IOException时，打印异常堆栈信息
        }
    }
}


/*
每当一个新的客户端连接到服务器时，程序会创建一个新的ServerHandler对象，并将表示客户端连接的Socket对象传递给它。
接着，程序创建一个新的Thread对象，将ServerHandler作为线程的执行目标。
最后，程序调用thread.start()方法启动线程，开始执行ServerHandler中的逻辑。
这样，服务器可以同时处理多个客户端连接，每个连接在独立的线程中运行，实现了并发。

 */