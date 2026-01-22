/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.settings;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.TimeStampMode;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import java.io.InputStream;
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
/*     */ public class PacketEventsSettings
/*     */ {
/*     */   private TimeStampMode timestampMode;
/*     */   private boolean defaultReencode;
/*     */   private boolean checkForUpdates;
/*     */   private boolean downsampleColors;
/*     */   private boolean debugEnabled;
/*     */   private boolean fullStackTraceEnabled;
/*     */   private boolean kickOnPacketExceptionEnabled;
/*     */   private boolean kickIfTerminated;
/*     */   private boolean preViaInjection;
/*     */   private Function<String, InputStream> resourceProvider;
/*     */   
/*     */   public PacketEventsSettings() {
/*  35 */     this.timestampMode = TimeStampMode.MILLIS;
/*  36 */     this.defaultReencode = true;
/*  37 */     this.checkForUpdates = true;
/*  38 */     this.downsampleColors = false;
/*  39 */     this.debugEnabled = false;
/*  40 */     this.fullStackTraceEnabled = false;
/*  41 */     this.kickOnPacketExceptionEnabled = true;
/*  42 */     this.kickIfTerminated = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  48 */     this.preViaInjection = false;
/*  49 */     this.resourceProvider = (path -> PacketEventsSettings.class.getClassLoader().getResourceAsStream(path));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public PacketEventsSettings timeStampMode(TimeStampMode timeStampMode) {
/*  61 */     this.timestampMode = timeStampMode;
/*  62 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public PacketEventsSettings reEncodeByDefault(boolean reEncodeByDefault) {
/*  73 */     this.defaultReencode = reEncodeByDefault;
/*  74 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public PacketEventsSettings checkForUpdates(boolean checkForUpdates) {
/*  85 */     this.checkForUpdates = checkForUpdates;
/*  86 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public PacketEventsSettings downsampleColors(boolean downsampleColors) {
/*  97 */     this.downsampleColors = downsampleColors;
/*  98 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public PacketEventsSettings bStats(boolean bStatsEnabled) {
/* 110 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public PacketEventsSettings debug(boolean debugEnabled) {
/* 121 */     this.debugEnabled = debugEnabled;
/* 122 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public PacketEventsSettings fullStackTrace(boolean fullStackTraceEnabled) {
/* 133 */     this.fullStackTraceEnabled = fullStackTraceEnabled;
/* 134 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public PacketEventsSettings kickOnPacketException(boolean kickOnPacketExceptionEnabled) {
/* 145 */     this.kickOnPacketExceptionEnabled = kickOnPacketExceptionEnabled;
/* 146 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public PacketEventsSettings kickIfTerminated(boolean kickIfTerminated) {
/* 157 */     this.kickIfTerminated = kickIfTerminated;
/* 158 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public PacketEventsSettings preViaInjection(boolean preViaInjection) {
/* 169 */     this.preViaInjection = preViaInjection;
/* 170 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public PacketEventsSettings customResourceProvider(Function<String, InputStream> resourceProvider) {
/* 182 */     this.resourceProvider = resourceProvider;
/* 183 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean reEncodeByDefault() {
/* 192 */     return this.defaultReencode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean shouldCheckForUpdates() {
/* 201 */     return this.checkForUpdates;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean shouldDownsampleColors() {
/* 210 */     return this.downsampleColors;
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
/*     */   @Deprecated
/*     */   public boolean isbStatsEnabled() {
/* 223 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDebugEnabled() {
/* 232 */     return this.debugEnabled;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFullStackTraceEnabled() {
/* 241 */     return this.fullStackTraceEnabled;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isKickOnPacketExceptionEnabled() {
/* 250 */     return this.kickOnPacketExceptionEnabled;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isKickIfTerminated() {
/* 259 */     return this.kickIfTerminated;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPreViaInjection() {
/* 268 */     return this.preViaInjection;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Function<String, InputStream> getResourceProvider() {
/* 278 */     return this.resourceProvider;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TimeStampMode getTimeStampMode() {
/* 287 */     return this.timestampMode;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\settings\PacketEventsSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */