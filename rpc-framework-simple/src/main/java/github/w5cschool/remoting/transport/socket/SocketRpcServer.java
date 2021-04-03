package github.w5cschool.remoting.transport.socket;

import github.w5cschool.provider.ServiceProviderImpl;
import github.w5cschool.remoting.transport.netty.server.NettyServer;
import github.w5cschool.config.CustomShutdownHook;
import github.w5cschool.entity.RpcServiceProperties;
import github.w5cschool.factory.SingletonFactory;
import github.w5cschool.provider.ServiceProvider;
import github.w5cschool.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * server
 * 1、创建ServerSocket对象并绑定ip地址和端口号port：server.bind(new InetSocketAddress(host,port))
 * 2、通过accept()方法监听客户端请求信息
 * 3、建立连接后，通过输入流读取客户端发送的请求信息
 * 4、通过输出流想客户端发送响应信息
 * 5、关闭相关资源
 */

@Slf4j
public class SocketRpcServer {

    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;


    public SocketRpcServer() {
        threadPool = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        SingletonFactory.getInstance(ServiceProviderImpl.class);
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }


    public void registerService(Object service) {
        serviceProvider.publishService(service);
    }

    public void registerService(Object service, RpcServiceProperties rpcServiceProperties) {
        serviceProvider.publishService(service, rpcServiceProperties);
    }

    public void start() {
        //Creates a ServerSocket object and binds to a port
        try (ServerSocket server = new ServerSocket()) {
            String host = InetAddress.getLocalHost().getHostAddress();
            server.bind(new InetSocketAddress(host, NettyServer.PORT));
            CustomShutdownHook.getCustomShutdownHook().clearAll();
            Socket socket;
            //Listen for client requests through the accept() method
            while ((socket = server.accept()) != null) {
                log.info("client connected [{}]", socket.getInetAddress());
                threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("occur IOException:", e);
        }
    }

}
