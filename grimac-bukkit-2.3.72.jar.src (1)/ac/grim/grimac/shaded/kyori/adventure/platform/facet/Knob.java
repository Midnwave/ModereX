/*     */ package ac.grim.grimac.shaded.kyori.adventure.platform.facet;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArraySet;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Consumer;
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
/*     */ public final class Knob
/*     */ {
/*  44 */   private static final String NAMESPACE = "net.kyo"
/*  45 */     .concat("ri.adventure");
/*  46 */   public static final boolean DEBUG = isEnabled("debug", false);
/*  47 */   private static final Set<Object> UNSUPPORTED = new CopyOnWriteArraySet();
/*     */   public static volatile BiConsumer<String, Throwable> ERR;
/*  49 */   public static volatile Consumer<String> OUT = System.out::println; static { Objects.requireNonNull(System.out); } static {
/*  50 */     ERR = ((message, err) -> {
/*     */         System.err.println(message);
/*     */         if (err != null) {
/*     */           err.printStackTrace(System.err);
/*     */         }
/*     */       });
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
/*     */   public static boolean isEnabled(@NotNull String key, boolean defaultValue) {
/*  69 */     return System.getProperty(NAMESPACE + "." + key, Boolean.toString(defaultValue))
/*  70 */       .equalsIgnoreCase("true");
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
/*     */   public static void logError(@Nullable Throwable error, @NotNull String format, @NotNull Object... arguments) {
/*  82 */     if (DEBUG) {
/*  83 */       ERR.accept(String.format(format, arguments), error);
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
/*     */   public static void logMessage(@NotNull String format, @NotNull Object... arguments) {
/*  95 */     if (DEBUG) {
/*  96 */       OUT.accept(String.format(format, arguments));
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
/*     */   public static void logUnsupported(@NotNull Object facet, @NotNull Object value) {
/* 108 */     if (DEBUG && UNSUPPORTED.add(value))
/* 109 */       OUT.accept(String.format("Unsupported value '%s' for facet: %s", new Object[] { value, facet })); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\facet\Knob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */