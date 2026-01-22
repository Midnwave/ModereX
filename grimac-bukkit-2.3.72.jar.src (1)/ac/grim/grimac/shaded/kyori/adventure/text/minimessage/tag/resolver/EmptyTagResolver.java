/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.ClaimConsumer;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*    */ import java.util.Map;
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
/*    */ final class EmptyTagResolver
/*    */   implements TagResolver, MappableResolver, SerializableResolver
/*    */ {
/* 36 */   static final EmptyTagResolver INSTANCE = new EmptyTagResolver();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) {
/* 43 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean has(@NotNull String name) {
/* 48 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean contributeToMap(@NotNull Map<String, Tag> map) {
/* 53 */     return true;
/*    */   }
/*    */   
/*    */   public void handle(@NotNull Component serializable, @NotNull ClaimConsumer consumer) {}
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\resolver\EmptyTagResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */