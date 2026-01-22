package ac.grim.grimac.shaded.kyori.adventure.text.minimessage;

import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
import ac.grim.grimac.shaded.kyori.adventure.pointer.Pointered;
import ac.grim.grimac.shaded.kyori.adventure.text.Component;
import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

@NonExtendable
public interface Context {
  @Nullable
  Pointered target();
  
  @NotNull
  Pointered targetOrThrow();
  
  @NotNull
  <T extends Pointered> T targetAsType(@NotNull Class<T> paramClass);
  
  @NotNull
  Component deserialize(@NotNull String paramString);
  
  @NotNull
  Component deserialize(@NotNull String paramString, @NotNull TagResolver paramTagResolver);
  
  @NotNull
  Component deserialize(@NotNull String paramString, @NotNull TagResolver... paramVarArgs);
  
  @NotNull
  ParsingException newException(@NotNull String paramString, @NotNull ArgumentQueue paramArgumentQueue);
  
  @NotNull
  ParsingException newException(@NotNull String paramString);
  
  @NotNull
  ParsingException newException(@NotNull String paramString, @Nullable Throwable paramThrowable, @NotNull ArgumentQueue paramArgumentQueue);
  
  boolean emitVirtuals();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\Context.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */