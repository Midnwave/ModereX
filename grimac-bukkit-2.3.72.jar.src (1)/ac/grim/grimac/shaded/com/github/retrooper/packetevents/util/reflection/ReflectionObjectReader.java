package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.reflection;

public interface ReflectionObjectReader {
  boolean readBoolean(int paramInt);
  
  byte readByte(int paramInt);
  
  short readShort(int paramInt);
  
  int readInt(int paramInt);
  
  long readLong(int paramInt);
  
  float readFloat(int paramInt);
  
  double readDouble(int paramInt);
  
  boolean[] readBooleanArray(int paramInt);
  
  byte[] readByteArray(int paramInt);
  
  short[] readShortArray(int paramInt);
  
  int[] readIntArray(int paramInt);
  
  long[] readLongArray(int paramInt);
  
  float[] readFloatArray(int paramInt);
  
  double[] readDoubleArray(int paramInt);
  
  String[] readStringArray(int paramInt);
  
  String readString(int paramInt);
  
  <T> T readObject(int paramInt, Class<? extends T> paramClass);
  
  <T> T[] readObjectArray(int paramInt, Class<? extends T> paramClass);
  
  Enum<?> readEnumConstant(int paramInt, Class<? extends Enum<?>> paramClass);
  
  Object readAnyObject(int paramInt);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\reflection\ReflectionObjectReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */