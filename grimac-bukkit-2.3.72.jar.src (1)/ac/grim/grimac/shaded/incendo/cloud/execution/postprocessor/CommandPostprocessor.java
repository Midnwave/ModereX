package ac.grim.grimac.shaded.incendo.cloud.execution.postprocessor;

import ac.grim.grimac.shaded.incendo.cloud.services.type.ConsumerService;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE)
public interface CommandPostprocessor<C> extends ConsumerService<CommandPostprocessingContext<C>> {}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\execution\postprocessor\CommandPostprocessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */