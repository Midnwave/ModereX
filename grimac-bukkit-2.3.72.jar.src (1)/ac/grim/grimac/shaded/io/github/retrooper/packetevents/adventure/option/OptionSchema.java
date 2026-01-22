/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.option;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*    */ import java.util.Objects;
/*    */ import java.util.Set;
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
/*    */ 
/*    */ 
/*    */ @NonExtendable
/*    */ public interface OptionSchema
/*    */ {
/*    */   static Mutable globalSchema() {
/* 48 */     return OptionSchemaImpl.Instances.GLOBAL;
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
/*    */   static Mutable childSchema(OptionSchema schema) {
/*    */     OptionSchemaImpl impl;
/* 62 */     if (schema instanceof OptionSchemaImpl.MutableImpl) {
/* 63 */       impl = (OptionSchemaImpl)((Mutable)schema).frozenView();
/*    */     } else {
/* 65 */       impl = (OptionSchemaImpl)schema;
/*    */     } 
/*    */     
/* 68 */     return new OptionSchemaImpl.MutableImpl(new OptionSchemaImpl(Objects.<OptionSchemaImpl>requireNonNull(impl, "impl")));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static Mutable emptySchema() {
/* 79 */     return new OptionSchemaImpl.MutableImpl(new OptionSchemaImpl(null));
/*    */   }
/*    */   
/*    */   Set<Option<?>> knownOptions();
/*    */   
/*    */   boolean has(Option<?> paramOption);
/*    */   
/*    */   OptionState.Builder stateBuilder();
/*    */   
/*    */   OptionState.VersionedBuilder versionedStateBuilder();
/*    */   
/*    */   OptionState emptyState();
/*    */   
/*    */   @NonExtendable
/*    */   public static interface Mutable extends OptionSchema {
/*    */     Option<String> stringOption(String param1String1, String param1String2);
/*    */     
/*    */     Option<Boolean> booleanOption(String param1String, boolean param1Boolean);
/*    */     
/*    */     Option<Integer> intOption(String param1String, int param1Int);
/*    */     
/*    */     Option<Double> doubleOption(String param1String, double param1Double);
/*    */     
/*    */     <E extends Enum<E>> Option<E> enumOption(String param1String, Class<E> param1Class, E param1E);
/*    */     
/*    */     OptionSchema frozenView();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\option\OptionSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */