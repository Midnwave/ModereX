/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Iterator;
/*     */ import java.util.Objects;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Predicate;
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
/*     */ final class JoinConfigurationImpl
/*     */   implements JoinConfiguration
/*     */ {
/*  39 */   static final Function<ComponentLike, Component> DEFAULT_CONVERTOR = ComponentLike::asComponent;
/*     */   static final Predicate<ComponentLike> DEFAULT_PREDICATE = componentLike -> true;
/*  41 */   static final JoinConfigurationImpl NULL = new JoinConfigurationImpl();
/*     */   
/*  43 */   static final JoinConfiguration STANDARD_NEW_LINES = JoinConfiguration.separator(Component.newline());
/*  44 */   static final JoinConfiguration STANDARD_SPACES = JoinConfiguration.separator(Component.space());
/*  45 */   static final JoinConfiguration STANDARD_COMMA_SEPARATED = JoinConfiguration.separator(Component.text(","));
/*  46 */   static final JoinConfiguration STANDARD_COMMA_SPACE_SEPARATED = JoinConfiguration.separator(Component.text(", "));
/*  47 */   static final JoinConfiguration STANDARD_ARRAY_LIKE = (JoinConfiguration)JoinConfiguration.builder()
/*  48 */     .separator(Component.text(", "))
/*  49 */     .prefix(Component.text("["))
/*  50 */     .suffix(Component.text("]"))
/*  51 */     .build();
/*     */   
/*     */   private final Component prefix;
/*     */   private final Component suffix;
/*     */   private final Component separator;
/*     */   private final Component lastSeparator;
/*     */   private final Component lastSeparatorIfSerial;
/*     */   private final Function<ComponentLike, Component> convertor;
/*     */   private final Predicate<ComponentLike> predicate;
/*     */   private final Style rootStyle;
/*     */   
/*     */   private JoinConfigurationImpl() {
/*  63 */     this.prefix = null;
/*  64 */     this.suffix = null;
/*  65 */     this.separator = null;
/*  66 */     this.lastSeparator = null;
/*  67 */     this.lastSeparatorIfSerial = null;
/*  68 */     this.convertor = DEFAULT_CONVERTOR;
/*  69 */     this.predicate = DEFAULT_PREDICATE;
/*  70 */     this.rootStyle = Style.empty();
/*     */   }
/*     */   
/*     */   private JoinConfigurationImpl(@NotNull BuilderImpl builder) {
/*  74 */     this.prefix = ComponentLike.unbox(builder.prefix);
/*  75 */     this.suffix = ComponentLike.unbox(builder.suffix);
/*  76 */     this.separator = ComponentLike.unbox(builder.separator);
/*  77 */     this.lastSeparator = ComponentLike.unbox(builder.lastSeparator);
/*  78 */     this.lastSeparatorIfSerial = ComponentLike.unbox(builder.lastSeparatorIfSerial);
/*  79 */     this.convertor = builder.convertor;
/*  80 */     this.predicate = builder.predicate;
/*  81 */     this.rootStyle = builder.rootStyle;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Component prefix() {
/*  86 */     return this.prefix;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Component suffix() {
/*  91 */     return this.suffix;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Component separator() {
/*  96 */     return this.separator;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Component lastSeparator() {
/* 101 */     return this.lastSeparator;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Component lastSeparatorIfSerial() {
/* 106 */     return this.lastSeparatorIfSerial;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Function<ComponentLike, Component> convertor() {
/* 111 */     return this.convertor;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Predicate<ComponentLike> predicate() {
/* 116 */     return this.predicate;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Style parentStyle() {
/* 121 */     return this.rootStyle;
/*     */   }
/*     */ 
/*     */   
/*     */   public JoinConfiguration.Builder toBuilder() {
/* 126 */     return new BuilderImpl(this);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 131 */     return Stream.of(new ExaminableProperty[] {
/* 132 */           ExaminableProperty.of("prefix", this.prefix), 
/* 133 */           ExaminableProperty.of("suffix", this.suffix), 
/* 134 */           ExaminableProperty.of("separator", this.separator), 
/* 135 */           ExaminableProperty.of("lastSeparator", this.lastSeparator), 
/* 136 */           ExaminableProperty.of("lastSeparatorIfSerial", this.lastSeparatorIfSerial), 
/* 137 */           ExaminableProperty.of("convertor", this.convertor), 
/* 138 */           ExaminableProperty.of("predicate", this.predicate), 
/* 139 */           ExaminableProperty.of("rootStyle", this.rootStyle)
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 145 */     return Internals.toString(this);
/*     */   }
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   static Component join(@NotNull JoinConfiguration config, @NotNull Iterable<? extends ComponentLike> components) {
/* 150 */     Objects.requireNonNull(config, "config");
/* 151 */     Objects.requireNonNull(components, "components");
/*     */     
/* 153 */     Iterator<? extends ComponentLike> it = components.iterator();
/*     */     
/* 155 */     if (!it.hasNext()) {
/* 156 */       return singleElementJoin(config, null);
/*     */     }
/*     */     
/* 159 */     ComponentLike component = Objects.<ComponentLike>requireNonNull(it.next(), "Null elements in \"components\" are not allowed");
/* 160 */     int componentsSeen = 0;
/*     */     
/* 162 */     if (!it.hasNext()) {
/* 163 */       return singleElementJoin(config, component);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 169 */     Component prefix = config.prefix();
/* 170 */     Component suffix = config.suffix();
/* 171 */     Function<ComponentLike, Component> convertor = config.convertor();
/* 172 */     Predicate<ComponentLike> predicate = config.predicate();
/* 173 */     Style rootStyle = config.parentStyle();
/* 174 */     boolean hasRootStyle = (rootStyle != Style.empty());
/*     */     
/* 176 */     Component separator = config.separator();
/* 177 */     boolean hasSeparator = (separator != null);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 182 */     TextComponent.Builder builder = hasRootStyle ? Component.text().style(rootStyle) : Component.text();
/* 183 */     if (prefix != null) builder.append(prefix);
/*     */     
/* 185 */     while (component != null) {
/* 186 */       if (!predicate.test(component)) {
/* 187 */         if (it.hasNext()) {
/* 188 */           component = it.next();
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/*     */         break;
/*     */       } 
/* 195 */       builder.append(Objects.<Component>requireNonNull(convertor.apply(component), "Null output from \"convertor\" is not allowed"));
/* 196 */       componentsSeen++;
/*     */       
/* 198 */       if (!it.hasNext()) {
/* 199 */         component = null; continue;
/*     */       } 
/* 201 */       component = Objects.<ComponentLike>requireNonNull(it.next(), "Null elements in \"components\" are not allowed");
/*     */       
/* 203 */       if (it.hasNext()) {
/* 204 */         if (hasSeparator) builder.append(separator);  continue;
/*     */       } 
/* 206 */       Component lastSeparator = null;
/*     */       
/* 208 */       if (componentsSeen > 1) lastSeparator = config.lastSeparatorIfSerial(); 
/* 209 */       if (lastSeparator == null) lastSeparator = config.lastSeparator(); 
/* 210 */       if (lastSeparator == null) lastSeparator = config.separator();
/*     */       
/* 212 */       if (lastSeparator != null) builder.append(lastSeparator);
/*     */     
/*     */     } 
/*     */ 
/*     */     
/* 217 */     if (suffix != null) builder.append(suffix); 
/* 218 */     return builder.build();
/*     */   }
/*     */   @NotNull
/*     */   static Component singleElementJoin(@NotNull JoinConfiguration config, @Nullable ComponentLike component) {
/* 222 */     Component prefix = config.prefix();
/* 223 */     Component suffix = config.suffix();
/* 224 */     Function<ComponentLike, Component> convertor = config.convertor();
/* 225 */     Predicate<ComponentLike> predicate = config.predicate();
/* 226 */     Style rootStyle = config.parentStyle();
/* 227 */     boolean hasRootStyle = (rootStyle != Style.empty());
/*     */     
/* 229 */     if (prefix == null && suffix == null) {
/*     */       Component result;
/* 231 */       if (component == null || !predicate.test(component)) {
/* 232 */         result = Component.empty();
/*     */       } else {
/* 234 */         result = convertor.apply(component);
/*     */       } 
/* 236 */       return hasRootStyle ? Component.text().style(rootStyle).append(result).build() : result;
/*     */     } 
/*     */     
/* 239 */     TextComponent.Builder builder = Component.text();
/* 240 */     if (prefix != null) builder.append(prefix); 
/* 241 */     if (component != null && predicate.test(component)) builder.append(convertor.apply(component)); 
/* 242 */     if (suffix != null) builder.append(suffix); 
/* 243 */     return hasRootStyle ? Component.text().style(rootStyle).append(builder).build() : builder.build();
/*     */   }
/*     */   
/*     */   static final class BuilderImpl implements JoinConfiguration.Builder {
/*     */     private ComponentLike prefix;
/*     */     private ComponentLike suffix;
/*     */     private ComponentLike separator;
/*     */     private ComponentLike lastSeparator;
/*     */     private ComponentLike lastSeparatorIfSerial;
/*     */     private Function<ComponentLike, Component> convertor;
/*     */     private Predicate<ComponentLike> predicate;
/*     */     private Style rootStyle;
/*     */     
/*     */     BuilderImpl() {
/* 257 */       this(JoinConfigurationImpl.NULL);
/*     */     }
/*     */     
/*     */     private BuilderImpl(@NotNull JoinConfigurationImpl joinConfig) {
/* 261 */       this.separator = joinConfig.separator;
/* 262 */       this.lastSeparator = joinConfig.lastSeparator;
/* 263 */       this.prefix = joinConfig.prefix;
/* 264 */       this.suffix = joinConfig.suffix;
/* 265 */       this.convertor = joinConfig.convertor;
/* 266 */       this.lastSeparatorIfSerial = joinConfig.lastSeparatorIfSerial;
/* 267 */       this.predicate = joinConfig.predicate;
/* 268 */       this.rootStyle = joinConfig.rootStyle;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public JoinConfiguration.Builder prefix(@Nullable ComponentLike prefix) {
/* 273 */       this.prefix = prefix;
/* 274 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public JoinConfiguration.Builder suffix(@Nullable ComponentLike suffix) {
/* 279 */       this.suffix = suffix;
/* 280 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public JoinConfiguration.Builder separator(@Nullable ComponentLike separator) {
/* 285 */       this.separator = separator;
/* 286 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public JoinConfiguration.Builder lastSeparator(@Nullable ComponentLike lastSeparator) {
/* 291 */       this.lastSeparator = lastSeparator;
/* 292 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public JoinConfiguration.Builder lastSeparatorIfSerial(@Nullable ComponentLike lastSeparatorIfSerial) {
/* 297 */       this.lastSeparatorIfSerial = lastSeparatorIfSerial;
/* 298 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public JoinConfiguration.Builder convertor(@NotNull Function<ComponentLike, Component> convertor) {
/* 303 */       this.convertor = Objects.<Function<ComponentLike, Component>>requireNonNull(convertor, "convertor");
/* 304 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public JoinConfiguration.Builder predicate(@NotNull Predicate<ComponentLike> predicate) {
/* 309 */       this.predicate = Objects.<Predicate<ComponentLike>>requireNonNull(predicate, "predicate");
/* 310 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public JoinConfiguration.Builder parentStyle(@NotNull Style parentStyle) {
/* 315 */       this.rootStyle = Objects.<Style>requireNonNull(parentStyle, "rootStyle");
/* 316 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public JoinConfiguration build() {
/* 321 */       return new JoinConfigurationImpl(this);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\JoinConfigurationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */