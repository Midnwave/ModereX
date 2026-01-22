/*    */ package ac.grim.grimac.shaded.kyori.option;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*    */ import ac.grim.grimac.shaded.kyori.option.value.ValueSource;
/*    */ import java.util.Map;
/*    */ import java.util.function.Consumer;
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
/*    */ public interface OptionState
/*    */ {
/*    */   @Deprecated
/*    */   static OptionState emptyOptionState() {
/* 48 */     return OptionSchema.globalSchema().emptyState();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   static Builder optionState() {
/* 60 */     return OptionSchema.globalSchema().stateBuilder();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   static VersionedBuilder versionedOptionState() {
/* 72 */     return OptionSchema.globalSchema().versionedStateBuilder();
/*    */   }
/*    */   
/*    */   OptionSchema schema();
/*    */   
/*    */   boolean has(Option<?> paramOption);
/*    */   
/*    */   <V> V value(Option<V> paramOption);
/*    */   
/*    */   @NonExtendable
/*    */   public static interface Builder {
/*    */     <V> Builder value(Option<V> param1Option, V param1V);
/*    */     
/*    */     Builder values(OptionState param1OptionState);
/*    */     
/*    */     Builder values(ValueSource param1ValueSource);
/*    */     
/*    */     OptionState build();
/*    */   }
/*    */   
/*    */   @NonExtendable
/*    */   public static interface VersionedBuilder {
/*    */     VersionedBuilder version(int param1Int, Consumer<OptionState.Builder> param1Consumer);
/*    */     
/*    */     OptionState.Versioned build();
/*    */   }
/*    */   
/*    */   @NonExtendable
/*    */   public static interface Versioned extends OptionState {
/*    */     Map<Integer, OptionState> childStates();
/*    */     
/*    */     Versioned at(int param1Int);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\option\OptionState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */