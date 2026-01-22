/*     */ package ac.grim.grimac.utils.anticheat;
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.api.GrimUser;
/*     */ import ac.grim.grimac.platform.api.player.PlatformPlayer;
/*     */ import ac.grim.grimac.platform.api.sender.Sender;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3f;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextReplacementConfig;
/*     */ import java.util.Map;
/*     */ import java.util.function.Function;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public final class MessageUtil {
/*     */   @Generated
/*     */   private MessageUtil() {
/*  24 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*  25 */   } private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-ORX]");
/*  26 */   private static final Pattern HEX_PATTERN = Pattern.compile("([&§]#[A-Fa-f0-9]{6})|([&§]x([&§][A-Fa-f0-9]){6})"); private static final char PLACEHOLDER_ESCAPE_CHAR = '￿';
/*     */   
/*     */   @NotNull
/*     */   public static String toUnlabledString(@Nullable Vector3i vec) {
/*  30 */     return (vec == null) ? "null" : ("" + vec.x + ", " + vec.x + ", " + vec.y);
/*     */   }
/*     */   @NotNull
/*     */   public static String toUnlabledString(@Nullable Vector3f vec) {
/*  34 */     return (vec == null) ? "null" : ("" + vec.x + ", " + vec.x + ", " + vec.y);
/*     */   }
/*     */   @NotNull
/*     */   public static String replacePlaceholders(@Nullable GrimPlayer player, @NotNull String string, boolean removeFormatting) {
/*  38 */     return replacePlaceholders(player, (player == null) ? null : player.platformPlayer, string, removeFormatting);
/*     */   }
/*     */   @NotNull
/*     */   public static String replacePlaceholders(@Nullable GrimPlayer player, @NotNull String string) {
/*  42 */     return replacePlaceholders(player, (player == null) ? null : player.platformPlayer, string, false);
/*     */   }
/*     */   @NotNull
/*     */   public static String replacePlaceholders(@Nullable Sender sender, @NotNull String string) {
/*  46 */     return replacePlaceholders((sender != null) ? sender.getPlatformPlayer() : null, string);
/*     */   }
/*     */   @NotNull
/*     */   public static String replacePlaceholders(@Nullable PlatformPlayer player, @NotNull String string) {
/*  50 */     return replacePlaceholders((player == null) ? null : GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(player.getUniqueId()), player, string, false);
/*     */   }
/*     */   @NotNull
/*     */   private static String replacePlaceholders(@Nullable GrimPlayer grimPlayer, @Nullable PlatformPlayer platformPlayer, @NotNull String string, boolean removeFormatting) {
/*  54 */     for (Map.Entry<String, String> entry : (Iterable<Map.Entry<String, String>>)GrimAPI.INSTANCE.getExternalAPI().getStaticReplacements().entrySet()) {
/*  55 */       string = string.replace(entry.getKey(), entry.getValue());
/*     */     }
/*     */     
/*  58 */     if (grimPlayer != null) {
/*  59 */       for (Map.Entry<String, Function<GrimUser, String>> entry : (Iterable<Map.Entry<String, Function<GrimUser, String>>>)GrimAPI.INSTANCE.getExternalAPI().getVariableReplacements().entrySet()) {
/*  60 */         String value = ((String)((Function<GrimPlayer, String>)entry.getValue()).apply(grimPlayer)).replace('%', '￿');
/*  61 */         if (removeFormatting) value = filterDiscordText(value); 
/*  62 */         string = string.replace(entry.getKey(), value);
/*     */       } 
/*     */     }
/*     */     
/*  66 */     return GrimAPI.INSTANCE.getMessagePlaceHolderManager().replacePlaceholders(platformPlayer, string).replace('￿', '%');
/*     */   }
/*     */   
/*     */   public static String filterDiscordText(String message) {
/*  70 */     if (message == null || message.isBlank()) return message; 
/*  71 */     StringBuilder sb = new StringBuilder(message.length());
/*  72 */     for (int i = 0; i < message.length(); i++) {
/*  73 */       char c = message.charAt(i);
/*     */       
/*  75 */       if (c == '\n') {
/*  76 */         sb.append("\\n");
/*     */       }
/*  78 */       else if (c == '`' || c == '*' || c == '_' || c == '~' || c == '|') {
/*  79 */         sb.append('\\').append(c);
/*     */       
/*     */       }
/*  82 */       else if (c == '#' || c == '>' || c == '-') {
/*     */         
/*  84 */         if (i + 1 < message.length() && message.charAt(i + 1) == ' ' && (i == 0 || message
/*  85 */           .charAt(i - 1) == '\n')) {
/*  86 */           sb.append("\\").append(c);
/*     */         } else {
/*  88 */           sb.append(c);
/*     */         } 
/*     */       } else {
/*  91 */         sb.append(c);
/*     */       } 
/*     */     } 
/*     */     
/*  95 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static Component replacePlaceholders(@NotNull GrimPlayer player, @NotNull Component component) {
/* 103 */     TextReplacementConfig safeReplacement = (TextReplacementConfig)TextReplacementConfig.builder().match("%[a-zA-Z0-9_]+%").replacement(placeholder -> Component.text(replacePlaceholders(player, placeholder.content()))).build();
/* 104 */     return component.replaceText(safeReplacement);
/*     */   }
/*     */   @NotNull
/*     */   public static Component miniMessage(@NotNull String string) {
/* 108 */     string = string.replace("%prefix%", GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("prefix", "&bGrim &8»"));
/*     */ 
/*     */     
/* 111 */     Matcher matcher = HEX_PATTERN.matcher(string);
/* 112 */     StringBuilder sb = new StringBuilder(string.length());
/*     */     
/* 114 */     while (matcher.find()) {
/* 115 */       matcher.appendReplacement(sb, "<#" + matcher.group(0).replaceAll("[&§#x]", "") + ">");
/*     */     }
/*     */     
/* 118 */     string = matcher.appendTail(sb).toString();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 143 */     string = translateAlternateColorCodes('&', string).replace("§0", "<!b><!i><!u><!st><!obf><black>").replace("§1", "<!b><!i><!u><!st><!obf><dark_blue>").replace("§2", "<!b><!i><!u><!st><!obf><dark_green>").replace("§3", "<!b><!i><!u><!st><!obf><dark_aqua>").replace("§4", "<!b><!i><!u><!st><!obf><dark_red>").replace("§5", "<!b><!i><!u><!st><!obf><dark_purple>").replace("§6", "<!b><!i><!u><!st><!obf><gold>").replace("§7", "<!b><!i><!u><!st><!obf><gray>").replace("§8", "<!b><!i><!u><!st><!obf><dark_gray>").replace("§9", "<!b><!i><!u><!st><!obf><blue>").replace("§a", "<!b><!i><!u><!st><!obf><green>").replace("§b", "<!b><!i><!u><!st><!obf><aqua>").replace("§c", "<!b><!i><!u><!st><!obf><red>").replace("§d", "<!b><!i><!u><!st><!obf><light_purple>").replace("§e", "<!b><!i><!u><!st><!obf><yellow>").replace("§f", "<!b><!i><!u><!st><!obf><white>").replace("§r", "<reset>").replace("§k", "<obfuscated>").replace("§l", "<bold>").replace("§m", "<strikethrough>").replace("§n", "<underlined>").replace("§o", "<italic>");
/*     */     
/* 145 */     return MiniMessage.miniMessage().deserialize(string).compact();
/*     */   }
/*     */   
/*     */   public static Component getParsedComponent(Sender sender, String key, String fallbackText) {
/* 149 */     String message = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse(key, fallbackText);
/* 150 */     message = replacePlaceholders(sender, message);
/* 151 */     return miniMessage(message);
/*     */   }
/*     */   @Contract("_, _ -> new")
/*     */   @NotNull
/*     */   public static String translateAlternateColorCodes(char altColorChar, @NotNull String textToTranslate) {
/* 156 */     char[] b = textToTranslate.toCharArray();
/*     */     
/* 158 */     for (int i = 0; i < b.length - 1; i++) {
/* 159 */       if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
/* 160 */         b[i] = '§';
/* 161 */         b[i + 1] = Character.toLowerCase(b[i + 1]);
/*     */       } 
/*     */     } 
/*     */     
/* 165 */     return new String(b);
/*     */   }
/*     */   @Contract("!null -> !null; null -> null")
/*     */   @Nullable
/*     */   public static String stripColor(@Nullable String input) {
/* 170 */     return (input == null) ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\anticheat\MessageUtil.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */