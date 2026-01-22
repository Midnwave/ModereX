/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.flag;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import org.apiguardian.api.API;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class FlagContext
/*     */ {
/*  47 */   public static final Object FLAG_PRESENCE_VALUE = new Object();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   private final Map<String, List> flagValues = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FlagContext create() {
/*  61 */     return new FlagContext();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addPresenceFlag(CommandFlag<?> flag) {
/*  70 */     ((List<Object>)this.flagValues.computeIfAbsent(flag
/*  71 */         .name(), $ -> new ArrayList()))
/*     */       
/*  73 */       .add(FLAG_PRESENCE_VALUE);
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
/*     */   public <T> void addValueFlag(CommandFlag<T> flag, T value) {
/*  87 */     ((List<T>)this.flagValues.computeIfAbsent(flag
/*  88 */         .name(), $ -> new ArrayList()))
/*     */       
/*  90 */       .add(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public <T> int count(CommandFlag<T> flag) {
/* 102 */     return getAll(flag).size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public int count(String flag) {
/* 113 */     return getAll(flag).size();
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
/*     */   public boolean isPresent(String flag) {
/* 125 */     List value = this.flagValues.get(flag);
/* 126 */     return (value != null && !value.isEmpty());
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public boolean isPresent(CommandFlag<Void> flag) {
/* 139 */     return isPresent(flag.name());
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public <T> Optional<T> getValue(String name) {
/* 158 */     List<T> value = this.flagValues.get(name);
/* 159 */     if (value == null || value.isEmpty()) {
/* 160 */       return Optional.empty();
/*     */     }
/* 162 */     return Optional.of(value.get(0));
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public <T> Optional<T> getValue(CommandFlag<T> flag) {
/* 181 */     return getValue(flag.name());
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
/*     */ 
/*     */   
/*     */   public <T> T getValue(String name, T defaultValue) {
/* 201 */     return getValue(name).orElse(defaultValue);
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
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public <T> T getValue(CommandFlag<T> name, T defaultValue) {
/* 222 */     return getValue(name).orElse(defaultValue);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public boolean hasFlag(String name) {
/* 237 */     return getValue(name).isPresent();
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public boolean hasFlag(CommandFlag<?> flag) {
/* 252 */     return getValue(flag).isPresent();
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public boolean contains(String name) {
/* 265 */     return hasFlag(name);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public boolean contains(CommandFlag<?> flag) {
/* 278 */     return hasFlag(flag);
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
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public <T> T get(String name) {
/* 298 */     return getValue(name).orElse(null);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public <T> T get(CommandFlag<T> flag) {
/* 317 */     return getValue(flag).orElse(null);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public <T> Collection<T> getAll(CommandFlag<T> flag) {
/* 331 */     List<? extends T> values = this.flagValues.get(flag.name());
/* 332 */     if (values != null) {
/* 333 */       return Collections.unmodifiableList(values);
/*     */     }
/* 335 */     return Collections.emptyList();
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public <T> Collection<T> getAll(String flag) {
/* 349 */     List<? extends T> values = this.flagValues.get(flag);
/* 350 */     if (values != null) {
/* 351 */       return Collections.unmodifiableList(values);
/*     */     }
/* 353 */     return Collections.emptyList();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\flag\FlagContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */