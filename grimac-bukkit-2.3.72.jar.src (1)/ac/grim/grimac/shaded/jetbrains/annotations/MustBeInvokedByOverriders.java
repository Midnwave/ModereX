package ac.grim.grimac.shaded.jetbrains.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface MustBeInvokedByOverriders {}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\jetbrains\annotations\MustBeInvokedByOverriders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */