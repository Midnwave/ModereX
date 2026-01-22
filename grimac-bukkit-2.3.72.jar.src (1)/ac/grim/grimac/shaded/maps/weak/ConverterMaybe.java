/*     */ package ac.grim.grimac.shaded.maps.weak;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.ZoneId;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Function;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConverterMaybe
/*     */ {
/*     */   protected final Object o;
/*     */   
/*     */   ConverterMaybe(Object o) {
/*  44 */     this.o = o;
/*     */   }
/*     */   private <T> Optional<T> optional(Function<Converter, T> fn) {
/*     */     
/*  48 */     try { return Optional.ofNullable(fn.apply(Converter.convert(this.o))); }
/*  49 */     catch (RuntimeException ex) { return Optional.empty(); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<String> intoString() {
/*  57 */     return optional(Converter::intoString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<Integer> intoInteger() {
/*  65 */     return optional(Converter::intoInteger);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<Long> intoLong() {
/*  73 */     return optional(Converter::intoLong);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<Double> intoDouble() {
/*  81 */     return optional(Converter::intoDouble);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<BigDecimal> intoDecimal() {
/*  89 */     return optional(Converter::intoDecimal);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<Map> intoMap() {
/*  97 */     return optional(Converter::intoMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<List> intoList() {
/* 105 */     return optional(Converter::intoList);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<LocalDateTime> intoLocalDateTime() {
/* 113 */     return optional(Converter::intoLocalDateTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<ZonedDateTime> intoZonedDateTime() {
/* 121 */     return optional(Converter::intoZonedDateTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<ZonedDateTime> intoZonedDateTimeOrUse(ZoneId fallback) {
/* 129 */     return optional(c -> c.intoZonedDateTimeOrUse(fallback));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\ConverterMaybe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */