package org.onosproject.roadm;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
class ser{
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void receive(int port){
        try {
            ServerSocket serverSocket = new ServerSocket(port); // 监听指定的端口
            while (true) {
                Socket socket = serverSocket.accept(); // 阻塞等待客户端连接
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String info = null;
                while ((info = br.readLine()) != null) { // 阻塞等待接收客户端发送的消息
                    log.info("receive message is {}", info);
                }
                socket.shutdownInput();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
