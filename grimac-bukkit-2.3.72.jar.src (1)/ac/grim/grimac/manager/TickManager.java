/*    */ package ac.grim.grimac.manager;
/*    */ 
/*    */ import ac.grim.grimac.manager.tick.Tickable;
/*    */ import ac.grim.grimac.manager.tick.impl.ClearRecentlyUpdatedBlocks;
/*    */ import ac.grim.grimac.manager.tick.impl.ClientVersionSetter;
/*    */ import ac.grim.grimac.manager.tick.impl.ResetTick;
/*    */ import ac.grim.grimac.manager.tick.impl.TickInventory;
/*    */ import com.google.common.collect.ClassToInstanceMap;
/*    */ import com.google.common.collect.ImmutableClassToInstanceMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TickManager
/*    */ {
/*    */   public int currentTick;
/* 18 */   ClassToInstanceMap<Tickable> syncTick = (ClassToInstanceMap<Tickable>)(new ImmutableClassToInstanceMap.Builder())
/* 19 */     .put(ResetTick.class, new ResetTick())
/* 20 */     .build();
/*    */   
/* 22 */   ClassToInstanceMap<Tickable> asyncTick = (ClassToInstanceMap<Tickable>)(new ImmutableClassToInstanceMap.Builder())
/* 23 */     .put(ClientVersionSetter.class, new ClientVersionSetter())
/* 24 */     .put(TickInventory.class, new TickInventory())
/* 25 */     .put(ClearRecentlyUpdatedBlocks.class, new ClearRecentlyUpdatedBlocks())
/* 26 */     .build();
/*    */ 
/*    */   
/*    */   public void tickSync() {
/* 30 */     this.currentTick++;
/* 31 */     this.syncTick.values().forEach(Tickable::tick);
/*    */   }
/*    */   
/*    */   public void tickAsync() {
/* 35 */     this.asyncTick.values().forEach(Tickable::tick);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\TickManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */