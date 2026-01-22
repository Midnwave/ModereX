package ac.grim.grimac.shaded.incendo.cloud.paper.suggestion.tooltips;

import ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion.TooltipSuggestion;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL, since = "2.0.0")
public interface CompletionMapper {
  AsyncTabCompleteEvent.Completion map(TooltipSuggestion paramTooltipSuggestion);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\paper\suggestion\tooltips\CompletionMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */