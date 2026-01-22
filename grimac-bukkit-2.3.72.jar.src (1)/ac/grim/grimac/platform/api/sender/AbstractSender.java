/*     */ package ac.grim.grimac.platform.api.sender;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.platform.api.player.PlatformPlayer;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import java.util.UUID;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class AbstractSender<T>
/*     */   implements Sender
/*     */ {
/*     */   private final SenderFactory<T> factory;
/*     */   private final T sender;
/*     */   private final UUID uniqueId;
/*     */   private final String name;
/*     */   private final boolean isConsole;
/*     */   
/*     */   AbstractSender(SenderFactory<T> factory, T sender) {
/*  26 */     this.factory = factory;
/*  27 */     this.sender = sender;
/*  28 */     this.uniqueId = factory.getUniqueId(this.sender);
/*  29 */     this.name = factory.getName(this.sender);
/*  30 */     this.isConsole = factory.isConsole(this.sender);
/*     */   }
/*     */ 
/*     */   
/*     */   public UUID getUniqueId() {
/*  35 */     return this.uniqueId;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  40 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendMessage(String message) {
/*  45 */     this.factory.sendMessage(this.sender, message);
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendMessage(Component message) {
/*  50 */     this.factory.sendMessage(this.sender, message);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasPermission(String permission) {
/*  55 */     return (isConsole() || this.factory.hasPermission(this.sender, permission));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasPermission(String permission, boolean defaultIfUnset) {
/*  60 */     return (isConsole() || this.factory.hasPermission(this.sender, permission, defaultIfUnset));
/*     */   }
/*     */ 
/*     */   
/*     */   public void performCommand(String commandLine) {
/*  65 */     this.factory.performCommand(this.sender, commandLine);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isConsole() {
/*  70 */     return this.isConsole;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPlayer() {
/*  75 */     return this.factory.isPlayer(this.sender);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValid() {
/*  80 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*     */     AbstractSender<?> that;
/*  86 */     if (o == this) return true; 
/*  87 */     if (o instanceof AbstractSender) { that = (AbstractSender)o; } else { return false; }
/*  88 */      return getUniqueId().equals(that.getUniqueId());
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  93 */     return this.uniqueId.hashCode();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public T getNativeSender() {
/*  98 */     return this.sender;
/*     */   }
/*     */ 
/*     */   
/*     */   public PlatformPlayer getPlatformPlayer() {
/* 103 */     return GrimAPI.INSTANCE.getPlatformPlayerFactory().getFromUUID(getUniqueId());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\sender\AbstractSender.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */