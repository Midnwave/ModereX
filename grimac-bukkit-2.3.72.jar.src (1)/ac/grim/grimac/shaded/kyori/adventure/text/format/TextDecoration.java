/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.format;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Index;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.TriState;
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
/*     */ public enum TextDecoration
/*     */   implements StyleBuilderApplicable, TextFormat
/*     */ {
/*  45 */   OBFUSCATED("obfuscated"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   BOLD("bold"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  57 */   STRIKETHROUGH("strikethrough"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  63 */   UNDERLINED("underlined"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  69 */   ITALIC("italic");
/*     */   
/*     */   public static final Index<String, TextDecoration> NAMES;
/*     */   
/*     */   private final String name;
/*     */   
/*     */   static {
/*  76 */     NAMES = Index.create(TextDecoration.class, constant -> constant.name);
/*     */   }
/*     */   
/*     */   TextDecoration(String name) {
/*  80 */     this.name = name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public final TextDecorationAndState as(boolean state) {
/*  93 */     return withState(state);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public final TextDecorationAndState as(@NotNull State state) {
/* 106 */     return withState(state);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public final TextDecorationAndState withState(boolean state) {
/* 117 */     return new TextDecorationAndStateImpl(this, State.byBoolean(state));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public final TextDecorationAndState withState(@NotNull State state) {
/* 128 */     return new TextDecorationAndStateImpl(this, state);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public final TextDecorationAndState withState(@NotNull TriState state) {
/* 139 */     return new TextDecorationAndStateImpl(this, State.byTriState(state));
/*     */   }
/*     */ 
/*     */   
/*     */   public void styleApply(Style.Builder style) {
/* 144 */     style.decorate(this);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String toString() {
/* 149 */     return this.name;
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
/*     */   public enum State
/*     */   {
/* 163 */     NOT_SET("not_set"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 169 */     FALSE("false"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 175 */     TRUE("true");
/*     */     
/*     */     private final String name;
/*     */     
/*     */     State(String name) {
/* 180 */       this.name = name;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 185 */       return this.name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public static State byBoolean(boolean flag) {
/* 196 */       return flag ? TRUE : FALSE;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public static State byBoolean(@Nullable Boolean flag) {
/* 207 */       return (flag == null) ? NOT_SET : byBoolean(flag.booleanValue());
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public static State byTriState(@NotNull TriState flag) {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: invokestatic requireNonNull : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */       //   4: pop
/*     */       //   5: getstatic ac/grim/grimac/shaded/kyori/adventure/text/format/TextDecoration$1.$SwitchMap$net$kyori$adventure$util$TriState : [I
/*     */       //   8: aload_0
/*     */       //   9: invokevirtual ordinal : ()I
/*     */       //   12: iaload
/*     */       //   13: tableswitch default -> 52, 1 -> 40, 2 -> 44, 3 -> 48
/*     */       //   40: getstatic ac/grim/grimac/shaded/kyori/adventure/text/format/TextDecoration$State.TRUE : Lac/grim/grimac/shaded/kyori/adventure/text/format/TextDecoration$State;
/*     */       //   43: areturn
/*     */       //   44: getstatic ac/grim/grimac/shaded/kyori/adventure/text/format/TextDecoration$State.FALSE : Lac/grim/grimac/shaded/kyori/adventure/text/format/TextDecoration$State;
/*     */       //   47: areturn
/*     */       //   48: getstatic ac/grim/grimac/shaded/kyori/adventure/text/format/TextDecoration$State.NOT_SET : Lac/grim/grimac/shaded/kyori/adventure/text/format/TextDecoration$State;
/*     */       //   51: areturn
/*     */       //   52: new java/lang/IllegalArgumentException
/*     */       //   55: dup
/*     */       //   56: new java/lang/StringBuilder
/*     */       //   59: dup
/*     */       //   60: invokespecial <init> : ()V
/*     */       //   63: ldc 'Unable to turn TriState: '
/*     */       //   65: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */       //   68: aload_0
/*     */       //   69: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*     */       //   72: ldc ' into a TextDecoration.State'
/*     */       //   74: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */       //   77: invokevirtual toString : ()Ljava/lang/String;
/*     */       //   80: invokespecial <init> : (Ljava/lang/String;)V
/*     */       //   83: athrow
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #218	-> 0
/*     */       //   #219	-> 5
/*     */       //   #220	-> 40
/*     */       //   #221	-> 44
/*     */       //   #222	-> 48
/*     */       //   #224	-> 52
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	84	0	flag	Lac/grim/grimac/shaded/kyori/adventure/util/TriState;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\format\TextDecoration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */