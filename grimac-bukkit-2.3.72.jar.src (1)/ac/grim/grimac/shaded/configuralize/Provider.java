/*    */ package ac.grim.grimac.shaded.configuralize;
/*    */ import ac.grim.grimac.shaded.maps.weak.Dynamic;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.nio.file.Files;
/*    */ import java.util.Map;
/*    */ import java.util.Objects;
/*    */ import java.util.stream.Collectors;
/*    */ 
/*    */ public class Provider {
/*    */   private final DynamicConfig config;
/*    */   private final Source source;
/*    */   private Dynamic defaults;
/*    */   private Dynamic values;
/*    */   
/*    */   private static Dynamic load(DynamicConfig config, Source source, String raw) throws ParseException {
/*    */     Map<?, ?> parsed;
/* 20 */     if (raw == null) throw new IllegalArgumentException("Can't load null config");
/*    */ 
/*    */     
/* 23 */     String extension = source.getFile().getName().substring(source.getFile().getName().lastIndexOf(".") + 1);
/*    */     try {
/* 25 */       if (extension.equalsIgnoreCase("yml")) {
/* 26 */         parsed = (Map<?, ?>)config.getYamlParser().loadAs(raw, Map.class);
/* 27 */       } else if (extension.equalsIgnoreCase("json")) {
/* 28 */         parsed = (Map<?, ?>)config.getJsonParser().parse(raw);
/*    */       } else {
/* 30 */         throw new IllegalArgumentException("Config source extension " + extension + " is not supported");
/*    */       } 
/* 32 */     } catch (ParseException|ac.grim.grimac.shaded.snakeyaml.parser.ParserException e) {
/* 33 */       throw new ParseException(source, e);
/*    */     } 
/* 35 */     return Dynamic.from(parsed);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Provider(DynamicConfig config, Source source) {
/* 44 */     this.config = config;
/* 45 */     this.source = source;
/*    */   }
/*    */   
/*    */   public void load() throws IOException, ParseException {
/* 49 */     this.defaults = loadResource();
/* 50 */     this.values = loadValues();
/*    */   }
/*    */   public Dynamic loadValues() throws ParseException, IOException {
/* 53 */     return load(this.config, this.source, new String(Files.readAllBytes(this.source.getFile().toPath())));
/*    */   }
/*    */   
/* 56 */   public Dynamic loadResource() throws ParseException, IOException { InputStream stream = this.source.getResource().openStream(); 
/* 57 */     try { Objects.requireNonNull(stream, "Unknown resource " + this.source.getResourcePath(this.config.getLanguage()));
/* 58 */       InputStreamReader reader = new InputStreamReader(stream); 
/* 59 */       try { BufferedReader buffer = new BufferedReader(reader); 
/* 60 */         try { Dynamic dynamic = load(this.config, this.source, buffer.lines().collect(Collectors.joining("\n")));
/* 61 */           buffer.close();
/* 62 */           reader.close();
/* 63 */           if (stream != null) stream.close();  return dynamic; } catch (Throwable throwable) { try { buffer.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (Throwable throwable) { try { reader.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (Throwable throwable) { if (stream != null)
/*    */         try { stream.close(); }
/*    */         catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*    */           throw throwable; }
/* 67 */      } public void saveDefaults() throws IOException { saveDefaults(false); }
/*    */ 
/*    */   
/* 70 */   public void saveDefaults(boolean overwrite) throws IOException { if (this.source.getFile().exists() && !overwrite)
/* 71 */       return;  if (!this.source.getFile().getParentFile().exists() && !this.source.getFile().getParentFile().mkdirs()) {
/* 72 */       throw new IOException("Failed to create directory " + this.source.getFile().getParentFile().getAbsolutePath());
/*    */     }
/*    */     
/* 75 */     String resource = this.source.getResourcePath(this.config.getLanguage());
/* 76 */     InputStream stream = this.source.getResource().openStream(); 
/* 77 */     try { Objects.requireNonNull(stream, "Unknown resource " + this.source.getResourcePath(this.config.getLanguage()));
/* 78 */       Files.copy(stream, this.source.getFile().toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
/* 79 */       if (stream != null) stream.close();  } catch (Throwable throwable) { if (stream != null)
/*    */         try { stream.close(); }
/*    */         catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*    */           throw throwable; }
/* 83 */      } public DynamicConfig getConfig() { return this.config; }
/*    */   
/*    */   public Source getSource() {
/* 86 */     return this.source;
/*    */   }
/*    */   public Dynamic getDefaults() {
/* 89 */     return this.defaults;
/*    */   }
/*    */   public Dynamic getValues() {
/* 92 */     return this.values;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\configuralize\Provider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */