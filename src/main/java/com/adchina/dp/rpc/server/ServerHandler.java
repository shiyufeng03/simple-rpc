package com.adchina.dp.rpc.server;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.adchina.dp.rpc.common.model.Request;
import com.adchina.dp.rpc.common.model.Respose;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<Request> {

    private Map<String, Object> handlerMap;

    public ServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, Request msg) throws Exception {
        Respose respose = new Respose();
        respose.setRequestId(msg.getRequestId());

        try {
            Object result = this.handler(msg);
            respose.setResult(result);
        } catch (Exception e) {
            respose.setExcption(e);
        }
        
        ctx.writeAndFlush(respose).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handler(Request request) throws Exception {
        String interfaceName = request.getInterfaceName();
        String version = request.getVersion();

        String key;
        if (StringUtils.isNotEmpty(version)) {
            key = interfaceName + "-" + version;
        } else {
            key = interfaceName;
        }

        Object bean = handlerMap.get(key);
        if (bean == null) {
            throw new RuntimeException(String.format("can't find service:", key));
        }

        Method method = bean.getClass().getMethod(request.getMethodName(), request.getParamTypes());
        Object result = method.invoke(bean, request.getParams());

        return result;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
