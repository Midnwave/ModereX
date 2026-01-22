/*    */ package ac.grim.grimac.utils.data.tags;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTags;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.function.Function;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class SyncedTags
/*    */ {
/* 22 */   public static final ResourceLocation CLIMBABLE = ResourceLocation.minecraft("climbable");
/* 23 */   public static final ResourceLocation MINEABLE_AXE = ResourceLocation.minecraft("mineable/axe");
/* 24 */   public static final ResourceLocation MINEABLE_PICKAXE = ResourceLocation.minecraft("mineable/pickaxe");
/* 25 */   public static final ResourceLocation MINEABLE_SHOVEL = ResourceLocation.minecraft("mineable/shovel");
/* 26 */   public static final ResourceLocation MINEABLE_HOE = ResourceLocation.minecraft("mineable/hoe");
/* 27 */   public static final ResourceLocation NEEDS_DIAMOND_TOOL = ResourceLocation.minecraft("needs_diamond_tool");
/* 28 */   public static final ResourceLocation NEEDS_IRON_TOOL = ResourceLocation.minecraft("needs_iron_tool");
/* 29 */   public static final ResourceLocation NEEDS_STONE_TOOL = ResourceLocation.minecraft("needs_stone_tool");
/* 30 */   public static final ResourceLocation SWORD_EFFICIENT = ResourceLocation.minecraft("sword_efficient");
/* 31 */   private static final ServerVersion VERSION = PacketEvents.getAPI().getServerManager().getVersion();
/* 32 */   private static final ResourceLocation BLOCK = VERSION.isNewerThanOrEquals(ServerVersion.V_1_21) ? ResourceLocation.minecraft("block") : ResourceLocation.minecraft("blocks");
/*    */   private final GrimPlayer player;
/*    */   private final Map<ResourceLocation, Map<ResourceLocation, SyncedTag<?>>> synced;
/*    */   
/*    */   public SyncedTags(GrimPlayer player) {
/* 37 */     this.player = player;
/* 38 */     this.synced = new HashMap<>();
/* 39 */     ClientVersion version = player.getClientVersion();
/* 40 */     trackTags(BLOCK, id -> StateTypes.getById(VERSION.toClientVersion(), id.intValue()), (SyncedTag.Builder<?>[])new SyncedTag.Builder[] {
/* 41 */           SyncedTag.builder(CLIMBABLE).defaults(BlockTags.CLIMBABLE.getStates()).supported(version.isNewerThanOrEquals(ClientVersion.V_1_16)), 
/* 42 */           SyncedTag.builder(MINEABLE_AXE).defaults(BlockTags.MINEABLE_AXE.getStates()).supported(version.isNewerThanOrEquals(ClientVersion.V_1_17)), 
/* 43 */           SyncedTag.builder(MINEABLE_PICKAXE).defaults(BlockTags.MINEABLE_PICKAXE.getStates()).supported(version.isNewerThanOrEquals(ClientVersion.V_1_17)), 
/* 44 */           SyncedTag.builder(MINEABLE_SHOVEL).defaults(BlockTags.MINEABLE_SHOVEL.getStates()).supported(version.isNewerThanOrEquals(ClientVersion.V_1_17)), 
/* 45 */           SyncedTag.builder(MINEABLE_HOE).defaults(BlockTags.MINEABLE_HOE.getStates()).supported(version.isNewerThanOrEquals(ClientVersion.V_1_17)), 
/* 46 */           SyncedTag.builder(NEEDS_DIAMOND_TOOL).defaults(BlockTags.NEEDS_DIAMOND_TOOL.getStates()).supported(version.isNewerThanOrEquals(ClientVersion.V_1_17)), 
/* 47 */           SyncedTag.builder(NEEDS_IRON_TOOL).defaults(BlockTags.NEEDS_IRON_TOOL.getStates()).supported(version.isNewerThanOrEquals(ClientVersion.V_1_17)), 
/* 48 */           SyncedTag.builder(NEEDS_STONE_TOOL).defaults(BlockTags.NEEDS_STONE_TOOL.getStates()).supported(version.isNewerThanOrEquals(ClientVersion.V_1_17)), 
/* 49 */           SyncedTag.builder(SWORD_EFFICIENT).defaults(BlockTags.SWORD_EFFICIENT.getStates()).supported(version.isNewerThanOrEquals(ClientVersion.V_1_20))
/*    */         });
/*    */   }
/*    */   
/*    */   @SafeVarargs
/*    */   private <T> void trackTags(ResourceLocation location, Function<Integer, T> remapper, SyncedTag.Builder<T>... syncedTags) {
/* 55 */     Map<ResourceLocation, SyncedTag<?>> tags = new HashMap<>(syncedTags.length);
/* 56 */     for (SyncedTag.Builder<T> syncedTag : syncedTags) {
/* 57 */       syncedTag.remapper(remapper);
/* 58 */       SyncedTag<T> built = syncedTag.build();
/* 59 */       tags.put(built.location(), built);
/*    */     } 
/* 61 */     this.synced.put(location, tags);
/*    */   }
/*    */   
/*    */   public SyncedTag<StateType> block(ResourceLocation tag) {
/* 65 */     Map<ResourceLocation, SyncedTag<?>> blockTags = this.synced.get(BLOCK);
/* 66 */     return (SyncedTag<StateType>)blockTags.get(tag);
/*    */   }
/*    */   
/*    */   public void handleTagSync(WrapperPlayServerTags tags) {
/* 70 */     if (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_13))
/* 71 */       return;  tags.getTagMap().forEach((location, tagList) -> {
/*    */           if (!this.synced.containsKey(location))
/*    */             return; 
/*    */           Map<ResourceLocation, SyncedTag<?>> syncedTags = this.synced.get(location);
/*    */           tagList.forEach(());
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\tags\SyncedTags.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */