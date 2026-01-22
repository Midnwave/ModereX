/*     */ package ac.grim.grimac.shaded.kyori.adventure.key;
/*     */ 
/*     */ import ac.grim.grimac.shaded.intellij.lang.annotations.RegExp;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Arrays;
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
/*     */ final class KeyImpl
/*     */   implements Key
/*     */ {
/*  39 */   static final Comparator<? super Key> COMPARATOR = Comparator.comparing(Key::value).thenComparing(Key::namespace);
/*     */   @RegExp
/*     */   static final String NAMESPACE_PATTERN = "[a-z0-9_\\-.]+";
/*     */   @RegExp
/*     */   static final String VALUE_PATTERN = "[a-z0-9_\\-./]+";
/*     */   private final String namespace;
/*     */   private final String value;
/*     */   
/*     */   KeyImpl(@NotNull String namespace, @NotNull String value) {
/*  48 */     checkError("namespace", namespace, namespace, value, Key.checkNamespace(namespace), "[a-z0-9_\\-.]+");
/*  49 */     checkError("value", value, namespace, value, Key.checkValue(value), "[a-z0-9_\\-./]+");
/*  50 */     this.namespace = Objects.<String>requireNonNull(namespace, "namespace");
/*  51 */     this.value = Objects.<String>requireNonNull(value, "value");
/*     */   }
/*     */   
/*     */   private static void checkError(String name, String checkPart, String namespace, String value, OptionalInt index, String pattern) {
/*  55 */     if (index.isPresent()) {
/*  56 */       int indexValue = index.getAsInt();
/*  57 */       char character = checkPart.charAt(indexValue);
/*  58 */       throw new InvalidKeyException(namespace, value, String.format("Non " + pattern + " character in %s of Key[%s] at index %d ('%s', bytes: %s)", new Object[] { name, 
/*     */ 
/*     */               
/*  61 */               asString(namespace, value), 
/*  62 */               Integer.valueOf(indexValue), 
/*  63 */               Character.valueOf(character), 
/*  64 */               Arrays.toString(String.valueOf(character).getBytes(StandardCharsets.UTF_8)) }));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static boolean allowedInNamespace(char character) {
/*  70 */     return (character == '_' || character == '-' || (character >= 'a' && character <= 'z') || (character >= '0' && character <= '9') || character == '.');
/*     */   }
/*     */   
/*     */   static boolean allowedInValue(char character) {
/*  74 */     return (character == '_' || character == '-' || (character >= 'a' && character <= 'z') || (character >= '0' && character <= '9') || character == '.' || character == '/');
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String namespace() {
/*  79 */     return this.namespace;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String value() {
/*  84 */     return this.value;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String asString() {
/*  89 */     return asString(this.namespace, this.value);
/*     */   }
/*     */   @NotNull
/*     */   private static String asString(@NotNull String namespace, @NotNull String value) {
/*  93 */     return namespace + ':' + value;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String toString() {
/*  98 */     return asString();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 103 */     return Stream.of(new ExaminableProperty[] {
/* 104 */           ExaminableProperty.of("namespace", this.namespace), 
/* 105 */           ExaminableProperty.of("value", this.value)
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 111 */     if (this == other) return true; 
/* 112 */     if (!(other instanceof Key)) return false; 
/* 113 */     Key that = (Key)other;
/* 114 */     return (Objects.equals(this.namespace, that.namespace()) && Objects.equals(this.value, that.value()));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 119 */     int result = this.namespace.hashCode();
/* 120 */     result = 31 * result + this.value.hashCode();
/* 121 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(@NotNull Key that) {
/* 126 */     return super.compareTo(that);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\key\KeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */