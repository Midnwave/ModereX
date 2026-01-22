package ac.grim.grimac.shaded.kyori.adventure.text;

import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;

@NonExtendable
public interface VirtualComponent extends TextComponent {
  @NotNull
  Class<?> contextType();
  
  @NotNull
  VirtualComponentRenderer<?> renderer();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\VirtualComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */