/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.slot.SlotDisplay;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.function.BiFunction;
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
/*    */ public class SingleInputOptionDisplay
/*    */ {
/*    */   private MappedEntitySet<ItemType> input;
/*    */   private SlotDisplay<?> optionDisplay;
/*    */   
/*    */   public SingleInputOptionDisplay(MappedEntitySet<ItemType> input, SlotDisplay<?> optionDisplay) {
/* 41 */     this.input = input;
/* 42 */     this.optionDisplay = optionDisplay;
/*    */   }
/*    */   
/*    */   public static SingleInputOptionDisplay read(PacketWrapper<?> wrapper) {
/* 46 */     MappedEntitySet<ItemType> ingredient = MappedEntitySet.read(wrapper, (BiFunction)ItemTypes.getRegistry());
/* 47 */     SlotDisplay<?> optionDisplay = SlotDisplay.read(wrapper);
/* 48 */     return new SingleInputOptionDisplay(ingredient, optionDisplay);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, SingleInputOptionDisplay recipe) {
/* 52 */     MappedEntitySet.write(wrapper, recipe.input);
/* 53 */     SlotDisplay.write(wrapper, recipe.optionDisplay);
/*    */   }
/*    */   
/*    */   public MappedEntitySet<ItemType> getInput() {
/* 57 */     return this.input;
/*    */   }
/*    */   
/*    */   public void setInput(MappedEntitySet<ItemType> input) {
/* 61 */     this.input = input;
/*    */   }
/*    */   
/*    */   public SlotDisplay<?> getOptionDisplay() {
/* 65 */     return this.optionDisplay;
/*    */   }
/*    */   
/*    */   public void setOptionDisplay(SlotDisplay<?> optionDisplay) {
/* 69 */     this.optionDisplay = optionDisplay;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\SingleInputOptionDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */