/*    */ package ac.grim.grimac.predictionengine.movementtick;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
/*    */ import ac.grim.grimac.utils.data.attribute.ValuedAttribute;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntityStrider;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ import ac.grim.grimac.utils.nmsutil.BlockProperties;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public class MovementTickerStrider
/*    */   extends MovementTickerRideable
/*    */ {
/* 20 */   private static final WrapperPlayServerUpdateAttributes.PropertyModifier SUFFOCATING_MODIFIER = new WrapperPlayServerUpdateAttributes.PropertyModifier(
/* 21 */       ResourceLocation.minecraft("suffocating"), -0.3400000035762787D, WrapperPlayServerUpdateAttributes.PropertyModifier.Operation.MULTIPLY_BASE);
/*    */   
/*    */   public MovementTickerStrider(GrimPlayer player) {
/* 24 */     super(player);
/* 25 */     this.movementInput = new Vector3dm(0, 0, 1);
/*    */   }
/*    */   
/*    */   public static void floatStrider(GrimPlayer player) {
/* 29 */     if (player.wasTouchingLava) {
/* 30 */       if (isAbove(player) && player.compensatedWorld.getLavaFluidLevelAt((int)Math.floor(player.x), (int)Math.floor(player.y + 1.0D), (int)Math.floor(player.z)) == 0.0D) {
/* 31 */         player.onGround = true;
/*    */       } else {
/* 33 */         player.clientVelocity.multiply(0.5D).add(new Vector3dm(0.0D, 0.05D, 0.0D));
/*    */       } 
/*    */     }
/*    */   }
/*    */   
/*    */   public static boolean isAbove(GrimPlayer player) {
/* 39 */     return (player.y > Math.floor(player.y) + 0.5D - 9.999999747378752E-6D);
/*    */   }
/*    */ 
/*    */   
/*    */   public void livingEntityAIStep() {
/* 44 */     super.livingEntityAIStep();
/*    */     
/* 46 */     StateType posMaterial = this.player.compensatedWorld.getBlockType(this.player.x, this.player.y, this.player.z);
/* 47 */     StateType belowMaterial = BlockProperties.getOnPos(this.player, this.player.mainSupportingBlockData, new Vector3d(this.player.x, this.player.y, this.player.z));
/*    */     
/* 49 */     PacketEntityStrider strider = (PacketEntityStrider)this.player.compensatedEntities.self.getRiding();
/* 50 */     strider
/* 51 */       .isShaking = (!BlockTags.STRIDER_WARM_BLOCKS.contains(posMaterial) && !BlockTags.STRIDER_WARM_BLOCKS.contains(belowMaterial) && !this.player.wasTouchingLava);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public float getSteeringSpeed() {
/* 57 */     PacketEntityStrider strider = (PacketEntityStrider)this.player.compensatedEntities.self.getRiding();
/*    */     
/* 59 */     boolean newSpeed = this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20);
/* 60 */     float coldSpeed = newSpeed ? 0.35F : 0.23F;
/*    */ 
/*    */ 
/*    */     
/* 64 */     ValuedAttribute movementSpeedAttr = strider.getAttribute(Attributes.MOVEMENT_SPEED).orElseThrow();
/* 65 */     float updatedMovementSpeed = (float)movementSpeedAttr.get();
/* 66 */     if (newSpeed) {
/* 67 */       WrapperPlayServerUpdateAttributes.Property lastProperty = movementSpeedAttr.property().orElse(null);
/* 68 */       if (lastProperty != null && (!strider.isShaking || lastProperty.getModifiers().stream().noneMatch(mod -> mod.getName().getKey().equals("suffocating")))) {
/* 69 */         WrapperPlayServerUpdateAttributes.Property newProperty = new WrapperPlayServerUpdateAttributes.Property(lastProperty.getAttribute(), lastProperty.getValue(), new ArrayList(lastProperty.getModifiers()));
/* 70 */         if (!strider.isShaking) {
/* 71 */           newProperty.getModifiers().removeIf(modifier -> modifier.getName().getKey().equals("suffocating"));
/*    */         } else {
/* 73 */           newProperty.getModifiers().add(SUFFOCATING_MODIFIER);
/*    */         } 
/* 75 */         movementSpeedAttr.with(newProperty);
/* 76 */         updatedMovementSpeed = (float)movementSpeedAttr.get();
/* 77 */         movementSpeedAttr.with(lastProperty);
/*    */       } 
/*    */     } 
/*    */     
/* 81 */     return updatedMovementSpeed * (strider.isShaking ? coldSpeed : 0.55F);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean canStandOnLava() {
/* 86 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\predictionengine\movementtick\MovementTickerStrider.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */