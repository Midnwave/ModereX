/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.option.value;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
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
/*     */ abstract class ValueTypeImpl<T>
/*     */   implements ValueType<T>
/*     */ {
/*     */   private final Class<T> type;
/*     */   
/*     */   ValueTypeImpl(Class<T> type) {
/*  35 */     this.type = type;
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<T> type() {
/*  40 */     return this.type;
/*     */   }
/*     */   
/*     */   static IllegalArgumentException doNotKnowHowToTurn(String input, Class<?> expected, String message) {
/*  44 */     throw new IllegalArgumentException("Do not know how to turn value '" + input + "' into a " + expected.getName() + ((message == null) ? "" : (": " + message)));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static final class Types
/*     */   {
/*  51 */     static ValueType<String> STRING = new ValueTypeImpl<String>(String.class)
/*     */       {
/*     */         public String parse(String plainValue) throws IllegalArgumentException {
/*  54 */           return plainValue;
/*     */         }
/*     */       };
/*  57 */     static ValueType<Boolean> BOOLEAN = new ValueTypeImpl<Boolean>(Boolean.class)
/*     */       {
/*     */         public Boolean parse(String plainValue) throws IllegalArgumentException {
/*  60 */           if (plainValue.equalsIgnoreCase("true"))
/*  61 */             return Boolean.TRUE; 
/*  62 */           if (plainValue.equalsIgnoreCase("false")) {
/*  63 */             return Boolean.FALSE;
/*     */           }
/*  65 */           throw doNotKnowHowToTurn(plainValue, Boolean.class, null);
/*     */         }
/*     */       };
/*     */     
/*  69 */     static ValueType<Integer> INT = new ValueTypeImpl<Integer>(Integer.class)
/*     */       {
/*     */         public Integer parse(String plainValue) throws IllegalArgumentException {
/*     */           try {
/*  73 */             return Integer.decode(plainValue);
/*  74 */           } catch (NumberFormatException ex) {
/*  75 */             throw doNotKnowHowToTurn(plainValue, Integer.class, ex.getMessage());
/*     */           } 
/*     */         }
/*     */       };
/*  79 */     static ValueType<Double> DOUBLE = new ValueTypeImpl<Double>(Double.class)
/*     */       {
/*     */         public Double parse(String plainValue) throws IllegalArgumentException {
/*     */           try {
/*  83 */             return Double.valueOf(Double.parseDouble(plainValue));
/*  84 */           } catch (NumberFormatException ex) {
/*  85 */             throw doNotKnowHowToTurn(plainValue, Double.class, ex.getMessage());
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   static final class EnumType<E extends Enum<E>> extends ValueTypeImpl<E> {
/*  92 */     private final Map<String, E> values = new HashMap<>();
/*     */     
/*     */     EnumType(Class<E> type) {
/*  95 */       super(type);
/*  96 */       for (Enum enum_ : (Enum[])type.getEnumConstants()) {
/*  97 */         this.values.put(enum_.name().toLowerCase(Locale.ROOT), (E)enum_);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public E parse(String plainValue) throws IllegalArgumentException {
/* 103 */       Enum enum_ = (Enum)this.values.get(plainValue.toLowerCase(Locale.ROOT));
/* 104 */       if (enum_ == null) {
/* 105 */         throw doNotKnowHowToTurn(plainValue, type(), null);
/*     */       }
/* 107 */       return (E)enum_;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\option\value\ValueTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */