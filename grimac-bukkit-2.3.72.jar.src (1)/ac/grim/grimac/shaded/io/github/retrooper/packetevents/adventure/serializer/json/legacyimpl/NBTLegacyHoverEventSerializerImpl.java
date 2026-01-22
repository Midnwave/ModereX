/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer.json.legacyimpl;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer.gson.BackwardCompatUtil;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer.json.LegacyHoverEventSerializer;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.nbt.BinaryTag;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.nbt.CompoundBinaryTag;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.nbt.TagStringIO;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.nbt.api.BinaryTagHolder;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEvent;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.util.Codec;
/*    */ import java.io.IOException;
/*    */ import java.util.Objects;
/*    */ import java.util.UUID;
/*    */ 
/*    */ final class NBTLegacyHoverEventSerializerImpl implements LegacyHoverEventSerializer {
/* 19 */   static final NBTLegacyHoverEventSerializerImpl INSTANCE = new NBTLegacyHoverEventSerializerImpl();
/* 20 */   private static final TagStringIO SNBT_IO = TagStringIO.get();
/*    */   
/* 22 */   private static final Codec<CompoundBinaryTag, String, IOException, IOException> SNBT_CODEC = BackwardCompatUtil.createCodec(SNBT_IO::asCompound, SNBT_IO::asString); static { Objects.requireNonNull(SNBT_IO); Objects.requireNonNull(SNBT_IO); }
/*    */ 
/*    */ 
/*    */   
/*    */   static final String ITEM_TYPE = "id";
/*    */   
/*    */   static final String ITEM_COUNT = "Count";
/*    */   
/*    */   static final String ITEM_TAG = "tag";
/*    */   
/*    */   static final String ENTITY_NAME = "name";
/*    */   
/*    */   static final String ENTITY_TYPE = "type";
/*    */   static final String ENTITY_ID = "id";
/*    */   
/*    */   public HoverEvent.ShowItem deserializeShowItem(@NotNull Component input) throws IOException {
/* 38 */     assertTextComponent(input);
/* 39 */     CompoundBinaryTag contents = (CompoundBinaryTag)SNBT_CODEC.decode(((TextComponent)input).content());
/* 40 */     CompoundBinaryTag tag = contents.getCompound("tag");
/*    */     
/* 42 */     return BackwardCompatUtil.createShowItem(
/* 43 */         Key.key(contents.getString("id")), contents
/* 44 */         .getByte("Count", (byte)1), 
/* 45 */         (tag == CompoundBinaryTag.empty()) ? null : BinaryTagHolder.encode(tag, SNBT_CODEC));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public Component serializeShowItem(HoverEvent.ShowItem input) throws IOException {
/* 54 */     CompoundBinaryTag.Builder builder = (CompoundBinaryTag.Builder)((CompoundBinaryTag.Builder)CompoundBinaryTag.builder().putString("id", input.item().asString())).putByte("Count", (byte)input.count());
/* 55 */     BinaryTagHolder nbt = input.nbt();
/* 56 */     if (nbt != null) {
/* 57 */       builder.put("tag", (BinaryTag)nbt.get(SNBT_CODEC));
/*    */     }
/* 59 */     return (Component)Component.text((String)SNBT_CODEC.encode(builder.build()));
/*    */   }
/*    */ 
/*    */   
/*    */   public HoverEvent.ShowEntity deserializeShowEntity(@NotNull Component input, Codec.Decoder<Component, String, ? extends RuntimeException> componentCodec) throws IOException {
/* 64 */     assertTextComponent(input);
/* 65 */     CompoundBinaryTag contents = (CompoundBinaryTag)SNBT_CODEC.decode(((TextComponent)input).content());
/*    */     
/* 67 */     return BackwardCompatUtil.createShowEntity(
/* 68 */         Key.key(contents.getString("type")), 
/* 69 */         UUID.fromString(contents.getString("id")), (Component)componentCodec
/* 70 */         .decode(contents.getString("name")));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public Component serializeShowEntity(HoverEvent.ShowEntity input, Codec.Encoder<Component, String, ? extends RuntimeException> componentCodec) throws IOException {
/* 79 */     CompoundBinaryTag.Builder builder = (CompoundBinaryTag.Builder)((CompoundBinaryTag.Builder)CompoundBinaryTag.builder().putString("id", input.id().toString())).putString("type", input.type().asString());
/* 80 */     Component name = input.name();
/* 81 */     if (name != null) {
/* 82 */       builder.putString("name", (String)componentCodec.encode(name));
/*    */     }
/* 84 */     return (Component)Component.text((String)SNBT_CODEC.encode(builder.build()));
/*    */   }
/*    */   
/*    */   private static void assertTextComponent(Component component) {
/* 88 */     if (!(component instanceof TextComponent) || !component.children().isEmpty())
/* 89 */       throw new IllegalArgumentException("Legacy events must be single Component instances"); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\serializer\json\legacyimpl\NBTLegacyHoverEventSerializerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */