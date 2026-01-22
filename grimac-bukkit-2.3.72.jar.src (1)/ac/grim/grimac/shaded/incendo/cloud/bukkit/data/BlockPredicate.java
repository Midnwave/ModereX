package ac.grim.grimac.shaded.incendo.cloud.bukkit.data;

import java.util.function.Predicate;
import org.bukkit.block.Block;

public interface BlockPredicate extends Predicate<Block> {
  BlockPredicate loadChunks();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\data\BlockPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */