package ac.grim.grimac.command;

import ac.grim.grimac.platform.api.sender.Sender;
import ac.grim.grimac.shaded.incendo.cloud.processors.requirements.Requirement;
import ac.grim.grimac.shaded.kyori.adventure.text.Component;

public interface SenderRequirement extends Requirement<Sender, SenderRequirement> {
  Component errorMessage(Sender paramSender);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\SenderRequirement.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */