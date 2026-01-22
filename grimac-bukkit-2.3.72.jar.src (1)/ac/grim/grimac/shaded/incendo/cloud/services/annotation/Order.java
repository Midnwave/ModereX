package ac.grim.grimac.shaded.incendo.cloud.services.annotation;

import ac.grim.grimac.shaded.incendo.cloud.services.ExecutionOrder;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {
  ExecutionOrder value() default ExecutionOrder.SOON;
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\services\annotation\Order.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */