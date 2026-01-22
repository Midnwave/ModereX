package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer;

import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;

public interface ClaimConsumer {
  void style(@NotNull String paramString, @NotNull Emitable paramEmitable);
  
  boolean component(@NotNull Emitable paramEmitable);
  
  boolean styleClaimed(@NotNull String paramString);
  
  boolean componentClaimed();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\serializer\ClaimConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */