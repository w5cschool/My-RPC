package github.w5cschool.remoting.transport.netty.client;

import github.w5cschool.extension.ExtensionLoader;
import github.w5cschool.remoting.dto.RpcRequest;
import github.w5cschool.remoting.dto.RpcResponse;
import github.w5cschool.remoting.transport.netty.codec.DefaultDecoder;
import github.w5cschool.remoting.transport.netty.codec.kyro.NettyKryoEncoder;
import github.w5cschool.serialize.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * initialize and close Bootstrap object
 *
 */
@Slf4j
public final class NettyClient {
    //启动引导组件
    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;

    private static boolean flag = false;

    // initialize resources such as EventLoopGroup, Bootstrap
    public NettyClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        Serializer kryoSerializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension("kyro");
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                //  The timeout period of the connection.
                //  If this time is exceeded or the connection cannot be established, the connection fails.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // If no data is sent to the server within 15 seconds, a heartbeat request is sent
                        ch.pipeline().addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS));
                        /*
                         config custom serialization codec
                         */
                        // RpcResponse -> ByteBuf
                        ch.pipeline().addLast(new DefaultDecoder(RpcResponse.class));
                        // ByteBuf -> RpcRequest
                        ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcRequest.class));
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }


    /**
     * connect server and get the channel ,so that you can send rpc message to server
     *
     * @param inetSocketAddress server address
     * @return the channel
     */
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {

        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
            bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                    completableFuture.complete(future.channel());
                    flag = true;
                } else {
                    throw new IllegalStateException();
                }
            });
            return completableFuture.get();
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

}
