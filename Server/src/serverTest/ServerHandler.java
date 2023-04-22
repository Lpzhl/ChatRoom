package serverTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

// ServerHandler类实现了Runnable接口，允许在新线程中运行
public class ServerHandler implements Runnable {
    private Socket socket; // 与客户端通信的套接字
    private DatabaseConnection dbConnection;// 数据库连接对象

    public ServerHandler(Socket socket) {
        this.socket = socket;
        dbConnection = new DatabaseConnection();
    }
    // run方法是Runnable接口的实现，它在新线程中执行
    @Override
    public void run() {
        if (socket.isClosed()) {
            System.out.println("Socket is closed before entering ServerHandler");
        } else {
            System.out.println("Socket is open before entering ServerHandler");
        }
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            // 创建一个PrintWriter对象，用于向客户端发送响应
            out = new PrintWriter(socket.getOutputStream(), true);
            // 创建一个BufferedReader对象，用于接收客户端发送的请求
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 读取客户端发送的请求
            String request = in.readLine();
            System.out.println("客户端请求信息："+request);

            if (request == null) {
                System.out.println("Client sent an empty request or disconnected");
                return;
            }

            // 将请求分割为请求的各个部分
            String[] requestParts = request.split(":");

            // 根据请求的第一个部分，判断是哪种请求并执行相应操作
            switch (requestParts[0]) {
                case "register"://处理注册账号的的请求
                    handleRegister(out, requestParts);
                    break;
                case "login"://处理登入的请求
                    handleLogin(out, requestParts);
                    break;
                case "email_verification"://发送验证码请求
                    handleEmailVerification(out, requestParts);
                    break;
                case "emailLogin": // 处理邮箱登录的请求
                    handleEmailLogin(out, requestParts);
                    break;
                case"resetPassword"://处理找回密码的请求
                    handleResetPassword(out,requestParts);
                    break;
                default:
                    out.println("error");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭输出流
            if (out != null) {
                out.close();
            }
            // 关闭输入流
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                // 关闭套接字
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*，因为它在所有处理请求的代码执行完毕后检查socket的状态。
        这段代码的作用是在处理客户端请求之前检查socket是否已关闭。
        如果socket已经关闭，它会打印“Socket is closed before entering ServerHandler”，否则会打印“Socket is open before entering ServerHandler”。
        */

    }


    // 处理注册请求
    private void handleRegister(PrintWriter out, String[] requestParts) {
        if (requestParts.length != 5) {
            out.println("error");
            return;
        }
        String username= requestParts[1];
        String password= requestParts[2];
        String email= requestParts[3];
        String nickname= requestParts[4];
        System.out.println(email+" "+nickname+" "+password+""+username);


        // 如果用户名已存在，返回"duplicate"，否则注册用户并返回"success"
        if (dbConnection.emailExists(email)) {
            out.println("duplicate");
        } else {
            dbConnection.registerUser(username, password,email,nickname);
            out.println("success");
        }
    }

    // 处理登录请求
    private void handleLogin(PrintWriter out, String[] requestParts) {
        // 检查请求是否包含正确数量的参数
        if (requestParts.length != 3) {
            out.println("error");
            return;
        }

        String username = requestParts[1];
        String password = requestParts[2];

        // 如果用户名和密码匹配，返回"success"，否则返回"fail"
        if (dbConnection.checkLogin(username, password)) {
            out.println("success");
        } else {
            out.println("fail");
        }
    }
    //处理发送验证码请求
    private void handleEmailVerification(PrintWriter out, String[] requestParts) {
        if (requestParts.length != 2) {
            out.println("error");
            return;
        }

        String email = requestParts[1];
        String generatedCode = EmailVca.generateRandomCode();
        EmailVca emailVca = new EmailVca(email, generatedCode);

        // 使用FutureTask代替Task
        FutureTask<Void> emailTask = new FutureTask<>(emailVca);

        // 创建一个新的线程来执行emailTask
        Thread emailThread = new Thread(emailTask);
        emailThread.setDaemon(true);
        emailThread.start();

        // 等待线程执行完成，以便在发生异常时处理
        try {
            emailThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 检查是否发生异常
        try {
            emailTask.get();
            out.println(generatedCode);
        } catch (ExecutionException e) {
            e.getCause().printStackTrace();
            out.println("fail");
        } catch (InterruptedException e) {
            e.printStackTrace();
            out.println("fail");
        }
    }


    // 处理邮箱登录请求的方法
    private void handleEmailLogin(PrintWriter out, String[] requestParts) {
        if (requestParts.length != 2) {
            out.println("error");
            System.out.println("请求格式错误");
            return;
        }
        String email = requestParts[1];
        // 如果邮箱和验证码匹配，返回"success"，否则返回"fail"
        if (dbConnection.emailExists(email)) {
            out.println("success");
            System.out.println("登录成功");
        } else {
            out.println("fail");
            System.out.println("电子邮件不存在,没有绑定账号");
        }
    }

    //处理找回密码请求
    private void handleResetPassword(PrintWriter out, String[] requestParts) {
        if (requestParts.length != 3) {
            out.println("error");
            System.out.println("请求格式错误!");
            return;
        }

        String username = requestParts[1];
        String newPassword = requestParts[2];

        if (dbConnection.userExists(username)) {
            if (dbConnection.updateUserPassword(username, newPassword)) {
                out.println("success");
                System.out.println("用户"+username+"修改密码成功!");
            } else {
                out.println("failure");
                System.out.println("用户"+username+"修改密码失败!");
            }
        } else {
            out.println("user_not_found");
            System.out.println("该"+username+"用户未注册！");
        }
    }


}



    /*
    String[] requestParts = request.split(":"); 这行代码将客户端发来的请求字符串根据冒号 (:) 进行分割，并将结果存储在一个字符串数组 requestParts 中。
    split 方法是 Java 中 String 类的一个方法，它使用给定的分隔符（在这里是冒号）将原始字符串分割成多个子字符串。
例如，如果客户端发送的请求是 "login:user123:password456"，那么 request.split(":") 将返回一个包含三个元素的字符串数组：{"login", "user123", "password456"}。
requestParts.length != 3 这个条件检查字符串数组 requestParts 的长度是否不等于 3。这是为了确保客户端发送的请求包含正确数量的参数。在这个示例中，一个有效的请求应包含三部分：操作类型（例如 "login" 或 "register"）、用户名和密码。
如果 requestParts 数组的长度不等于 3，那么请求就被认为是无效的，需要向客户端返回一个 "error" 响应。
换句话说，requestParts.length != 3 这个条件用于检查客户端请求是否包含三个由冒号分隔的部分，以确保请求是有效的。如果请求无效，代码将返回一个 "error" 响应并终止当前处理方法。
     */