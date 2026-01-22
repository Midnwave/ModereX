/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson;
/*    */ 
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEvent;
/*    */ import com.google.gson.TypeAdapter;
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
/*    */ final class HoverEventActionSerializer
/*    */ {
/* 30 */   static final TypeAdapter<HoverEvent.Action<?>> INSTANCE = IndexedSerializer.lenient("hover action", HoverEvent.Action.NAMES);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\gson\HoverEventActionSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */