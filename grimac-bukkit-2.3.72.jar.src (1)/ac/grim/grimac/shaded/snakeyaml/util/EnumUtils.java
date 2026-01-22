/*    */ package ac.grim.grimac.shaded.snakeyaml.util;
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
/*    */ public class EnumUtils
/*    */ {
/*    */   public static <T extends Enum<T>> T findEnumInsensitiveCase(Class<T> enumType, String name) {
/* 33 */     for (Enum enum_ : (Enum[])enumType.getEnumConstants()) {
/* 34 */       if (enum_.name().compareToIgnoreCase(name) == 0) {
/* 35 */         return (T)enum_;
/*    */       }
/*    */     } 
/* 38 */     throw new IllegalArgumentException("No enum constant " + enumType
/* 39 */         .getCanonicalName() + "." + name);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyam\\util\EnumUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */