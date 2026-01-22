package ac.grim.grimac.shaded.incendo.cloud.brigadier.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0")
public interface ArgumentTypeFactory<T> {
  ArgumentType<T> create();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\argument\ArgumentTypeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */