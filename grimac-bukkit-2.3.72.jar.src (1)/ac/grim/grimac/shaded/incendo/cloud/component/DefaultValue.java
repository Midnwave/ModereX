/*     */ package ac.grim.grimac.shaded.incendo.cloud.component;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import java.util.Objects;
/*     */ import org.apiguardian.api.API;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.STABLE)
/*     */ @FunctionalInterface
/*     */ public interface DefaultValue<C, T>
/*     */ {
/*     */   static <C, T> DefaultValue<C, T> constant(T value) {
/*  53 */     return new ConstantDefaultValue<>(Objects.requireNonNull(value, "value"));
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
/*     */   static <C, T> DefaultValue<C, T> dynamic(DefaultValueProvider<C, T> expression) {
/*  66 */     Objects.requireNonNull(expression, "expression");
/*  67 */     return failableDynamic(ctx -> ArgumentParseResult.success(expression.evaluateDefault(ctx)));
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
/*     */   static <C, T> DefaultValue<C, T> failableDynamic(DefaultValue<C, T> expression) {
/*  80 */     return new DynamicDefaultValue<>(Objects.<DefaultValue>requireNonNull(expression, "expression"));
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
/*     */   static <C, T> DefaultValue<C, T> parsed(String value) {
/*  92 */     return new ParsedDefaultValue<>(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ArgumentParseResult<T> evaluateDefault(CommandContext<C> paramCommandContext);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class ConstantDefaultValue<C, T>
/*     */     implements DefaultValue<C, T>
/*     */   {
/*     */     private final ArgumentParseResult<T> value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private ConstantDefaultValue(T value) {
/* 121 */       this.value = ArgumentParseResult.success(value);
/*     */     }
/*     */ 
/*     */     
/*     */     public ArgumentParseResult<T> evaluateDefault(CommandContext<C> context) {
/* 126 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object object) {
/* 131 */       if (this == object) {
/* 132 */         return true;
/*     */       }
/* 134 */       if (object == null || getClass() != object.getClass()) {
/* 135 */         return false;
/*     */       }
/* 137 */       ConstantDefaultValue<?, ?> that = (ConstantDefaultValue<?, ?>)object;
/* 138 */       return Objects.equals(this.value.parsedValue().get(), that.value.parsedValue().get());
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 143 */       return Objects.hash(new Object[] { this.value });
/*     */     }
/*     */   }
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   @FunctionalInterface
/*     */   public static interface DefaultValueProvider<C, T> { T evaluateDefault(CommandContext<C> param1CommandContext); }
/*     */   
/*     */   public static final class DynamicDefaultValue<C, T> implements DefaultValue<C, T> { private DynamicDefaultValue(DefaultValue<C, T> defaultValue) {
/* 152 */       this.defaultValue = defaultValue;
/*     */     }
/*     */     private final DefaultValue<C, T> defaultValue;
/*     */     
/*     */     public ArgumentParseResult<T> evaluateDefault(CommandContext<C> context) {
/* 157 */       return this.defaultValue.evaluateDefault(context);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object object) {
/* 162 */       if (this == object) {
/* 163 */         return true;
/*     */       }
/* 165 */       if (object == null || getClass() != object.getClass()) {
/* 166 */         return false;
/*     */       }
/* 168 */       DynamicDefaultValue<?, ?> that = (DynamicDefaultValue<?, ?>)object;
/* 169 */       return Objects.equals(this.defaultValue, that.defaultValue);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 174 */       return Objects.hash(new Object[] { this.defaultValue });
/*     */     } }
/*     */ 
/*     */   
/*     */   public static final class ParsedDefaultValue<C, T>
/*     */     implements DefaultValue<C, T> {
/*     */     private final String value;
/*     */     
/*     */     private ParsedDefaultValue(String string) {
/* 183 */       this.value = string;
/*     */     }
/*     */ 
/*     */     
/*     */     public ArgumentParseResult<T> evaluateDefault(CommandContext<C> context) {
/* 188 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String value() {
/* 197 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object object) {
/* 202 */       if (this == object) {
/* 203 */         return true;
/*     */       }
/* 205 */       if (object == null || getClass() != object.getClass()) {
/* 206 */         return false;
/*     */       }
/* 208 */       ParsedDefaultValue<?, ?> that = (ParsedDefaultValue<?, ?>)object;
/* 209 */       return Objects.equals(this.value, that.value);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 214 */       return Objects.hash(new Object[] { this.value });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\component\DefaultValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */