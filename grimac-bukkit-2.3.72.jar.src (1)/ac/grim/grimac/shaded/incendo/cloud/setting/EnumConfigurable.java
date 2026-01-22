/*    */ package ac.grim.grimac.shaded.incendo.cloud.setting;
/*    */ 
/*    */ import java.util.EnumSet;
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
/*    */ final class EnumConfigurable<S extends Enum<S> & Setting>
/*    */   implements Configurable<S>
/*    */ {
/*    */   private final EnumSet<S> settings;
/*    */   
/*    */   EnumConfigurable(Class<S> settingClass) {
/* 35 */     this.settings = EnumSet.noneOf(settingClass);
/*    */   }
/*    */   
/*    */   EnumConfigurable(S defaultSetting) {
/* 39 */     this.settings = EnumSet.of(defaultSetting);
/*    */   }
/*    */ 
/*    */   
/*    */   public EnumConfigurable<S> set(S setting, boolean value) {
/* 44 */     if (value) {
/* 45 */       this.settings.add(setting);
/*    */     } else {
/* 47 */       this.settings.remove(setting);
/*    */     } 
/* 49 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean get(S setting) {
/* 54 */     return this.settings.contains(setting);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\setting\EnumConfigurable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */