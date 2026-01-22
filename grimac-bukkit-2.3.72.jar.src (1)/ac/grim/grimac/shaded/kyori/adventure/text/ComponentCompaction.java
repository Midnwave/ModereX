/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.VisibleForTesting;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ final class ComponentCompaction
/*     */ {
/*     */   @VisibleForTesting
/*     */   static final boolean SIMPLIFY_STYLE_FOR_BLANK_COMPONENTS = false;
/*     */   
/*     */   static Component compact(@NotNull Component self, @Nullable Style parentStyle) {
/*  44 */     List<Component> children = self.children();
/*  45 */     Component optimized = self.children(Collections.emptyList());
/*  46 */     if (parentStyle != null) {
/*  47 */       optimized = optimized.style(self.style().unmerge(parentStyle));
/*     */     }
/*     */     
/*  50 */     int childrenSize = children.size();
/*     */     
/*  52 */     if (childrenSize == 0) {
/*     */       
/*  54 */       if (isBlank(optimized)) {
/*  55 */         optimized = optimized.style(simplifyStyleForBlank(optimized.style(), parentStyle));
/*     */       }
/*     */ 
/*     */       
/*  59 */       return optimized;
/*     */     } 
/*     */ 
/*     */     
/*  63 */     if (childrenSize == 1 && isText(optimized)) {
/*  64 */       TextComponent textComponent = (TextComponent)optimized;
/*     */       
/*  66 */       if (textComponent.content().isEmpty()) {
/*  67 */         Component child = children.get(0);
/*     */ 
/*     */         
/*  70 */         return child.style(child.style().merge(optimized.style(), Style.Merge.Strategy.IF_ABSENT_ON_TARGET)).compact();
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  76 */     Style childParentStyle = optimized.style();
/*  77 */     if (parentStyle != null) {
/*  78 */       childParentStyle = childParentStyle.merge(parentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
/*     */     }
/*     */ 
/*     */     
/*  82 */     List<Component> childrenToAppend = new ArrayList<>(children.size()); int i;
/*  83 */     for (i = 0; i < children.size(); i++) {
/*  84 */       Component child = children.get(i);
/*     */ 
/*     */       
/*  87 */       child = compact(child, childParentStyle);
/*     */ 
/*     */       
/*  90 */       if (child.children().isEmpty() && isText(child)) {
/*  91 */         TextComponent textComponent = (TextComponent)child;
/*     */         
/*  93 */         if (textComponent.content().isEmpty()) {
/*     */           continue;
/*     */         }
/*     */       } 
/*     */       
/*  98 */       childrenToAppend.add(child);
/*     */       
/*     */       continue;
/*     */     } 
/* 102 */     if (isText(optimized)) {
/* 103 */       while (!childrenToAppend.isEmpty()) {
/* 104 */         Component child = childrenToAppend.get(0);
/* 105 */         Style childStyle = child.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
/*     */         
/* 107 */         if (isText(child) && Objects.equals(childStyle, childParentStyle)) {
/*     */ 
/*     */           
/* 110 */           optimized = joinText((TextComponent)optimized, (TextComponent)child);
/* 111 */           childrenToAppend.remove(0);
/*     */ 
/*     */           
/* 114 */           childrenToAppend.addAll(0, child.children());
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 124 */     for (i = 0; i + 1 < childrenToAppend.size(); ) {
/* 125 */       Component child = childrenToAppend.get(i);
/* 126 */       Component neighbor = childrenToAppend.get(i + 1);
/*     */       
/* 128 */       if (child.children().isEmpty() && isText(child) && isText(neighbor)) {
/*     */         
/* 130 */         Style childStyle = child.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
/* 131 */         Style neighborStyle = neighbor.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
/*     */ 
/*     */         
/* 134 */         if (childStyle.equals(neighborStyle)) {
/* 135 */           Component combined = joinText((TextComponent)child, (TextComponent)neighbor);
/*     */ 
/*     */           
/* 138 */           childrenToAppend.set(i, combined);
/* 139 */           childrenToAppend.remove(i + 1);
/*     */ 
/*     */           
/*     */           continue;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 147 */       i++;
/*     */     } 
/*     */ 
/*     */     
/* 151 */     if (childrenToAppend.isEmpty() && isBlank(optimized)) {
/* 152 */       optimized = optimized.style(simplifyStyleForBlank(optimized.style(), parentStyle));
/*     */     }
/*     */     
/* 155 */     return optimized.children((List)childrenToAppend);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isBlank(Component component) {
/* 165 */     if (isText(component)) {
/* 166 */       TextComponent textComponent = (TextComponent)component;
/*     */       
/* 168 */       String content = textComponent.content();
/*     */       
/* 170 */       for (int i = 0; i < content.length(); i++) {
/* 171 */         char c = content.charAt(i);
/* 172 */         if (c != ' ') return false;
/*     */       
/*     */       } 
/* 175 */       return true;
/*     */     } 
/* 177 */     return false;
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
/*     */   @NotNull
/*     */   private static Style simplifyStyleForBlank(@NotNull Style style, @Nullable Style parentStyle) {
/* 192 */     return style;
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
/*     */   private static TextComponent joinText(TextComponent one, TextComponent two) {
/* 216 */     return TextComponentImpl.create((List)two.children(), one.style(), one.content() + two.content());
/*     */   }
/*     */   
/*     */   private static boolean isText(Component component) {
/* 220 */     return (component instanceof TextComponent && !(component instanceof VirtualComponent));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\ComponentCompaction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */