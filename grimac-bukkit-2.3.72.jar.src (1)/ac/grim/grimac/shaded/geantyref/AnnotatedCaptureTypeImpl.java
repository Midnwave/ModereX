/*     */ package ac.grim.grimac.shaded.geantyref;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.AnnotatedType;
/*     */ import java.lang.reflect.AnnotatedTypeVariable;
/*     */ import java.lang.reflect.AnnotatedWildcardType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.stream.Stream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class AnnotatedCaptureTypeImpl
/*     */   extends AnnotatedTypeImpl
/*     */   implements AnnotatedCaptureType
/*     */ {
/*     */   private final AnnotatedWildcardType wildcard;
/*     */   private final AnnotatedTypeVariable variable;
/*     */   private final AnnotatedType[] lowerBounds;
/*     */   private AnnotatedType[] upperBounds;
/*     */   private final CaptureType type;
/*     */   private final Annotation[] declaredAnnotations;
/*     */   
/*     */   AnnotatedCaptureTypeImpl(AnnotatedWildcardType wildcard, AnnotatedTypeVariable variable) {
/*  32 */     this(new CaptureTypeImpl((WildcardType)wildcard.getType(), (TypeVariable)variable.getType()), wildcard, variable);
/*     */   }
/*     */   
/*     */   AnnotatedCaptureTypeImpl(CaptureType type, AnnotatedWildcardType wildcard, AnnotatedTypeVariable variable) {
/*  36 */     this(type, wildcard, variable, (AnnotatedType[])null, (Annotation[])null);
/*     */   }
/*     */   
/*     */   AnnotatedCaptureTypeImpl(CaptureType type, AnnotatedWildcardType wildcard, AnnotatedTypeVariable variable, AnnotatedType[] upperBounds, Annotation[] annotations) {
/*  40 */     this(type, wildcard, variable, wildcard.getAnnotatedLowerBounds(), upperBounds, annotations);
/*     */   }
/*     */ 
/*     */   
/*     */   AnnotatedCaptureTypeImpl(CaptureType type, AnnotatedWildcardType wildcard, AnnotatedTypeVariable variable, AnnotatedType[] lowerBounds, AnnotatedType[] upperBounds, Annotation[] annotations) {
/*  45 */     super(type, (annotations != null) ? annotations : (Annotation[])Stream.concat(Arrays.stream((Object[])wildcard.getAnnotations()), Arrays.stream((Object[])variable.getAnnotations())).toArray(x$0 -> new Annotation[x$0]));
/*  46 */     this.type = type;
/*  47 */     this.wildcard = wildcard;
/*  48 */     this.variable = variable;
/*  49 */     this.lowerBounds = lowerBounds;
/*  50 */     this.upperBounds = upperBounds;
/*  51 */     this
/*     */ 
/*     */       
/*  54 */       .declaredAnnotations = (Annotation[])Stream.concat(Arrays.stream((Object[])wildcard.getDeclaredAnnotations()), Arrays.stream((Object[])variable.getDeclaredAnnotations())).toArray(x$0 -> new Annotation[x$0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void init(VarMap varMap) {
/*  62 */     ArrayList<AnnotatedType> upperBoundsList = new ArrayList<>(Arrays.asList(varMap.map(this.variable.getAnnotatedBounds())));
/*     */     
/*  64 */     List<AnnotatedType> wildcardUpperBounds = Arrays.asList(this.wildcard.getAnnotatedUpperBounds());
/*  65 */     if (!wildcardUpperBounds.isEmpty() && ((AnnotatedType)wildcardUpperBounds.get(0)).getType() == Object.class) {
/*     */       
/*  67 */       upperBoundsList.addAll(wildcardUpperBounds.subList(1, wildcardUpperBounds.size()));
/*     */     } else {
/*  69 */       upperBoundsList.addAll(wildcardUpperBounds);
/*     */     } 
/*  71 */     this.upperBounds = new AnnotatedType[upperBoundsList.size()];
/*  72 */     upperBoundsList.toArray(this.upperBounds);
/*     */     
/*  74 */     ((CaptureTypeImpl)this.type).init(varMap);
/*     */   }
/*     */ 
/*     */   
/*     */   AnnotatedCaptureTypeImpl setAnnotations(Annotation[] annotations) {
/*  79 */     this.annotations = toMap(annotations);
/*  80 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Annotation[] getDeclaredAnnotations() {
/*  85 */     return this.declaredAnnotations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotatedType[] getAnnotatedUpperBounds() {
/*  96 */     assert this.upperBounds != null;
/*  97 */     return (AnnotatedType[])this.upperBounds.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAnnotatedUpperBounds(AnnotatedType[] upperBounds) {
/* 102 */     this.upperBounds = upperBounds;
/* 103 */     this.type.setUpperBounds((Type[])Arrays.<AnnotatedType>stream(upperBounds).map(AnnotatedType::getType).toArray(x$0 -> new Type[x$0]));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotatedType[] getAnnotatedLowerBounds() {
/* 113 */     return (AnnotatedType[])this.lowerBounds.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   public AnnotatedTypeVariable getAnnotatedTypeVariable() {
/* 118 */     return this.variable;
/*     */   }
/*     */ 
/*     */   
/*     */   public AnnotatedWildcardType getAnnotatedWildcardType() {
/* 123 */     return this.wildcard;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\AnnotatedCaptureTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */