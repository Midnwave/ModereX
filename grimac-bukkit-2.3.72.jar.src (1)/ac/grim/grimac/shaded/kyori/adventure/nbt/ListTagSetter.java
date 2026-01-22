package ac.grim.grimac.shaded.kyori.adventure.nbt;

import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;

public interface ListTagSetter<R, T extends BinaryTag> {
  @NotNull
  R add(T paramT);
  
  @NotNull
  R add(Iterable<? extends T> paramIterable);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\ListTagSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */