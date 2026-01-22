package ac.grim.grimac.shaded.fastutil;

import java.util.Iterator;

public interface BidirectionalIterator<K> extends Iterator<K> {
  K previous();
  
  boolean hasPrevious();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\fastutil\BidirectionalIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */