/*    */ package ac.grim.grimac.manager;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.manager.init.Initable;
/*    */ import ac.grim.grimac.manager.init.load.LoadableInitable;
/*    */ import ac.grim.grimac.manager.init.start.CommandRegister;
/*    */ import ac.grim.grimac.manager.init.start.JavaVersion;
/*    */ import ac.grim.grimac.manager.init.start.StartableInitable;
/*    */ import ac.grim.grimac.manager.init.start.ViaVersion;
/*    */ import ac.grim.grimac.manager.init.stop.StoppableInitable;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEventsAPI;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ import com.google.common.collect.ImmutableList;
/*    */ import com.google.common.collect.UnmodifiableIterator;
/*    */ import java.util.ArrayList;
/*    */ import java.util.function.Supplier;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class InitManager {
/*    */   private final ImmutableList<LoadableInitable> initializersOnLoad;
/*    */   private final ImmutableList<StartableInitable> initializersOnStart;
/*    */   
/*    */   @Generated
/* 26 */   public boolean isLoaded() { return this.loaded; } private final ImmutableList<StoppableInitable> initializersOnStop; private boolean loaded = false; private boolean started = false; @Generated
/*    */   public boolean isStarted() {
/* 28 */     return this.started; } private boolean stopped = false; @Generated
/*    */   public boolean isStopped() {
/* 30 */     return this.stopped;
/*    */   }
/*    */   
/*    */   public InitManager(PacketEventsAPI<?> packetEventsAPI, Supplier<CommandManager<Sender>> commandManager, Initable... platformSpecificInitables) {
/* 34 */     ArrayList<LoadableInitable> extraLoadableInitables = new ArrayList<>();
/* 35 */     ArrayList<StartableInitable> extraStartableInitables = new ArrayList<>();
/* 36 */     ArrayList<StoppableInitable> extraStoppableInitables = new ArrayList<>();
/* 37 */     for (Initable initable : platformSpecificInitables) {
/* 38 */       if (initable instanceof LoadableInitable) extraLoadableInitables.add((LoadableInitable)initable); 
/* 39 */       if (initable instanceof StartableInitable) extraStartableInitables.add((StartableInitable)initable); 
/* 40 */       if (initable instanceof StoppableInitable) extraStoppableInitables.add((StoppableInitable)initable);
/*    */     
/*    */     } 
/* 43 */     this
/*    */ 
/*    */ 
/*    */       
/* 47 */       .initializersOnLoad = ImmutableList.builder().add(new PacketEventsInit(packetEventsAPI)).add(() -> GrimAPI.INSTANCE.getExternalAPI().load()).addAll(extraLoadableInitables).build();
/*    */     
/* 49 */     this
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 64 */       .initializersOnStart = ImmutableList.builder().add(GrimAPI.INSTANCE.getExternalAPI()).add(new PacketManager()).add(new ViaBackwardsManager()).add(new TickRunner()).add(new CommandRegister(commandManager)).add(new PacketLimiter()).add(GrimAPI.INSTANCE.getAlertManager()).add(GrimAPI.INSTANCE.getDiscordManager()).add(GrimAPI.INSTANCE.getSpectateManager()).add(GrimAPI.INSTANCE.getViolationDatabaseManager()).add(new JavaVersion()).add(new ViaVersion()).add(new TAB()).addAll(extraStartableInitables).build();
/*    */     
/* 66 */     this
/*    */ 
/*    */       
/* 69 */       .initializersOnStop = ImmutableList.builder().add(new TerminatePacketEvents()).addAll(extraStoppableInitables).build();
/*    */   }
/*    */   
/*    */   public void load() {
/* 73 */     for (UnmodifiableIterator<LoadableInitable> unmodifiableIterator = this.initializersOnLoad.iterator(); unmodifiableIterator.hasNext(); ) { LoadableInitable initable = unmodifiableIterator.next();
/*    */       try {
/* 75 */         initable.load();
/* 76 */       } catch (Exception e) {
/* 77 */         LogUtil.error("Failed to load " + initable.getClass().getSimpleName(), e);
/*    */       }  }
/* 79 */      this.loaded = true;
/*    */   }
/*    */   
/*    */   public void start() {
/* 83 */     for (UnmodifiableIterator<StartableInitable> unmodifiableIterator = this.initializersOnStart.iterator(); unmodifiableIterator.hasNext(); ) { StartableInitable initable = unmodifiableIterator.next();
/*    */       try {
/* 85 */         initable.start();
/* 86 */       } catch (Exception e) {
/* 87 */         LogUtil.error("Failed to start " + initable.getClass().getSimpleName(), e);
/*    */       }  }
/* 89 */      this.started = true;
/*    */   }
/*    */   
/*    */   public void stop() {
/* 93 */     for (UnmodifiableIterator<StoppableInitable> unmodifiableIterator = this.initializersOnStop.iterator(); unmodifiableIterator.hasNext(); ) { StoppableInitable initable = unmodifiableIterator.next();
/*    */       try {
/* 95 */         initable.stop();
/* 96 */       } catch (Exception e) {
/* 97 */         LogUtil.error("Failed to stop " + initable.getClass().getSimpleName(), e);
/*    */       }  }
/* 99 */      this.stopped = true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\InitManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */