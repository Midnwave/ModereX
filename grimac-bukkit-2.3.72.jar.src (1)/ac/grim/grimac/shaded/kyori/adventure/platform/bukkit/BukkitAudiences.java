/*    */ package ac.grim.grimac.shaded.kyori.adventure.platform.bukkit;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.audience.Audience;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.platform.AudienceProvider;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.sound.Sound;
/*    */ import java.util.function.Predicate;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Entity;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface BukkitAudiences
/*    */   extends AudienceProvider
/*    */ {
/*    */   @NotNull
/*    */   static BukkitAudiences create(@NotNull Plugin plugin) {
/* 53 */     return BukkitAudiencesImpl.instanceFor(plugin);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   static Builder builder(@NotNull Plugin plugin) {
/* 66 */     return BukkitAudiencesImpl.builder(plugin);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static Sound.Emitter asEmitter(@NotNull Entity entity) {
/* 77 */     return new BukkitEmitter(entity);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   Audience sender(@NotNull CommandSender paramCommandSender);
/*    */   
/*    */   @NotNull
/*    */   Audience player(@NotNull Player paramPlayer);
/*    */   
/*    */   @NotNull
/*    */   Audience filter(@NotNull Predicate<CommandSender> paramPredicate);
/*    */   
/*    */   public static interface Builder extends AudienceProvider.Builder<BukkitAudiences, Builder> {}
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\bukkit\BukkitAudiences.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */