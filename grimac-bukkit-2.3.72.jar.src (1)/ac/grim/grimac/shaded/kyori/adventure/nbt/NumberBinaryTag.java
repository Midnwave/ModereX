package ac.grim.grimac.shaded.kyori.adventure.nbt;

import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;

public interface NumberBinaryTag extends BinaryTag {
  @NotNull
  BinaryTagType<? extends NumberBinaryTag> type();
  
  byte byteValue();
  
  double doubleValue();
  
  float floatValue();
  
  int intValue();
  
  long longValue();
  
  short shortValue();
  
  @NotNull
  Number numberValue();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\NumberBinaryTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */