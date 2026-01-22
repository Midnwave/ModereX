package ac.grim.grimac.shaded.geantyref;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;

public interface AnnotatedCaptureType extends AnnotatedType {
  AnnotatedType[] getAnnotatedUpperBounds();
  
  AnnotatedType[] getAnnotatedLowerBounds();
  
  AnnotatedTypeVariable getAnnotatedTypeVariable();
  
  AnnotatedWildcardType getAnnotatedWildcardType();
  
  void setAnnotatedUpperBounds(AnnotatedType[] paramArrayOfAnnotatedType);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\AnnotatedCaptureType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */