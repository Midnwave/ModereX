package ac.grim.grimac.shaded.incendo.cloud.parser;

import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
import java.util.concurrent.CompletableFuture;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE)
public interface MappedArgumentParser<C, I, O> extends ArgumentParser<C, O> {
  ArgumentParser<C, I> baseParser();
  
  @FunctionalInterface
  public static interface Mapper<C, I, O> {
    CompletableFuture<ArgumentParseResult<O>> map(CommandContext<C> param1CommandContext, ArgumentParseResult<I> param1ArgumentParseResult);
  }
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\MappedArgumentParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */