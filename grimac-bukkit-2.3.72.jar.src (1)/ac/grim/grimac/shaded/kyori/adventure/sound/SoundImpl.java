/*     */ package ac.grim.grimac.shaded.kyori.adventure.sound;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.ShadyPines;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Objects;
/*     */ import java.util.OptionalLong;
/*     */ import java.util.function.Supplier;
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
/*     */ abstract class SoundImpl
/*     */   implements Sound
/*     */ {
/*  40 */   static final Sound.Emitter EMITTER_SELF = new Sound.Emitter()
/*     */     {
/*     */       public String toString() {
/*  43 */         return "SelfSoundEmitter";
/*     */       }
/*     */     };
/*     */   
/*     */   private final Sound.Source source;
/*     */   private final float volume;
/*     */   private final float pitch;
/*     */   private final OptionalLong seed;
/*     */   private SoundStop stop;
/*     */   
/*     */   SoundImpl(@NotNull Sound.Source source, float volume, float pitch, OptionalLong seed) {
/*  54 */     this.source = source;
/*  55 */     this.volume = volume;
/*  56 */     this.pitch = pitch;
/*  57 */     this.seed = seed;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Sound.Source source() {
/*  62 */     return this.source;
/*     */   }
/*     */ 
/*     */   
/*     */   public float volume() {
/*  67 */     return this.volume;
/*     */   }
/*     */ 
/*     */   
/*     */   public float pitch() {
/*  72 */     return this.pitch;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public OptionalLong seed() {
/*  77 */     return this.seed;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public SoundStop asStop() {
/*  82 */     if (this.stop == null) this.stop = SoundStop.namedOnSource(name(), source()); 
/*  83 */     return this.stop;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/*  88 */     if (this == other) return true; 
/*  89 */     if (!(other instanceof SoundImpl)) return false; 
/*  90 */     SoundImpl that = (SoundImpl)other;
/*  91 */     return (name().equals(that.name()) && this.source == that.source && 
/*     */       
/*  93 */       ShadyPines.equals(this.volume, that.volume) && 
/*  94 */       ShadyPines.equals(this.pitch, that.pitch) && this.seed
/*  95 */       .equals(that.seed));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 100 */     int result = name().hashCode();
/* 101 */     result = 31 * result + this.source.hashCode();
/* 102 */     result = 31 * result + Float.hashCode(this.volume);
/* 103 */     result = 31 * result + Float.hashCode(this.pitch);
/* 104 */     result = 31 * result + this.seed.hashCode();
/* 105 */     return result;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 110 */     return Stream.of(new ExaminableProperty[] {
/* 111 */           ExaminableProperty.of("name", name()), 
/* 112 */           ExaminableProperty.of("source", this.source), 
/* 113 */           ExaminableProperty.of("volume", this.volume), 
/* 114 */           ExaminableProperty.of("pitch", this.pitch), 
/* 115 */           ExaminableProperty.of("seed", this.seed)
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 121 */     return Internals.toString(this);
/*     */   }
/*     */   
/*     */   static final class BuilderImpl implements Sound.Builder {
/*     */     private static final float DEFAULT_VOLUME = 1.0F;
/*     */     private static final float DEFAULT_PITCH = 1.0F;
/*     */     private Key eagerType;
/*     */     private Supplier<? extends Sound.Type> lazyType;
/* 129 */     private Sound.Source source = Sound.Source.MASTER;
/* 130 */     private float volume = 1.0F;
/* 131 */     private float pitch = 1.0F;
/* 132 */     private OptionalLong seed = OptionalLong.empty();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     BuilderImpl(@NotNull Sound existing) {
/* 138 */       if (existing instanceof SoundImpl.Eager) {
/* 139 */         type(((SoundImpl.Eager)existing).name);
/* 140 */       } else if (existing instanceof SoundImpl.Lazy) {
/* 141 */         type(((SoundImpl.Lazy)existing).supplier);
/*     */       } else {
/* 143 */         throw new IllegalArgumentException("Unknown sound type " + existing + ", must be Eager or Lazy");
/*     */       } 
/*     */       
/* 146 */       source(existing.source())
/* 147 */         .volume(existing.volume())
/* 148 */         .pitch(existing.pitch())
/* 149 */         .seed(existing.seed());
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Sound.Builder type(@NotNull Key type) {
/* 154 */       this.eagerType = Objects.<Key>requireNonNull(type, "type");
/* 155 */       this.lazyType = null;
/* 156 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Sound.Builder type(@NotNull Sound.Type type) {
/* 161 */       this.eagerType = Objects.<Key>requireNonNull(((Sound.Type)Objects.<Sound.Type>requireNonNull(type, "type")).key(), "type.key()");
/* 162 */       this.lazyType = null;
/* 163 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Sound.Builder type(@NotNull Supplier<? extends Sound.Type> typeSupplier) {
/* 168 */       this.lazyType = Objects.<Supplier<? extends Sound.Type>>requireNonNull(typeSupplier, "typeSupplier");
/* 169 */       this.eagerType = null;
/* 170 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Sound.Builder source(@NotNull Sound.Source source) {
/* 175 */       this.source = Objects.<Sound.Source>requireNonNull(source, "source");
/* 176 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Sound.Builder source(Sound.Source.Provider source) {
/* 181 */       return source(source.soundSource());
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Sound.Builder volume(float volume) {
/* 186 */       this.volume = volume;
/* 187 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Sound.Builder pitch(float pitch) {
/* 192 */       this.pitch = pitch;
/* 193 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Sound.Builder seed(long seed) {
/* 198 */       this.seed = OptionalLong.of(seed);
/* 199 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Sound.Builder seed(@NotNull OptionalLong seed) {
/* 204 */       this.seed = Objects.<OptionalLong>requireNonNull(seed, "seed");
/* 205 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Sound build() {
/* 210 */       if (this.eagerType != null)
/* 211 */         return new SoundImpl.Eager(this.eagerType, this.source, this.volume, this.pitch, this.seed); 
/* 212 */       if (this.lazyType != null) {
/* 213 */         return new SoundImpl.Lazy(this.lazyType, this.source, this.volume, this.pitch, this.seed);
/*     */       }
/* 215 */       throw new IllegalStateException("A sound type must be provided to build a sound");
/*     */     }
/*     */     
/*     */     BuilderImpl() {} }
/*     */   
/*     */   static final class Eager extends SoundImpl {
/*     */     final Key name;
/*     */     
/*     */     Eager(@NotNull Key name, @NotNull Sound.Source source, float volume, float pitch, OptionalLong seed) {
/* 224 */       super(source, volume, pitch, seed);
/* 225 */       this.name = name;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Key name() {
/* 230 */       return this.name;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class Lazy extends SoundImpl {
/*     */     final Supplier<? extends Sound.Type> supplier;
/*     */     
/*     */     Lazy(@NotNull Supplier<? extends Sound.Type> supplier, @NotNull Sound.Source source, float volume, float pitch, OptionalLong seed) {
/* 238 */       super(source, volume, pitch, seed);
/* 239 */       this.supplier = supplier;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Key name() {
/* 244 */       return ((Sound.Type)this.supplier.get()).key();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\sound\SoundImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */