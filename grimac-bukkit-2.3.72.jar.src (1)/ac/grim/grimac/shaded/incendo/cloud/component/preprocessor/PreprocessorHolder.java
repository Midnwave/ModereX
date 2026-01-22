package ac.grim.grimac.shaded.incendo.cloud.component.preprocessor;

import java.util.Collection;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE)
public interface PreprocessorHolder<C> {
  Collection<ComponentPreprocessor<C>> preprocessors();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\component\preprocessor\PreprocessorHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */