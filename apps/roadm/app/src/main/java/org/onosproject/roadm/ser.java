/*
package org.onosproject.roadm;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

class Ser {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8888); // 监听指定的端口
        while (true) {
            Socket socket = serverSocket.accept(); // 阻塞等待客户端连接
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String info = null;
            while ((info = br.readLine()) != null) { // 阻塞等待接收客户端发送的消息
                System.out.println(info);
            }
            socket.shutdownInput();
        }
    }
}
*/