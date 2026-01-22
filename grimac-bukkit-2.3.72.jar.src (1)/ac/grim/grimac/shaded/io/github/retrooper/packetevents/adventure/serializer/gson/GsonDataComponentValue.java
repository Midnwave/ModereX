/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer.gson;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.event.DataComponentValue;
/*    */ import com.google.gson.JsonElement;
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
/*    */ public interface GsonDataComponentValue
/*    */   extends DataComponentValue
/*    */ {
/*    */   static GsonDataComponentValue gsonDataComponentValue(@NotNull JsonElement data) {
/* 51 */     if (data instanceof com.google.gson.JsonNull) {
/* 52 */       return GsonDataComponentValueImpl.RemovedGsonComponentValueImpl.INSTANCE;
/*    */     }
/* 54 */     return new GsonDataComponentValueImpl(Objects.<JsonElement>requireNonNull(data, "data"));
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   JsonElement element();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\serializer\gson\GsonDataComponentValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */