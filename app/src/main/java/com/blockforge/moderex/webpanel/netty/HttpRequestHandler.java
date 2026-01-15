package com.blockforge.moderex.webpanel.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface HttpRequestHandler {

    void handleRequest(ChannelHandlerContext ctx, String method, String path, String headers, ByteBuf body);

    void handleWebSocketUpgrade(ChannelHandlerContext ctx, String path, String headers, String wsKey);
}
