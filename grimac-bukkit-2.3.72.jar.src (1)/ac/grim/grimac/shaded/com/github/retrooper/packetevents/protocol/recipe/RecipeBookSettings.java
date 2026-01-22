/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ public final class RecipeBookSettings
/*     */ {
/*     */   private final Map<RecipeBookType, TypeState> states;
/*     */   
/*     */   public RecipeBookSettings(Map<RecipeBookType, TypeState> states) {
/*  32 */     this.states = states;
/*     */   }
/*     */   
/*     */   public static RecipeBookSettings read(PacketWrapper<?> wrapper) {
/*  36 */     Map<RecipeBookType, TypeState> state = new EnumMap<>(RecipeBookType.class);
/*  37 */     for (RecipeBookType bookType : RecipeBookType.values()) {
/*  38 */       state.put(bookType, TypeState.read(wrapper));
/*     */     }
/*  40 */     return new RecipeBookSettings(state);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, RecipeBookSettings settings) {
/*  44 */     for (RecipeBookType bookType : RecipeBookType.values()) {
/*  45 */       TypeState.write(wrapper, settings.getState(bookType));
/*     */     }
/*     */   }
/*     */   
/*     */   public TypeState getState(RecipeBookType type) {
/*  50 */     return this.states.computeIfAbsent(type, $ -> new TypeState());
/*     */   }
/*     */   
/*     */   public void setState(RecipeBookType type, TypeState state) {
/*  54 */     this.states.put(type, state);
/*     */   }
/*     */   
/*     */   public Map<RecipeBookType, TypeState> getStates() {
/*  58 */     return this.states;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  63 */     if (this == obj) return true; 
/*  64 */     if (!(obj instanceof RecipeBookSettings)) return false; 
/*  65 */     RecipeBookSettings that = (RecipeBookSettings)obj;
/*  66 */     return this.states.equals(that.states);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  71 */     return Objects.hashCode(this.states);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  76 */     return "RecipeBookSettings{states=" + this.states + '}';
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class TypeState
/*     */   {
/*     */     private static final boolean DEFAULT_OPEN = false;
/*     */     private static final boolean DEFAULT_FILTERING = false;
/*     */     private boolean open;
/*     */     private boolean filtering;
/*     */     
/*     */     public TypeState() {
/*  88 */       this(false, false);
/*     */     }
/*     */     
/*     */     public TypeState(boolean open, boolean filtering) {
/*  92 */       this.open = open;
/*  93 */       this.filtering = filtering;
/*     */     }
/*     */     
/*     */     public static TypeState read(PacketWrapper<?> wrapper) {
/*  97 */       boolean open = wrapper.readBoolean();
/*  98 */       boolean filtering = wrapper.readBoolean();
/*  99 */       return new TypeState(open, filtering);
/*     */     }
/*     */     
/*     */     public static void write(PacketWrapper<?> wrapper, TypeState state) {
/* 103 */       wrapper.writeBoolean(state.open);
/* 104 */       wrapper.writeBoolean(state.filtering);
/*     */     }
/*     */     
/*     */     public boolean isOpen() {
/* 108 */       return this.open;
/*     */     }
/*     */     
/*     */     public void setOpen(boolean open) {
/* 112 */       this.open = open;
/*     */     }
/*     */     
/*     */     public boolean isFiltering() {
/* 116 */       return this.filtering;
/*     */     }
/*     */     
/*     */     public void setFiltering(boolean filtering) {
/* 120 */       this.filtering = filtering;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 125 */       if (this == obj) return true; 
/* 126 */       if (!(obj instanceof TypeState)) return false; 
/* 127 */       TypeState typeState = (TypeState)obj;
/* 128 */       if (this.open != typeState.open) return false; 
/* 129 */       return (this.filtering == typeState.filtering);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 134 */       return Objects.hash(new Object[] { Boolean.valueOf(this.open), Boolean.valueOf(this.filtering) });
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 139 */       return "TypeState{open=" + this.open + ", filtering=" + this.filtering + '}';
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\RecipeBookSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */