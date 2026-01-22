package ac.grim.grimac.command;

import ac.grim.grimac.platform.api.sender.Sender;
import ac.grim.grimac.shaded.incendo.cloud.CommandManager;

public interface BuildableCommand {
  void register(CommandManager<Sender> paramCommandManager);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\BuildableCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */