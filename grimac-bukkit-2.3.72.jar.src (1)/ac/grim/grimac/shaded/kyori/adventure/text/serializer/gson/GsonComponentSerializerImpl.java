/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.JSONComponentSerializer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.JSONOptions;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Services;
/*     */ import ac.grim.grimac.shaded.kyori.option.OptionState;
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.GsonBuilder;
/*     */ import com.google.gson.JsonElement;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.UnaryOperator;
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
/*     */ final class GsonComponentSerializerImpl
/*     */   implements GsonComponentSerializer
/*     */ {
/*  42 */   private static final Optional<GsonComponentSerializer.Provider> SERVICE = Services.service(GsonComponentSerializer.Provider.class); private final Gson serializer; private final UnaryOperator<GsonBuilder> populator;
/*  43 */   static final Consumer<GsonComponentSerializer.Builder> BUILDER = SERVICE
/*  44 */     .<Consumer<GsonComponentSerializer.Builder>>map(GsonComponentSerializer.Provider::builder)
/*  45 */     .orElseGet(() -> ());
/*     */   private final LegacyHoverEventSerializer legacyHoverSerializer;
/*     */   private final OptionState flags;
/*     */   
/*     */   static final class Instances
/*     */   {
/*  51 */     static final GsonComponentSerializer INSTANCE = GsonComponentSerializerImpl.SERVICE
/*  52 */       .map(GsonComponentSerializer.Provider::gson)
/*  53 */       .orElseGet(() -> new GsonComponentSerializerImpl((OptionState)JSONOptions.byDataVersion(), null));
/*  54 */     static final GsonComponentSerializer LEGACY_INSTANCE = GsonComponentSerializerImpl.SERVICE
/*  55 */       .map(GsonComponentSerializer.Provider::gsonLegacy)
/*  56 */       .orElseGet(() -> new GsonComponentSerializerImpl((OptionState)JSONOptions.byDataVersion().at(2525), null));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   GsonComponentSerializerImpl(OptionState flags, LegacyHoverEventSerializer legacyHoverSerializer) {
/*  65 */     this.flags = flags;
/*  66 */     this.legacyHoverSerializer = legacyHoverSerializer;
/*  67 */     this.populator = (builder -> {
/*     */         builder.registerTypeAdapterFactory(new SerializerFactory(flags, legacyHoverSerializer));
/*     */         return builder;
/*     */       });
/*  71 */     this
/*     */ 
/*     */       
/*  74 */       .serializer = ((GsonBuilder)this.populator.apply((new GsonBuilder()).disableHtmlEscaping())).create();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Gson serializer() {
/*  79 */     return this.serializer;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public UnaryOperator<GsonBuilder> populator() {
/*  84 */     return this.populator;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Component deserialize(@NotNull String string) {
/*  89 */     Component component = (Component)serializer().fromJson(string, Component.class);
/*  90 */     if (component == null) throw ComponentSerializerImpl.notSureHowToDeserialize(string); 
/*  91 */     return component;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Component deserializeOr(@Nullable String input, @Nullable Component fallback) {
/*  96 */     if (input == null) return fallback; 
/*  97 */     Component component = (Component)serializer().fromJson(input, Component.class);
/*  98 */     if (component == null) return fallback; 
/*  99 */     return component;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String serialize(@NotNull Component component) {
/* 104 */     return serializer().toJson(component);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Component deserializeFromTree(@NotNull JsonElement input) {
/* 109 */     Component component = (Component)serializer().fromJson(input, Component.class);
/* 110 */     if (component == null) throw ComponentSerializerImpl.notSureHowToDeserialize(input); 
/* 111 */     return component;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public JsonElement serializeToTree(@NotNull Component component) {
/* 116 */     return serializer().toJsonTree(component);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public GsonComponentSerializer.Builder toBuilder() {
/* 121 */     return new BuilderImpl(this);
/*     */   }
/*     */   
/*     */   static final class BuilderImpl implements GsonComponentSerializer.Builder {
/* 125 */     private OptionState flags = (OptionState)JSONOptions.byDataVersion();
/*     */     private LegacyHoverEventSerializer legacyHoverSerializer;
/*     */     
/*     */     BuilderImpl() {
/* 129 */       GsonComponentSerializerImpl.BUILDER.accept(this);
/*     */     }
/*     */     
/*     */     BuilderImpl(GsonComponentSerializerImpl serializer) {
/* 133 */       this();
/* 134 */       this.flags = serializer.flags;
/* 135 */       this.legacyHoverSerializer = serializer.legacyHoverSerializer;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public GsonComponentSerializer.Builder options(@NotNull OptionState flags) {
/* 140 */       this.flags = Objects.<OptionState>requireNonNull(flags, "flags");
/* 141 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public GsonComponentSerializer.Builder editOptions(@NotNull Consumer<OptionState.Builder> optionEditor) {
/* 147 */       OptionState.Builder builder = JSONOptions.schema().stateBuilder().values(this.flags);
/* 148 */       ((Consumer<OptionState.Builder>)Objects.<Consumer<OptionState.Builder>>requireNonNull(optionEditor, "flagEditor")).accept(builder);
/* 149 */       this.flags = builder.build();
/* 150 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public GsonComponentSerializer.Builder legacyHoverEventSerializer(LegacyHoverEventSerializer serializer) {
/* 155 */       this.legacyHoverSerializer = serializer;
/* 156 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public GsonComponentSerializer build() {
/* 161 */       return new GsonComponentSerializerImpl(this.flags, this.legacyHoverSerializer);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\gson\GsonComponentSerializerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */