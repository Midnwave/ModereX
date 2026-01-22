/*     */ package ac.grim.grimac.utils.data.attribute;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attribute;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
/*     */ import ac.grim.grimac.utils.latency.CompensatedEntities;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import java.util.List;
/*     */ import java.util.Optional;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.function.Function;
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ValuedAttribute
/*     */ {
/*  18 */   private static final Function<Double, Double> DEFAULT_GET_REWRITE = Function.identity();
/*     */   
/*     */   private final Attribute attribute;
/*     */   
/*     */   private final double min;
/*     */   
/*     */   private final double max;
/*     */   
/*     */   private final double defaultValue;
/*     */   
/*     */   private WrapperPlayServerUpdateAttributes.Property lastProperty;
/*     */   private double value;
/*     */   private BiFunction<Double, Double, Double> setRewriter;
/*     */   private Function<Double, Double> getRewriter;
/*     */   
/*     */   private ValuedAttribute(Attribute attribute, double defaultValue, double min, double max) {
/*  34 */     if (defaultValue < min || defaultValue > max) {
/*  35 */       throw new IllegalArgumentException("Default value must be between min and max!");
/*     */     }
/*     */     
/*  38 */     this.attribute = attribute;
/*  39 */     this.defaultValue = defaultValue;
/*  40 */     this.value = defaultValue;
/*  41 */     this.min = min;
/*  42 */     this.max = max;
/*  43 */     this.getRewriter = DEFAULT_GET_REWRITE;
/*     */   }
/*     */ 
/*     */   
/*     */   public static ValuedAttribute ranged(Attribute attribute, double defaultValue, double min, double max) {
/*  48 */     return new ValuedAttribute(attribute, defaultValue, min, max);
/*     */   }
/*     */   
/*     */   public ValuedAttribute withSetRewriter(BiFunction<Double, Double, Double> rewriteFunction) {
/*  52 */     this.setRewriter = rewriteFunction;
/*  53 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ValuedAttribute requiredVersion(GrimPlayer player, ClientVersion requiredVersion) {
/*  63 */     withSetRewriter((oldValue, newValue) -> player.getClientVersion().isOlderThan(requiredVersion) ? oldValue : newValue);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  69 */     return this;
/*     */   }
/*     */   
/*     */   public ValuedAttribute withGetRewriter(Function<Double, Double> getRewriteFunction) {
/*  73 */     this.getRewriter = getRewriteFunction;
/*  74 */     return this;
/*     */   }
/*     */   
/*     */   public Attribute attribute() {
/*  78 */     return this.attribute;
/*     */   }
/*     */   
/*     */   public void reset() {
/*  82 */     this.value = this.defaultValue;
/*  83 */     this.lastProperty = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public double get() {
/*  88 */     return ((Double)this.getRewriter.apply(Double.valueOf(this.value))).doubleValue();
/*     */   }
/*     */   
/*     */   public void override(double value) {
/*  92 */     this.value = value;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public Optional<WrapperPlayServerUpdateAttributes.Property> property() {
/*  97 */     return Optional.ofNullable(this.lastProperty);
/*     */   }
/*     */   
/*     */   public void recalculate() {
/* 101 */     with(this.lastProperty);
/*     */   }
/*     */   
/*     */   public double with(WrapperPlayServerUpdateAttributes.Property property) {
/* 105 */     double baseValue = property.getValue();
/* 106 */     double additionSum = 0.0D;
/* 107 */     double multiplyBaseSum = 0.0D;
/* 108 */     double multiplyTotalProduct = 1.0D;
/*     */ 
/*     */     
/* 111 */     List<WrapperPlayServerUpdateAttributes.PropertyModifier> modifiers = property.getModifiers();
/* 112 */     modifiers.removeIf(modifier -> (modifier.getUUID().equals(CompensatedEntities.SPRINTING_MODIFIER_UUID) || modifier.getName().getKey().equals("sprinting")));
/*     */ 
/*     */     
/* 115 */     for (WrapperPlayServerUpdateAttributes.PropertyModifier modifier : modifiers) {
/* 116 */       switch (modifier.getOperation()) {
/*     */         case ADDITION:
/* 118 */           additionSum += modifier.getAmount();
/*     */         
/*     */         case MULTIPLY_BASE:
/* 121 */           multiplyBaseSum += modifier.getAmount();
/*     */         
/*     */         case MULTIPLY_TOTAL:
/* 124 */           multiplyTotalProduct *= 1.0D + modifier.getAmount();
/*     */       } 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 130 */     double newValue = GrimMath.clamp((baseValue + additionSum) * (1.0D + multiplyBaseSum) * multiplyTotalProduct, this.min, this.max);
/*     */     
/* 132 */     if (this.setRewriter != null) {
/* 133 */       newValue = ((Double)this.setRewriter.apply(Double.valueOf(this.value), Double.valueOf(newValue))).doubleValue();
/*     */     }
/*     */     
/* 136 */     if (newValue < this.min || newValue > this.max) {
/* 137 */       throw new IllegalArgumentException("New value must be between min and max!");
/*     */     }
/*     */     
/* 140 */     this.lastProperty = property;
/* 141 */     return this.value = newValue;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\attribute\ValuedAttribute.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */