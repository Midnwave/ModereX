/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.renderer;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.BlockNBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.EntityNBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.KeybindComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ScoreComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.SelectorComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.StorageNBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslatableComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.VirtualComponent;
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
/*     */ public abstract class AbstractComponentRenderer<C>
/*     */   implements ComponentRenderer<C>
/*     */ {
/*     */   @NotNull
/*     */   public Component render(@NotNull Component component, @NotNull C context) {
/*  48 */     if (component instanceof VirtualComponent) {
/*  49 */       component = renderVirtual((VirtualComponent)component, context);
/*     */     }
/*  51 */     if (component instanceof TextComponent)
/*  52 */       return renderText((TextComponent)component, context); 
/*  53 */     if (component instanceof TranslatableComponent)
/*  54 */       return renderTranslatable((TranslatableComponent)component, context); 
/*  55 */     if (component instanceof KeybindComponent)
/*  56 */       return renderKeybind((KeybindComponent)component, context); 
/*  57 */     if (component instanceof ScoreComponent)
/*  58 */       return renderScore((ScoreComponent)component, context); 
/*  59 */     if (component instanceof SelectorComponent)
/*  60 */       return renderSelector((SelectorComponent)component, context); 
/*  61 */     if (component instanceof ac.grim.grimac.shaded.kyori.adventure.text.NBTComponent) {
/*  62 */       if (component instanceof BlockNBTComponent)
/*  63 */         return renderBlockNbt((BlockNBTComponent)component, context); 
/*  64 */       if (component instanceof EntityNBTComponent)
/*  65 */         return renderEntityNbt((EntityNBTComponent)component, context); 
/*  66 */       if (component instanceof StorageNBTComponent) {
/*  67 */         return renderStorageNbt((StorageNBTComponent)component, context);
/*     */       }
/*     */     } 
/*  70 */     return component;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected abstract Component renderBlockNbt(@NotNull BlockNBTComponent paramBlockNBTComponent, @NotNull C paramC);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected abstract Component renderEntityNbt(@NotNull EntityNBTComponent paramEntityNBTComponent, @NotNull C paramC);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected abstract Component renderStorageNbt(@NotNull StorageNBTComponent paramStorageNBTComponent, @NotNull C paramC);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected abstract Component renderKeybind(@NotNull KeybindComponent paramKeybindComponent, @NotNull C paramC);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected abstract Component renderScore(@NotNull ScoreComponent paramScoreComponent, @NotNull C paramC);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected abstract Component renderSelector(@NotNull SelectorComponent paramSelectorComponent, @NotNull C paramC);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected abstract Component renderText(@NotNull TextComponent paramTextComponent, @NotNull C paramC);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected Component renderVirtual(@NotNull VirtualComponent component, @NotNull C context) {
/* 145 */     return (Component)component;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected abstract Component renderTranslatable(@NotNull TranslatableComponent paramTranslatableComponent, @NotNull C paramC);
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\renderer\AbstractComponentRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */