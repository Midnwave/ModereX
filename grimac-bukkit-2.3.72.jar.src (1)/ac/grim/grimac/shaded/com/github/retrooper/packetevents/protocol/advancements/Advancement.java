/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.advancements;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Advancement
/*     */ {
/*     */   @Nullable
/*     */   private ResourceLocation parent;
/*     */   @Nullable
/*     */   private AdvancementDisplay display;
/*     */   private List<String> criteria;
/*     */   private List<List<String>> requirements;
/*     */   private boolean sendsTelemetryData;
/*     */   
/*     */   public Advancement(@Nullable ResourceLocation parent, @Nullable AdvancementDisplay display, List<List<String>> requirements, boolean sendsTelemetryData) {
/*  50 */     this(parent, display, Collections.emptyList(), requirements, sendsTelemetryData);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public Advancement(@Nullable ResourceLocation parent, @Nullable AdvancementDisplay display, List<String> criteria, List<List<String>> requirements, boolean sendsTelemetryData) {
/*  61 */     this.parent = parent;
/*  62 */     this.display = display;
/*  63 */     this.criteria = criteria;
/*  64 */     this.requirements = requirements;
/*  65 */     this.sendsTelemetryData = sendsTelemetryData;
/*     */   }
/*     */   
/*     */   public static Advancement read(PacketWrapper<?> wrapper) {
/*  69 */     ResourceLocation parentId = (ResourceLocation)wrapper.readOptional(ResourceLocation::read);
/*  70 */     AdvancementDisplay display = (AdvancementDisplay)wrapper.readOptional(AdvancementDisplay::read);
/*     */     
/*  72 */     List<String> criteria = wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_20_2) ? wrapper.readList(PacketWrapper::readString) : null;
/*  73 */     List<List<String>> requirements = wrapper.readList(ew -> wrapper.readList(PacketWrapper::readString));
/*     */ 
/*     */     
/*  76 */     boolean sendsTelemetryData = (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20) && wrapper.readBoolean());
/*  77 */     return new Advancement(parentId, display, criteria, requirements, sendsTelemetryData);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, Advancement advancement) {
/*  81 */     wrapper.writeOptional(advancement.parent, ResourceLocation::write);
/*  82 */     wrapper.writeOptional(advancement.display, AdvancementDisplay::write);
/*  83 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_20_2)) {
/*  84 */       wrapper.writeList(advancement.criteria, PacketWrapper::writeString);
/*     */     }
/*  86 */     wrapper.writeList(advancement.getRequirements(), (ew, anyList) -> ew.writeList(anyList, PacketWrapper::writeString));
/*     */     
/*  88 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20))
/*  89 */       wrapper.writeBoolean(advancement.sendsTelemetryData); 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public ResourceLocation getParent() {
/*  94 */     return this.parent;
/*     */   }
/*     */   
/*     */   public void setParent(@Nullable ResourceLocation parent) {
/*  98 */     this.parent = parent;
/*     */   }
/*     */   @Nullable
/*     */   public AdvancementDisplay getDisplay() {
/* 102 */     return this.display;
/*     */   }
/*     */   
/*     */   public void setDisplay(@Nullable AdvancementDisplay display) {
/* 106 */     this.display = display;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getCriteria() {
/* 113 */     return this.criteria;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCriteria(List<String> criteria) {
/* 120 */     this.criteria = criteria;
/*     */   }
/*     */   
/*     */   public List<List<String>> getRequirements() {
/* 124 */     return this.requirements;
/*     */   }
/*     */   
/*     */   public void setRequirements(List<List<String>> requirements) {
/* 128 */     this.requirements = requirements;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSendsTelemetryData() {
/* 135 */     return this.sendsTelemetryData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSendsTelemetryData(boolean sendsTelemetryData) {
/* 142 */     this.sendsTelemetryData = sendsTelemetryData;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\advancements\Advancement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */