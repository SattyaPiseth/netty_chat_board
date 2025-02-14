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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Sattya
 * Created on 2/13/2025 11:30 PM
 */
@AllArgsConstructor
public class ChatClient {
    private final String host;
    private final int port;

    public void start() throws InterruptedException {

        try(EventLoopGroup group = new NioEventLoopGroup();) {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new StringDecoder(), new StringEncoder(), new ChatClientHandler());
                        }
                    });

            Channel channel = bootstrap.connect(host, port).sync().channel();

            // Use try-with-resources for BufferedReader to ensure it is closed
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String message;
                while ((message = reader.readLine()) != null) {
                    if (message.equalsIgnoreCase("/exit")) {
                        System.out.println("Exiting chat...");
                        break;
                    }
                    channel.writeAndFlush(message + "\r\n");
                }
            }

            channel.close().sync();  // Gracefully close the connection
        } catch (IOException e) {
            throw new RuntimeException("Error reading user input", e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var host = PropertiesConfig.get("server.host");
        var port = PropertiesConfig.getInt("server.port");
        new ChatClient(host, port).start();
    }
}
