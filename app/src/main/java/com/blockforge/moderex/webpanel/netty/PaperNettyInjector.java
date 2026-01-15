package com.blockforge.moderex.webpanel.netty;

import com.blockforge.moderex.ModereX;
import io.netty.channel.Channel;

import java.lang.reflect.Method;

public class PaperNettyInjector implements NettyInjector {

    private ModereX plugin;
    private HttpRequestHandler handler;
    private Object listener;
    private Object registeredKey;
    private boolean injected = false;

    @Override
    public boolean inject(ModereX plugin, HttpRequestHandler httpHandler) {
        this.plugin = plugin;
        this.handler = httpHandler;

        try {
            // Try Paper's official ChannelInitializeListener API
            Class<?> listenerClass = Class.forName("io.papermc.paper.network.ChannelInitializeListener");
            Class<?> holderClass = Class.forName("io.papermc.paper.network.ChannelInitializeListenerHolder");

            // Create our listener that adds the HTTP detector
            listener = java.lang.reflect.Proxy.newProxyInstance(
                    listenerClass.getClassLoader(),
                    new Class<?>[]{listenerClass},
                    (proxy, method, args) -> {
                        if ("afterInitChannel".equals(method.getName()) && args.length == 1) {
                            Channel channel = (Channel) args[0];
                            injectChannel(channel);
                        }
                        return null;
                    }
            );

            // Try different method signatures for different Paper versions
            org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(plugin, "http_injector");

            // Try 1: addListener(NamespacedKey, ChannelInitializeListener) - older Paper
            try {
                Method addMethod = holderClass.getMethod("addListener", org.bukkit.NamespacedKey.class, listenerClass);
                addMethod.invoke(null, key, listener);
                registeredKey = key;
                injected = true;
                plugin.getLogger().info("[Netty] Successfully injected using Paper API (v1)");
                return true;
            } catch (NoSuchMethodException ignored) {}

            // Try 2: addListener(Key, ChannelInitializeListener) - newer Paper using Adventure Key
            try {
                Class<?> adventureKeyClass = Class.forName("net.kyori.adventure.key.Key");
                Method addMethod = holderClass.getMethod("addListener", adventureKeyClass, listenerClass);
                // NamespacedKey implements Key, so we can use it directly
                addMethod.invoke(null, key, listener);
                registeredKey = key;
                injected = true;
                plugin.getLogger().info("[Netty] Successfully injected using Paper API (v2)");
                return true;
            } catch (NoSuchMethodException | ClassNotFoundException ignored) {}

            // Try 3: Check for static methods with different patterns
            for (Method m : holderClass.getMethods()) {
                if (m.getName().equals("addListener") && m.getParameterCount() == 2) {
                    try {
                        m.invoke(null, key, listener);
                        registeredKey = key;
                        injected = true;
                        plugin.getLogger().info("[Netty] Successfully injected using Paper API (dynamic)");
                        return true;
                    } catch (Exception ignored) {}
                }
            }

            // Paper API found but method signature unknown
            plugin.logDebug("[Netty] Paper API found but incompatible version");
            return false;

        } catch (ClassNotFoundException e) {
            plugin.logDebug("[Netty] Paper injection API not available");
            return false;
        } catch (Exception e) {
            plugin.logDebug("[Netty] Failed to inject using Paper API: " + e.getMessage());
            return false;
        }
    }

    private void injectChannel(Channel channel) {
        try {
            // Add our HTTP detector at the beginning of the pipeline
            if (channel.pipeline().get("moderex_http_detector") == null) {
                channel.pipeline().addFirst("moderex_http_detector", new HttpDetectorHandler(handler));
            }
        } catch (Exception e) {
            plugin.logDebug("[Netty] Failed to inject channel: " + e.getMessage());
        }
    }

    @Override
    public boolean remove() {
        if (!injected || plugin == null || registeredKey == null) return false;

        try {
            Class<?> holderClass = Class.forName("io.papermc.paper.network.ChannelInitializeListenerHolder");

            // Try different removal methods
            for (Method m : holderClass.getMethods()) {
                if (m.getName().equals("removeListener") && m.getParameterCount() == 1) {
                    try {
                        m.invoke(null, registeredKey);
                        injected = false;
                        plugin.getLogger().info("[Netty] Removed Paper API injection");
                        return true;
                    } catch (Exception ignored) {}
                }
            }

            return false;
        } catch (Exception e) {
            plugin.logDebug("[Netty] Failed to remove Paper API injection: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isInjected() {
        return injected;
    }

    @Override
    public String getName() {
        return "Paper API";
    }
}
