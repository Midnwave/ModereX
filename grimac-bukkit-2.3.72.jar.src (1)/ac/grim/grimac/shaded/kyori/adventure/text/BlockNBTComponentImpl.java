/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.ShadyPines;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.regex.Pattern;
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
/*     */ final class BlockNBTComponentImpl
/*     */   extends NBTComponentImpl<BlockNBTComponent, BlockNBTComponent.Builder>
/*     */   implements BlockNBTComponent
/*     */ {
/*     */   private final BlockNBTComponent.Pos pos;
/*     */   
/*     */   static BlockNBTComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable ComponentLike separator, @NotNull BlockNBTComponent.Pos pos) {
/*  43 */     return new BlockNBTComponentImpl(
/*  44 */         ComponentLike.asComponents(children, IS_NOT_EMPTY), 
/*  45 */         Objects.<Style>requireNonNull(style, "style"), 
/*  46 */         Objects.<String>requireNonNull(nbtPath, "nbtPath"), interpret, 
/*     */         
/*  48 */         ComponentLike.unbox(separator), 
/*  49 */         Objects.<BlockNBTComponent.Pos>requireNonNull(pos, "pos"));
/*     */   }
/*     */ 
/*     */   
/*     */   BlockNBTComponentImpl(@NotNull List<Component> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable Component separator, @NotNull BlockNBTComponent.Pos pos) {
/*  54 */     super(children, style, nbtPath, interpret, separator);
/*  55 */     this.pos = pos;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BlockNBTComponent nbtPath(@NotNull String nbtPath) {
/*  60 */     if (Objects.equals(this.nbtPath, nbtPath)) return this; 
/*  61 */     return create((List)this.children, this.style, nbtPath, this.interpret, this.separator, this.pos);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BlockNBTComponent interpret(boolean interpret) {
/*  66 */     if (this.interpret == interpret) return this; 
/*  67 */     return create((List)this.children, this.style, this.nbtPath, interpret, this.separator, this.pos);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Component separator() {
/*  72 */     return this.separator;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BlockNBTComponent separator(@Nullable ComponentLike separator) {
/*  77 */     return create((List)this.children, this.style, this.nbtPath, this.interpret, separator, this.pos);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BlockNBTComponent.Pos pos() {
/*  82 */     return this.pos;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BlockNBTComponent pos(@NotNull BlockNBTComponent.Pos pos) {
/*  87 */     return create((List)this.children, this.style, this.nbtPath, this.interpret, this.separator, pos);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BlockNBTComponent children(@NotNull List<? extends ComponentLike> children) {
/*  92 */     return create(children, this.style, this.nbtPath, this.interpret, this.separator, this.pos);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BlockNBTComponent style(@NotNull Style style) {
/*  97 */     return create((List)this.children, style, this.nbtPath, this.interpret, this.separator, this.pos);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 102 */     if (this == other) return true; 
/* 103 */     if (!(other instanceof BlockNBTComponent)) return false; 
/* 104 */     if (!super.equals(other)) return false; 
/* 105 */     BlockNBTComponent that = (BlockNBTComponent)other;
/* 106 */     return Objects.equals(this.pos, that.pos());
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 111 */     int result = super.hashCode();
/* 112 */     result = 31 * result + this.pos.hashCode();
/* 113 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 118 */     return Internals.toString(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public BlockNBTComponent.Builder toBuilder() {
/* 123 */     return new BuilderImpl(this);
/*     */   }
/*     */   
/*     */   static final class BuilderImpl extends AbstractNBTComponentBuilder<BlockNBTComponent, BlockNBTComponent.Builder> implements BlockNBTComponent.Builder {
/*     */     @Nullable
/*     */     private BlockNBTComponent.Pos pos;
/*     */     
/*     */     BuilderImpl() {}
/*     */     
/*     */     BuilderImpl(@NotNull BlockNBTComponent component) {
/* 133 */       super(component);
/* 134 */       this.pos = component.pos();
/*     */     }
/*     */ 
/*     */     
/*     */     public BlockNBTComponent.Builder pos(@NotNull BlockNBTComponent.Pos pos) {
/* 139 */       this.pos = Objects.<BlockNBTComponent.Pos>requireNonNull(pos, "pos");
/* 140 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public BlockNBTComponent build() {
/* 145 */       if (this.nbtPath == null) throw new IllegalStateException("nbt path must be set"); 
/* 146 */       if (this.pos == null) throw new IllegalStateException("pos must be set"); 
/* 147 */       return BlockNBTComponentImpl.create((List)this.children, buildStyle(), this.nbtPath, this.interpret, this.separator, this.pos);
/*     */     }
/*     */   }
/*     */   
/*     */   static final class LocalPosImpl implements BlockNBTComponent.LocalPos {
/*     */     private final double left;
/*     */     private final double up;
/*     */     private final double forwards;
/*     */     
/*     */     LocalPosImpl(double left, double up, double forwards) {
/* 157 */       this.left = left;
/* 158 */       this.up = up;
/* 159 */       this.forwards = forwards;
/*     */     }
/*     */ 
/*     */     
/*     */     public double left() {
/* 164 */       return this.left;
/*     */     }
/*     */ 
/*     */     
/*     */     public double up() {
/* 169 */       return this.up;
/*     */     }
/*     */ 
/*     */     
/*     */     public double forwards() {
/* 174 */       return this.forwards;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Stream<? extends ExaminableProperty> examinableProperties() {
/* 179 */       return Stream.of(new ExaminableProperty[] {
/* 180 */             ExaminableProperty.of("left", this.left), 
/* 181 */             ExaminableProperty.of("up", this.up), 
/* 182 */             ExaminableProperty.of("forwards", this.forwards)
/*     */           });
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object other) {
/* 188 */       if (this == other) return true; 
/* 189 */       if (!(other instanceof BlockNBTComponent.LocalPos)) return false; 
/* 190 */       BlockNBTComponent.LocalPos that = (BlockNBTComponent.LocalPos)other;
/* 191 */       return (ShadyPines.equals(that.left(), left()) && 
/* 192 */         ShadyPines.equals(that.up(), up()) && 
/* 193 */         ShadyPines.equals(that.forwards(), forwards()));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 198 */       int result = Double.hashCode(this.left);
/* 199 */       result = 31 * result + Double.hashCode(this.up);
/* 200 */       result = 31 * result + Double.hashCode(this.forwards);
/* 201 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 206 */       return String.format("^%f ^%f ^%f", new Object[] { Double.valueOf(this.left), Double.valueOf(this.up), Double.valueOf(this.forwards) });
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public String asString() {
/* 211 */       return BlockNBTComponentImpl.Tokens.serializeLocal(this.left) + ' ' + BlockNBTComponentImpl.Tokens.serializeLocal(this.up) + ' ' + BlockNBTComponentImpl.Tokens.serializeLocal(this.forwards);
/*     */     }
/*     */   }
/*     */   
/*     */   static final class WorldPosImpl implements BlockNBTComponent.WorldPos {
/*     */     private final BlockNBTComponent.WorldPos.Coordinate x;
/*     */     private final BlockNBTComponent.WorldPos.Coordinate y;
/*     */     private final BlockNBTComponent.WorldPos.Coordinate z;
/*     */     
/*     */     WorldPosImpl(BlockNBTComponent.WorldPos.Coordinate x, BlockNBTComponent.WorldPos.Coordinate y, BlockNBTComponent.WorldPos.Coordinate z) {
/* 221 */       this.x = Objects.<BlockNBTComponent.WorldPos.Coordinate>requireNonNull(x, "x");
/* 222 */       this.y = Objects.<BlockNBTComponent.WorldPos.Coordinate>requireNonNull(y, "y");
/* 223 */       this.z = Objects.<BlockNBTComponent.WorldPos.Coordinate>requireNonNull(z, "z");
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public BlockNBTComponent.WorldPos.Coordinate x() {
/* 228 */       return this.x;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public BlockNBTComponent.WorldPos.Coordinate y() {
/* 233 */       return this.y;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public BlockNBTComponent.WorldPos.Coordinate z() {
/* 238 */       return this.z;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Stream<? extends ExaminableProperty> examinableProperties() {
/* 243 */       return Stream.of(new ExaminableProperty[] {
/* 244 */             ExaminableProperty.of("x", this.x), 
/* 245 */             ExaminableProperty.of("y", this.y), 
/* 246 */             ExaminableProperty.of("z", this.z)
/*     */           });
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object other) {
/* 252 */       if (this == other) return true; 
/* 253 */       if (!(other instanceof BlockNBTComponent.WorldPos)) return false; 
/* 254 */       BlockNBTComponent.WorldPos that = (BlockNBTComponent.WorldPos)other;
/* 255 */       return (this.x.equals(that.x()) && this.y
/* 256 */         .equals(that.y()) && this.z
/* 257 */         .equals(that.z()));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 262 */       int result = this.x.hashCode();
/* 263 */       result = 31 * result + this.y.hashCode();
/* 264 */       result = 31 * result + this.z.hashCode();
/* 265 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 270 */       return this.x.toString() + ' ' + this.y.toString() + ' ' + this.z.toString();
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public String asString() {
/* 275 */       return BlockNBTComponentImpl.Tokens.serializeCoordinate(x()) + ' ' + BlockNBTComponentImpl.Tokens.serializeCoordinate(y()) + ' ' + BlockNBTComponentImpl.Tokens.serializeCoordinate(z());
/*     */     }
/*     */     
/*     */     static final class CoordinateImpl implements BlockNBTComponent.WorldPos.Coordinate {
/*     */       private final int value;
/*     */       private final BlockNBTComponent.WorldPos.Coordinate.Type type;
/*     */       
/*     */       CoordinateImpl(int value, @NotNull BlockNBTComponent.WorldPos.Coordinate.Type type) {
/* 283 */         this.value = value;
/* 284 */         this.type = Objects.<BlockNBTComponent.WorldPos.Coordinate.Type>requireNonNull(type, "type");
/*     */       }
/*     */ 
/*     */       
/*     */       public int value() {
/* 289 */         return this.value;
/*     */       }
/*     */       
/*     */       @NotNull
/*     */       public BlockNBTComponent.WorldPos.Coordinate.Type type() {
/* 294 */         return this.type;
/*     */       }
/*     */       
/*     */       @NotNull
/*     */       public Stream<? extends ExaminableProperty> examinableProperties() {
/* 299 */         return Stream.of(new ExaminableProperty[] {
/* 300 */               ExaminableProperty.of("value", this.value), 
/* 301 */               ExaminableProperty.of("type", this.type)
/*     */             });
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean equals(@Nullable Object other) {
/* 307 */         if (this == other) return true; 
/* 308 */         if (!(other instanceof BlockNBTComponent.WorldPos.Coordinate)) return false; 
/* 309 */         BlockNBTComponent.WorldPos.Coordinate that = (BlockNBTComponent.WorldPos.Coordinate)other;
/* 310 */         return (value() == that.value() && 
/* 311 */           type() == that.type());
/*     */       }
/*     */ 
/*     */       
/*     */       public int hashCode() {
/* 316 */         int result = this.value;
/* 317 */         result = 31 * result + this.type.hashCode();
/* 318 */         return result;
/*     */       }
/*     */ 
/*     */       
/*     */       public String toString() {
/* 323 */         return ((this.type == BlockNBTComponent.WorldPos.Coordinate.Type.RELATIVE) ? "~" : "") + this.value;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   static final class Tokens {
/* 329 */     static final Pattern LOCAL_PATTERN = Pattern.compile("^\\^(-?\\d+(\\.\\d+)?) \\^(-?\\d+(\\.\\d+)?) \\^(-?\\d+(\\.\\d+)?)$");
/* 330 */     static final Pattern WORLD_PATTERN = Pattern.compile("^(~?)(-?\\d+) (~?)(-?\\d+) (~?)(-?\\d+)$");
/*     */     
/*     */     static final String LOCAL_SYMBOL = "^";
/*     */     
/*     */     static final String RELATIVE_SYMBOL = "~";
/*     */     
/*     */     static final String ABSOLUTE_SYMBOL = "";
/*     */ 
/*     */     
/*     */     static BlockNBTComponent.WorldPos.Coordinate deserializeCoordinate(String prefix, String value) {
/* 340 */       int i = Integer.parseInt(value);
/* 341 */       if (prefix.equals(""))
/* 342 */         return BlockNBTComponent.WorldPos.Coordinate.absolute(i); 
/* 343 */       if (prefix.equals("~")) {
/* 344 */         return BlockNBTComponent.WorldPos.Coordinate.relative(i);
/*     */       }
/* 346 */       throw new AssertionError();
/*     */     }
/*     */ 
/*     */     
/*     */     static String serializeLocal(double value) {
/* 351 */       return "^" + value;
/*     */     }
/*     */     
/*     */     static String serializeCoordinate(BlockNBTComponent.WorldPos.Coordinate coordinate) {
/* 355 */       return ((coordinate.type() == BlockNBTComponent.WorldPos.Coordinate.Type.RELATIVE) ? "~" : "") + coordinate.value();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\BlockNBTComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */