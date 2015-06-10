package com.adchina.dp.rpc.common.codec;

import com.adchina.dp.rpc.common.SerializationHelper;
import com.adchina.dp.rpc.common.model.Request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder<Request>{

    private Class<?> genericClass;
    
    public RpcEncoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }
    
    @Override
    protected void encode(ChannelHandlerContext ctx, Request msg, ByteBuf out) throws Exception {
        if(genericClass.isInstance(msg)){
            byte[] data = SerializationHelper.serialize(msg);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }

}
