/*    */ package ac.grim.grimac.platform.bukkit.command;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.platform.api.command.PlayerSelector;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.platform.bukkit.sender.BukkitSenderFactory;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.data.SinglePlayerSelector;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class BukkitPlayerSelectorAdapter
/*    */   implements PlayerSelector {
/*    */   public BukkitPlayerSelectorAdapter(SinglePlayerSelector bukkitSelector) {
/* 15 */     this.bukkitSelector = bukkitSelector;
/*    */   }
/*    */   private final SinglePlayerSelector bukkitSelector;
/*    */   
/*    */   public boolean isSingle() {
/* 20 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public Sender getSinglePlayer() {
/* 25 */     return ((BukkitSenderFactory)GrimAPI.INSTANCE.getSenderFactory()).map((CommandSender)this.bukkitSelector.single());
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<Sender> getPlayers() {
/* 30 */     return Collections.singletonList(((BukkitSenderFactory)GrimAPI.INSTANCE.getSenderFactory()).map((CommandSender)this.bukkitSelector.single()));
/*    */   }
/*    */ 
/*    */   
/*    */   public String inputString() {
/* 35 */     return this.bukkitSelector.inputString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\command\BukkitPlayerSelectorAdapter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */