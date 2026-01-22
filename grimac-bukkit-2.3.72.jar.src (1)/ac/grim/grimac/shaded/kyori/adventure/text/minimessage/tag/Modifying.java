package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag;

import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.OverrideOnly;
import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
import ac.grim.grimac.shaded.kyori.adventure.text.Component;
import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tree.Node;

@OverrideOnly
public interface Modifying extends Tag {
  default void visit(@NotNull Node current, int depth) {}
  
  default void postVisit() {}
  
  Component apply(@NotNull Component paramComponent, int paramInt);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\Modifying.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */