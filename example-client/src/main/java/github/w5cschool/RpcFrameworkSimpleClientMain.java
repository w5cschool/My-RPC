package github.w5cschool;

import github.w5cschool.entity.RpcServiceProperties;
import github.w5cschool.proxy.RpcClientProxy;
import github.w5cschool.remoting.transport.ClientTransport;
import github.w5cschool.remoting.transport.socket.SocketRpcClient;


public class RpcFrameworkSimpleClientMain {
    public static void main(String[] args) {
        ClientTransport clientTransport = new SocketRpcClient();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(clientTransport, rpcServiceProperties);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
}
