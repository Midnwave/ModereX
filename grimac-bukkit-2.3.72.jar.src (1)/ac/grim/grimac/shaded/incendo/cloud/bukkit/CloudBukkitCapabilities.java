/*    */ package ac.grim.grimac.shaded.incendo.cloud.bukkit;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.CloudCapability;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CraftBukkitReflection;
/*    */ import java.util.Arrays;
/*    */ import java.util.Set;
/*    */ import java.util.stream.Collectors;
/*    */ import org.apiguardian.api.API;
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
/*    */ public enum CloudBukkitCapabilities
/*    */   implements CloudCapability
/*    */ {
/* 42 */   BRIGADIER((CraftBukkitReflection.classExists("com.mojang.brigadier.tree.CommandNode") && 
/* 43 */     CraftBukkitReflection.findOBCClass("command.BukkitCommandWrapper") != null)),
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 49 */   NATIVE_BRIGADIER(CraftBukkitReflection.classExists("com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent")),
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
/* 61 */   COMMODORE_BRIGADIER((BRIGADIER.capable() && 
/* 62 */     !NATIVE_BRIGADIER.capable() && 
/* 63 */     !CraftBukkitReflection.classExists("org.bukkit.entity.Warden"))),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 68 */   ASYNCHRONOUS_COMPLETION(CraftBukkitReflection.classExists("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent"));
/*    */   
/*    */   @API(status = API.Status.INTERNAL)
/*    */   public static final Set<CloudBukkitCapabilities> CAPABLE;
/*    */   
/*    */   private final boolean capable;
/*    */ 
/*    */   
/*    */   static {
/* 77 */     CAPABLE = (Set<CloudBukkitCapabilities>)Arrays.<CloudBukkitCapabilities>stream(values()).filter(CloudBukkitCapabilities::capable).collect(Collectors.toSet());
/*    */   }
/*    */ 
/*    */   
/*    */   CloudBukkitCapabilities(boolean capable) {
/* 82 */     this.capable = capable;
/*    */   }
/*    */   
/*    */   boolean capable() {
/* 86 */     return this.capable;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 91 */     return name();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\CloudBukkitCapabilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */