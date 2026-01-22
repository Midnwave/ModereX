/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.Caption;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
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
/*     */ public final class BukkitCaptionKeys
/*     */ {
/*  38 */   private static final Collection<Caption> RECOGNIZED_CAPTIONS = new LinkedList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  43 */   public static final Caption ARGUMENT_PARSE_FAILURE_ENCHANTMENT = of("argument.parse.failure.enchantment");
/*     */ 
/*     */ 
/*     */   
/*  47 */   public static final Caption ARGUMENT_PARSE_FAILURE_MATERIAL = of("argument.parse.failure.material");
/*     */ 
/*     */ 
/*     */   
/*  51 */   public static final Caption ARGUMENT_PARSE_FAILURE_OFFLINEPLAYER = of("argument.parse.failure.offlineplayer");
/*     */ 
/*     */ 
/*     */   
/*  55 */   public static final Caption ARGUMENT_PARSE_FAILURE_PLAYER = of("argument.parse.failure.player");
/*     */ 
/*     */ 
/*     */   
/*  59 */   public static final Caption ARGUMENT_PARSE_FAILURE_WORLD = of("argument.parse.failure.world");
/*     */ 
/*     */ 
/*     */   
/*  63 */   public static final Caption ARGUMENT_PARSE_FAILURE_SELECTOR_UNSUPPORTED = of("argument.parse.failure.selector.unsupported");
/*     */ 
/*     */ 
/*     */   
/*  67 */   public static final Caption ARGUMENT_PARSE_FAILURE_LOCATION_INVALID_FORMAT = of("argument.parse.failure.location.invalid_format");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  72 */   public static final Caption ARGUMENT_PARSE_FAILURE_LOCATION_MIXED_LOCAL_ABSOLUTE = of("argument.parse.failure.location.mixed_local_absolute");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  80 */   public static final Caption ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_NAMESPACE = of("argument.parse.failure.namespacedkey.namespace");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  87 */   public static final Caption ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_KEY = of("argument.parse.failure.namespacedkey.key");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  94 */   public static final Caption ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_NEED_NAMESPACE = of("argument.parse.failure.namespacedkey.need_namespace");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 101 */   public static final Caption ARGUMENT_PARSE_FAILURE_REGISTRY_ENTRY_MISSING = of("argument.parse.failure.registry_entry.missing");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Caption of(String key) {
/* 107 */     Caption caption = Caption.of(key);
/* 108 */     RECOGNIZED_CAPTIONS.add(caption);
/* 109 */     return caption;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static Collection<Caption> bukkitCaptionKeys() {
/* 120 */     return Collections.unmodifiableCollection(RECOGNIZED_CAPTIONS);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\BukkitCaptionKeys.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */