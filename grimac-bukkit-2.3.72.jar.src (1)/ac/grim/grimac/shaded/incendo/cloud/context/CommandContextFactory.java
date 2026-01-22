package ac.grim.grimac.shaded.incendo.cloud.context;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE)
public interface CommandContextFactory<C> {
  @API(status = API.Status.STABLE)
  CommandContext<C> create(boolean paramBoolean, C paramC);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\context\CommandContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */