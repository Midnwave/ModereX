/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.bungeecord;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import com.google.gson.Gson;
/*    */ import com.google.gson.GsonBuilder;
/*    */ import com.google.gson.TypeAdapterFactory;
/*    */ import java.lang.reflect.Field;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ final class GsonInjections
/*    */ {
/*    */   public static Field field(@NotNull Class<?> klass, @NotNull String name) throws NoSuchFieldException {
/* 50 */     Field field = klass.getDeclaredField(name);
/* 51 */     field.setAccessible(true);
/* 52 */     return field;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean injectGson(@NotNull Gson existing, @NotNull Consumer<GsonBuilder> accepter) {
/*    */     try {
/* 60 */       Field factoriesField = field(Gson.class, "factories");
/* 61 */       Field builderFactoriesField = field(GsonBuilder.class, "factories");
/* 62 */       Field builderHierarchyFactoriesField = field(GsonBuilder.class, "hierarchyFactories");
/*    */       
/* 64 */       GsonBuilder builder = new GsonBuilder();
/* 65 */       accepter.accept(builder);
/*    */       
/* 67 */       List<TypeAdapterFactory> existingFactories = (List<TypeAdapterFactory>)factoriesField.get(existing);
/* 68 */       List<TypeAdapterFactory> newFactories = new ArrayList<>();
/* 69 */       newFactories.addAll((List)builderFactoriesField.get(builder));
/* 70 */       Collections.reverse(newFactories);
/* 71 */       newFactories.addAll((List)builderHierarchyFactoriesField.get(builder));
/*    */       
/* 73 */       List<TypeAdapterFactory> modifiedFactories = new ArrayList<>(existingFactories);
/*    */ 
/*    */       
/* 76 */       int index = findExcluderIndex(modifiedFactories);
/*    */       
/* 78 */       Collections.reverse(newFactories);
/* 79 */       for (TypeAdapterFactory newFactory : newFactories) {
/* 80 */         modifiedFactories.add(index, newFactory);
/*    */       }
/*    */       
/* 83 */       factoriesField.set(existing, modifiedFactories);
/* 84 */       return true;
/* 85 */     } catch (NoSuchFieldException|IllegalAccessException ex) {
/* 86 */       return false;
/*    */     } 
/*    */   }
/*    */   
/*    */   private static int findExcluderIndex(@NotNull List<TypeAdapterFactory> factories) {
/* 91 */     for (int i = 0, size = factories.size(); i < size; i++) {
/* 92 */       TypeAdapterFactory factory = factories.get(i);
/* 93 */       if (factory instanceof com.google.gson.internal.Excluder) {
/* 94 */         return i + 1;
/*    */       }
/*    */     } 
/* 97 */     return 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\bungeecord\GsonInjections.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */