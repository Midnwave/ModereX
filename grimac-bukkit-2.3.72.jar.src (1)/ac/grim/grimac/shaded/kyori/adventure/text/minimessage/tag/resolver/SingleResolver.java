/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*    */ import java.util.Map;
/*    */ import java.util.Objects;
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
/*    */ final class SingleResolver
/*    */   implements TagResolver.Single, MappableResolver
/*    */ {
/*    */   private final String key;
/*    */   private final Tag tag;
/*    */   
/*    */   SingleResolver(String key, Tag tag) {
/* 36 */     this.key = key;
/* 37 */     this.tag = tag;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String key() {
/* 42 */     return this.key;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Tag tag() {
/* 47 */     return this.tag;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean contributeToMap(@NotNull Map<String, Tag> map) {
/* 52 */     map.put(this.key, this.tag);
/* 53 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 58 */     return Objects.hash(new Object[] { this.key, this.tag });
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 63 */     if (this == other) {
/* 64 */       return true;
/*    */     }
/* 66 */     if (other == null) {
/* 67 */       return false;
/*    */     }
/* 69 */     if (getClass() != other.getClass()) {
/* 70 */       return false;
/*    */     }
/* 72 */     SingleResolver that = (SingleResolver)other;
/* 73 */     return (Objects.equals(this.key, that.key) && 
/* 74 */       Objects.equals(this.tag, that.tag));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\resolver\SingleResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */