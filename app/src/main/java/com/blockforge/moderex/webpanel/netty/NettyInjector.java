package com.blockforge.moderex.webpanel.netty;

import com.blockforge.moderex.ModereX;

public interface NettyInjector {

    boolean inject(ModereX plugin, HttpRequestHandler httpHandler);

    boolean remove();

    boolean isInjected();

    String getName();
}
