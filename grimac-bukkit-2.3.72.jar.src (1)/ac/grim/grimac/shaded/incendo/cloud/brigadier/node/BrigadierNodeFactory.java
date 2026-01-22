package ac.grim.grimac.shaded.incendo.cloud.brigadier.node;

import ac.grim.grimac.shaded.incendo.cloud.Command;
import ac.grim.grimac.shaded.incendo.cloud.brigadier.permission.BrigadierPermissionChecker;
import ac.grim.grimac.shaded.incendo.cloud.internal.CommandNode;
import com.mojang.brigadier.Command;
import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.brigadier.*"})
public interface BrigadierNodeFactory<C, S, N extends com.mojang.brigadier.tree.CommandNode<S>> {
  N createNode(String paramString, CommandNode<C> paramCommandNode, Command<S> paramCommand, BrigadierPermissionChecker<C> paramBrigadierPermissionChecker);
  
  N createNode(String paramString, Command<C> paramCommand, Command<S> paramCommand1, BrigadierPermissionChecker<C> paramBrigadierPermissionChecker);
  
  N createNode(String paramString, Command<C> paramCommand, Command<S> paramCommand1);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\node\BrigadierNodeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */