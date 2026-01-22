package ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer;

public interface ByteBufAllocationOperator {
  Object wrappedBuffer(byte[] paramArrayOfbyte);
  
  Object copiedBuffer(byte[] paramArrayOfbyte);
  
  Object buffer();
  
  Object buffer(int paramInt);
  
  Object directBuffer();
  
  Object directBuffer(int paramInt);
  
  Object compositeBuffer();
  
  Object compositeBuffer(int paramInt);
  
  Object emptyBuffer();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\netty\buffer\ByteBufAllocationOperator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */