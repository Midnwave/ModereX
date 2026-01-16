package zoruafan.foxanticheat.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FoxFlagEvent extends Event implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private Player player;
   private String checkType;
   private int vls;
   private String details;
   private boolean cancelled;
   private String module;
   private String log;

   public FoxFlagEvent(Player player, String checkType, int vls, String details, String module, String log) {
      this.player = player;
      this.checkType = checkType;
      this.vls = vls;
      this.details = details;
      this.module = module;
      this.log = log;
   }

   public Player getPlayer() {
      return this.player;
   }

   public String getCheckType() {
      return this.checkType;
   }

   public int getVLS() {
      return this.vls;
   }

   public String getDetails() {
      return this.details;
   }

   public String getModule() {
      return this.module;
   }

   public String getLog() {
      return this.log;
   }

   public void setCancelled(boolean value) {
      this.cancelled = value;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
