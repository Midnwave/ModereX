package ac.grim.grimac.shaded.kyori.adventure.text;

import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;

public interface BuildableComponent<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>> extends Buildable<C, B>, Component {
  @NotNull
  B toBuilder();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\BuildableComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */