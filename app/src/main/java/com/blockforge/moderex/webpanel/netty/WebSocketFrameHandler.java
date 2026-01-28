package com.blockforge.moderex.webpanel.netty;

import com.blockforge.moderex.ModereX;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

/**
 * Handles WebSocket frames after the HTTP upgrade handshake.
 * Bridges WebSocket communication to the HybridPanelServer.
 */
public class WebSocketFrameHandler extends ChannelInboundHandlerAdapter {

    private static final String WS_MAGIC_STRING = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    private final ModereX plugin;
    private final String connectionId;
    private ChannelHandlerContext channelCtx;
    private boolean closed = false;

    public WebSocketFrameHandler(ModereX plugin) {
        this.plugin = plugin;
        this.connectionId = UUID.randomUUID().toString();
    }

    /**
     * Performs the WebSocket handshake and sends 101 Switching Protocols response.
     */
    public static void sendUpgradeResponse(ChannelHandlerContext ctx, String wsKey) throws Exception {
        String acceptKey = Base64.getEncoder().encodeToString(
                MessageDigest.getInstance("SHA-1")
                        .digest((wsKey + WS_MAGIC_STRING).getBytes(StandardCharsets.UTF_8))
        );

        String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Accept: " + acceptKey + "\r\n" +
                "\r\n";

        ByteBuf buf = Unpooled.copiedBuffer(response.getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(buf);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channelCtx = ctx;
        // Register with the panel server
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().registerSamePortConnection(connectionId, this);
            plugin.logDebug("[SamePort WS] Connection registered: " + connectionId);
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            ctx.fireChannelRead(msg);
            return;
        }

        ByteBuf buf = (ByteBuf) msg;
        try {
            while (buf.isReadable()) {
                if (!processFrame(ctx, buf)) {
                    break;
                }
            }
        } finally {
            ReferenceCountUtil.release(buf);
        }
    }

    private boolean processFrame(ChannelHandlerContext ctx, ByteBuf buf) {
        if (buf.readableBytes() < 2) return false;

        buf.markReaderIndex();

        byte b0 = buf.readByte();
        byte b1 = buf.readByte();

        int opcode = b0 & 0x0F;
        boolean masked = (b1 & 0x80) != 0;
        int payloadLen = b1 & 0x7F;

        // Extended payload length
        if (payloadLen == 126) {
            if (buf.readableBytes() < 2) {
                buf.resetReaderIndex();
                return false;
            }
            payloadLen = buf.readUnsignedShort();
        } else if (payloadLen == 127) {
            if (buf.readableBytes() < 8) {
                buf.resetReaderIndex();
                return false;
            }
            payloadLen = (int) buf.readLong();
        }

        // Masking key
        byte[] maskKey = null;
        if (masked) {
            if (buf.readableBytes() < 4) {
                buf.resetReaderIndex();
                return false;
            }
            maskKey = new byte[4];
            buf.readBytes(maskKey);
        }

        // Payload
        if (buf.readableBytes() < payloadLen) {
            buf.resetReaderIndex();
            return false;
        }

        byte[] payload = new byte[payloadLen];
        buf.readBytes(payload);

        // Unmask if needed
        if (masked && maskKey != null) {
            for (int i = 0; i < payload.length; i++) {
                payload[i] ^= maskKey[i % 4];
            }
        }

        // Handle frame by opcode
        switch (opcode) {
            case 0x1 -> { // Text frame
                String message = new String(payload, StandardCharsets.UTF_8);
                handleTextMessage(message);
            }
            case 0x2 -> { // Binary frame
                // Binary messages not used
            }
            case 0x8 -> { // Close frame
                handleClose(ctx);
            }
            case 0x9 -> { // Ping
                sendPong(ctx, payload);
            }
            case 0xA -> { // Pong
                // Ignore pong frames
            }
        }

        return true;
    }

    private void handleTextMessage(String message) {
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().handleSamePortMessage(connectionId, message);
        }
    }

    private void handleClose(ChannelHandlerContext ctx) {
        if (!closed) {
            closed = true;
            sendCloseFrame(ctx);
            unregister();
        }
    }

    private void sendCloseFrame(ChannelHandlerContext ctx) {
        ByteBuf buf = ctx.alloc().buffer(2);
        buf.writeByte(0x88); // FIN + Close opcode
        buf.writeByte(0x00); // No payload
        ctx.writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendPong(ChannelHandlerContext ctx, byte[] payload) {
        ByteBuf buf = ctx.alloc().buffer(2 + payload.length);
        buf.writeByte(0x8A); // FIN + Pong opcode
        buf.writeByte(payload.length);
        buf.writeBytes(payload);
        ctx.writeAndFlush(buf);
    }

    /**
     * Sends a text frame to the client.
     */
    public void send(String message) {
        if (channelCtx == null || !channelCtx.channel().isActive()) return;

        byte[] payload = message.getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = channelCtx.alloc().buffer(10 + payload.length);

        buf.writeByte(0x81); // FIN + Text opcode

        // Payload length (server doesn't mask)
        if (payload.length < 126) {
            buf.writeByte(payload.length);
        } else if (payload.length < 65536) {
            buf.writeByte(126);
            buf.writeShort(payload.length);
        } else {
            buf.writeByte(127);
            buf.writeLong(payload.length);
        }

        buf.writeBytes(payload);
        channelCtx.writeAndFlush(buf);
    }

    public void close() {
        if (channelCtx != null && channelCtx.channel().isActive()) {
            handleClose(channelCtx);
        }
    }

    public String getConnectionId() {
        return connectionId;
    }

    public String getRemoteAddress() {
        if (channelCtx != null && channelCtx.channel().remoteAddress() != null) {
            return channelCtx.channel().remoteAddress().toString();
        }
        return "unknown";
    }

    private void unregister() {
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().unregisterSamePortConnection(connectionId);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        unregister();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        plugin.logDebug("[SamePort WS] Error: " + cause.getMessage());
        // Only close for fatal errors (IO exceptions, broken pipe, etc.)
        // Don't close for application-level exceptions that were already handled
        if (cause instanceof java.io.IOException || cause instanceof io.netty.channel.ChannelException) {
            unregister();
            ctx.close();
        }
        // For other exceptions, just log them - they've already been handled by the message processor
    }
}
