package ac.grim.grimac.shaded.incendo.cloud.services.type;

import ac.grim.grimac.shaded.incendo.cloud.services.State;

@FunctionalInterface
public interface SideEffectService<Context> extends Service<Context, State> {
  State handle(Context paramContext) throws Exception;
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\services\type\SideEffectService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */