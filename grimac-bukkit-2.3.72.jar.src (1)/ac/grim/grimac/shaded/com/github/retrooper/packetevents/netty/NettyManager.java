package ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufAllocationOperator;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufOperator;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel.ChannelOperator;

public interface NettyManager {
  ChannelOperator getChannelOperator();
  
  ByteBufOperator getByteBufOperator();
  
  ByteBufAllocationOperator getByteBufAllocationOperator();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\netty\NettyManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */