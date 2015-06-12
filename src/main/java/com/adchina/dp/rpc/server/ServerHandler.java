package com.adchina.dp.rpc.server;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adchina.dp.rpc.common.model.Request;
import com.adchina.dp.rpc.common.model.Respose;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<Request> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);
    
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
        LOGGER.debug("start handler rpc request, requestId:" + request.getRequestId());
        
        String interfaceName = request.getInterfaceName();
        String version = request.getVersion();

        String serviceKey;
        if (StringUtils.isNotEmpty(version)) {
            serviceKey = interfaceName + "-" + version;
        } else {
            serviceKey = interfaceName;
        }

        Object bean = handlerMap.get(serviceKey);
        if (bean == null) {
            throw new RuntimeException(String.format("can't find service:", serviceKey));
        }

        Method method = bean.getClass().getMethod(request.getMethodName(), request.getParamTypes());
        Object result = method.invoke(bean, request.getParams());

        LOGGER.debug("success handler rpc request, requestId:" + request.getRequestId());
        
        return result;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        LOGGER.error("handler error", cause);
        ctx.close();
    }

}
