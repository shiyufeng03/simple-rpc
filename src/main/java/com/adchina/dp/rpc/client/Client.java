package com.adchina.dp.rpc.client;

import com.adchina.dp.rpc.common.codec.RpcDecode;
import com.adchina.dp.rpc.common.codec.RpcEncoder;
import com.adchina.dp.rpc.common.model.Request;
import com.adchina.dp.rpc.common.model.Respose;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client extends SimpleChannelInboundHandler<Respose>{
    private String host;
    private int port;
    
    private Respose respose;
    
    public Client(String host, int port){
        this.host = host;
        this.port = port;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Respose msg) throws Exception {
        this.respose = msg;
    }
    
    public Respose send(Request request) throws Exception{
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipe = ch.pipeline();
                    pipe.addLast(new RpcEncoder(Request.class));
                    pipe.addLast(new RpcDecode(Respose.class));
                    pipe.addLast(Client.this);
                }
            });
            
            ChannelFuture future = bootstrap.connect(host, port).sync();
            
            future.channel().closeFuture().sync();
            
            return respose;
        }finally{
            workerGroup.shutdownGracefully();
        }
    }

}
