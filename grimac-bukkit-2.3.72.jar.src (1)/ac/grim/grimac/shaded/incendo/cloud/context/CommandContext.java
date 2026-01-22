/*     */ package ac.grim.grimac.shaded.incendo.cloud.context;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.Caption;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionFormatter;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionRegistry;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionVariable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.key.MutableCloudKeyContainer;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.flag.FlagContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.permission.Permission;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.util.annotation.AnnotationAccessor;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Supplier;
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
/*     */ public class CommandContext<C>
/*     */   implements MutableCloudKeyContainer
/*     */ {
/*  63 */   private final List<ParsingContext<C>> parsingContexts = new LinkedList<>();
/*  64 */   private final FlagContext flagContext = FlagContext.create();
/*  65 */   private final Map<CloudKey<?>, Object> internalStorage = new HashMap<>();
/*     */   private final C commandSender;
/*     */   private final boolean suggestions;
/*     */   private final CaptionRegistry<C> captionRegistry;
/*     */   private final CommandManager<C> commandManager;
/*  70 */   private volatile Command<C> currentCommand = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public CommandContext(C commandSender, CommandManager<C> commandManager) {
/*  80 */     this(false, commandSender, commandManager);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public CommandContext(boolean suggestions, C commandSender, CommandManager<C> commandManager) {
/*  96 */     this.commandSender = commandSender;
/*  97 */     this.suggestions = suggestions;
/*  98 */     this.commandManager = commandManager;
/*  99 */     this.captionRegistry = commandManager.captionRegistry();
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
/*     */   public String formatCaption(Caption caption, CaptionVariable... variables) {
/* 113 */     return formatCaption(this.commandManager.captionFormatter(), caption, variables);
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
/*     */   public String formatCaption(Caption caption, List<CaptionVariable> variables) {
/* 127 */     return formatCaption(this.commandManager.captionFormatter(), caption, variables);
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
/*     */   public <T> T formatCaption(CaptionFormatter<C, T> formatter, Caption caption, CaptionVariable... variables) {
/* 144 */     return (T)formatter.formatCaption(caption, this.commandSender, this.captionRegistry
/*     */ 
/*     */         
/* 147 */         .caption(caption, this.commandSender), variables);
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
/*     */   public <T> T formatCaption(CaptionFormatter<C, T> formatter, Caption caption, List<CaptionVariable> variables) {
/* 166 */     return (T)formatter.formatCaption(caption, this.commandSender, this.captionRegistry
/*     */ 
/*     */         
/* 169 */         .caption(caption, this.commandSender), variables);
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
/*     */   public C sender() {
/* 181 */     return this.commandSender;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public boolean hasPermission(Permission permission) {
/* 192 */     return this.commandManager.testPermission(this.commandSender, permission).allowed();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public boolean hasPermission(String permission) {
/* 203 */     return this.commandManager.hasPermission(this.commandSender, permission);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSuggestions() {
/* 212 */     return this.suggestions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> void store(String key, T value) {
/* 220 */     this.internalStorage.put(CloudKey.of(key), value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> void store(CloudKey<T> key, T value) {
/* 228 */     this.internalStorage.put(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(CloudKey<?> key) {
/* 236 */     return this.internalStorage.containsKey(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> Optional<T> optional(CloudKey<T> key) {
/* 244 */     Object value = this.internalStorage.get(key);
/* 245 */     if (value != null) {
/* 246 */       T castedValue = (T)value;
/* 247 */       return Optional.of(castedValue);
/*     */     } 
/* 249 */     return Optional.empty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> Optional<T> optional(String key) {
/* 258 */     Object value = this.internalStorage.get(CloudKey.of(key));
/* 259 */     if (value != null) {
/* 260 */       T castedValue = (T)value;
/* 261 */       return Optional.of(castedValue);
/*     */     } 
/* 263 */     return Optional.empty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(CloudKey<?> key) {
/* 272 */     this.internalStorage.remove(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T computeIfAbsent(CloudKey<T> key, Function<CloudKey<T>, T> defaultFunction) {
/* 283 */     T castedValue = (T)this.internalStorage.computeIfAbsent(key, k -> defaultFunction.apply(k));
/*     */ 
/*     */ 
/*     */     
/* 287 */     return castedValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public CommandInput rawInput() {
/* 297 */     return ((CommandInput)getOrDefault("__raw_input__", CommandInput.empty())).copy();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.MAINTAINED)
/*     */   public ParsingContext<C> createParsingContext(CommandComponent<C> component) {
/* 308 */     ParsingContext<C> parsingContext = new ParsingContext<>(component);
/* 309 */     this.parsingContexts.add(parsingContext);
/* 310 */     return parsingContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.MAINTAINED)
/*     */   public ParsingContext<C> parsingContext(CommandComponent<C> component) {
/* 321 */     return (ParsingContext<C>)this.parsingContexts.stream()
/* 322 */       .filter(context -> context.component().equals(component))
/* 323 */       .findFirst()
/* 324 */       .orElseThrow(java.util.NoSuchElementException::new);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.MAINTAINED)
/*     */   public ParsingContext<C> parsingContext(int position) {
/* 335 */     return this.parsingContexts.get(position);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.MAINTAINED)
/*     */   public ParsingContext<C> parsingContext(String name) {
/* 346 */     return (ParsingContext<C>)this.parsingContexts.stream()
/* 347 */       .filter(context -> context.component().name().equals(name))
/* 348 */       .findFirst()
/* 349 */       .orElseThrow(java.util.NoSuchElementException::new);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.MAINTAINED)
/*     */   public List<ParsingContext<C>> parsingContexts() {
/* 359 */     return Collections.unmodifiableList(this.parsingContexts);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FlagContext flags() {
/* 368 */     return this.flagContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Command<C> command() {
/* 378 */     if (this.currentCommand == null) {
/* 379 */       throw new IllegalStateException("The current command is only available once a command has been parsed. Mainly from execution handlers and post processors.");
/*     */     }
/*     */     
/* 382 */     return this.currentCommand;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.INTERNAL)
/*     */   public void command(Command<C> command) {
/* 392 */     this.currentCommand = Objects.<Command<C>>requireNonNull(command, "command");
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
/*     */   public <T> Optional<T> inject(Class<T> clazz) {
/* 405 */     if (this.commandManager == null) {
/* 406 */       throw new UnsupportedOperationException("Cannot retrieve injectable values from a command context that is not associated with a command manager");
/*     */     }
/*     */ 
/*     */     
/* 410 */     return this.commandManager.parameterInjectorRegistry().getInjectable(clazz, this, AnnotationAccessor.empty());
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
/*     */   public <T> Optional<T> inject(TypeToken<T> type) {
/* 423 */     if (this.commandManager == null) {
/* 424 */       throw new UnsupportedOperationException("Cannot retrieve injectable values from a command context that is not associated with a command manager");
/*     */     }
/*     */ 
/*     */     
/* 428 */     return this.commandManager.parameterInjectorRegistry().getInjectable(type, this, AnnotationAccessor.empty());
/*     */   }
/*     */ 
/*     */   
/*     */   public final Map<CloudKey<?>, ? extends Object> all() {
/* 433 */     return Collections.unmodifiableMap(this.internalStorage);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\context\CommandContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */