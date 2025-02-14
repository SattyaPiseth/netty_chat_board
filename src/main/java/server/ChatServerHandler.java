package server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sattya
 * create at 2/13/2025 11:21 PM
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {
    private static final ConcurrentHashMap<Channel,String> clients = new ConcurrentHashMap<>();
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        clients.put(ctx.channel(), "User-"+ctx.channel().id());
        broadcastMessage("SERVER","New user joined the chat: "+clients.get(ctx.channel()));

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg){
        String sender = clients.get(ctx.channel());
        broadcastMessage(sender,msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
       String user = clients.remove(ctx.channel());
       broadcastMessage("SERVER",user+" left the chat.");
    }

    private void broadcastMessage(String sender, String message){
        for (Channel channel : clients.keySet()){
            channel.writeAndFlush(sender + ": "+message+ "\n");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
