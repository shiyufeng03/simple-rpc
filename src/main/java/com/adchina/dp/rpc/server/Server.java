package com.adchina.dp.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.adchina.dp.rpc.common.codec.RpcDecode;
import com.adchina.dp.rpc.common.codec.RpcEncoder;
import com.adchina.dp.rpc.common.model.Request;
import com.adchina.dp.rpc.common.model.Respose;
import com.adchina.dp.rpc.registy.ServiceRegisty;

public class Server implements ApplicationContextAware, InitializingBean{

    private String address;
    private ServiceRegisty registy;
    private Map<String, Object> handlerMap = new HashMap<String, Object>();
    
    public Server(String address){
        this(address, null);
    }
    
    public Server(String address, ServiceRegisty registy){
        this.address = address;
        this.registy = registy;
    }
    
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline line = ch.pipeline();
                    line.addLast(new RpcDecode(Request.class));
                    line.addLast(new RpcEncoder(Respose.class));
                    line.addLast(new ServerHandler(handlerMap));
                }
            });
            
            bootstrap.option(ChannelOption.SO_BACKLOG, 512);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            
            String[] val = StringUtils.split(address, ":");
            int port = Integer.parseInt(val[1]);
            ChannelFuture future = bootstrap.bind(port).sync();
            
            if(registy != null){
                for(String interfaceName : handlerMap.keySet()){
                    registy.register(interfaceName, address);
                }
            }
            
            future.channel().closeFuture().sync();
        }finally{
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> javaBeans =  ctx.getBeansWithAnnotation(Service.class);
        if(javaBeans != null && javaBeans.size() > 0){
            for(Map.Entry<String, Object> entry : javaBeans.entrySet()){
                Service service = entry.getValue().getClass().getAnnotation(Service.class);
                String serviceName = service.value().getName();
                String serviceVersion = service.version();
                if(StringUtils.isNotEmpty(serviceVersion)){
                    serviceName = serviceName + "-" + serviceVersion;
                }
                
                System.out.println("find service:" + serviceName);
                this.handlerMap.put(serviceName, entry.getValue());
            }
        }
    }

}
