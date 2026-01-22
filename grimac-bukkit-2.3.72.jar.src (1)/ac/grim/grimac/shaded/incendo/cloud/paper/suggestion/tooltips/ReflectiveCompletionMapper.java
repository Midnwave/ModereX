/*     */ package ac.grim.grimac.shaded.incendo.cloud.paper.suggestion.tooltips;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion.TooltipSuggestion;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CraftBukkitReflection;
/*     */ import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
/*     */ import com.mojang.brigadier.Message;
/*     */ import io.papermc.paper.brigadier.PaperBrigadier;
/*     */ import io.papermc.paper.command.brigadier.MessageComponentSerializer;
/*     */ import java.lang.invoke.MethodHandle;
/*     */ import java.lang.invoke.MethodHandles;
/*     */ import java.lang.reflect.Method;
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
/*     */ final class ReflectiveCompletionMapper
/*     */   implements CompletionMapper
/*     */ {
/*     */   private final CompletionMapper wrapped;
/*     */   
/*     */   ReflectiveCompletionMapper() {
/*  41 */     if (CraftBukkitReflection.classExists("io.papermc.paper.command.brigadier.MessageComponentSerializer")) {
/*  42 */       this.wrapped = new Modern();
/*     */     } else {
/*  44 */       this.wrapped = new Legacy();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public AsyncTabCompleteEvent.Completion map(TooltipSuggestion suggestion) {
/*  50 */     return this.wrapped.map(suggestion);
/*     */   }
/*     */   
/*     */   private static final class Modern
/*     */     implements CompletionMapper {
/*     */     private final Object serializer;
/*     */     private final Method deserializeOrNull;
/*     */     private final Method completionWithTooltipMethod;
/*     */     
/*     */     Modern() {
/*  60 */       Method instance = CraftBukkitReflection.needMethod(MessageComponentSerializer.class, "message", new Class[0]);
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/*  65 */         this.serializer = instance.invoke(null, new Object[0]);
/*  66 */         this.deserializeOrNull = CraftBukkitReflection.needMethod(MessageComponentSerializer.class, "deserializeOrNull", new Class[] { Object.class });
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  71 */         this.completionWithTooltipMethod = CraftBukkitReflection.needMethod(AsyncTabCompleteEvent.Completion.class, "completion", new Class[] { String.class, this.deserializeOrNull
/*     */ 
/*     */ 
/*     */               
/*  75 */               .getReturnType() });
/*     */       }
/*  77 */       catch (ReflectiveOperationException e) {
/*  78 */         throw new RuntimeException(e);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public AsyncTabCompleteEvent.Completion map(TooltipSuggestion suggestion) {
/*     */       try {
/*  85 */         return (AsyncTabCompleteEvent.Completion)this.completionWithTooltipMethod.invoke(null, new Object[] { suggestion
/*     */               
/*  87 */               .suggestion(), this.deserializeOrNull
/*  88 */               .invoke(this.serializer, new Object[] { suggestion.tooltip() }) });
/*     */       }
/*  90 */       catch (ReflectiveOperationException e) {
/*  91 */         throw new RuntimeException(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class Legacy
/*     */     implements CompletionMapper {
/*     */     private final MethodHandle completionWithTooltip;
/*     */     private final MethodHandle componentFromMessage;
/*     */     
/*     */     Legacy() {
/* 102 */       Method componentFromMessageMethod = CraftBukkitReflection.needMethod(PaperBrigadier.class, "componentFromMessage", new Class[] { Message.class });
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 107 */       Method completionWithTooltipMethod = CraftBukkitReflection.needMethod(AsyncTabCompleteEvent.Completion.class, "completion", new Class[] { String.class, componentFromMessageMethod
/*     */ 
/*     */ 
/*     */             
/* 111 */             .getReturnType() });
/*     */       
/*     */       try {
/* 114 */         this.componentFromMessage = MethodHandles.publicLookup().unreflect(componentFromMessageMethod);
/* 115 */         this.completionWithTooltip = MethodHandles.publicLookup().unreflect(completionWithTooltipMethod);
/* 116 */       } catch (IllegalAccessException e) {
/* 117 */         throw new RuntimeException(e);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public AsyncTabCompleteEvent.Completion map(TooltipSuggestion suggestion) {
/* 123 */       Message tooltip = suggestion.tooltip();
/* 124 */       if (tooltip == null) {
/* 125 */         return AsyncTabCompleteEvent.Completion.completion(suggestion.suggestion());
/*     */       }
/*     */       try {
/* 128 */         Object component = this.componentFromMessage.invoke(tooltip);
/* 129 */         return this.completionWithTooltip.invoke(suggestion.suggestion(), component);
/* 130 */       } catch (Throwable e) {
/* 131 */         throw new RuntimeException(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\paper\suggestion\tooltips\ReflectiveCompletionMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */