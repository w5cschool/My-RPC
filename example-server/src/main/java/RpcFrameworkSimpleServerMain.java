import github.w5cschool.HelloService;
import github.w5cschool.entity.RpcServiceProperties;
import github.w5cschool.remoting.transport.socket.SocketRpcServer;
import github.w5cschool.serviceimpl.HelloServiceImpl;


public class RpcFrameworkSimpleServerMain {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        socketRpcServer.registerService(helloService, rpcServiceProperties);
        socketRpcServer.start();
    }
}
