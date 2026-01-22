package ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel;

import java.net.SocketAddress;
import java.util.List;

public interface ChannelOperator {
  SocketAddress remoteAddress(Object paramObject);
  
  SocketAddress localAddress(Object paramObject);
  
  boolean isOpen(Object paramObject);
  
  Object close(Object paramObject);
  
  Object write(Object paramObject1, Object paramObject2);
  
  Object flush(Object paramObject);
  
  Object writeAndFlush(Object paramObject1, Object paramObject2);
  
  Object fireChannelRead(Object paramObject1, Object paramObject2);
  
  Object writeInContext(Object paramObject1, String paramString, Object paramObject2);
  
  Object flushInContext(Object paramObject, String paramString);
  
  Object writeAndFlushInContext(Object paramObject1, String paramString, Object paramObject2);
  
  Object fireChannelReadInContext(Object paramObject1, String paramString, Object paramObject2);
  
  List<String> pipelineHandlerNames(Object paramObject);
  
  Object getPipelineHandler(Object paramObject, String paramString);
  
  Object getPipelineContext(Object paramObject, String paramString);
  
  Object getPipeline(Object paramObject);
  
  void runInEventLoop(Object paramObject, Runnable paramRunnable);
  
  Object pooledByteBuf(Object paramObject);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\netty\channel\ChannelOperator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */