/*     */ package ac.grim.grimac.api.alerts;
/*     */ 
/*     */ import ac.grim.grimac.api.GrimUser;
/*     */ import lombok.NonNull;
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
/*     */ public interface AlertManager
/*     */ {
/*     */   boolean hasAlertsEnabled(@NonNull GrimUser paramGrimUser);
/*     */   
/*     */   default boolean toggleAlerts(@NonNull GrimUser player) {
/*  25 */     if (player == null) throw new NullPointerException("player is marked non-null but is null"); 
/*  26 */     return toggleAlerts(player, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean toggleAlerts(@NonNull GrimUser player, boolean silent) {
/*  37 */     if (player == null) throw new NullPointerException("player is marked non-null but is null"); 
/*  38 */     boolean newState = !hasAlertsEnabled(player);
/*  39 */     setAlertsEnabled(player, newState, silent);
/*  40 */     return newState;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void setAlertsEnabled(@NonNull GrimUser player, boolean enabled) {
/*  51 */     if (player == null) throw new NullPointerException("player is marked non-null but is null"); 
/*  52 */     setAlertsEnabled(player, enabled, false);
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
/*     */   void setAlertsEnabled(@NonNull GrimUser paramGrimUser, boolean paramBoolean1, boolean paramBoolean2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean hasVerboseEnabled(@NonNull GrimUser paramGrimUser);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean toggleVerbose(@NonNull GrimUser player) {
/*  84 */     if (player == null) throw new NullPointerException("player is marked non-null but is null"); 
/*  85 */     return toggleVerbose(player, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean toggleVerbose(@NonNull GrimUser player, boolean silent) {
/*  96 */     if (player == null) throw new NullPointerException("player is marked non-null but is null"); 
/*  97 */     boolean newState = !hasVerboseEnabled(player);
/*  98 */     setVerboseEnabled(player, newState, silent);
/*  99 */     return newState;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void setVerboseEnabled(@NonNull GrimUser player, boolean enabled) {
/* 110 */     if (player == null) throw new NullPointerException("player is marked non-null but is null"); 
/* 111 */     setVerboseEnabled(player, enabled, false);
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
/*     */   void setVerboseEnabled(@NonNull GrimUser paramGrimUser, boolean paramBoolean1, boolean paramBoolean2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean hasBrandsEnabled(@NonNull GrimUser paramGrimUser);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean toggleBrands(@NonNull GrimUser player) {
/* 144 */     if (player == null) throw new NullPointerException("player is marked non-null but is null"); 
/* 145 */     return toggleBrands(player, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean toggleBrands(@NonNull GrimUser player, boolean silent) {
/* 156 */     if (player == null) throw new NullPointerException("player is marked non-null but is null"); 
/* 157 */     boolean newState = !hasBrandsEnabled(player);
/* 158 */     setBrandsEnabled(player, newState, silent);
/* 159 */     return newState;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void setBrandsEnabled(@NonNull GrimUser player, boolean enabled) {
/* 170 */     if (player == null) throw new NullPointerException("player is marked non-null but is null"); 
/* 171 */     setBrandsEnabled(player, enabled, false);
/*     */   }
/*     */   
/*     */   void setBrandsEnabled(@NonNull GrimUser paramGrimUser, boolean paramBoolean1, boolean paramBoolean2);
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\alerts\AlertManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */