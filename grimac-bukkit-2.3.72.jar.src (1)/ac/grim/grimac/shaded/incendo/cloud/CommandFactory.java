package ac.grim.grimac.shaded.incendo.cloud;

import java.util.List;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE)
@FunctionalInterface
public interface CommandFactory<C> {
  List<Command<? extends C>> createCommands(CommandManager<C> paramCommandManager);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\CommandFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */