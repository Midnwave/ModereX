package com.blockforge.moderex.webpanel.netty;

import com.blockforge.moderex.ModereX;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionNettyInjector implements NettyInjector {

    private ModereX plugin;
    private HttpRequestHandler handler;
    private boolean injected = false;
    private List<ChannelFuture> channelFutures;
    private final List<Channel> injectedChannels = new ArrayList<>();

    @Override
    public boolean inject(ModereX plugin, HttpRequestHandler httpHandler) {
        this.plugin = plugin;
        this.handler = httpHandler;

        try {
            Object serverConnection = getServerConnection();
            if (serverConnection == null) {
                plugin.logDebug("[Netty] Could not find ServerConnection");
                return false;
            }

            // Find the list of channel futures
            channelFutures = findChannelFutures(serverConnection);
            if (channelFutures == null || channelFutures.isEmpty()) {
                plugin.logDebug("[Netty] Could not find channel futures");
                return false;
            }

            // Inject into each channel's pipeline
            int successCount = 0;
            for (ChannelFuture future : channelFutures) {
                if (injectIntoChannel(future.channel())) {
                    successCount++;
                }
            }

            if (successCount > 0) {
                injected = true;
                plugin.getLogger().info("[Netty] Successfully injected into " + successCount + " channel(s) using reflection");
                return true;
            }

            plugin.logDebug("[Netty] Failed to inject into any channels");
            return false;

        } catch (Exception e) {
            plugin.logDebug("[Netty] Failed to inject using reflection: " + e.getMessage());
            return false;
        }
    }

    private Object getServerConnection() {
        try {
            Object server = plugin.getServer();

            // Get the CraftServer's handle (MinecraftServer)
            Method getServerMethod = server.getClass().getMethod("getServer");
            Object minecraftServer = getServerMethod.invoke(server);

            // Modern Paper (1.20.5+) - Try getConnection method first
            try {
                Method getConnectionMethod = minecraftServer.getClass().getMethod("getConnection");
                Object connection = getConnectionMethod.invoke(minecraftServer);
                if (connection != null) {
                    plugin.logDebug("[Netty] Found ServerConnection via getConnection()");
                    return connection;
                }
            } catch (NoSuchMethodException ignored) {}

            // Fallback: Search through fields
            Class<?> clazz = minecraftServer.getClass();
            while (clazz != null && clazz != Object.class) {
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(minecraftServer);
                        if (value != null) {
                            String className = value.getClass().getSimpleName();
                            if (className.equals("ServerConnection") ||
                                className.equals("ServerConnectionListener") ||
                                className.contains("Connection")) {
                                plugin.logDebug("[Netty] Found ServerConnection via field: " + field.getName());
                                return value;
                            }
                        }
                    } catch (Exception ignored) {}
                }
                clazz = clazz.getSuperclass();
            }
        } catch (Exception e) {
            plugin.logDebug("[Netty] Error getting ServerConnection: " + e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<ChannelFuture> findChannelFutures(Object serverConnection) {
        try {
            // Search all fields for List<ChannelFuture>
            Class<?> clazz = serverConnection.getClass();
            while (clazz != null && clazz != Object.class) {
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(serverConnection);
                        if (value instanceof List) {
                            List<?> list = (List<?>) value;
                            if (!list.isEmpty() && list.get(0) instanceof ChannelFuture) {
                                plugin.logDebug("[Netty] Found channel futures in field: " + field.getName());
                                return (List<ChannelFuture>) list;
                            }
                        }
                    } catch (Exception ignored) {}
                }
                clazz = clazz.getSuperclass();
            }

            // Alternative: look for "channels" or "endpoints" field names
            clazz = serverConnection.getClass();
            while (clazz != null && clazz != Object.class) {
                for (Field field : clazz.getDeclaredFields()) {
                    String fieldName = field.getName().toLowerCase();
                    if (fieldName.contains("channel") || fieldName.contains("endpoint") || fieldName.contains("future")) {
                        field.setAccessible(true);
                        try {
                            Object value = field.get(serverConnection);
                            if (value instanceof List) {
                                plugin.logDebug("[Netty] Found potential channel list in: " + field.getName());
                                return (List<ChannelFuture>) value;
                            }
                        } catch (Exception ignored) {}
                    }
                }
                clazz = clazz.getSuperclass();
            }
        } catch (Exception e) {
            plugin.logDebug("[Netty] Error finding channel futures: " + e.getMessage());
        }
        return null;
    }

    private boolean injectIntoChannel(Channel channel) {
        try {
            ChannelPipeline pipeline = channel.pipeline();

            // Method 1: Add our handler directly to existing pipeline at the front
            // This works for server bootstrap channels
            if (pipeline.get("moderex_http_detector") == null) {
                // Find a safe position to inject (after encoder if present)
                ChannelHandler first = pipeline.first();
                if (first != null) {
                    pipeline.addFirst("moderex_http_detector", new HttpDetectorHandler(handler));
                    injectedChannels.add(channel);
                    plugin.logDebug("[Netty] Injected HTTP detector into channel pipeline");
                    return true;
                }
            }

            // Method 2: If it's a ChannelInitializer, wrap it
            ChannelHandler existingHandler = pipeline.first();
            if (existingHandler instanceof ChannelInitializer) {
                ChannelInitializer<?> oldInit = (ChannelInitializer<?>) existingHandler;

                pipeline.replace(existingHandler, "moderex_wrapper", new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        // Call original initializer via reflection
                        try {
                            Method initMethod = ChannelInitializer.class.getDeclaredMethod("initChannel", Channel.class);
                            initMethod.setAccessible(true);
                            initMethod.invoke(oldInit, ch);
                        } catch (Exception e) {
                            plugin.logDebug("[Netty] Error calling original initializer: " + e.getMessage());
                        }

                        // Add our HTTP detector at the front
                        if (ch.pipeline().get("moderex_http_detector") == null) {
                            ch.pipeline().addFirst("moderex_http_detector", new HttpDetectorHandler(handler));
                        }
                    }
                });
                injectedChannels.add(channel);
                plugin.logDebug("[Netty] Wrapped ChannelInitializer with HTTP detector");
                return true;
            }

        } catch (Exception e) {
            plugin.logDebug("[Netty] Error injecting into channel: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean remove() {
        if (!injected) return false;

        try {
            int removedCount = 0;
            for (Channel channel : injectedChannels) {
                try {
                    ChannelPipeline pipeline = channel.pipeline();

                    // Remove our handler
                    if (pipeline.get("moderex_http_detector") != null) {
                        pipeline.remove("moderex_http_detector");
                        removedCount++;
                    }

                    // Remove wrapper if present
                    if (pipeline.get("moderex_wrapper") != null) {
                        pipeline.remove("moderex_wrapper");
                    }
                } catch (Exception ignored) {}
            }

            injectedChannels.clear();
            injected = false;
            plugin.getLogger().info("[Netty] Removed reflection injection from " + removedCount + " channel(s)");
            return true;
        } catch (Exception e) {
            plugin.logDebug("[Netty] Failed to remove reflection injection: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isInjected() {
        return injected;
    }

    @Override
    public String getName() {
        return "Reflection";
    }
}
