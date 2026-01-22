/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.clickevent;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTEnd;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.adventure.NbtTagHolder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.nbt.api.BinaryTagHolder;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
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
/*    */ public class CustomClickEvent
/*    */   implements ClickEvent
/*    */ {
/*    */   private final ResourceLocation id;
/*    */   private final NBT payload;
/*    */   
/*    */   public CustomClickEvent(ResourceLocation id, NBT payload) {
/* 37 */     this.id = id;
/* 38 */     this.payload = payload;
/*    */   }
/*    */   
/*    */   public static CustomClickEvent decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 42 */     ResourceLocation id = (ResourceLocation)compound.getOrThrow("id", ResourceLocation::decode, wrapper);
/* 43 */     NBT payload = compound.getTagOrNull("payload");
/* 44 */     return new CustomClickEvent(id, payload);
/*    */   }
/*    */   
/*    */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, CustomClickEvent clickEvent) {
/* 48 */     compound.set("id", clickEvent.id, ResourceLocation::encode, wrapper);
/* 49 */     if (clickEvent.payload != null) {
/* 50 */       compound.setTag("payload", clickEvent.payload);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public ClickEventAction<?> getAction() {
/* 56 */     return ClickEventActions.CUSTOM;
/*    */   }
/*    */ 
/*    */   
/*    */   public ClickEvent asAdventure() {
/* 61 */     return ClickEvent.custom(this.id
/* 62 */         .key(), (BinaryTagHolder)new NbtTagHolder((this.payload != null) ? this.payload : (NBT)NBTEnd.INSTANCE));
/*    */   }
/*    */   
/*    */   public ResourceLocation getId() {
/* 66 */     return this.id;
/*    */   }
/*    */   
/*    */   public NBT getPayload() {
/* 70 */     return this.payload;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\clickevent\CustomClickEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */