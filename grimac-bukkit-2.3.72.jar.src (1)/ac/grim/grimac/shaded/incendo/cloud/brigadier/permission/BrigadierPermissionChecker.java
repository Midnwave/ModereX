package ac.grim.grimac.shaded.incendo.cloud.brigadier.permission;

import ac.grim.grimac.shaded.incendo.cloud.permission.Permission;
import org.apiguardian.api.API;

@FunctionalInterface
@API(status = API.Status.INTERNAL, since = "2.0.0")
public interface BrigadierPermissionChecker<C> {
  boolean hasPermission(C paramC, Permission paramPermission);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\permission\BrigadierPermissionChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */