/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import java.util.Objects;
/*     */ import javax.annotation.CheckReturnValue;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.ParametersAreNonnullByDefault;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ import org.apiguardian.api.API;
/*     */ import org.immutables.value.Generated;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ParametersAreNonnullByDefault
/*     */ @CheckReturnValue
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "ParserDescriptor", generator = "Immutables")
/*     */ @Immutable
/*     */ final class ParserDescriptorImpl<C, T>
/*     */   implements ParserDescriptor<C, T>
/*     */ {
/*     */   private final ArgumentParser<C, T> parser;
/*     */   private final TypeToken<T> valueType;
/*     */   
/*     */   private ParserDescriptorImpl(ArgumentParser<C, T> parser, TypeToken<T> valueType) {
/*  56 */     this.parser = Objects.<ArgumentParser<C, T>>requireNonNull(parser, "parser");
/*  57 */     this.valueType = Objects.<TypeToken<T>>requireNonNull(valueType, "valueType");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ParserDescriptorImpl(ParserDescriptorImpl<C, T> original, ArgumentParser<C, T> parser, TypeToken<T> valueType) {
/*  64 */     this.parser = parser;
/*  65 */     this.valueType = valueType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParser<C, T> parser() {
/*  73 */     return this.parser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeToken<T> valueType() {
/*  81 */     return this.valueType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ParserDescriptorImpl<C, T> withParser(ArgumentParser<C, T> value) {
/*  91 */     if (this.parser == value) return this; 
/*  92 */     ArgumentParser<C, T> newValue = Objects.<ArgumentParser<C, T>>requireNonNull(value, "parser");
/*  93 */     return new ParserDescriptorImpl(this, newValue, this.valueType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ParserDescriptorImpl<C, T> withValueType(TypeToken<T> value) {
/* 103 */     if (this.valueType == value) return this; 
/* 104 */     TypeToken<T> newValue = Objects.<TypeToken<T>>requireNonNull(value, "valueType");
/* 105 */     return new ParserDescriptorImpl(this, this.parser, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 114 */     if (this == another) return true; 
/* 115 */     return (another instanceof ParserDescriptorImpl && 
/* 116 */       equalTo(0, (ParserDescriptorImpl<?, ?>)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, ParserDescriptorImpl<?, ?> another) {
/* 120 */     return (this.parser.equals(another.parser) && this.valueType
/* 121 */       .equals(another.valueType));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 130 */     int h = 5381;
/* 131 */     h += (h << 5) + this.parser.hashCode();
/* 132 */     h += (h << 5) + this.valueType.hashCode();
/* 133 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 142 */     return "ParserDescriptor{parser=" + this.parser + ", valueType=" + this.valueType + "}";
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
/*     */ 
/*     */   
/*     */   public static <C, T> ParserDescriptorImpl<C, T> of(ArgumentParser<C, T> parser, TypeToken<T> valueType) {
/* 157 */     return new ParserDescriptorImpl<>(parser, valueType);
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
/*     */   public static <C, T> ParserDescriptorImpl<C, T> copyOf(ParserDescriptor<C, T> instance) {
/* 170 */     if (instance instanceof ParserDescriptorImpl) {
/* 171 */       return (ParserDescriptorImpl<C, T>)instance;
/*     */     }
/* 173 */     return of(instance.parser(), instance.valueType());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\ParserDescriptorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */