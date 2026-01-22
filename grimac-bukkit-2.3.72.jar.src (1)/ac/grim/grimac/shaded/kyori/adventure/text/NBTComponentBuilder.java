package ac.grim.grimac.shaded.kyori.adventure.text;

import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;

public interface NBTComponentBuilder<C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> extends ComponentBuilder<C, B> {
  @Contract("_ -> this")
  @NotNull
  B nbtPath(@NotNull String paramString);
  
  @Contract("_ -> this")
  @NotNull
  B interpret(boolean paramBoolean);
  
  @Contract("_ -> this")
  @NotNull
  B separator(@Nullable ComponentLike paramComponentLike);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\NBTComponentBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */