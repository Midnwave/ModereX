package ac.grim.grimac.shaded.incendo.cloud.brigadier.argument;

import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
import ac.grim.grimac.shaded.incendo.cloud.brigadier.CloudBrigadierManager;

public interface BrigadierMappingContributor {
  <C, S> void contribute(CommandManager<C> paramCommandManager, CloudBrigadierManager<C, S> paramCloudBrigadierManager);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\argument\BrigadierMappingContributor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */