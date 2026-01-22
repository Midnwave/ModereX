/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.VirtualComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.VirtualComponentRenderer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.flattener.ComponentFlattener;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node.TagNode;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node.ValueNode;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.Emitable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Inserting;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Modifying;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tree.Node;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Collections;
/*     */ import java.util.PrimitiveIterator;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.stream.Stream;
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
/*     */ abstract class AbstractColorChangingTag
/*     */   implements Modifying, Examinable
/*     */ {
/*  65 */   private static final ComponentFlattener LENGTH_CALCULATOR = (ComponentFlattener)ComponentFlattener.builder()
/*  66 */     .mapper(TextComponent.class, TextComponent::content)
/*  67 */     .unknownMapper(x -> "_")
/*  68 */     .build();
/*     */   
/*     */   private boolean visited;
/*  71 */   private int size = 0;
/*  72 */   private int disableApplyingColorDepth = -1;
/*     */   private final boolean emitVirtuals;
/*     */   
/*     */   AbstractColorChangingTag(Context ctx) {
/*  76 */     this.emitVirtuals = ctx.emitVirtuals();
/*     */   }
/*     */   
/*     */   protected final int size() {
/*  80 */     return this.size;
/*     */   }
/*     */ 
/*     */   
/*     */   public final void visit(@NotNull Node current, int depth) {
/*  85 */     if (this.visited) {
/*  86 */       throw new IllegalStateException("Color changing tag instances cannot be re-used, return a new one for each resolve");
/*     */     }
/*     */     
/*  89 */     if (current instanceof ValueNode) {
/*  90 */       String value = ((ValueNode)current).value();
/*  91 */       this.size += value.codePointCount(0, value.length());
/*  92 */     } else if (current instanceof TagNode) {
/*  93 */       TagNode tag = (TagNode)current;
/*  94 */       if (tag.tag() instanceof Inserting)
/*     */       {
/*  96 */         LENGTH_CALCULATOR.flatten(((Inserting)tag.tag()).value(), s -> this.size += s.codePointCount(0, s.length()));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final void postVisit() {
/* 104 */     this.visited = true;
/* 105 */     init();
/*     */   }
/*     */ 
/*     */   
/*     */   public final Component apply(@NotNull Component current, int depth) {
/* 110 */     if (this.emitVirtuals && depth == 0)
/*     */     {
/* 112 */       return (Component)Component.virtual(Void.class, new TagInfoHolder(preserveData(), current), current.style());
/*     */     }
/*     */     
/* 115 */     if ((this.disableApplyingColorDepth != -1 && depth > this.disableApplyingColorDepth) || current.style().color() != null) {
/* 116 */       if (this.disableApplyingColorDepth == -1 || depth < this.disableApplyingColorDepth) {
/* 117 */         this.disableApplyingColorDepth = depth;
/*     */       }
/*     */ 
/*     */       
/* 121 */       if (current instanceof TextComponent) {
/* 122 */         skipColorForLengthOf(((TextComponent)current).content());
/*     */       }
/* 124 */       return current.children(Collections.emptyList());
/*     */     } 
/*     */     
/* 127 */     this.disableApplyingColorDepth = -1;
/* 128 */     if (current instanceof VirtualComponent) {
/*     */ 
/*     */       
/* 131 */       skipColorForLengthOf(((VirtualComponent)current).content());
/*     */       
/* 133 */       return current.children(Collections.emptyList());
/* 134 */     }  if (current instanceof TextComponent && ((TextComponent)current).content().length() > 0) {
/* 135 */       TextComponent textComponent = (TextComponent)current;
/* 136 */       String content = textComponent.content();
/*     */       
/* 138 */       TextComponent.Builder parent = Component.text();
/*     */ 
/*     */       
/* 141 */       int[] holder = new int[1];
/* 142 */       for (PrimitiveIterator.OfInt it = content.codePoints().iterator(); it.hasNext(); ) {
/* 143 */         holder[0] = it.nextInt();
/* 144 */         TextComponent textComponent1 = Component.text(new String(holder, 0, 1), current.style().color(color()));
/* 145 */         advanceColor();
/* 146 */         parent.append((Component)textComponent1);
/*     */       } 
/*     */       
/* 149 */       return (Component)parent.build();
/* 150 */     }  if (!(current instanceof TextComponent)) {
/* 151 */       Component ret = current.children(Collections.emptyList()).colorIfAbsent(color());
/* 152 */       advanceColor();
/* 153 */       return ret;
/*     */     } 
/*     */     
/* 156 */     return Component.empty().mergeStyle(current);
/*     */   }
/*     */   
/*     */   private void skipColorForLengthOf(String content) {
/* 160 */     int len = content.codePointCount(0, content.length());
/* 161 */     for (int i = 0; i < len; i++)
/*     */     {
/* 163 */       advanceColor();
/*     */     }
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
/*     */   @NotNull
/*     */   public final String toString() {
/* 199 */     return Internals.toString(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static final class TagInfoHolder
/*     */     implements VirtualComponentRenderer<Void>, Emitable
/*     */   {
/*     */     private final Consumer<TokenEmitter> output;
/*     */     
/*     */     private final Component originalComp;
/*     */ 
/*     */     
/*     */     TagInfoHolder(Consumer<TokenEmitter> output, Component originalComp) {
/* 213 */       this.output = output;
/* 214 */       this.originalComp = originalComp;
/*     */     }
/*     */ 
/*     */     
/*     */     public ComponentLike apply(@NotNull Void context) {
/* 219 */       return (ComponentLike)this.originalComp;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public String fallbackString() {
/* 224 */       return "";
/*     */     }
/*     */ 
/*     */     
/*     */     public void emit(@NotNull TokenEmitter emitter) {
/* 229 */       this.output.accept(emitter);
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public Component substitute() {
/* 234 */       return this.originalComp;
/*     */     } }
/*     */   
/*     */   @Nullable
/*     */   static Emitable claimComponent(Component comp) {
/* 239 */     if (!(comp instanceof VirtualComponent)) {
/* 240 */       return null;
/*     */     }
/*     */     
/* 243 */     VirtualComponentRenderer<?> holder = ((VirtualComponent)comp).renderer();
/* 244 */     if (!(holder instanceof TagInfoHolder)) {
/* 245 */       return null;
/*     */     }
/*     */     
/* 248 */     return (TagInfoHolder)holder;
/*     */   }
/*     */   
/*     */   protected abstract void init();
/*     */   
/*     */   protected abstract void advanceColor();
/*     */   
/*     */   protected abstract TextColor color();
/*     */   
/*     */   @NotNull
/*     */   protected abstract Consumer<TokenEmitter> preserveData();
/*     */   
/*     */   @NotNull
/*     */   public abstract Stream<? extends ExaminableProperty> examinableProperties();
/*     */   
/*     */   public abstract boolean equals(@Nullable Object paramObject);
/*     */   
/*     */   public abstract int hashCode();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\AbstractColorChangingTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */