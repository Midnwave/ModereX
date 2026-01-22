/*    */ package ac.grim.grimac.utils.latency;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*    */ import ac.grim.grimac.utils.common.GrimArguments;
/*    */ import ac.grim.grimac.utils.data.Pair;
/*    */ import java.util.ArrayList;
/*    */ import java.util.LinkedList;
/*    */ import java.util.ListIterator;
/*    */ 
/*    */ public class LatencyUtils
/*    */ {
/* 15 */   private final LinkedList<Pair<Integer, Runnable>> transactionMap = new LinkedList<>();
/*    */ 
/*    */   
/*    */   private final GrimPlayer player;
/*    */   
/* 20 */   private final ArrayList<Runnable> tasksToRun = new ArrayList<>();
/*    */   
/*    */   public LatencyUtils(GrimPlayer player) {
/* 23 */     this.player = player;
/*    */   }
/*    */   
/*    */   public void addRealTimeTask(int transaction, Runnable runnable) {
/* 27 */     addRealTimeTask(transaction, false, runnable);
/*    */   }
/*    */   
/*    */   public void addRealTimeTaskAsync(int transaction, Runnable runnable) {
/* 31 */     addRealTimeTask(transaction, true, runnable);
/*    */   }
/*    */   
/*    */   public void addRealTimeTask(int transaction, boolean async, Runnable runnable) {
/* 35 */     if (this.player.lastTransactionReceived.get() >= transaction) {
/* 36 */       if (async) {
/* 37 */         this.player.runSafely(runnable);
/*    */       } else {
/* 39 */         runnable.run();
/*    */       } 
/*    */       return;
/*    */     } 
/* 43 */     synchronized (this) {
/* 44 */       this.transactionMap.add(new Pair(Integer.valueOf(transaction), runnable));
/*    */     } 
/*    */   }
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void handleNettySyncTransaction(int transaction) {
/* 68 */     synchronized (this) {
/* 69 */       this.tasksToRun.clear();
/*    */ 
/*    */       
/* 72 */       ListIterator<Pair<Integer, Runnable>> iterator = this.transactionMap.listIterator();
/* 73 */       while (iterator.hasNext()) {
/* 74 */         Pair<Integer, Runnable> pair = iterator.next();
/*    */ 
/*    */         
/* 77 */         if (transaction + 1 < ((Integer)pair.first()).intValue()) {
/*    */           break;
/*    */         }
/*    */         
/* 81 */         if (transaction == ((Integer)pair.first()).intValue() - 1) {
/*    */           continue;
/*    */         }
/* 84 */         this.tasksToRun.add((Runnable)pair.second());
/* 85 */         iterator.remove();
/*    */       } 
/*    */       
/* 88 */       for (Runnable runnable : this.tasksToRun) {
/*    */         try {
/* 90 */           runnable.run();
/* 91 */         } catch (Exception e) {
/* 92 */           LogUtil.error("An error has occurred when running transactions for player: " + this.player.user.getName(), e);
/*    */           
/* 94 */           if (GrimArguments.TRANSACTION_KICKS)
/* 95 */             this.player.disconnect(MessageUtil.miniMessage(MessageUtil.replacePlaceholders(this.player, GrimAPI.INSTANCE.getConfigManager().getDisconnectPacketError()))); 
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\latency\LatencyUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */