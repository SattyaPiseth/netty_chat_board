package client;

import config.PropertiesConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.AllArgsConstructor;

import java.util.Scanner;

/**
 * @author Sattya
 * create at 2/13/2025 11:30 PM
 */
@AllArgsConstructor
public class ChatClient {
    private final String host;
    private final int port;

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new StringDecoder(),new StringEncoder(),new ChatClientHandler());
                        }
                    });
            Channel channel = bootstrap.connect(host,port).sync().channel();
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()){
                String message = scanner.nextLine();
                channel.writeAndFlush(message);
            }
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var host = PropertiesConfig.get("server.host");
        var port = PropertiesConfig.getInt("server.port");
        new ChatClient(host, port).start();
    }
}
