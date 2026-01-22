/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.BlockNBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.EntityNBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.NBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.StorageNBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.ParsingException;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.Emitable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class NbtTag
/*     */ {
/*     */   private static final String NBT = "nbt";
/*     */   private static final String DATA = "data";
/*     */   private static final String BLOCK = "block";
/*     */   private static final String ENTITY = "entity";
/*     */   private static final String STORAGE = "storage";
/*     */   private static final String INTERPRET = "interpret";
/*  56 */   static final TagResolver RESOLVER = SerializableResolver.claimingComponent(
/*  57 */       StandardTags.names(new String[] { "nbt", "data" }, ), NbtTag::resolve, NbtTag::emit);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Tag resolve(ArgumentQueue args, Context ctx) throws ParsingException {
/*     */     StorageNBTComponent.Builder builder;
/*  67 */     String type = args.popOr("a type of block, entity, or storage is required").lowerValue();
/*     */     
/*  69 */     if ("block".equals(type)) {
/*  70 */       String pos = args.popOr("A position is required").value();
/*     */       
/*     */       try {
/*  73 */         BlockNBTComponent.Builder builder1 = Component.blockNBT().pos(BlockNBTComponent.Pos.fromString(pos));
/*  74 */       } catch (IllegalArgumentException ex) {
/*  75 */         throw ctx.newException(ex.getMessage(), args);
/*     */       } 
/*  77 */     } else if ("entity".equals(type)) {
/*     */       
/*  79 */       EntityNBTComponent.Builder builder1 = Component.entityNBT().selector(args.popOr("A selector is required").value());
/*  80 */     } else if ("storage".equals(type)) {
/*     */       
/*  82 */       builder = Component.storageNBT().storage(Key.key(args.popOr("A storage key is required").value()));
/*     */     } else {
/*  84 */       throw ctx.newException("Unknown nbt tag type '" + type + "'", args);
/*     */     } 
/*     */     
/*  87 */     builder.nbtPath(args.popOr("An NBT path is required").value());
/*     */     
/*  89 */     if (args.hasNext()) {
/*  90 */       String popped = args.pop().value();
/*     */       
/*  92 */       if ("interpret".equalsIgnoreCase(popped)) {
/*  93 */         builder.interpret(true);
/*     */       } else {
/*  95 */         builder.separator((ComponentLike)ctx.deserialize(popped));
/*     */         
/*  97 */         if (args.hasNext() && args.pop().value().equalsIgnoreCase("interpret")) {
/*  98 */           builder.interpret(true);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 103 */     return Tag.inserting((Component)builder.build());
/*     */   }
/*     */   @Nullable
/*     */   static Emitable emit(Component comp) {
/*     */     String type;
/*     */     String id;
/* 109 */     if (comp instanceof BlockNBTComponent) {
/* 110 */       type = "block";
/* 111 */       id = ((BlockNBTComponent)comp).pos().asString();
/* 112 */     } else if (comp instanceof EntityNBTComponent) {
/* 113 */       type = "entity";
/* 114 */       id = ((EntityNBTComponent)comp).selector();
/* 115 */     } else if (comp instanceof StorageNBTComponent) {
/* 116 */       type = "storage";
/* 117 */       id = ((StorageNBTComponent)comp).storage().asString();
/*     */     } else {
/* 119 */       return null;
/*     */     } 
/*     */     
/* 122 */     return out -> {
/*     */         NBTComponent<?, ?> nbt = (NBTComponent<?, ?>)comp;
/*     */         out.tag("nbt").argument(type).argument(id).argument(nbt.nbtPath());
/*     */         if (nbt.separator() != null)
/*     */           out.argument(nbt.separator()); 
/*     */         if (nbt.interpret())
/*     */           out.argument("interpret"); 
/*     */       };
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\NbtTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */