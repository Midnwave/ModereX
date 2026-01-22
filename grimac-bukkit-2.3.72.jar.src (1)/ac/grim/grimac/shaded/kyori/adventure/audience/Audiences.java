/*    */ package ac.grim.grimac.shaded.kyori.adventure.audience;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.resource.ResourcePackCallback;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.resource.ResourcePackStatus;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.UUID;
/*    */ import java.util.function.Consumer;
/*    */ import java.util.function.Supplier;
/*    */ import java.util.stream.Collector;
/*    */ import java.util.stream.Collectors;
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
/*    */ public final class Audiences
/*    */ {
/*    */   static final Collector<? super Audience, ?, ForwardingAudience> COLLECTOR;
/*    */   
/*    */   static {
/* 41 */     COLLECTOR = Collectors.collectingAndThen(
/* 42 */         Collectors.toCollection(ArrayList::new), audiences -> Audience.audience(Collections.unmodifiableCollection(audiences)));
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
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public static Consumer<? super Audience> sendingMessage(@NotNull ComponentLike message) {
/* 57 */     return audience -> audience.sendMessage(message);
/*    */   }
/*    */   @NotNull
/*    */   static ResourcePackCallback unwrapCallback(Audience forwarding, Audience dest, @NotNull ResourcePackCallback cb) {
/* 61 */     if (cb == ResourcePackCallback.noOp()) return cb;
/*    */     
/* 63 */     return (uuid, status, audience) -> cb.packEventReceived(uuid, status, (audience == dest) ? forwarding : audience);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\audience\Audiences.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */