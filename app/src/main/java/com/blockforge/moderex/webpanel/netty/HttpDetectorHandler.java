package com.blockforge.moderex.webpanel.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpDetectorHandler extends ChannelInboundHandlerAdapter {

    private static final byte[] HTTP_METHODS = {
            'G', // GET
            'P', // POST, PUT, PATCH
            'H', // HEAD
            'D', // DELETE
            'O', // OPTIONS
            'C', // CONNECT
            'T'  // TRACE
    };

    private final HttpRequestHandler handler;
    private boolean detectionComplete = false;
    private boolean isHttp = false;

    // HTTP state
    private StringBuilder requestBuffer = new StringBuilder();
    private String method;
    private String path;
    private String headers;
    private int contentLength = 0;
    private ByteBuf bodyBuffer;
    private boolean headersComplete = false;

    public HttpDetectorHandler(HttpRequestHandler handler) {
        this.handler = handler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            ctx.fireChannelRead(msg);
            return;
        }

        ByteBuf buf = (ByteBuf) msg;

        // First packet detection
        if (!detectionComplete) {
            if (buf.readableBytes() < 1) {
                ctx.fireChannelRead(msg);
                return;
            }

            byte firstByte = buf.getByte(buf.readerIndex());
            isHttp = isHttpMethod(firstByte);
            detectionComplete = true;

            if (!isHttp) {
                // Not HTTP, remove ourselves and pass through to Minecraft
                ctx.pipeline().remove(this);
                ctx.fireChannelRead(msg);
                return;
            }
        }

        // Handle HTTP traffic
        if (isHttp) {
            handleHttpData(ctx, buf);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private boolean isHttpMethod(byte b) {
        for (byte method : HTTP_METHODS) {
            if (b == method) {
                return true;
            }
        }
        return false;
    }

    private void handleHttpData(ChannelHandlerContext ctx, ByteBuf buf) {
        try {
            if (!headersComplete) {
                // Read headers
                while (buf.isReadable()) {
                    byte b = buf.readByte();
                    requestBuffer.append((char) b);

                    // Check for end of headers (\r\n\r\n)
                    String current = requestBuffer.toString();
                    if (current.endsWith("\r\n\r\n")) {
                        headersComplete = true;
                        parseHeaders(current);
                        break;
                    }
                }
            }

            if (headersComplete) {
                // Read body if present
                if (contentLength > 0) {
                    if (bodyBuffer == null) {
                        bodyBuffer = ctx.alloc().buffer(contentLength);
                    }
                    int toRead = Math.min(buf.readableBytes(), contentLength - bodyBuffer.readableBytes());
                    bodyBuffer.writeBytes(buf, toRead);

                    if (bodyBuffer.readableBytes() >= contentLength) {
                        // Complete request
                        processRequest(ctx);
                    }
                } else {
                    // No body, process immediately
                    processRequest(ctx);
                }
            }
        } finally {
            ReferenceCountUtil.release(buf);
        }
    }

    private void parseHeaders(String headerStr) {
        String[] lines = headerStr.split("\r\n");
        if (lines.length > 0) {
            String[] requestLine = lines[0].split(" ");
            if (requestLine.length >= 2) {
                method = requestLine[0];
                path = requestLine[1];
            }
        }
        headers = headerStr;

        // Parse Content-Length
        for (String line : lines) {
            if (line.toLowerCase().startsWith("content-length:")) {
                try {
                    contentLength = Integer.parseInt(line.substring(15).trim());
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    private void processRequest(ChannelHandlerContext ctx) {
        // Check for WebSocket upgrade
        if (headers != null && headers.toLowerCase().contains("upgrade: websocket")) {
            String wsKey = extractHeader("sec-websocket-key");
            if (wsKey != null) {
                handler.handleWebSocketUpgrade(ctx, path, headers, wsKey);
                return;
            }
        }

        // Regular HTTP request
        handler.handleRequest(ctx, method, path, headers, bodyBuffer);

        // Reset state for keep-alive connections
        resetState();
    }

    private String extractHeader(String name) {
        if (headers == null) return null;
        String lowerHeaders = headers.toLowerCase();
        String lowerName = name.toLowerCase() + ":";
        int idx = lowerHeaders.indexOf(lowerName);
        if (idx < 0) return null;

        int start = idx + lowerName.length();
        int end = headers.indexOf("\r\n", start);
        if (end < 0) end = headers.length();

        return headers.substring(start, end).trim();
    }

    private void resetState() {
        requestBuffer = new StringBuilder();
        method = null;
        path = null;
        headers = null;
        contentLength = 0;
        headersComplete = false;
        if (bodyBuffer != null) {
            ReferenceCountUtil.release(bodyBuffer);
            bodyBuffer = null;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        resetState();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        resetState();
        ctx.close();
    }
}
