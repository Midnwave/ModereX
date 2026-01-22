/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.button;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtDecoder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtEncoder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import org.jspecify.annotations.NullMarked;
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
/*    */ @NullMarked
/*    */ public class CommonButtonData
/*    */ {
/*    */   private final Component label;
/*    */   private final Component tooltip;
/*    */   private final int width;
/*    */   
/*    */   public CommonButtonData(Component label, Component tooltip, int width) {
/* 37 */     this.label = label;
/* 38 */     this.tooltip = tooltip;
/* 39 */     this.width = width;
/*    */   }
/*    */   
/*    */   public static CommonButtonData decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 43 */     AdventureSerializer serializer = AdventureSerializer.serializer(wrapper);
/* 44 */     Component label = (Component)compound.getOrThrow("label", (NbtDecoder)serializer, wrapper);
/* 45 */     Component tooltip = (Component)compound.getOrNull("tooltip", (NbtDecoder)serializer, wrapper);
/* 46 */     int width = compound.getNumberTagValueOrDefault("width", Integer.valueOf(150)).intValue();
/* 47 */     return new CommonButtonData(label, tooltip, width);
/*    */   }
/*    */   
/*    */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, CommonButtonData button) {
/* 51 */     AdventureSerializer serializer = AdventureSerializer.serializer(wrapper);
/* 52 */     compound.set("label", button.label, (NbtEncoder)serializer, wrapper);
/* 53 */     if (button.tooltip != null) {
/* 54 */       compound.set("tooltip", button.tooltip, (NbtEncoder)serializer, wrapper);
/*    */     }
/* 56 */     if (button.width != 150) {
/* 57 */       compound.setTag("width", (NBT)new NBTInt(button.width));
/*    */     }
/*    */   }
/*    */   
/*    */   public Component getLabel() {
/* 62 */     return this.label;
/*    */   }
/*    */   
/*    */   public Component getTooltip() {
/* 66 */     return this.tooltip;
/*    */   }
/*    */   
/*    */   public int getWidth() {
/* 70 */     return this.width;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\button\CommonButtonData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */