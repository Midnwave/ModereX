package ac.grim.grimac.shaded.incendo.cloud.bukkit.annotation.specifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultNamespace {
  String value();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\annotation\specifier\DefaultNamespace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */