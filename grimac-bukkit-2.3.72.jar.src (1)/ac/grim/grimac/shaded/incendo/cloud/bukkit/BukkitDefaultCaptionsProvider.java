/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionProvider;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.DelegatingCaptionProvider;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BukkitDefaultCaptionsProvider<C>
/*     */   extends DelegatingCaptionProvider<C>
/*     */ {
/*     */   public static final String ARGUMENT_PARSE_FAILURE_ENCHANTMENT = "'<input>' is not a valid enchantment";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_MATERIAL = "'<input>' is not a valid material name";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_OFFLINEPLAYER = "No player found for input '<input>'";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_PLAYER = "No player found for input '<input>'";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_WORLD = "'<input>' is not a valid Minecraft world";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_SELECTOR_UNSUPPORTED = "Entity selector argument type not supported below Minecraft 1.13.";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_LOCATION_INVALID_FORMAT = "'<input>' is not a valid location. Required format is '<x> <y> <z>'";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_LOCATION_MIXED_LOCAL_ABSOLUTE = "Cannot mix local and absolute coordinates. (either all coordinates use '^' or none do)";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_NAMESPACE = "Invalid namespace '<input>'. Must be [a-z0-9._-]";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_KEY = "Invalid key '<input>'. Must be [a-z0-9/._-]";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_NEED_NAMESPACE = "Invalid input '<input>', requires an explicit namespace.";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_REGISTRY_ENTRY_MISSING = "No such entry '<input>' in '<registry>' registry.";
/*  93 */   private static final CaptionProvider<?> PROVIDER = (CaptionProvider<?>)CaptionProvider.constantProvider()
/*  94 */     .putCaption(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_ENCHANTMENT, "'<input>' is not a valid enchantment")
/*     */ 
/*     */     
/*  97 */     .putCaption(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_MATERIAL, "'<input>' is not a valid material name")
/*     */ 
/*     */     
/* 100 */     .putCaption(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_OFFLINEPLAYER, "No player found for input '<input>'")
/*     */ 
/*     */     
/* 103 */     .putCaption(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER, "No player found for input '<input>'")
/*     */ 
/*     */     
/* 106 */     .putCaption(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_WORLD, "'<input>' is not a valid Minecraft world")
/*     */ 
/*     */     
/* 109 */     .putCaption(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_SELECTOR_UNSUPPORTED, "Entity selector argument type not supported below Minecraft 1.13.")
/*     */ 
/*     */     
/* 112 */     .putCaption(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_LOCATION_INVALID_FORMAT, "'<input>' is not a valid location. Required format is '<x> <y> <z>'")
/*     */ 
/*     */     
/* 115 */     .putCaption(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_LOCATION_MIXED_LOCAL_ABSOLUTE, "Cannot mix local and absolute coordinates. (either all coordinates use '^' or none do)")
/*     */ 
/*     */     
/* 118 */     .putCaption(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_NAMESPACE, "Invalid namespace '<input>'. Must be [a-z0-9._-]")
/*     */ 
/*     */     
/* 121 */     .putCaption(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_KEY, "Invalid key '<input>'. Must be [a-z0-9/._-]")
/*     */ 
/*     */     
/* 124 */     .putCaption(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_NEED_NAMESPACE, "Invalid input '<input>', requires an explicit namespace.")
/*     */ 
/*     */ 
/*     */     
/* 128 */     .putCaption(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_REGISTRY_ENTRY_MISSING, "No such entry '<input>' in '<registry>' registry.")
/*     */ 
/*     */ 
/*     */     
/* 132 */     .build();
/*     */ 
/*     */ 
/*     */   
/*     */   public CaptionProvider<C> delegate() {
/* 137 */     return (CaptionProvider)PROVIDER;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\BukkitDefaultCaptionsProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */