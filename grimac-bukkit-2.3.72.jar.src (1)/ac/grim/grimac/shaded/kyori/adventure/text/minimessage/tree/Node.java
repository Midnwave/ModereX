package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tree;

import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
import java.util.List;

@NonExtendable
public interface Node {
  @NotNull
  String toString();
  
  @NotNull
  List<? extends Node> children();
  
  @Nullable
  Node parent();
  
  @NonExtendable
  public static interface Root extends Node {
    @NotNull
    String input();
  }
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tree\Node.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */