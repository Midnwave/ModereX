package ac.grim.grimac.shaded.kyori.adventure.util;

import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PACKAGE, ElementType.ANNOTATION_TYPE})
@Internal
public @interface PlatformAPI {}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventur\\util\PlatformAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */