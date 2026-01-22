/*     */ package ac.grim.grimac.shaded.kyori.adventure.internal.properties;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.VisibleForTesting;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.Optional;
/*     */ import java.util.Properties;
/*     */ import java.util.function.Function;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class AdventurePropertiesImpl
/*     */ {
/*     */   private static final String FILESYSTEM_DIRECTORY_NAME = "config";
/*     */   private static final String FILESYSTEM_FILE_NAME = "adventure.properties";
/*  41 */   private static final Properties PROPERTIES = new Properties();
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  46 */     Path path = Optional.<String>ofNullable(System.getProperty(systemPropertyName("config"))).map(x$0 -> Paths.get(x$0, new String[0])).orElseGet(() -> Paths.get("config", new String[] { "adventure.properties" }));
/*  47 */     if (Files.isRegularFile(path, new java.nio.file.LinkOption[0])) {
/*  48 */       try { InputStream is = Files.newInputStream(path, new java.nio.file.OpenOption[0]); 
/*  49 */         try { PROPERTIES.load(is);
/*  50 */           if (is != null) is.close();  } catch (Throwable throwable) { if (is != null) try { is.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (IOException e)
/*     */       
/*  52 */       { print(e); }
/*     */     
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static void print(Throwable ex) {
/*  59 */     ex.printStackTrace();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @VisibleForTesting
/*     */   @NotNull
/*     */   static String systemPropertyName(String name) {
/*  67 */     return String.join(".", new CharSequence[] { "net", "kyori", "adventure", name });
/*     */   }
/*     */   
/*     */   static <T> AdventureProperties.Property<T> property(@NotNull String name, @NotNull Function<String, T> parser, @Nullable T defaultValue) {
/*  71 */     return new PropertyImpl<>(name, parser, defaultValue);
/*     */   }
/*     */   private static final class PropertyImpl<T> implements AdventureProperties.Property<T> { private final String name;
/*     */     private final Function<String, T> parser;
/*     */     @Nullable
/*     */     private final T defaultValue;
/*     */     private boolean valueCalculated;
/*     */     @Nullable
/*     */     private T value;
/*     */     
/*     */     PropertyImpl(@NotNull String name, @NotNull Function<String, T> parser, @Nullable T defaultValue) {
/*  82 */       this.name = name;
/*  83 */       this.parser = parser;
/*  84 */       this.defaultValue = defaultValue;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public T value() {
/*  89 */       if (!this.valueCalculated) {
/*  90 */         String property = AdventurePropertiesImpl.systemPropertyName(this.name);
/*  91 */         String value = System.getProperty(property, AdventurePropertiesImpl.PROPERTIES.getProperty(this.name));
/*  92 */         if (value != null) {
/*  93 */           this.value = this.parser.apply(value);
/*     */         }
/*  95 */         if (this.value == null) {
/*  96 */           this.value = this.defaultValue;
/*     */         }
/*  98 */         this.valueCalculated = true;
/*     */       } 
/* 100 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object that) {
/* 105 */       return (this == that);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 110 */       return this.name.hashCode();
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\internal\properties\AdventurePropertiesImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */