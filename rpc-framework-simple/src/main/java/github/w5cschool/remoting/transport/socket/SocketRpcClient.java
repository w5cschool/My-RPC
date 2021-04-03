package github.w5cschool.remoting.transport.socket;

import github.w5cschool.entity.RpcServiceProperties;
import github.w5cschool.exception.RpcException;
import github.w5cschool.extension.ExtensionLoader;
import github.w5cschool.registry.ServiceDiscovery;
import github.w5cschool.remoting.dto.RpcRequest;
import github.w5cschool.remoting.transport.ClientTransport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 基于 Socket 传输 RpcRequest
 *
 * 1、创建Socket对象并且连接指定的服务器的ip地址和端口号port：socket.connect(inetSocketAddress)
 * 2、建立连接后，通过输出流 向服务器端发送请求信息
 * 3、通过输入流获取服务器响应的信息
 * 4、关闭相关资源
 */
@AllArgsConstructor
@Slf4j
public class SocketRpcClient implements ClientTransport {
    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // build rpc service name by rpcRequest
        String rpcServiceName = RpcServiceProperties.builder().serviceName(rpcRequest.getInterfaceName())
                .group(rpcRequest.getGroup()).version(rpcRequest.getVersion()).build().toRpcServiceName();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcServiceName);
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // Send data to the server through the output stream
            objectOutputStream.writeObject(rpcRequest);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // Read RpcResponse from the input stream
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException("调用服务失败:", e);
        }
    }
}
