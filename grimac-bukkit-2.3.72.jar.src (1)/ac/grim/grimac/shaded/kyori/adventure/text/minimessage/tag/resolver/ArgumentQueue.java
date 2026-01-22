package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver;

import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
import java.util.function.Supplier;

@NonExtendable
public interface ArgumentQueue {
  Tag.Argument pop();
  
  Tag.Argument popOr(@NotNull String paramString);
  
  Tag.Argument popOr(@NotNull Supplier<String> paramSupplier);
  
  Tag.Argument peek();
  
  boolean hasNext();
  
  void reset();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\resolver\ArgumentQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */