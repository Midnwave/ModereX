package ac.grim.grimac.shaded.geantyref;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public interface CaptureType extends Type {
  Type[] getUpperBounds();
  
  void setUpperBounds(Type[] paramArrayOfType);
  
  Type[] getLowerBounds();
  
  TypeVariable<?> getTypeVariable();
  
  WildcardType getWildcardType();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\CaptureType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */