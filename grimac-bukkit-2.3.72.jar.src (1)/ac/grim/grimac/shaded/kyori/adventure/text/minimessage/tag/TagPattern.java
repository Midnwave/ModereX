package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag;

import ac.grim.grimac.shaded.intellij.lang.annotations.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
@Pattern("[!?#]?[a-z0-9_-]*")
public @interface TagPattern {}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\TagPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */