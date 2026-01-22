package ac.grim.grimac.shaded.configuralize.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Option {
  String key();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\configuralize\mapping\Option.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */