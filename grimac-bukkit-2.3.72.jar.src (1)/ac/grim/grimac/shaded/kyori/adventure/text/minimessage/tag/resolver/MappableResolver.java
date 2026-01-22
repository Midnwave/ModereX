package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver;

import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
import java.util.Map;

interface MappableResolver {
  boolean contributeToMap(@NotNull Map<String, Tag> paramMap);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\resolver\MappableResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */