package ac.grim.grimac.shaded.snakeyaml.constructor;

import ac.grim.grimac.shaded.snakeyaml.nodes.Node;

public interface Construct {
  Object construct(Node paramNode);
  
  void construct2ndStep(Node paramNode, Object paramObject);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\constructor\Construct.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */