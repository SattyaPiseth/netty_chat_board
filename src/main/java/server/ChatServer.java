package server;

import config.PropertiesConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.AllArgsConstructor;

/**
 * @author Sattya
 * create at 2/13/2025 10:44 PM
 */
@AllArgsConstructor
public class ChatServer {
    private final String host;
    private final int port;

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    new ChatServerHandler()
                                    );
                        }
                    });
            ChannelFuture future = bootstrap.bind(host,port).sync();
            System.out.println("Chat server started on "+host);
            System.out.println("Chat server started on port " + port);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var host = PropertiesConfig.get("server.host");
        var port = PropertiesConfig.getInt("server.port");
        new ChatServer(host,port).start();
    }
}
