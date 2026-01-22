/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.bungeecord;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.ComponentSerializer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.GsonBuilder;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Objects;
/*     */ import net.md_5.bungee.api.chat.BaseComponent;
/*     */ import net.md_5.bungee.chat.ComponentSerializer;
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
/*     */ public final class BungeeComponentSerializer
/*     */   implements ComponentSerializer<Component, Component, BaseComponent[]>
/*     */ {
/*     */   private static boolean SUPPORTED = true;
/*     */   
/*     */   static {
/*  48 */     bind();
/*     */   }
/*     */   
/*  51 */   private static final BungeeComponentSerializer MODERN = new BungeeComponentSerializer(GsonComponentSerializer.gson(), LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build());
/*  52 */   private static final BungeeComponentSerializer PRE_1_16 = new BungeeComponentSerializer(GsonComponentSerializer.builder().downsampleColors().emitLegacyHoverEvent().build(), LegacyComponentSerializer.legacySection());
/*     */ 
/*     */   
/*     */   private final GsonComponentSerializer serializer;
/*     */ 
/*     */   
/*     */   private final LegacyComponentSerializer legacySerializer;
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isNative() {
/*  63 */     return SUPPORTED;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BungeeComponentSerializer get() {
/*  73 */     return MODERN;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BungeeComponentSerializer legacy() {
/*  83 */     return PRE_1_16;
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
/*     */   public static BungeeComponentSerializer of(GsonComponentSerializer serializer, LegacyComponentSerializer legacySerializer) {
/*  95 */     if (serializer == null || legacySerializer == null) return null; 
/*  96 */     return new BungeeComponentSerializer(serializer, legacySerializer);
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
/*     */   public static boolean inject(Gson existing) {
/* 110 */     boolean result = GsonInjections.injectGson(Objects.<Gson>requireNonNull(existing, "existing"), builder -> {
/*     */           GsonComponentSerializer.gson().populator().apply(builder);
/*     */           builder.registerTypeAdapterFactory(new SelfSerializable.AdapterFactory());
/*     */         });
/* 114 */     SUPPORTED &= result;
/* 115 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private BungeeComponentSerializer(GsonComponentSerializer serializer, LegacyComponentSerializer legacySerializer) {
/* 122 */     this.serializer = serializer;
/* 123 */     this.legacySerializer = legacySerializer;
/*     */   }
/*     */   
/*     */   private static void bind() {
/*     */     try {
/* 128 */       Field gsonField = GsonInjections.field(ComponentSerializer.class, "gson");
/* 129 */       inject((Gson)gsonField.get(null));
/* 130 */     } catch (Throwable error) {
/* 131 */       SUPPORTED = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Component deserialize(@NotNull BaseComponent[] input) {
/* 137 */     Objects.requireNonNull(input, "input");
/*     */     
/* 139 */     if (input.length == 1 && input[0] instanceof AdapterComponent) {
/* 140 */       return ((AdapterComponent)input[0]).component;
/*     */     }
/* 142 */     return this.serializer.deserialize(ComponentSerializer.toString(input));
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public BaseComponent[] serialize(@NotNull Component component) {
/* 148 */     Objects.requireNonNull(component, "component");
/*     */     
/* 150 */     if (SUPPORTED) {
/* 151 */       return new BaseComponent[] { new AdapterComponent(component) };
/*     */     }
/* 153 */     return ComponentSerializer.parse((String)this.serializer.serialize(component));
/*     */   }
/*     */   
/*     */   class AdapterComponent
/*     */     extends BaseComponent
/*     */     implements SelfSerializable {
/*     */     private final Component component;
/*     */     private volatile String legacy;
/*     */     
/*     */     AdapterComponent(Component component) {
/* 163 */       this.component = component;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toLegacyText() {
/* 168 */       if (this.legacy == null) {
/* 169 */         this.legacy = BungeeComponentSerializer.this.legacySerializer.serialize(this.component);
/*     */       }
/* 171 */       return this.legacy;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public BaseComponent duplicate() {
/* 176 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(JsonWriter out) throws IOException {
/* 181 */       BungeeComponentSerializer.this.serializer.serializer().getAdapter(Component.class).write(out, this.component);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\bungeecord\BungeeComponentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */