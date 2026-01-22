/*    */ package ac.grim.grimac.shaded.incendo.cloud.util;
/*    */ 
/*    */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*    */ import java.lang.reflect.ParameterizedType;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Arrays;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @API(status = API.Status.INTERNAL)
/*    */ public final class TypeUtils
/*    */ {
/*    */   public static String simpleName(Type type) {
/* 47 */     String simpleName = GenericTypeReflector.erase(type).getSimpleName();
/* 48 */     if (type instanceof ParameterizedType) {
/*    */ 
/*    */       
/* 51 */       String paramTypes = Arrays.<Type>stream(((ParameterizedType)type).getActualTypeArguments()).map(TypeUtils::simpleName).collect(Collectors.joining(", "));
/* 52 */       return simpleName + '<' + paramTypes + '>';
/*    */     } 
/* 54 */     return simpleName;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\clou\\util\TypeUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */