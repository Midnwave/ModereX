package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.reflection;

public interface ReflectionObjectWriter {
  void writeBoolean(int paramInt, boolean paramBoolean);
  
  void writeByte(int paramInt, byte paramByte);
  
  void writeShort(int paramInt, short paramShort);
  
  void writeInt(int paramInt1, int paramInt2);
  
  void writeLong(int paramInt, long paramLong);
  
  void writeFloat(int paramInt, float paramFloat);
  
  void writeDouble(int paramInt, double paramDouble);
  
  void writeString(int paramInt, String paramString);
  
  void writeBooleanArray(int paramInt, boolean[] paramArrayOfboolean);
  
  void writeByteArray(int paramInt, byte[] paramArrayOfbyte);
  
  void writeShortArray(int paramInt, short[] paramArrayOfshort);
  
  void writeIntArray(int paramInt, int[] paramArrayOfint);
  
  void writeLongArray(int paramInt, long[] paramArrayOflong);
  
  void writeFloatArray(int paramInt, float[] paramArrayOffloat);
  
  void writeDoubleArray(int paramInt, double[] paramArrayOfdouble);
  
  void writeStringArray(int paramInt, String[] paramArrayOfString);
  
  void writeObject(int paramInt, Object paramObject);
  
  void writeAnyObject(int paramInt, Object paramObject);
  
  void writeEnumConstant(int paramInt, Enum<?> paramEnum);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\reflection\ReflectionObjectWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */