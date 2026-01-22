/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.input;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTByte;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
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
/*    */ @NullMarked
/*    */ public class BooleanInputControl
/*    */   implements InputControl
/*    */ {
/*    */   private final Component label;
/*    */   private final boolean initial;
/*    */   private final String onTrue;
/*    */   private final String onFalse;
/*    */   
/*    */   public BooleanInputControl(Component label, boolean initial, String onTrue, String onFalse) {
/* 38 */     this.label = label;
/* 39 */     this.initial = initial;
/* 40 */     this.onTrue = onTrue;
/* 41 */     this.onFalse = onFalse;
/*    */   }
/*    */   
/*    */   public static BooleanInputControl decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 45 */     Component label = (Component)compound.getOrThrow("label", (NbtDecoder)AdventureSerializer.serializer(wrapper), wrapper);
/* 46 */     boolean initial = compound.getBoolean("initial");
/* 47 */     String onTrue = compound.getStringTagValueOrDefault("on_true", "true");
/* 48 */     String onFalse = compound.getStringTagValueOrDefault("on_false", "false");
/* 49 */     return new BooleanInputControl(label, initial, onTrue, onFalse);
/*    */   }
/*    */   
/*    */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, BooleanInputControl control) {
/* 53 */     compound.set("label", control.label, (NbtEncoder)AdventureSerializer.serializer(wrapper), wrapper);
/* 54 */     if (control.initial) {
/* 55 */       compound.setTag("initial", (NBT)new NBTByte(true));
/*    */     }
/* 57 */     if (!"true".equals(control.onTrue)) {
/* 58 */       compound.setTag("on_true", (NBT)new NBTString(control.onTrue));
/*    */     }
/* 60 */     if (!"false".equals(control.onFalse)) {
/* 61 */       compound.setTag("on_false", (NBT)new NBTString(control.onFalse));
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public InputControlType<?> getType() {
/* 67 */     return InputControlTypes.BOOLEAN;
/*    */   }
/*    */   
/*    */   public Component getLabel() {
/* 71 */     return this.label;
/*    */   }
/*    */   
/*    */   public boolean isInitial() {
/* 75 */     return this.initial;
/*    */   }
/*    */   
/*    */   public String getOnTrue() {
/* 79 */     return this.onTrue;
/*    */   }
/*    */   
/*    */   public String getOnFalse() {
/* 83 */     return this.onFalse;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\input\BooleanInputControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */