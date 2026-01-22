/*     */ package ac.grim.grimac.utils.nmsutil;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityGuardian;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntitySizeable;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityTrackXRot;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import lombok.Generated;
/*     */ 
/*     */ 
/*     */ public final class BoundingBoxSize
/*     */ {
/*     */   @Generated
/*     */   private BoundingBoxSize() {
/*  21 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*     */   }
/*     */   
/*     */   public static float getWidth(GrimPlayer player, PacketEntity packetEntity) {
/*  25 */     if (packetEntity.type == EntityTypes.TURTLE && packetEntity.isBaby) return 0.36F; 
/*  26 */     return getWidthMinusBaby(player, packetEntity) * (packetEntity.isBaby ? 0.5F : 1.0F);
/*     */   }
/*     */   
/*     */   private static float getWidthMinusBaby(GrimPlayer player, PacketEntity packetEntity) {
/*  30 */     EntityType type = packetEntity.type;
/*  31 */     if (EntityTypes.AXOLOTL.equals(type))
/*  32 */       return 0.75F; 
/*  33 */     if (EntityTypes.PANDA.equals(type))
/*  34 */       return 1.3F; 
/*  35 */     if (EntityTypes.BAT.equals(type) || EntityTypes.PARROT.equals(type) || EntityTypes.COD.equals(type) || EntityTypes.EVOKER_FANGS.equals(type) || EntityTypes.TROPICAL_FISH.equals(type) || EntityTypes.FROG.equals(type))
/*  36 */       return 0.5F; 
/*  37 */     if (EntityTypes.ARMADILLO.equals(type) || EntityTypes.BEE.equals(type) || EntityTypes.PUFFERFISH.equals(type) || EntityTypes.SALMON.equals(type) || EntityTypes.SNOW_GOLEM.equals(type) || EntityTypes.CAVE_SPIDER.equals(type))
/*  38 */       return 0.7F; 
/*  39 */     if (EntityTypes.WITHER_SKELETON.equals(type))
/*  40 */       return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.7F : 0.72F; 
/*  41 */     if (EntityTypes.WITHER_SKULL.equals(type) || EntityTypes.SHULKER_BULLET.equals(type))
/*  42 */       return 0.3125F; 
/*  43 */     if (EntityTypes.HOGLIN.equals(type) || EntityTypes.ZOGLIN.equals(type))
/*  44 */       return 1.3964844F; 
/*  45 */     if (EntityTypes.SKELETON_HORSE.equals(type) || EntityTypes.ZOMBIE_HORSE.equals(type) || EntityTypes.HORSE.equals(type) || EntityTypes.DONKEY.equals(type) || EntityTypes.MULE.equals(type))
/*  46 */       return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 1.3964844F : 1.4F; 
/*  47 */     if (EntityTypes.isTypeInstanceOf(type, EntityTypes.BOAT))
/*  48 */       return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 1.375F : 1.5F; 
/*  49 */     if (EntityTypes.HAPPY_GHAST.equals(type))
/*  50 */       return 4.0F; 
/*  51 */     if (EntityTypes.CHICKEN.equals(type) || EntityTypes.ENDERMITE.equals(type) || EntityTypes.SILVERFISH.equals(type) || EntityTypes.VEX.equals(type) || EntityTypes.TADPOLE.equals(type))
/*  52 */       return 0.4F; 
/*  53 */     if (EntityTypes.RABBIT.equals(type))
/*  54 */       return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.4F : 0.6F; 
/*  55 */     if (EntityTypes.CREAKING.equals(type) || EntityTypes.STRIDER.equals(type) || EntityTypes.COW.equals(type) || EntityTypes.SHEEP.equals(type) || EntityTypes.MOOSHROOM.equals(type) || EntityTypes.PIG.equals(type) || EntityTypes.LLAMA.equals(type) || EntityTypes.DOLPHIN.equals(type) || EntityTypes.WITHER.equals(type) || EntityTypes.TRADER_LLAMA.equals(type) || EntityTypes.WARDEN.equals(type) || EntityTypes.GOAT.equals(type))
/*  56 */       return 0.9F; 
/*  57 */     if (EntityTypes.PHANTOM.equals(type)) {
/*  58 */       if (packetEntity instanceof PacketEntitySizeable) { PacketEntitySizeable sizeable = (PacketEntitySizeable)packetEntity;
/*  59 */         return 0.9F + sizeable.size * 0.2F; }
/*     */ 
/*     */       
/*  62 */       return 1.5F;
/*  63 */     }  if (packetEntity instanceof PacketEntityGuardian) { PacketEntityGuardian packetEntityGuardian = (PacketEntityGuardian)packetEntity;
/*  64 */       return packetEntityGuardian.isElder ? 1.9975F : 0.85F; }
/*  65 */      if (EntityTypes.END_CRYSTAL.equals(type))
/*  66 */       return 2.0F; 
/*  67 */     if (EntityTypes.ENDER_DRAGON.equals(type))
/*  68 */       return 16.0F; 
/*  69 */     if (EntityTypes.FIREBALL.equals(type))
/*  70 */       return 1.0F; 
/*  71 */     if (EntityTypes.GHAST.equals(type))
/*  72 */       return 4.0F; 
/*  73 */     if (EntityTypes.GIANT.equals(type))
/*  74 */       return 3.6F; 
/*  75 */     if (EntityTypes.GUARDIAN.equals(type))
/*  76 */       return 0.85F; 
/*  77 */     if (EntityTypes.IRON_GOLEM.equals(type))
/*  78 */       return 1.4F; 
/*  79 */     if (EntityTypes.MAGMA_CUBE.equals(type)) {
/*  80 */       if (packetEntity instanceof PacketEntitySizeable) { PacketEntitySizeable sizeable = (PacketEntitySizeable)packetEntity;
/*  81 */         float size = sizeable.size;
/*  82 */         return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5) ? (
/*  83 */           0.52F * size) : (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? (
/*  84 */           2.04F * 0.255F * size) : (
/*  85 */           0.51000005F * size)); }
/*     */ 
/*     */       
/*  88 */       return 0.98F;
/*  89 */     }  if (EntityTypes.isTypeInstanceOf(type, EntityTypes.MINECART_ABSTRACT))
/*  90 */       return 0.98F; 
/*  91 */     if (EntityTypes.PLAYER.equals(type))
/*  92 */       return 0.6F; 
/*  93 */     if (EntityTypes.POLAR_BEAR.equals(type))
/*  94 */       return 1.4F; 
/*  95 */     if (EntityTypes.RAVAGER.equals(type))
/*  96 */       return 1.95F; 
/*  97 */     if (EntityTypes.SHULKER.equals(type))
/*  98 */       return 1.0F; 
/*  99 */     if (EntityTypes.SLIME.equals(type)) {
/* 100 */       if (packetEntity instanceof PacketEntitySizeable) { PacketEntitySizeable sizeable = (PacketEntitySizeable)packetEntity;
/* 101 */         float size = sizeable.size;
/* 102 */         return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5) ? (
/* 103 */           0.52F * size) : (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? (
/* 104 */           2.04F * 0.255F * size) : (0.51000005F * size)); }
/*     */ 
/*     */       
/* 107 */       return 0.3125F;
/* 108 */     }  if (EntityTypes.SMALL_FIREBALL.equals(type))
/* 109 */       return 0.3125F; 
/* 110 */     if (EntityTypes.SPIDER.equals(type))
/* 111 */       return 1.4F; 
/* 112 */     if (EntityTypes.SQUID.equals(type))
/* 113 */       return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.8F : 0.95F; 
/* 114 */     if (EntityTypes.TURTLE.equals(type))
/* 115 */       return 1.2F; 
/* 116 */     if (EntityTypes.ALLAY.equals(type))
/* 117 */       return 0.35F; 
/* 118 */     if (EntityTypes.SNIFFER.equals(type))
/* 119 */       return 1.9F; 
/* 120 */     if (EntityTypes.CAMEL.equals(type))
/* 121 */       return 1.7F; 
/* 122 */     if (EntityTypes.WIND_CHARGE.equals(type))
/* 123 */       return 0.3125F; 
/* 124 */     if (EntityTypes.ARMOR_STAND.equals(type))
/* 125 */       return 0.5F; 
/* 126 */     if (EntityTypes.FALLING_BLOCK.equals(type))
/* 127 */       return 0.98F; 
/* 128 */     if (EntityTypes.FIREWORK_ROCKET.equals(type)) {
/* 129 */       return 0.25F;
/*     */     }
/* 131 */     return 0.6F;
/*     */   }
/*     */   
/*     */   public static Vector3d getRidingOffsetFromVehicle(PacketEntity entity, GrimPlayer player) {
/* 135 */     SimpleCollisionBox box = entity.getPossibleCollisionBoxes();
/* 136 */     double x = (box.maxX + box.minX) / 2.0D;
/* 137 */     double y = box.minY;
/* 138 */     double z = (box.maxZ + box.minZ) / 2.0D;
/*     */     
/* 140 */     if (entity instanceof PacketEntityTrackXRot) { PacketEntityTrackXRot xRotEntity = (PacketEntityTrackXRot)entity;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 146 */       if (EntityTypes.isTypeInstanceOf(entity.type, EntityTypes.BOAT)) {
/* 147 */         float f = 0.0F;
/* 148 */         float f1 = (float)(getPassengerRidingOffset(player, entity) - 0.3499999940395355D);
/*     */         
/* 150 */         if (!entity.passengers.isEmpty()) {
/* 151 */           int i = entity.passengers.indexOf(player.compensatedEntities.self);
/*     */           
/* 153 */           if (i == 0) {
/* 154 */             f = 0.2F;
/* 155 */           } else if (i == 1) {
/* 156 */             f = -0.6F;
/*     */           } 
/*     */         } 
/*     */         
/* 160 */         Vector3d vec3 = new Vector3d(f, 0.0D, 0.0D);
/* 161 */         vec3 = yRot(GrimMath.radians(-xRotEntity.interpYaw) - 1.5707964F, vec3);
/* 162 */         return new Vector3d(x + vec3.x, y + f1, z + vec3.z);
/* 163 */       }  if (entity.type == EntityTypes.LLAMA) {
/* 164 */         float f = player.trigHandler.cos(GrimMath.radians(xRotEntity.interpYaw));
/* 165 */         float f1 = player.trigHandler.sin(GrimMath.radians(xRotEntity.interpYaw));
/* 166 */         return new Vector3d(x + (0.3F * f1), y + getPassengerRidingOffset(player, entity) - 0.3499999940395355D, z + (0.3F * f));
/* 167 */       }  if (entity.type == EntityTypes.CHICKEN) {
/* 168 */         float f = player.trigHandler.sin(GrimMath.radians(xRotEntity.interpYaw));
/* 169 */         float f1 = player.trigHandler.cos(GrimMath.radians(xRotEntity.interpYaw));
/* 170 */         y += (getHeight(player, entity) * 0.5F);
/* 171 */         return new Vector3d(x + (0.1F * f), y - 0.3499999940395355D, z - (0.1F * f1));
/*     */       }  }
/*     */ 
/*     */     
/* 175 */     return new Vector3d(x, y + getPassengerRidingOffset(player, entity) - 0.3499999940395355D, z);
/*     */   }
/*     */   
/*     */   private static Vector3d yRot(float yaw, Vector3d start) {
/* 179 */     double cos = (float)Math.cos(yaw);
/* 180 */     double sin = (float)Math.sin(yaw);
/* 181 */     return new Vector3d(start.x * cos + start.z * sin, start.y, start.z * cos - start.x * sin);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float getHeight(GrimPlayer player, PacketEntity packetEntity) {
/* 190 */     if (packetEntity.type == EntityTypes.TURTLE && packetEntity.isBaby) return 0.12F; 
/* 191 */     return getHeightMinusBaby(player, packetEntity) * (packetEntity.isBaby ? 0.5F : 1.0F);
/*     */   }
/*     */   
/*     */   public static double getMyRidingOffset(PacketEntity packetEntity) {
/* 195 */     EntityType type = packetEntity.type;
/* 196 */     if (EntityTypes.PIGLIN.equals(type) || EntityTypes.ZOMBIFIED_PIGLIN.equals(type) || EntityTypes.ZOMBIE.equals(type))
/* 197 */       return packetEntity.isBaby ? -0.05D : -0.45D; 
/* 198 */     if (EntityTypes.SKELETON.equals(type))
/* 199 */       return -0.6D; 
/* 200 */     if (EntityTypes.ENDERMITE.equals(type) || EntityTypes.SILVERFISH.equals(type))
/* 201 */       return 0.1D; 
/* 202 */     if (EntityTypes.EVOKER.equals(type) || EntityTypes.ILLUSIONER.equals(type) || EntityTypes.PILLAGER.equals(type) || EntityTypes.RAVAGER.equals(type) || EntityTypes.VINDICATOR.equals(type) || EntityTypes.WITCH.equals(type))
/* 203 */       return -0.45D; 
/* 204 */     if (EntityTypes.PLAYER.equals(type)) {
/* 205 */       return -0.35D;
/*     */     }
/*     */     
/* 208 */     if (EntityTypes.isTypeInstanceOf(type, EntityTypes.ABSTRACT_ANIMAL)) {
/* 209 */       return 0.14D;
/*     */     }
/*     */     
/* 212 */     return 0.0D;
/*     */   }
/*     */   
/*     */   public static double getPassengerRidingOffset(GrimPlayer player, PacketEntity packetEntity) {
/* 216 */     if (packetEntity instanceof ac.grim.grimac.utils.data.packetentity.PacketEntityHorse) {
/* 217 */       return getHeight(player, packetEntity) * 0.75D - 0.25D;
/*     */     }
/* 219 */     EntityType type = packetEntity.type;
/* 220 */     if (EntityTypes.isTypeInstanceOf(type, EntityTypes.MINECART_ABSTRACT))
/* 221 */       return 0.0D; 
/* 222 */     if (EntityTypes.isTypeInstanceOf(type, EntityTypes.BOAT))
/* 223 */       return -0.1D; 
/* 224 */     if (EntityTypes.HAPPY_GHAST.equals(type))
/* 225 */       return 0.5D; 
/* 226 */     if (EntityTypes.HOGLIN.equals(type) || EntityTypes.ZOGLIN.equals(type))
/* 227 */       return getHeight(player, packetEntity) - (packetEntity.isBaby ? 0.2D : 0.15D); 
/* 228 */     if (EntityTypes.LLAMA.equals(type))
/* 229 */       return getHeight(player, packetEntity) * 0.67D; 
/* 230 */     if (EntityTypes.PIGLIN.equals(type))
/* 231 */       return getHeight(player, packetEntity) * 0.92D; 
/* 232 */     if (EntityTypes.RAVAGER.equals(type))
/* 233 */       return 2.1D; 
/* 234 */     if (EntityTypes.SKELETON.equals(type))
/* 235 */       return getHeight(player, packetEntity) * 0.75D - 0.1875D; 
/* 236 */     if (EntityTypes.SPIDER.equals(type))
/* 237 */       return getHeight(player, packetEntity) * 0.5D; 
/* 238 */     if (EntityTypes.STRIDER.equals(type)) {
/* 239 */       return getHeight(player, packetEntity) - 0.19D;
/*     */     }
/* 241 */     return getHeight(player, packetEntity) * 0.75D;
/*     */   }
/*     */   
/*     */   private static float getHeightMinusBaby(GrimPlayer player, PacketEntity packetEntity) {
/* 245 */     EntityType type = packetEntity.type;
/* 246 */     if (EntityTypes.ARMADILLO.equals(type))
/* 247 */       return 0.65F; 
/* 248 */     if (EntityTypes.AXOLOTL.equals(type))
/* 249 */       return 0.42F; 
/* 250 */     if (EntityTypes.BEE.equals(type) || EntityTypes.DOLPHIN.equals(type) || EntityTypes.ALLAY.equals(type))
/* 251 */       return 0.6F; 
/* 252 */     if (EntityTypes.EVOKER_FANGS.equals(type) || EntityTypes.VEX.equals(type))
/* 253 */       return 0.8F; 
/* 254 */     if (EntityTypes.SQUID.equals(type))
/* 255 */       return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.8F : 0.95F; 
/* 256 */     if (EntityTypes.PARROT.equals(type) || EntityTypes.BAT.equals(type) || EntityTypes.PIG.equals(type) || EntityTypes.SPIDER.equals(type))
/* 257 */       return 0.9F; 
/* 258 */     if (EntityTypes.WITHER_SKULL.equals(type) || EntityTypes.SHULKER_BULLET.equals(type))
/* 259 */       return 0.3125F; 
/* 260 */     if (EntityTypes.BLAZE.equals(type))
/* 261 */       return 1.8F; 
/* 262 */     if (EntityTypes.isTypeInstanceOf(type, EntityTypes.BOAT))
/*     */     {
/*     */       
/* 265 */       return 0.5625F; } 
/* 266 */     if (EntityTypes.HAPPY_GHAST.equals(type))
/* 267 */       return 4.0F; 
/* 268 */     if (EntityTypes.CAT.equals(type))
/* 269 */       return 0.7F; 
/* 270 */     if (EntityTypes.CAVE_SPIDER.equals(type))
/* 271 */       return 0.5F; 
/* 272 */     if (EntityTypes.FROG.equals(type))
/* 273 */       return 0.55F; 
/* 274 */     if (EntityTypes.CHICKEN.equals(type))
/* 275 */       return 0.7F; 
/* 276 */     if (EntityTypes.HOGLIN.equals(type) || EntityTypes.ZOGLIN.equals(type))
/* 277 */       return 1.4F; 
/* 278 */     if (EntityTypes.COW.equals(type))
/* 279 */       return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 1.4F : 1.3F; 
/* 280 */     if (EntityTypes.STRIDER.equals(type))
/* 281 */       return 1.7F; 
/* 282 */     if (EntityTypes.CREEPER.equals(type))
/* 283 */       return 1.7F; 
/* 284 */     if (EntityTypes.DONKEY.equals(type))
/* 285 */       return 1.5F; 
/* 286 */     if (packetEntity instanceof PacketEntityGuardian) { PacketEntityGuardian packetEntityGuardian = (PacketEntityGuardian)packetEntity;
/* 287 */       return packetEntityGuardian.isElder ? 1.9975F : 0.85F; }
/* 288 */      if (EntityTypes.ENDERMAN.equals(type) || EntityTypes.WARDEN.equals(type))
/* 289 */       return 2.9F; 
/* 290 */     if (EntityTypes.ENDERMITE.equals(type) || EntityTypes.COD.equals(type))
/* 291 */       return 0.3F; 
/* 292 */     if (EntityTypes.END_CRYSTAL.equals(type))
/* 293 */       return 2.0F; 
/* 294 */     if (EntityTypes.ENDER_DRAGON.equals(type))
/* 295 */       return 8.0F; 
/* 296 */     if (EntityTypes.FIREBALL.equals(type))
/* 297 */       return 1.0F; 
/* 298 */     if (EntityTypes.FOX.equals(type))
/* 299 */       return 0.7F; 
/* 300 */     if (EntityTypes.GHAST.equals(type))
/* 301 */       return 4.0F; 
/* 302 */     if (EntityTypes.GIANT.equals(type))
/* 303 */       return 12.0F; 
/* 304 */     if (EntityTypes.GUARDIAN.equals(type))
/* 305 */       return 0.85F; 
/* 306 */     if (EntityTypes.HORSE.equals(type))
/* 307 */       return 1.6F; 
/* 308 */     if (EntityTypes.IRON_GOLEM.equals(type))
/* 309 */       return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 2.7F : 2.9F; 
/* 310 */     if (EntityTypes.CREAKING.equals(type))
/* 311 */       return 2.7F; 
/* 312 */     if (EntityTypes.LLAMA.equals(type) || EntityTypes.TRADER_LLAMA.equals(type))
/* 313 */       return 1.87F; 
/* 314 */     if (EntityTypes.TROPICAL_FISH.equals(type))
/* 315 */       return 0.4F; 
/* 316 */     if (EntityTypes.MAGMA_CUBE.equals(type)) {
/* 317 */       if (packetEntity instanceof PacketEntitySizeable) { PacketEntitySizeable sizeable = (PacketEntitySizeable)packetEntity;
/* 318 */         float size = sizeable.size;
/* 319 */         return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5) ? (
/* 320 */           0.52F * size) : (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? (
/* 321 */           2.04F * 0.255F * size) : (
/* 322 */           0.51000005F * size)); }
/*     */ 
/*     */       
/* 325 */       return 0.7F;
/* 326 */     }  if (EntityTypes.isTypeInstanceOf(type, EntityTypes.MINECART_ABSTRACT))
/* 327 */       return 0.7F; 
/* 328 */     if (EntityTypes.MULE.equals(type))
/* 329 */       return 1.6F; 
/* 330 */     if (EntityTypes.MOOSHROOM.equals(type))
/* 331 */       return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 1.4F : 1.3F; 
/* 332 */     if (EntityTypes.OCELOT.equals(type))
/* 333 */       return 0.7F; 
/* 334 */     if (EntityTypes.PANDA.equals(type))
/* 335 */       return 1.25F; 
/* 336 */     if (EntityTypes.PHANTOM.equals(type)) {
/* 337 */       if (packetEntity instanceof PacketEntitySizeable) { PacketEntitySizeable sizeable = (PacketEntitySizeable)packetEntity;
/* 338 */         return 0.5F + sizeable.size * 0.1F; }
/*     */ 
/*     */       
/* 341 */       return 1.8F;
/* 342 */     }  if (EntityTypes.PLAYER.equals(type))
/* 343 */       return 1.8F; 
/* 344 */     if (EntityTypes.POLAR_BEAR.equals(type))
/* 345 */       return 1.4F; 
/* 346 */     if (EntityTypes.PUFFERFISH.equals(type))
/* 347 */       return 0.7F; 
/* 348 */     if (EntityTypes.RABBIT.equals(type))
/* 349 */       return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.5F : 0.7F; 
/* 350 */     if (EntityTypes.RAVAGER.equals(type))
/* 351 */       return 2.2F; 
/* 352 */     if (EntityTypes.SALMON.equals(type))
/* 353 */       return 0.4F; 
/* 354 */     if (EntityTypes.SHEEP.equals(type) || EntityTypes.GOAT.equals(type))
/* 355 */       return 1.3F; 
/* 356 */     if (EntityTypes.SHULKER.equals(type))
/* 357 */       return 2.0F; 
/* 358 */     if (EntityTypes.SILVERFISH.equals(type))
/* 359 */       return 0.3F; 
/* 360 */     if (EntityTypes.SKELETON.equals(type))
/* 361 */       return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 1.99F : 1.95F; 
/* 362 */     if (EntityTypes.SKELETON_HORSE.equals(type))
/* 363 */       return 1.6F; 
/* 364 */     if (EntityTypes.SLIME.equals(type)) {
/* 365 */       if (packetEntity instanceof PacketEntitySizeable) { PacketEntitySizeable sizeable = (PacketEntitySizeable)packetEntity;
/* 366 */         float size = sizeable.size;
/* 367 */         return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5) ? (
/* 368 */           0.52F * size) : (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? (
/* 369 */           2.04F * 0.255F * size) : (
/* 370 */           0.51000005F * size)); }
/*     */ 
/*     */       
/* 373 */       return 0.3125F;
/* 374 */     }  if (EntityTypes.SMALL_FIREBALL.equals(type))
/* 375 */       return 0.3125F; 
/* 376 */     if (EntityTypes.SNOW_GOLEM.equals(type))
/* 377 */       return 1.9F; 
/* 378 */     if (EntityTypes.STRAY.equals(type))
/* 379 */       return 1.99F; 
/* 380 */     if (EntityTypes.TURTLE.equals(type))
/* 381 */       return 0.4F; 
/* 382 */     if (EntityTypes.WITHER.equals(type))
/* 383 */       return 3.5F; 
/* 384 */     if (EntityTypes.WITHER_SKELETON.equals(type))
/* 385 */       return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 2.4F : 2.535F; 
/* 386 */     if (EntityTypes.WOLF.equals(type))
/* 387 */       return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.85F : 0.8F; 
/* 388 */     if (EntityTypes.ZOMBIE_HORSE.equals(type))
/* 389 */       return 1.6F; 
/* 390 */     if (EntityTypes.TADPOLE.equals(type))
/* 391 */       return 0.3F; 
/* 392 */     if (EntityTypes.SNIFFER.equals(type))
/* 393 */       return 1.75F; 
/* 394 */     if (EntityTypes.CAMEL.equals(type))
/* 395 */       return 2.375F; 
/* 396 */     if (EntityTypes.BREEZE.equals(type))
/* 397 */       return 1.77F; 
/* 398 */     if (EntityTypes.BOGGED.equals(type))
/* 399 */       return 1.99F; 
/* 400 */     if (EntityTypes.WIND_CHARGE.equals(type))
/* 401 */       return 0.3125F; 
/* 402 */     if (EntityTypes.ARMOR_STAND.equals(type))
/* 403 */       return 1.975F; 
/* 404 */     if (EntityTypes.FALLING_BLOCK.equals(type))
/* 405 */       return 0.98F; 
/* 406 */     if (EntityTypes.VILLAGER.equals(type) && player.getClientVersion().isOlderThan(ClientVersion.V_1_9))
/* 407 */       return 1.8F; 
/* 408 */     if (EntityTypes.FIREWORK_ROCKET.equals(type)) {
/* 409 */       return 0.25F;
/*     */     }
/* 411 */     return 1.95F;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\BoundingBoxSize.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */