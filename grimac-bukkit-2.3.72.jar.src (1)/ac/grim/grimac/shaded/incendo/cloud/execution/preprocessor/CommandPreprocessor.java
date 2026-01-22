package ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor;

import ac.grim.grimac.shaded.incendo.cloud.services.type.ConsumerService;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE)
public interface CommandPreprocessor<C> extends ConsumerService<CommandPreprocessingContext<C>> {}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\execution\preprocessor\CommandPreprocessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */