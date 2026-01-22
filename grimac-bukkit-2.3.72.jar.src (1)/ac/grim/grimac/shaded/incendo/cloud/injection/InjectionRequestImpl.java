/*     */ package ac.grim.grimac.shaded.incendo.cloud.injection;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.util.annotation.AnnotationAccessor;
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
/*     */ @ParametersAreNonnullByDefault
/*     */ @CheckReturnValue
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "InjectionRequest", generator = "Immutables")
/*     */ @Immutable
/*     */ final class InjectionRequestImpl<C>
/*     */   implements InjectionRequest<C>
/*     */ {
/*     */   private final CommandContext<C> commandContext;
/*     */   private final TypeToken<?> injectedType;
/*     */   private final transient Class<?> injectedClass;
/*     */   private final AnnotationAccessor annotationAccessor;
/*     */   
/*     */   private InjectionRequestImpl(CommandContext<C> commandContext, TypeToken<?> injectedType, AnnotationAccessor annotationAccessor) {
/*  59 */     this.commandContext = Objects.<CommandContext<C>>requireNonNull(commandContext, "commandContext");
/*  60 */     this.injectedType = Objects.<TypeToken>requireNonNull(injectedType, "injectedType");
/*  61 */     this.annotationAccessor = Objects.<AnnotationAccessor>requireNonNull(annotationAccessor, "annotationAccessor");
/*  62 */     this.injectedClass = Objects.<Class<?>>requireNonNull(super.injectedClass(), "injectedClass");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private InjectionRequestImpl(InjectionRequestImpl<C> original, CommandContext<C> commandContext, TypeToken<?> injectedType, AnnotationAccessor annotationAccessor) {
/*  70 */     this.commandContext = commandContext;
/*  71 */     this.injectedType = injectedType;
/*  72 */     this.annotationAccessor = annotationAccessor;
/*  73 */     this.injectedClass = Objects.<Class<?>>requireNonNull(super.injectedClass(), "injectedClass");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandContext<C> commandContext() {
/*  81 */     return this.commandContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeToken<?> injectedType() {
/*  89 */     return this.injectedType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> injectedClass() {
/*  97 */     return this.injectedClass;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnnotationAccessor annotationAccessor() {
/* 105 */     return this.annotationAccessor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final InjectionRequestImpl<C> withCommandContext(CommandContext<C> value) {
/* 115 */     if (this.commandContext == value) return this; 
/* 116 */     CommandContext<C> newValue = Objects.<CommandContext<C>>requireNonNull(value, "commandContext");
/* 117 */     return new InjectionRequestImpl(this, newValue, this.injectedType, this.annotationAccessor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final InjectionRequestImpl<C> withInjectedType(TypeToken<?> value) {
/* 127 */     if (this.injectedType == value) return this; 
/* 128 */     TypeToken<?> newValue = Objects.<TypeToken>requireNonNull(value, "injectedType");
/* 129 */     return new InjectionRequestImpl(this, this.commandContext, newValue, this.annotationAccessor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final InjectionRequestImpl<C> withAnnotationAccessor(AnnotationAccessor value) {
/* 139 */     if (this.annotationAccessor == value) return this; 
/* 140 */     AnnotationAccessor newValue = Objects.<AnnotationAccessor>requireNonNull(value, "annotationAccessor");
/* 141 */     return new InjectionRequestImpl(this, this.commandContext, this.injectedType, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 150 */     if (this == another) return true; 
/* 151 */     return (another instanceof InjectionRequestImpl && 
/* 152 */       equalTo(0, (InjectionRequestImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, InjectionRequestImpl<?> another) {
/* 156 */     return (this.commandContext.equals(another.commandContext) && this.injectedType
/* 157 */       .equals(another.injectedType) && this.injectedClass
/* 158 */       .equals(another.injectedClass) && this.annotationAccessor
/* 159 */       .equals(another.annotationAccessor));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 168 */     int h = 5381;
/* 169 */     h += (h << 5) + this.commandContext.hashCode();
/* 170 */     h += (h << 5) + this.injectedType.hashCode();
/* 171 */     h += (h << 5) + this.injectedClass.hashCode();
/* 172 */     h += (h << 5) + this.annotationAccessor.hashCode();
/* 173 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 182 */     return "InjectionRequest{commandContext=" + this.commandContext + ", injectedType=" + this.injectedType + ", injectedClass=" + this.injectedClass + ", annotationAccessor=" + this.annotationAccessor + "}";
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
/*     */ 
/*     */   
/*     */   public static <C> InjectionRequestImpl<C> of(CommandContext<C> commandContext, TypeToken<?> injectedType, AnnotationAccessor annotationAccessor) {
/* 199 */     return new InjectionRequestImpl<>(commandContext, injectedType, annotationAccessor);
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
/*     */   public static <C> InjectionRequestImpl<C> copyOf(InjectionRequest<C> instance) {
/* 211 */     if (instance instanceof InjectionRequestImpl) {
/* 212 */       return (InjectionRequestImpl<C>)instance;
/*     */     }
/* 214 */     return of(instance.commandContext(), instance.injectedType(), instance.annotationAccessor());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\injection\InjectionRequestImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */