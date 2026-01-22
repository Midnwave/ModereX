/*    */ package ac.grim.grimac.shaded.incendo.cloud.setting;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @API(status = API.Status.STABLE)
/*    */ public interface Configurable<S extends Setting>
/*    */ {
/*    */   static <E extends Enum<E> & Setting> Configurable<E> enumConfigurable(Class<E> enumClass) {
/* 46 */     return new EnumConfigurable<>(enumClass);
/*    */   }
/*    */   
/*    */   Configurable<S> set(S paramS, boolean paramBoolean);
/*    */   
/*    */   boolean get(S paramS);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\setting\Configurable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */