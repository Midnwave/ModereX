/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.option;
/*     */ 
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.option.value.ValueType;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
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
/*     */ final class OptionSchemaImpl
/*     */   implements OptionSchema
/*     */ {
/*     */   final OptionState emptyState;
/*  39 */   final ConcurrentMap<String, Option<?>> options = new ConcurrentHashMap<>();
/*     */   
/*     */   OptionSchemaImpl(OptionSchemaImpl parent) {
/*  42 */     if (parent != null) {
/*  43 */       this.options.putAll(parent.options);
/*     */     }
/*  45 */     this.emptyState = new OptionStateImpl(this, new IdentityHashMap<>());
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Option<?>> knownOptions() {
/*  50 */     return Collections.unmodifiableSet(new HashSet<>(this.options.values()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean has(Option<?> option) {
/*  55 */     Option<?> own = this.options.get(option.id());
/*  56 */     return (own != null && own.equals(option));
/*     */   }
/*     */ 
/*     */   
/*     */   public OptionState.Builder stateBuilder() {
/*  61 */     return new OptionStateImpl.BuilderImpl(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public OptionState.VersionedBuilder versionedStateBuilder() {
/*  66 */     return new OptionStateImpl.VersionedBuilderImpl(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public OptionState emptyState() {
/*  71 */     return this.emptyState;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  76 */     return "OptionSchemaImpl{options=" + this.options + '}';
/*     */   }
/*     */ 
/*     */   
/*     */   static final class Instances
/*     */   {
/*  82 */     static OptionSchemaImpl.MutableImpl GLOBAL = new OptionSchemaImpl.MutableImpl();
/*     */   }
/*     */   
/*     */   final class MutableImpl
/*     */     implements OptionSchema.Mutable
/*     */   {
/*     */     <T> Option<T> register(String id, ValueType<T> type, T defaultValue) {
/*  89 */       Option<T> ret = new OptionImpl<>(Objects.<String>requireNonNull(id, "id"), Objects.<ValueType<T>>requireNonNull(type, "type"), defaultValue);
/*     */ 
/*     */ 
/*     */       
/*  93 */       if (OptionSchemaImpl.this.options.putIfAbsent(id, ret) != null) {
/*  94 */         throw new IllegalStateException("Key " + id + " has already been used. Option keys must be unique within a schema.");
/*     */       }
/*     */       
/*  97 */       return ret;
/*     */     }
/*     */ 
/*     */     
/*     */     public Option<String> stringOption(String id, String defaultValue) {
/* 102 */       return register(id, ValueType.stringType(), defaultValue);
/*     */     }
/*     */ 
/*     */     
/*     */     public Option<Boolean> booleanOption(String id, boolean defaultValue) {
/* 107 */       return register(id, ValueType.booleanType(), Boolean.valueOf(defaultValue));
/*     */     }
/*     */ 
/*     */     
/*     */     public Option<Integer> intOption(String id, int defaultValue) {
/* 112 */       return register(id, ValueType.integerType(), Integer.valueOf(defaultValue));
/*     */     }
/*     */ 
/*     */     
/*     */     public Option<Double> doubleOption(String id, double defaultValue) {
/* 117 */       return register(id, ValueType.doubleType(), Double.valueOf(defaultValue));
/*     */     }
/*     */ 
/*     */     
/*     */     public <E extends Enum<E>> Option<E> enumOption(String id, Class<E> enumClazz, E defaultValue) {
/* 122 */       return register(id, ValueType.enumType(enumClazz), defaultValue);
/*     */     }
/*     */ 
/*     */     
/*     */     public OptionSchema frozenView() {
/* 127 */       return OptionSchemaImpl.this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Set<Option<?>> knownOptions() {
/* 134 */       return OptionSchemaImpl.this.knownOptions();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean has(Option<?> option) {
/* 139 */       return OptionSchemaImpl.this.has(option);
/*     */     }
/*     */ 
/*     */     
/*     */     public OptionState.Builder stateBuilder() {
/* 144 */       return OptionSchemaImpl.this.stateBuilder();
/*     */     }
/*     */ 
/*     */     
/*     */     public OptionState.VersionedBuilder versionedStateBuilder() {
/* 149 */       return OptionSchemaImpl.this.versionedStateBuilder();
/*     */     }
/*     */ 
/*     */     
/*     */     public OptionState emptyState() {
/* 154 */       return OptionSchemaImpl.this.emptyState();
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 159 */       return "MutableImpl{schema=" + OptionSchemaImpl.this + "}";
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\option\OptionSchemaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */