package com.adchina.dp.rpc.common.codec;

import com.adchina.dp.rpc.common.SerializationHelper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 包括request和response的encoder
 * 
 * @author Steven.Shi
 *
 */
public class RpcEncoder extends MessageToByteEncoder{

    private Class<?> genericClass;
    
    public RpcEncoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }
    
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(genericClass.isInstance(msg)){
            byte[] data = SerializationHelper.serialize(msg);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }

}
