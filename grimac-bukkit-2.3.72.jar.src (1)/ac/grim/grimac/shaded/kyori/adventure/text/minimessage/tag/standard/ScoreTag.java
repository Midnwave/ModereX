/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.ScoreComponent;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.ParsingException;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.Emitable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class ScoreTag
/*    */ {
/*    */   public static final String SCORE = "score";
/* 40 */   static final TagResolver RESOLVER = SerializableResolver.claimingComponent("score", ScoreTag::create, ScoreTag::emit);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static Tag create(ArgumentQueue args, Context ctx) throws ParsingException {
/* 46 */     String name = args.popOr("A scoreboard member name is required").value();
/* 47 */     String objective = args.popOr("An objective name is required").value();
/* 48 */     return Tag.inserting((Component)Component.score(name, objective));
/*    */   }
/*    */   @Nullable
/*    */   static Emitable emit(Component component) {
/* 52 */     if (!(component instanceof ScoreComponent)) return null;
/*    */     
/* 54 */     ScoreComponent score = (ScoreComponent)component;
/*    */     
/* 56 */     return emit -> emit.tag("score").argument(score.name()).argument(score.objective());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\ScoreTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */