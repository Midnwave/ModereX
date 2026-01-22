package ac.grim.grimac.shaded.incendo.cloud.syntax;

import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
import ac.grim.grimac.shaded.incendo.cloud.internal.CommandNode;
import java.util.List;
import org.apiguardian.api.API;

@FunctionalInterface
@API(status = API.Status.STABLE)
public interface CommandSyntaxFormatter<C> {
  String apply(C paramC, List<CommandComponent<C>> paramList, CommandNode<C> paramCommandNode);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\syntax\CommandSyntaxFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */