/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.event;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.audience.Audience;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.dialog.DialogLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Keyed;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.nbt.api.BinaryTagHolder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleBuilderApplicable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Index;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.net.URL;
/*     */ import java.util.Objects;
/*     */ import java.util.function.Consumer;
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
/*     */ public final class ClickEvent
/*     */   implements Examinable, StyleBuilderApplicable
/*     */ {
/*     */   private final Action action;
/*     */   private final Payload payload;
/*     */   
/*     */   @NotNull
/*     */   public static ClickEvent openUrl(@NotNull String url) {
/*  63 */     return new ClickEvent(Action.OPEN_URL, Payload.string(url));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static ClickEvent openUrl(@NotNull URL url) {
/*  74 */     return openUrl(url.toExternalForm());
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
/*     */   @NotNull
/*     */   public static ClickEvent openFile(@NotNull String file) {
/*  87 */     return new ClickEvent(Action.OPEN_FILE, Payload.string(file));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static ClickEvent runCommand(@NotNull String command) {
/*  98 */     return new ClickEvent(Action.RUN_COMMAND, Payload.string(command));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static ClickEvent suggestCommand(@NotNull String command) {
/* 109 */     return new ClickEvent(Action.SUGGEST_COMMAND, Payload.string(command));
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
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public static ClickEvent changePage(@NotNull String page) {
/* 123 */     Objects.requireNonNull(page, "page");
/* 124 */     return new ClickEvent(Action.CHANGE_PAGE, Payload.integer(Integer.parseInt(page)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static ClickEvent changePage(int page) {
/* 135 */     return new ClickEvent(Action.CHANGE_PAGE, Payload.integer(page));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static ClickEvent copyToClipboard(@NotNull String text) {
/* 147 */     return new ClickEvent(Action.COPY_TO_CLIPBOARD, Payload.string(text));
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
/*     */   @NotNull
/*     */   public static ClickEvent callback(@NotNull ClickCallback<Audience> function) {
/* 160 */     return ClickCallbackInternals.PROVIDER.create(Objects.<ClickCallback<Audience>>requireNonNull(function, "function"), ClickCallbackOptionsImpl.DEFAULT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static ClickEvent callback(@NotNull ClickCallback<Audience> function, ClickCallback.Options options) {
/* 172 */     return ClickCallbackInternals.PROVIDER.create(Objects.<ClickCallback<Audience>>requireNonNull(function, "function"), Objects.<ClickCallback.Options>requireNonNull(options, "options"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static ClickEvent callback(@NotNull ClickCallback<Audience> function, @NotNull Consumer<ClickCallback.Options.Builder> optionsBuilder) {
/* 184 */     return ClickCallbackInternals.PROVIDER.create(
/* 185 */         Objects.<ClickCallback<Audience>>requireNonNull(function, "function"), 
/* 186 */         (ClickCallback.Options)AbstractBuilder.configureAndBuild(ClickCallback.Options.builder(), Objects.<Consumer>requireNonNull(optionsBuilder, "optionsBuilder")));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static ClickEvent showDialog(@NotNull DialogLike dialog) {
/* 198 */     Objects.requireNonNull(dialog, "dialog");
/* 199 */     return new ClickEvent(Action.SHOW_DIALOG, Payload.dialog(dialog));
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
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public static ClickEvent custom(@NotNull Key key, @NotNull String data) {
/* 214 */     return custom(key, BinaryTagHolder.binaryTagHolder(data));
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
/*     */   @NotNull
/*     */   public static ClickEvent custom(@NotNull Key key, @NotNull BinaryTagHolder nbt) {
/* 229 */     Objects.requireNonNull(key, "key");
/* 230 */     Objects.requireNonNull(nbt, "nbt");
/* 231 */     return new ClickEvent(Action.CUSTOM, Payload.custom(key, nbt));
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
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public static ClickEvent clickEvent(@NotNull Action action, @NotNull String value) {
/* 247 */     if (action == Action.CHANGE_PAGE) return changePage(value); 
/* 248 */     if (!action.payloadType().equals(Payload.Text.class)) throw new IllegalArgumentException("Action " + action + " does not support string payloads"); 
/* 249 */     return new ClickEvent(action, Payload.string(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ClickEvent(@NotNull Action action, @NotNull Payload payload) {
/* 256 */     if (!action.supports(payload)) throw new IllegalArgumentException("Action " + action + " does not support payload " + payload); 
/* 257 */     this.action = Objects.<Action>requireNonNull(action, "action");
/* 258 */     this.payload = Objects.<Payload>requireNonNull(payload, "payload");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Action action() {
/* 268 */     return this.action;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public String value() {
/* 281 */     if (this.payload instanceof Payload.Text)
/* 282 */       return ((Payload.Text)this.payload).value(); 
/* 283 */     if (this.action == Action.CHANGE_PAGE) {
/* 284 */       return String.valueOf(((Payload.Int)this.payload).integer());
/*     */     }
/* 286 */     throw new IllegalStateException("Payload is not a string payload, is " + this.payload);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Payload payload() {
/* 297 */     return this.payload;
/*     */   }
/*     */ 
/*     */   
/*     */   public void styleApply(Style.Builder style) {
/* 302 */     style.clickEvent(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 307 */     if (this == other) return true; 
/* 308 */     if (other == null || getClass() != other.getClass()) return false; 
/* 309 */     ClickEvent that = (ClickEvent)other;
/* 310 */     return (this.action == that.action && Objects.equals(this.payload, that.payload));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 315 */     int result = this.action.hashCode();
/* 316 */     result = 31 * result + this.payload.hashCode();
/* 317 */     return result;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 322 */     return Stream.of(new ExaminableProperty[] {
/* 323 */           ExaminableProperty.of("action", this.action), 
/* 324 */           ExaminableProperty.of("payload", this.payload) });
/*     */   } public static interface Custom extends Payload, Keyed {
/*     */     @Deprecated
/*     */     @NotNull
/*     */     String data(); @NotNull
/*     */     BinaryTagHolder nbt(); } public static interface Dialog extends Payload { @NotNull
/* 330 */     DialogLike dialog(); } public String toString() { return Internals.toString(this); }
/*     */ 
/*     */   
/*     */   public static interface Int
/*     */     extends Payload {
/*     */     int integer();
/*     */   }
/*     */   
/*     */   public static interface Text
/*     */     extends Payload {
/*     */     @NotNull
/*     */     String value();
/*     */   }
/*     */   
/*     */   public static interface Payload
/*     */     extends Examinable {
/*     */     static Text string(@NotNull String value) {
/* 347 */       Objects.requireNonNull(value, "value");
/* 348 */       return new PayloadImpl.TextImpl(value);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     static Int integer(int integer) {
/* 359 */       return new PayloadImpl.IntImpl(integer);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     static Dialog dialog(@NotNull DialogLike dialog) {
/* 370 */       Objects.requireNonNull(dialog, "dialog");
/* 371 */       return new PayloadImpl.DialogImpl(dialog);
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
/*     */     
/*     */     @Deprecated
/*     */     static Custom custom(@NotNull Key key, @NotNull String data) {
/* 386 */       return custom(key, BinaryTagHolder.binaryTagHolder(data));
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
/*     */ 
/*     */     
/*     */     static Custom custom(@NotNull Key key, @NotNull BinaryTagHolder nbt) {
/* 401 */       Objects.requireNonNull(key, "key");
/* 402 */       Objects.requireNonNull(nbt, "nbt");
/* 403 */       return new PayloadImpl.CustomImpl(key, nbt);
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
/*     */     public static interface Custom
/*     */       extends Payload, Keyed
/*     */     {
/*     */       @Deprecated
/*     */       @NotNull
/*     */       String data();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       @NotNull
/*     */       BinaryTagHolder nbt();
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
/*     */     public static interface Dialog
/*     */       extends Payload
/*     */     {
/*     */       @NotNull
/*     */       DialogLike dialog();
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
/*     */     public static interface Int
/*     */       extends Payload
/*     */     {
/*     */       int integer();
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
/*     */     public static interface Text
/*     */       extends Payload
/*     */     {
/*     */       @NotNull
/*     */       String value();
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
/*     */   public enum Action
/*     */   {
/* 493 */     OPEN_URL("open_url", true, (String)ClickEvent.Payload.Text.class),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 501 */     OPEN_FILE("open_file", false, (String)ClickEvent.Payload.Text.class),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 507 */     RUN_COMMAND("run_command", true, (String)ClickEvent.Payload.Text.class),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 513 */     SUGGEST_COMMAND("suggest_command", true, (String)ClickEvent.Payload.Text.class),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 519 */     CHANGE_PAGE("change_page", true, (String)ClickEvent.Payload.Int.class),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 526 */     COPY_TO_CLIPBOARD("copy_to_clipboard", true, (String)ClickEvent.Payload.Text.class),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 535 */     SHOW_DIALOG("show_dialog", false, (String)ClickEvent.Payload.Dialog.class),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 542 */     CUSTOM("custom", true, (String)ClickEvent.Payload.Custom.class);
/*     */     public static final Index<String, Action> NAMES;
/*     */     private final String name;
/*     */     private final boolean readable;
/*     */     private final Class<? extends ClickEvent.Payload> payloadType;
/*     */     
/*     */     static {
/* 549 */       NAMES = Index.create(Action.class, constant -> constant.name);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     Action(@NotNull String name, boolean readable, Class<? extends ClickEvent.Payload> payloadType) {
/* 560 */       this.name = name;
/* 561 */       this.readable = readable;
/* 562 */       this.payloadType = payloadType;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean readable() {
/* 573 */       return this.readable;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean supports(@NotNull ClickEvent.Payload payload) {
/* 584 */       Objects.requireNonNull(payload, "payload");
/* 585 */       return this.payloadType.isAssignableFrom(payload.getClass());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public Class<? extends ClickEvent.Payload> payloadType() {
/* 595 */       return this.payloadType;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public String toString() {
/* 600 */       return this.name;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\event\ClickEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */