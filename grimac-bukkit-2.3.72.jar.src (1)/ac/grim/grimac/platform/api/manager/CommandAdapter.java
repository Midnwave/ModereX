package ac.grim.grimac.platform.api.manager;

import ac.grim.grimac.platform.api.command.PlayerSelector;
import ac.grim.grimac.platform.api.sender.Sender;
import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;

public interface CommandAdapter {
  ParserDescriptor<Sender, PlayerSelector> singlePlayerSelectorParser();
  
  SuggestionProvider<Sender> onlinePlayerSuggestions();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\manager\CommandAdapter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */