/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.bstats;
/*     */ 
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.bstats.charts.CustomChart;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.bstats.json.JsonObjectBuilder;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.HashSet;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ScheduledThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MetricsBase
/*     */ {
/*     */   public static final String METRICS_VERSION = "3.1.0";
/*     */   private static final String REPORT_URL = "https://bStats.org/api/v2/data/%s";
/*     */   private final ScheduledExecutorService scheduler;
/*     */   private final String platform;
/*     */   private final String serverUuid;
/*     */   private final int serviceId;
/*     */   private final Consumer<JsonObjectBuilder> appendPlatformDataConsumer;
/*     */   private final Consumer<JsonObjectBuilder> appendServiceDataConsumer;
/*     */   private final Consumer<Runnable> submitTaskConsumer;
/*     */   private final Supplier<Boolean> checkServiceEnabledSupplier;
/*     */   private final BiConsumer<String, Throwable> errorLogger;
/*     */   private final Consumer<String> infoLogger;
/*     */   private final boolean logErrors;
/*     */   private final boolean logSentData;
/*     */   private final boolean logResponseStatusText;
/*  51 */   private final Set<CustomChart> customCharts = new HashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean enabled;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MetricsBase(String platform, String serverUuid, int serviceId, boolean enabled, Consumer<JsonObjectBuilder> appendPlatformDataConsumer, Consumer<JsonObjectBuilder> appendServiceDataConsumer, Consumer<Runnable> submitTaskConsumer, Supplier<Boolean> checkServiceEnabledSupplier, BiConsumer<String, Throwable> errorLogger, Consumer<String> infoLogger, boolean logErrors, boolean logSentData, boolean logResponseStatusText, boolean skipRelocateCheck) {
/* 101 */     ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1, task -> {
/*     */           Thread thread = new Thread(task, "bStats-Metrics");
/*     */ 
/*     */           
/*     */           thread.setDaemon(true);
/*     */ 
/*     */           
/*     */           return thread;
/*     */         });
/*     */     
/* 111 */     scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
/* 112 */     this.scheduler = scheduler;
/*     */     
/* 114 */     this.platform = platform;
/* 115 */     this.serverUuid = serverUuid;
/* 116 */     this.serviceId = serviceId;
/* 117 */     this.enabled = enabled;
/* 118 */     this.appendPlatformDataConsumer = appendPlatformDataConsumer;
/* 119 */     this.appendServiceDataConsumer = appendServiceDataConsumer;
/* 120 */     this.submitTaskConsumer = submitTaskConsumer;
/* 121 */     this.checkServiceEnabledSupplier = checkServiceEnabledSupplier;
/* 122 */     this.errorLogger = errorLogger;
/* 123 */     this.infoLogger = infoLogger;
/* 124 */     this.logErrors = logErrors;
/* 125 */     this.logSentData = logSentData;
/* 126 */     this.logResponseStatusText = logResponseStatusText;
/*     */     
/* 128 */     if (!skipRelocateCheck) {
/* 129 */       checkRelocation();
/*     */     }
/*     */     
/* 132 */     if (enabled)
/*     */     {
/* 134 */       startSubmitting();
/*     */     }
/*     */   }
/*     */   
/*     */   public void addCustomChart(CustomChart chart) {
/* 139 */     this.customCharts.add(chart);
/*     */   }
/*     */   
/*     */   public void shutdown() {
/* 143 */     this.scheduler.shutdown();
/*     */   }
/*     */   
/*     */   private void startSubmitting() {
/* 147 */     Runnable submitTask = () -> {
/*     */         if (!this.enabled || !((Boolean)this.checkServiceEnabledSupplier.get()).booleanValue()) {
/*     */           this.scheduler.shutdown();
/*     */ 
/*     */ 
/*     */           
/*     */           return;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/*     */         if (this.submitTaskConsumer != null) {
/*     */           this.submitTaskConsumer.accept(this::submitData);
/*     */         } else {
/*     */           submitData();
/*     */         } 
/*     */       };
/*     */ 
/*     */ 
/*     */     
/* 167 */     long initialDelay = (long)(60000.0D * (3.0D + Math.random() * 3.0D));
/* 168 */     long secondDelay = (long)(60000.0D * Math.random() * 30.0D);
/* 169 */     this.scheduler.schedule(submitTask, initialDelay, TimeUnit.MILLISECONDS);
/* 170 */     this.scheduler.scheduleAtFixedRate(submitTask, initialDelay + secondDelay, 1800000L, TimeUnit.MILLISECONDS);
/*     */   }
/*     */   
/*     */   private void submitData() {
/* 174 */     JsonObjectBuilder baseJsonBuilder = new JsonObjectBuilder();
/* 175 */     this.appendPlatformDataConsumer.accept(baseJsonBuilder);
/*     */     
/* 177 */     JsonObjectBuilder serviceJsonBuilder = new JsonObjectBuilder();
/* 178 */     this.appendServiceDataConsumer.accept(serviceJsonBuilder);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 183 */     JsonObjectBuilder.JsonObject[] chartData = (JsonObjectBuilder.JsonObject[])this.customCharts.stream().map(customChart -> customChart.getRequestJsonObject(this.errorLogger, this.logErrors)).filter(Objects::nonNull).toArray(x$0 -> new JsonObjectBuilder.JsonObject[x$0]);
/*     */     
/* 185 */     serviceJsonBuilder.appendField("id", this.serviceId);
/* 186 */     serviceJsonBuilder.appendField("customCharts", chartData);
/* 187 */     baseJsonBuilder.appendField("service", serviceJsonBuilder.build());
/* 188 */     baseJsonBuilder.appendField("serverUUID", this.serverUuid);
/* 189 */     baseJsonBuilder.appendField("metricsVersion", "3.1.0");
/*     */     
/* 191 */     JsonObjectBuilder.JsonObject data = baseJsonBuilder.build();
/*     */     
/* 193 */     this.scheduler.execute(() -> {
/*     */           
/*     */           try {
/*     */             sendData(data);
/* 197 */           } catch (Exception e) {
/*     */             if (this.logErrors) {
/*     */               this.errorLogger.accept("Could not submit bStats metrics data", e);
/*     */             }
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   private void sendData(JsonObjectBuilder.JsonObject data) throws Exception {
/* 207 */     if (this.logSentData) {
/* 208 */       this.infoLogger.accept("Sent bStats metrics data: " + data.toString());
/*     */     }
/*     */     
/* 211 */     String url = String.format("https://bStats.org/api/v2/data/%s", new Object[] { this.platform });
/* 212 */     HttpsURLConnection connection = (HttpsURLConnection)(new URL(url)).openConnection();
/*     */ 
/*     */     
/* 215 */     byte[] compressedData = compress(data.toString());
/*     */     
/* 217 */     connection.setRequestMethod("POST");
/* 218 */     connection.addRequestProperty("Accept", "application/json");
/* 219 */     connection.addRequestProperty("Connection", "close");
/* 220 */     connection.addRequestProperty("Content-Encoding", "gzip");
/* 221 */     connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
/* 222 */     connection.setRequestProperty("Content-Type", "application/json");
/* 223 */     connection.setRequestProperty("User-Agent", "Metrics-Service/1");
/*     */     
/* 225 */     connection.setDoOutput(true);
/* 226 */     DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream()); 
/* 227 */     try { outputStream.write(compressedData);
/* 228 */       outputStream.close(); } catch (Throwable throwable) { try { outputStream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */        throw throwable; }
/* 230 */      StringBuilder builder = new StringBuilder();
/* 231 */     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream())); 
/*     */     try { String line;
/* 233 */       while ((line = bufferedReader.readLine()) != null) {
/* 234 */         builder.append(line);
/*     */       }
/* 236 */       bufferedReader.close(); } catch (Throwable throwable) { try { bufferedReader.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */        throw throwable; }
/* 238 */      if (this.logResponseStatusText) {
/* 239 */       this.infoLogger.accept("Sent data to bStats and received response: " + builder);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkRelocation() {
/* 248 */     if (System.getProperty("bstats.relocatecheck") == null || 
/* 249 */       !System.getProperty("bstats.relocatecheck").equals("false")) {
/*     */ 
/*     */       
/* 252 */       String defaultPackage = new String(new byte[] { 111, 114, 103, 46, 98, 115, 116, 97, 116, 115 });
/*     */       
/* 254 */       String examplePackage = new String(new byte[] { 121, 111, 117, 114, 46, 112, 97, 99, 107, 97, 103, 101 });
/*     */ 
/*     */ 
/*     */       
/* 258 */       if (MetricsBase.class.getPackage().getName().startsWith(defaultPackage) || MetricsBase.class
/* 259 */         .getPackage().getName().startsWith(examplePackage)) {
/* 260 */         throw new IllegalStateException("bStats Metrics class has not been relocated correctly!");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static byte[] compress(String str) throws IOException {
/* 272 */     if (str == null) {
/* 273 */       return null;
/*     */     }
/* 275 */     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
/* 276 */     GZIPOutputStream gzip = new GZIPOutputStream(outputStream); 
/* 277 */     try { gzip.write(str.getBytes(StandardCharsets.UTF_8));
/* 278 */       gzip.close(); } catch (Throwable throwable) { try { gzip.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }
/* 279 */      return outputStream.toByteArray();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\bstats\MetricsBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */