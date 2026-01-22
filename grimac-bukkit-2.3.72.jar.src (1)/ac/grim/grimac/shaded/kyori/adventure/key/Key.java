/*     */ package ac.grim.grimac.shaded.kyori.adventure.key;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Comparator;
/*     */ import java.util.Objects;
/*     */ import java.util.OptionalInt;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface Key
/*     */   extends Comparable<Key>, Examinable, Namespaced, Keyed
/*     */ {
/*     */   public static final String MINECRAFT_NAMESPACE = "minecraft";
/*     */   public static final char DEFAULT_SEPARATOR = ':';
/*     */   
/*     */   @NotNull
/*     */   static Key key(@KeyPattern @NotNull String string) {
/*  89 */     return key(string, ':');
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
/*     */   @NotNull
/*     */   static Key key(@NotNull String string, char character) {
/* 109 */     Objects.requireNonNull(string, "string");
/* 110 */     int index = string.indexOf(character);
/* 111 */     String namespace = (index >= 1) ? string.substring(0, index) : "minecraft";
/* 112 */     String value = (index >= 0) ? string.substring(index + 1) : string;
/* 113 */     return key(namespace, value);
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
/*     */   static Key key(@NotNull Namespaced namespaced, @Value @NotNull String value) {
/* 126 */     return key(((Namespaced)Objects.<Namespaced>requireNonNull(namespaced, "namespaced")).namespace(), value);
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
/*     */   static Key key(@Namespace @NotNull String namespace, @Value @NotNull String value) {
/* 139 */     return new KeyImpl(namespace, value);
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
/*     */   static Comparator<? super Key> comparator() {
/* 151 */     return KeyImpl.COMPARATOR;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean parseable(@Nullable String string) {
/* 162 */     if (string == null) {
/* 163 */       return false;
/*     */     }
/* 165 */     int index = string.indexOf(':');
/* 166 */     String namespace = (index >= 1) ? string.substring(0, index) : "minecraft";
/* 167 */     String value = (index >= 0) ? string.substring(index + 1) : string;
/* 168 */     return (parseableNamespace(namespace) && parseableValue(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean parseableNamespace(@NotNull String namespace) {
/* 179 */     return !checkNamespace(namespace).isPresent();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static OptionalInt checkNamespace(@NotNull String namespace) {
/* 190 */     Objects.requireNonNull(namespace, "namespace");
/* 191 */     for (int i = 0, length = namespace.length(); i < length; i++) {
/* 192 */       if (!allowedInNamespace(namespace.charAt(i))) {
/* 193 */         return OptionalInt.of(i);
/*     */       }
/*     */     } 
/* 196 */     return OptionalInt.empty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean parseableValue(@NotNull String value) {
/* 207 */     return !checkValue(value).isPresent();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static OptionalInt checkValue(@NotNull String value) {
/* 218 */     Objects.requireNonNull(value, "value");
/* 219 */     for (int i = 0, length = value.length(); i < length; i++) {
/* 220 */       if (!allowedInValue(value.charAt(i))) {
/* 221 */         return OptionalInt.of(i);
/*     */       }
/*     */     } 
/* 224 */     return OptionalInt.empty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean allowedInNamespace(char character) {
/* 235 */     return KeyImpl.allowedInNamespace(character);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean allowedInValue(char character) {
/* 246 */     return KeyImpl.allowedInValue(character);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */   default String asMinimalString() {
/* 285 */     if (namespace().equals("minecraft")) {
/* 286 */       return value();
/*     */     }
/* 288 */     return asString();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   default Stream<? extends ExaminableProperty> examinableProperties() {
/* 293 */     return Stream.of(new ExaminableProperty[] {
/* 294 */           ExaminableProperty.of("namespace", namespace()), 
/* 295 */           ExaminableProperty.of("value", value())
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   default int compareTo(@NotNull Key that) {
/* 301 */     return comparator().compare(this, that);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   default Key key() {
/* 306 */     return this;
/*     */   }
/*     */   
/*     */   @Namespace
/*     */   @NotNull
/*     */   String namespace();
/*     */   
/*     */   @Value
/*     */   @NotNull
/*     */   String value();
/*     */   
/*     */   @NotNull
/*     */   String asString();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\key\Key.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */