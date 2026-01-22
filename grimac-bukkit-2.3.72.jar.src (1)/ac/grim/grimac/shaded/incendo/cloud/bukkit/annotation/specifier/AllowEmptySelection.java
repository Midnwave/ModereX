package ac.grim.grimac.shaded.incendo.cloud.bukkit.annotation.specifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apiguardian.api.API;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@API(status = API.Status.STABLE, since = "1.8.0")
public @interface AllowEmptySelection {
  boolean value() default true;
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\annotation\specifier\AllowEmptySelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */