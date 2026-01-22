/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.legacy;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextReplacementConfig;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.flattener.ComponentFlattener;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.flattener.FlattenerListener;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextDecoration;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextFormat;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Services;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.Set;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class LegacyComponentSerializerImpl
/*     */   implements LegacyComponentSerializer
/*     */ {
/*  53 */   static final Pattern DEFAULT_URL_PATTERN = Pattern.compile("(?:(https?)://)?([-\\w_.]+\\.\\w{2,})(/\\S*)?");
/*  54 */   static final Pattern URL_SCHEME_PATTERN = Pattern.compile("^[a-z][a-z0-9+\\-.]*:");
/*  55 */   private static final TextDecoration[] DECORATIONS = TextDecoration.values();
/*     */   
/*     */   private static final char LEGACY_BUNGEE_HEX_CHAR = 'x';
/*  58 */   private static final Optional<LegacyComponentSerializer.Provider> SERVICE = Services.service(LegacyComponentSerializer.Provider.class); private final char character; private final char hexCharacter; @Nullable
/*  59 */   private final TextReplacementConfig urlReplacementConfig; private final boolean hexColours; static final Consumer<LegacyComponentSerializer.Builder> BUILDER = SERVICE
/*  60 */     .<Consumer<LegacyComponentSerializer.Builder>>map(LegacyComponentSerializer.Provider::legacy)
/*  61 */     .orElseGet(() -> ());
/*     */   private final boolean useTerriblyStupidHexFormat;
/*     */   private final ComponentFlattener flattener;
/*     */   private final CharacterAndFormatSet formats;
/*     */   
/*     */   static final class Instances {
/*  67 */     static final LegacyComponentSerializer SECTION = LegacyComponentSerializerImpl.SERVICE
/*  68 */       .map(LegacyComponentSerializer.Provider::legacySection)
/*  69 */       .orElseGet(() -> new LegacyComponentSerializerImpl('§', '#', null, false, false, ComponentFlattener.basic(), CharacterAndFormatSet.DEFAULT));
/*  70 */     static final LegacyComponentSerializer AMPERSAND = LegacyComponentSerializerImpl.SERVICE
/*  71 */       .map(LegacyComponentSerializer.Provider::legacyAmpersand)
/*  72 */       .orElseGet(() -> new LegacyComponentSerializerImpl('&', '#', null, false, false, ComponentFlattener.basic(), CharacterAndFormatSet.DEFAULT));
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
/*     */   LegacyComponentSerializerImpl(char character, char hexCharacter, @Nullable TextReplacementConfig urlReplacementConfig, boolean hexColours, boolean useTerriblyStupidHexFormat, ComponentFlattener flattener, CharacterAndFormatSet formats) {
/*  84 */     this.character = character;
/*  85 */     this.hexCharacter = hexCharacter;
/*  86 */     this.urlReplacementConfig = urlReplacementConfig;
/*  87 */     this.hexColours = hexColours;
/*  88 */     this.useTerriblyStupidHexFormat = useTerriblyStupidHexFormat;
/*  89 */     this.flattener = flattener;
/*  90 */     this.formats = formats;
/*     */   }
/*     */   @Nullable
/*     */   private FormatCodeType determineFormatType(char legacy, String input, int pos) {
/*  94 */     if (pos >= 14) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  99 */       int expectedCharacterPosition = pos - 14;
/* 100 */       int expectedIndicatorPosition = pos - 13;
/* 101 */       if (input.charAt(expectedCharacterPosition) == this.character && input.charAt(expectedIndicatorPosition) == 'x') {
/* 102 */         return FormatCodeType.BUNGEECORD_UNUSUAL_HEX;
/*     */       }
/*     */     } 
/* 105 */     if (legacy == this.hexCharacter && input.length() - pos >= 6)
/* 106 */       return FormatCodeType.KYORI_HEX; 
/* 107 */     if (this.formats.characters.indexOf(legacy) != -1) {
/* 108 */       return FormatCodeType.MOJANG_LEGACY;
/*     */     }
/* 110 */     return null;
/*     */   }
/*     */   @Nullable
/*     */   static LegacyFormat legacyFormat(char character) {
/* 114 */     int index = CharacterAndFormatSet.DEFAULT.characters.indexOf(character);
/* 115 */     if (index != -1) {
/* 116 */       TextFormat format = CharacterAndFormatSet.DEFAULT.formats.get(index);
/* 117 */       if (format instanceof NamedTextColor)
/* 118 */         return new LegacyFormat((NamedTextColor)format); 
/* 119 */       if (format instanceof TextDecoration)
/* 120 */         return new LegacyFormat((TextDecoration)format); 
/* 121 */       if (format instanceof Reset) {
/* 122 */         return LegacyFormat.RESET;
/*     */       }
/*     */     } 
/* 125 */     return null;
/*     */   }
/*     */   @Nullable
/*     */   private DecodedFormat decodeTextFormat(char legacy, String input, int pos) {
/* 129 */     FormatCodeType foundFormat = determineFormatType(legacy, input, pos);
/* 130 */     if (foundFormat == null) {
/* 131 */       return null;
/*     */     }
/* 133 */     if (foundFormat == FormatCodeType.KYORI_HEX) {
/* 134 */       TextColor parsed = tryParseHexColor(input.substring(pos, pos + 6));
/* 135 */       if (parsed != null)
/* 136 */         return new DecodedFormat(foundFormat, (TextFormat)parsed); 
/*     */     } else {
/* 138 */       if (foundFormat == FormatCodeType.MOJANG_LEGACY)
/* 139 */         return new DecodedFormat(foundFormat, this.formats.formats.get(this.formats.characters.indexOf(legacy))); 
/* 140 */       if (foundFormat == FormatCodeType.BUNGEECORD_UNUSUAL_HEX) {
/* 141 */         StringBuilder foundHex = new StringBuilder(6);
/* 142 */         for (int i = pos - 1; i >= pos - 11; i -= 2) {
/* 143 */           foundHex.append(input.charAt(i));
/*     */         }
/* 145 */         TextColor parsed = tryParseHexColor(foundHex.reverse().toString());
/* 146 */         if (parsed != null)
/* 147 */           return new DecodedFormat(foundFormat, (TextFormat)parsed); 
/*     */       } 
/*     */     } 
/* 150 */     return null;
/*     */   }
/*     */   @Nullable
/*     */   private static TextColor tryParseHexColor(String hexDigits) {
/*     */     try {
/* 155 */       int color = Integer.parseInt(hexDigits, 16);
/* 156 */       return TextColor.color(color);
/* 157 */     } catch (NumberFormatException ex) {
/* 158 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static boolean isHexTextColor(TextFormat format) {
/* 163 */     return (format instanceof TextColor && !(format instanceof NamedTextColor));
/*     */   } @Nullable
/*     */   private String toLegacyCode(TextFormat format) {
/*     */     TextColor textColor;
/* 167 */     if (isHexTextColor(format)) {
/* 168 */       TextColor color = (TextColor)format;
/* 169 */       if (this.hexColours) {
/* 170 */         String hex = String.format("%06x", new Object[] { Integer.valueOf(color.value()) });
/* 171 */         if (this.useTerriblyStupidHexFormat) {
/*     */           
/* 173 */           StringBuilder legacy = new StringBuilder(String.valueOf('x'));
/* 174 */           for (int i = 0, length = hex.length(); i < length; i++) {
/* 175 */             legacy.append(this.character).append(hex.charAt(i));
/*     */           }
/* 177 */           return legacy.toString();
/*     */         } 
/*     */         
/* 180 */         return this.hexCharacter + hex;
/*     */       } 
/*     */       
/* 183 */       if (!(color instanceof NamedTextColor))
/*     */       {
/*     */         
/* 186 */         textColor = TextColor.nearestColorTo(this.formats.colors, color);
/*     */       }
/*     */     } 
/*     */     
/* 190 */     int index = this.formats.formats.indexOf(textColor);
/* 191 */     if (index == -1)
/*     */     {
/* 193 */       return null;
/*     */     }
/* 195 */     return Character.toString(this.formats.characters.charAt(index));
/*     */   }
/*     */   
/*     */   private TextComponent extractUrl(TextComponent component) {
/* 199 */     if (this.urlReplacementConfig == null) return component; 
/* 200 */     Component newComponent = component.replaceText(this.urlReplacementConfig);
/* 201 */     if (newComponent instanceof TextComponent) return (TextComponent)newComponent; 
/* 202 */     return (TextComponent)((TextComponent.Builder)Component.text().append(newComponent)).build();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TextComponent deserialize(@NotNull String input) {
/* 207 */     int next = input.lastIndexOf(this.character, input.length() - 2);
/* 208 */     if (next == -1) {
/* 209 */       return extractUrl(Component.text(input));
/*     */     }
/*     */     
/* 212 */     List<TextComponent> parts = new ArrayList<>();
/*     */     
/* 214 */     TextComponent.Builder current = null;
/* 215 */     boolean reset = false;
/*     */     
/* 217 */     int pos = input.length();
/*     */     do {
/* 219 */       DecodedFormat decoded = decodeTextFormat(input.charAt(next + 1), input, next + 2);
/* 220 */       if (decoded != null) {
/* 221 */         int from = next + ((decoded.encodedFormat == FormatCodeType.KYORI_HEX) ? 8 : 2);
/* 222 */         if (from != pos) {
/* 223 */           if (current != null) {
/* 224 */             if (reset) {
/* 225 */               parts.add((TextComponent)current.build());
/* 226 */               reset = false;
/* 227 */               current = Component.text();
/*     */             } else {
/* 229 */               current = (TextComponent.Builder)Component.text().append((Component)current.build());
/*     */             } 
/*     */           } else {
/* 232 */             current = Component.text();
/*     */           } 
/*     */           
/* 235 */           current.content(input.substring(from, pos));
/* 236 */         } else if (current == null) {
/* 237 */           current = Component.text();
/*     */         } 
/*     */         
/* 240 */         if (!reset) {
/* 241 */           reset = applyFormat(current, decoded.format);
/*     */         }
/* 243 */         if (decoded.encodedFormat == FormatCodeType.BUNGEECORD_UNUSUAL_HEX)
/*     */         {
/*     */ 
/*     */           
/* 247 */           next -= 12;
/*     */         }
/* 249 */         pos = next;
/*     */       } 
/*     */       
/* 252 */       next = input.lastIndexOf(this.character, next - 1);
/* 253 */     } while (next != -1);
/*     */     
/* 255 */     if (current != null) {
/* 256 */       parts.add((TextComponent)current.build());
/*     */     }
/*     */     
/* 259 */     String remaining = (pos > 0) ? input.substring(0, pos) : "";
/* 260 */     if (parts.size() == 1 && remaining.isEmpty()) {
/* 261 */       return extractUrl(parts.get(0));
/*     */     }
/* 263 */     Collections.reverse(parts);
/* 264 */     return extractUrl((TextComponent)((TextComponent.Builder)Component.text().content(remaining).append(parts)).build());
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public String serialize(@NotNull Component component) {
/* 270 */     Cereal state = new Cereal();
/* 271 */     this.flattener.flatten(component, state);
/* 272 */     return state.toString();
/*     */   }
/*     */   
/*     */   private static boolean applyFormat(TextComponent.Builder builder, @NotNull TextFormat format) {
/* 276 */     if (format instanceof TextColor) {
/* 277 */       builder.colorIfAbsent((TextColor)format);
/* 278 */       return true;
/* 279 */     }  if (format instanceof TextDecoration) {
/* 280 */       builder.decoration((TextDecoration)format, TextDecoration.State.TRUE);
/* 281 */       return false;
/* 282 */     }  if (format instanceof Reset) {
/* 283 */       return true;
/*     */     }
/* 285 */     throw new IllegalArgumentException(String.format("unknown format '%s'", new Object[] { format.getClass() }));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public LegacyComponentSerializer.Builder toBuilder() {
/* 290 */     return new BuilderImpl(this);
/*     */   }
/*     */   
/*     */   private final class Cereal
/*     */     implements FlattenerListener {
/* 295 */     private final StringBuilder sb = new StringBuilder();
/* 296 */     private final StyleState style = new StyleState(); @Nullable
/*     */     private TextFormat lastWritten;
/* 298 */     private StyleState[] styles = new StyleState[8];
/* 299 */     private int head = -1;
/*     */ 
/*     */     
/*     */     public void pushStyle(@NotNull Style pushed) {
/* 303 */       int idx = ++this.head;
/* 304 */       if (idx >= this.styles.length) {
/* 305 */         this.styles = Arrays.<StyleState>copyOf(this.styles, this.styles.length * 2);
/*     */       }
/* 307 */       StyleState state = this.styles[idx];
/*     */       
/* 309 */       if (state == null) {
/* 310 */         this.styles[idx] = state = new StyleState();
/*     */       }
/*     */       
/* 313 */       if (idx > 0) {
/*     */ 
/*     */         
/* 316 */         state.set(this.styles[idx - 1]);
/*     */       } else {
/* 318 */         state.clear();
/*     */       } 
/*     */       
/* 321 */       state.apply(pushed);
/*     */     }
/*     */ 
/*     */     
/*     */     public void component(@NotNull String text) {
/* 326 */       if (!text.isEmpty()) {
/* 327 */         if (this.head < 0) throw new IllegalStateException("No style has been pushed!");
/*     */         
/* 329 */         this.styles[this.head].applyFormat();
/* 330 */         this.sb.append(text);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void popStyle(@NotNull Style style) {
/* 336 */       if (this.head-- < 0) {
/* 337 */         throw new IllegalStateException("Tried to pop beyond what was pushed!");
/*     */       }
/*     */     }
/*     */     
/*     */     void append(@NotNull TextFormat format) {
/* 342 */       if (this.lastWritten != format) {
/* 343 */         String legacyCode = LegacyComponentSerializerImpl.this.toLegacyCode(format);
/* 344 */         if (legacyCode == null) {
/*     */           return;
/*     */         }
/* 347 */         this.sb.append(LegacyComponentSerializerImpl.this.character).append(legacyCode);
/*     */       } 
/* 349 */       this.lastWritten = format;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 354 */       return this.sb.toString();
/*     */     }
/*     */     
/*     */     private Cereal() {}
/*     */     
/*     */     private final class StyleState
/*     */     {
/*     */       @Nullable
/*     */       private TextColor color;
/* 363 */       private final Set<TextDecoration> decorations = EnumSet.noneOf(TextDecoration.class);
/*     */       private boolean needsReset;
/*     */       
/*     */       void set(@NotNull StyleState that) {
/* 367 */         this.color = that.color;
/* 368 */         this.decorations.clear();
/* 369 */         this.decorations.addAll(that.decorations);
/*     */       }
/*     */       
/*     */       public void clear() {
/* 373 */         this.color = null;
/* 374 */         this.decorations.clear();
/*     */       }
/*     */       
/*     */       void apply(@NotNull Style component) {
/* 378 */         TextColor color = component.color();
/* 379 */         if (color != null) {
/* 380 */           this.color = color;
/*     */         }
/*     */         
/* 383 */         for (int i = 0, length = LegacyComponentSerializerImpl.DECORATIONS.length; i < length; i++) {
/* 384 */           TextDecoration decoration = LegacyComponentSerializerImpl.DECORATIONS[i];
/* 385 */           switch (component.decoration(decoration)) {
/*     */             case TRUE:
/* 387 */               this.decorations.add(decoration);
/*     */               break;
/*     */             case FALSE:
/* 390 */               if (this.decorations.remove(decoration)) {
/* 391 */                 this.needsReset = true;
/*     */               }
/*     */               break;
/*     */           } 
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/*     */       void applyFormat() {
/* 400 */         boolean colorChanged = (this.color != LegacyComponentSerializerImpl.Cereal.this.style.color);
/* 401 */         if (this.needsReset) {
/* 402 */           if (!colorChanged) {
/* 403 */             LegacyComponentSerializerImpl.Cereal.this.append(Reset.INSTANCE);
/*     */           }
/* 405 */           this.needsReset = false;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 410 */         if (colorChanged || LegacyComponentSerializerImpl.Cereal.this.lastWritten == Reset.INSTANCE) {
/* 411 */           applyFullFormat();
/*     */ 
/*     */           
/*     */           return;
/*     */         } 
/*     */         
/* 417 */         if (!this.decorations.containsAll(LegacyComponentSerializerImpl.Cereal.this.style.decorations)) {
/* 418 */           applyFullFormat();
/*     */           
/*     */           return;
/*     */         } 
/*     */         
/* 423 */         for (TextDecoration decoration : this.decorations) {
/* 424 */           if (LegacyComponentSerializerImpl.Cereal.this.style.decorations.add(decoration)) {
/* 425 */             LegacyComponentSerializerImpl.Cereal.this.append((TextFormat)decoration);
/*     */           }
/*     */         } 
/*     */       }
/*     */       
/*     */       private void applyFullFormat() {
/* 431 */         if (this.color != null) {
/* 432 */           LegacyComponentSerializerImpl.Cereal.this.append((TextFormat)this.color);
/*     */         } else {
/* 434 */           LegacyComponentSerializerImpl.Cereal.this.append(Reset.INSTANCE);
/*     */         } 
/* 436 */         LegacyComponentSerializerImpl.Cereal.this.style.color = this.color;
/*     */         
/* 438 */         for (TextDecoration decoration : this.decorations) {
/* 439 */           LegacyComponentSerializerImpl.Cereal.this.append((TextFormat)decoration);
/*     */         }
/*     */         
/* 442 */         LegacyComponentSerializerImpl.Cereal.this.style.decorations.clear();
/* 443 */         LegacyComponentSerializerImpl.Cereal.this.style.decorations.addAll(this.decorations);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   static final class BuilderImpl implements LegacyComponentSerializer.Builder {
/* 449 */     private char character = '§';
/* 450 */     private char hexCharacter = '#';
/* 451 */     private TextReplacementConfig urlReplacementConfig = null;
/*     */     private boolean hexColours = false;
/*     */     private boolean useTerriblyStupidHexFormat = false;
/* 454 */     private ComponentFlattener flattener = ComponentFlattener.basic();
/* 455 */     private CharacterAndFormatSet formats = CharacterAndFormatSet.DEFAULT;
/*     */     
/*     */     BuilderImpl() {
/* 458 */       LegacyComponentSerializerImpl.BUILDER.accept(this);
/*     */     }
/*     */     
/*     */     BuilderImpl(@NotNull LegacyComponentSerializerImpl serializer) {
/* 462 */       this();
/* 463 */       this.character = serializer.character;
/* 464 */       this.hexCharacter = serializer.hexCharacter;
/* 465 */       this.urlReplacementConfig = serializer.urlReplacementConfig;
/* 466 */       this.hexColours = serializer.hexColours;
/* 467 */       this.useTerriblyStupidHexFormat = serializer.useTerriblyStupidHexFormat;
/* 468 */       this.flattener = serializer.flattener;
/* 469 */       this.formats = serializer.formats;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public LegacyComponentSerializer.Builder character(char legacyCharacter) {
/* 474 */       this.character = legacyCharacter;
/* 475 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public LegacyComponentSerializer.Builder hexCharacter(char legacyHexCharacter) {
/* 480 */       this.hexCharacter = legacyHexCharacter;
/* 481 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public LegacyComponentSerializer.Builder extractUrls() {
/* 486 */       return extractUrls(LegacyComponentSerializerImpl.DEFAULT_URL_PATTERN, null);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public LegacyComponentSerializer.Builder extractUrls(@NotNull Pattern pattern) {
/* 491 */       return extractUrls(pattern, null);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public LegacyComponentSerializer.Builder extractUrls(@Nullable Style style) {
/* 496 */       return extractUrls(LegacyComponentSerializerImpl.DEFAULT_URL_PATTERN, style);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public LegacyComponentSerializer.Builder extractUrls(@NotNull Pattern pattern, @Nullable Style style) {
/* 501 */       Objects.requireNonNull(pattern, "pattern");
/* 502 */       this
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 511 */         .urlReplacementConfig = (TextReplacementConfig)TextReplacementConfig.builder().match(pattern).replacement(url -> { String clickUrl = url.content(); if (!LegacyComponentSerializerImpl.URL_SCHEME_PATTERN.matcher(clickUrl).find()) clickUrl = "http://" + clickUrl;  return (ComponentLike)((style == null) ? url : (TextComponent.Builder)url.style(style)).clickEvent(ClickEvent.openUrl(clickUrl)); }).build();
/* 512 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public LegacyComponentSerializer.Builder hexColors() {
/* 517 */       this.hexColours = true;
/* 518 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public LegacyComponentSerializer.Builder useUnusualXRepeatedCharacterHexFormat() {
/* 523 */       this.useTerriblyStupidHexFormat = true;
/* 524 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public LegacyComponentSerializer.Builder flattener(@NotNull ComponentFlattener flattener) {
/* 529 */       this.flattener = Objects.<ComponentFlattener>requireNonNull(flattener, "flattener");
/* 530 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public LegacyComponentSerializer.Builder formats(@NotNull List<CharacterAndFormat> formats) {
/* 535 */       this.formats = CharacterAndFormatSet.of(formats);
/* 536 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public LegacyComponentSerializer build() {
/* 541 */       return new LegacyComponentSerializerImpl(this.character, this.hexCharacter, this.urlReplacementConfig, this.hexColours, this.useTerriblyStupidHexFormat, this.flattener, this.formats);
/*     */     }
/*     */   }
/*     */   
/*     */   enum FormatCodeType {
/* 546 */     MOJANG_LEGACY,
/* 547 */     KYORI_HEX,
/* 548 */     BUNGEECORD_UNUSUAL_HEX;
/*     */   }
/*     */   
/*     */   static final class DecodedFormat {
/*     */     final LegacyComponentSerializerImpl.FormatCodeType encodedFormat;
/*     */     final TextFormat format;
/*     */     
/*     */     private DecodedFormat(LegacyComponentSerializerImpl.FormatCodeType encodedFormat, TextFormat format) {
/* 556 */       if (format == null) {
/* 557 */         throw new IllegalStateException("No format found");
/*     */       }
/* 559 */       this.encodedFormat = encodedFormat;
/* 560 */       this.format = format;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\legacy\LegacyComponentSerializerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */