package com.example.voip_app.util.udp;

import com.alibaba.fastjson.JSON;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;


public class NettyReceiverHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private ChannelHandlerContext channelHandlerContext;
    private FrameResultedCallback frameCallback;

    void setOnFrameCallback(FrameResultedCallback callback) {
        this.frameCallback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet)
            throws Exception {
        ByteBuf buf = packet.copy().content();
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String str = new String(req, "UTF-8");
        Message message = JSON.parseObject(str,Message.class);
        if (message.getMsgtype().equals(Message.MES_TYPE_VIDEO)){
            if (frameCallback !=null){
                frameCallback.onVideoData(message.getFrame());
            }
        }else if (message.getMsgtype().equals(Message.MES_TYPE_AUDIO)){
            if (frameCallback !=null){
                frameCallback.onAudioData(message.getFrame());
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channelHandlerContext = ctx;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);

    }

    public void sendData(String ip, int port, Object data, String type) {
        Message message = null;
        if (data instanceof byte[]) {
            message = new Message();
            message.setFrame((byte[]) data);
            message.setMsgtype(type);
            message.setTimestamp(System.currentTimeMillis());
        }else if (data instanceof String){
            message = new Message();
            message.setMsgBody((String) data);
            message.setMsgtype(type);
            message.setTimestamp(System.currentTimeMillis());
        }

        if (channelHandlerContext != null) {
            channelHandlerContext.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(JSON.toJSONString(message).getBytes()),
                    new InetSocketAddress(ip, port)));
        }

    }

    public interface FrameResultedCallback {
        void onVideoData(byte[] data);

        void onAudioData(byte[] data);
    }
}