package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.storage;

public abstract class BaseStorage {
  public abstract long[] getData();
  
  public abstract int getBitsPerEntry();
  
  abstract int getSize();
  
  public abstract int get(int paramInt);
  
  public abstract void set(int paramInt1, int paramInt2);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\storage\BaseStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */