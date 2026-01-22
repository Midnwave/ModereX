/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.stream.Stream;
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
/*     */ public interface BlockNBTComponent
/*     */   extends NBTComponent<BlockNBTComponent, BlockNBTComponent.Builder>, ScopedComponent<BlockNBTComponent>
/*     */ {
/*     */   @NotNull
/*     */   Pos pos();
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   BlockNBTComponent pos(@NotNull Pos paramPos);
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   default BlockNBTComponent localPos(double left, double up, double forwards) {
/*  79 */     return pos(LocalPos.localPos(left, up, forwards));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   default BlockNBTComponent worldPos(WorldPos.Coordinate x, WorldPos.Coordinate y, WorldPos.Coordinate z) {
/*  93 */     return pos(WorldPos.worldPos(x, y, z));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   default BlockNBTComponent absoluteWorldPos(int x, int y, int z) {
/* 107 */     return worldPos(WorldPos.Coordinate.absolute(x), WorldPos.Coordinate.absolute(y), WorldPos.Coordinate.absolute(z));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   default BlockNBTComponent relativeWorldPos(int x, int y, int z) {
/* 121 */     return worldPos(WorldPos.Coordinate.relative(x), WorldPos.Coordinate.relative(y), WorldPos.Coordinate.relative(z));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   default Stream<? extends ExaminableProperty> examinableProperties() {
/* 126 */     return Stream.concat(
/* 127 */         Stream.of(
/* 128 */           ExaminableProperty.of("pos", pos())), super
/*     */         
/* 130 */         .examinableProperties());
/*     */   }
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
/*     */   public static interface Builder
/*     */     extends NBTComponentBuilder<BlockNBTComponent, Builder>
/*     */   {
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder pos(@NotNull BlockNBTComponent.Pos param1Pos);
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
/*     */     @Contract("_, _, _ -> this")
/*     */     @NotNull
/*     */     default Builder localPos(double left, double up, double forwards) {
/* 161 */       return pos(BlockNBTComponent.LocalPos.localPos(left, up, forwards));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract("_, _, _ -> this")
/*     */     @NotNull
/*     */     default Builder worldPos(BlockNBTComponent.WorldPos.Coordinate x, BlockNBTComponent.WorldPos.Coordinate y, BlockNBTComponent.WorldPos.Coordinate z) {
/* 175 */       return pos(BlockNBTComponent.WorldPos.worldPos(x, y, z));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract("_, _, _ -> this")
/*     */     @NotNull
/*     */     default Builder absoluteWorldPos(int x, int y, int z) {
/* 189 */       return worldPos(BlockNBTComponent.WorldPos.Coordinate.absolute(x), BlockNBTComponent.WorldPos.Coordinate.absolute(y), BlockNBTComponent.WorldPos.Coordinate.absolute(z));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract("_, _, _ -> this")
/*     */     @NotNull
/*     */     default Builder relativeWorldPos(int x, int y, int z) {
/* 203 */       return worldPos(BlockNBTComponent.WorldPos.Coordinate.relative(x), BlockNBTComponent.WorldPos.Coordinate.relative(y), BlockNBTComponent.WorldPos.Coordinate.relative(z));
/*     */     }
/*     */   }
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
/*     */   public static interface Pos
/*     */     extends Examinable
/*     */   {
/*     */     @NotNull
/*     */     static Pos fromString(@NotNull String input) throws IllegalArgumentException {
/* 226 */       Matcher localMatch = BlockNBTComponentImpl.Tokens.LOCAL_PATTERN.matcher(input);
/* 227 */       if (localMatch.matches()) {
/* 228 */         return BlockNBTComponent.LocalPos.localPos(
/* 229 */             Double.parseDouble(localMatch.group(1)), 
/* 230 */             Double.parseDouble(localMatch.group(3)), 
/* 231 */             Double.parseDouble(localMatch.group(5)));
/*     */       }
/*     */ 
/*     */       
/* 235 */       Matcher worldMatch = BlockNBTComponentImpl.Tokens.WORLD_PATTERN.matcher(input);
/* 236 */       if (worldMatch.matches()) {
/* 237 */         return BlockNBTComponent.WorldPos.worldPos(
/* 238 */             BlockNBTComponentImpl.Tokens.deserializeCoordinate(worldMatch.group(1), worldMatch.group(2)), 
/* 239 */             BlockNBTComponentImpl.Tokens.deserializeCoordinate(worldMatch.group(3), worldMatch.group(4)), 
/* 240 */             BlockNBTComponentImpl.Tokens.deserializeCoordinate(worldMatch.group(5), worldMatch.group(6)));
/*     */       }
/*     */ 
/*     */       
/* 244 */       throw new IllegalArgumentException("Cannot convert position specification '" + input + "' into a position");
/*     */     }
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
/*     */     @NotNull
/*     */     String asString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface LocalPos
/*     */     extends Pos
/*     */   {
/*     */     @NotNull
/*     */     static LocalPos localPos(double left, double up, double forwards) {
/* 273 */       return new BlockNBTComponentImpl.LocalPosImpl(left, up, forwards);
/*     */     }
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
/*     */     @Deprecated
/*     */     @ScheduledForRemoval(inVersion = "5.0.0")
/*     */     @NotNull
/*     */     static LocalPos of(double left, double up, double forwards) {
/* 289 */       return new BlockNBTComponentImpl.LocalPosImpl(left, up, forwards);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     double left();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     double up();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     double forwards();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface WorldPos
/*     */     extends Pos
/*     */   {
/*     */     @NotNull
/*     */     static WorldPos worldPos(@NotNull Coordinate x, @NotNull Coordinate y, @NotNull Coordinate z) {
/* 333 */       return new BlockNBTComponentImpl.WorldPosImpl(x, y, z);
/*     */     }
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
/*     */     @Deprecated
/*     */     @ScheduledForRemoval(inVersion = "5.0.0")
/*     */     @NotNull
/*     */     static WorldPos of(@NotNull Coordinate x, @NotNull Coordinate y, @NotNull Coordinate z) {
/* 349 */       return new BlockNBTComponentImpl.WorldPosImpl(x, y, z);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     Coordinate x();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     Coordinate y();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     Coordinate z();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static interface Coordinate
/*     */       extends Examinable
/*     */     {
/*     */       @NotNull
/*     */       static Coordinate absolute(int value) {
/* 390 */         return coordinate(value, Type.ABSOLUTE);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       @NotNull
/*     */       static Coordinate relative(int value) {
/* 401 */         return coordinate(value, Type.RELATIVE);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       @NotNull
/*     */       static Coordinate coordinate(int value, @NotNull Type type) {
/* 413 */         return new BlockNBTComponentImpl.WorldPosImpl.CoordinateImpl(value, type);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       @Deprecated
/*     */       @ScheduledForRemoval(inVersion = "5.0.0")
/*     */       @NotNull
/*     */       static Coordinate of(int value, @NotNull Type type) {
/* 428 */         return new BlockNBTComponentImpl.WorldPosImpl.CoordinateImpl(value, type);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       int value();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       @NotNull
/*     */       Type type();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public enum Type
/*     */       {
/* 458 */         ABSOLUTE,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 464 */         RELATIVE; } } } public static interface Coordinate extends Examinable { @NotNull static Coordinate absolute(int value) { return coordinate(value, Type.ABSOLUTE); } @NotNull static Coordinate relative(int value) { return coordinate(value, Type.RELATIVE); } @NotNull static Coordinate coordinate(int value, @NotNull Type type) { return new BlockNBTComponentImpl.WorldPosImpl.CoordinateImpl(value, type); } @Deprecated @ScheduledForRemoval(inVersion = "5.0.0") @NotNull static Coordinate of(int value, @NotNull Type type) { return new BlockNBTComponentImpl.WorldPosImpl.CoordinateImpl(value, type); } int value(); @NotNull Type type(); public enum Type { ABSOLUTE, RELATIVE; }
/*     */      }
/*     */ 
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\BlockNBTComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */