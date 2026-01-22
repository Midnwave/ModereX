package ac.grim.grimac.checks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CheckData {
  String name() default "UNKNOWN";
  
  String alternativeName() default "UNKNOWN";
  
  String configName() default "DEFAULT";
  
  String description() default "No description provided";
  
  double decay() default 0.05D;
  
  double setback() default 25.0D;
  
  boolean experimental() default false;
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\CheckData.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */