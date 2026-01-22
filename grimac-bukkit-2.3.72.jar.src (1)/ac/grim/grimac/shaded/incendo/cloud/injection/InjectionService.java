package ac.grim.grimac.shaded.incendo.cloud.injection;

import ac.grim.grimac.shaded.incendo.cloud.services.type.Service;
import org.apiguardian.api.API;

@FunctionalInterface
@API(status = API.Status.STABLE)
public interface InjectionService<C> extends Service<InjectionRequest<C>, Object> {}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\injection\InjectionService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */