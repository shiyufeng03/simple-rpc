package com.adchina.dp.rpc.common.codec;

import java.util.List;

import com.adchina.dp.rpc.common.SerializationHelper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 这里是一个通用的编码模型，通过genericClass来区分要
 * 包括request和response的decoder
 * 
 * @author Steven.Shi
 *
 */
public class RpcDecoder extends ByteToMessageDecoder{
    private Class<?> genericClass;
    
    public RpcDecoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < 4){
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if(in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        
        Object obj = SerializationHelper.deserialize(data, genericClass);
        out.add(obj);
    }

}
