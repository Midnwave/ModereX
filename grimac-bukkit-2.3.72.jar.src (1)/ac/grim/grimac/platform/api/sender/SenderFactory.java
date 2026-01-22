/*    */ package ac.grim.grimac.platform.api.sender;
/*    */ 
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import java.util.Objects;
/*    */ import java.util.UUID;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class SenderFactory<T>
/*    */ {
/*    */   protected abstract UUID getUniqueId(T paramT);
/*    */   
/*    */   protected abstract String getName(T paramT);
/*    */   
/*    */   protected abstract void sendMessage(T paramT, String paramString);
/*    */   
/*    */   protected abstract void sendMessage(T paramT, Component paramComponent);
/*    */   
/*    */   protected abstract boolean hasPermission(T paramT, String paramString);
/*    */   
/*    */   protected abstract boolean hasPermission(T paramT, String paramString, boolean paramBoolean);
/*    */   
/*    */   protected abstract void performCommand(T paramT, String paramString);
/*    */   
/*    */   protected abstract boolean isConsole(T paramT);
/*    */   
/*    */   protected abstract boolean isPlayer(T paramT);
/*    */   
/*    */   protected boolean shouldSplitNewlines(T sender) {
/* 34 */     return isConsole(sender);
/*    */   }
/*    */   
/*    */   public final Sender wrap(T sender) {
/* 38 */     Objects.requireNonNull(sender, "sender");
/* 39 */     return new AbstractSender<>(this, sender);
/*    */   }
/*    */ 
/*    */   
/*    */   public final T unwrap(Sender sender) {
/* 44 */     Objects.requireNonNull(sender, "sender");
/* 45 */     return (T)sender.getNativeSender();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\sender\SenderFactory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */