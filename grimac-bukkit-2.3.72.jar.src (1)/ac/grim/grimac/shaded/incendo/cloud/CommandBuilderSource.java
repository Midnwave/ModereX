/*     */ package ac.grim.grimac.shaded.incendo.cloud;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.description.Description;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.meta.CommandMeta;
/*     */ import java.util.Collection;
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
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.STABLE)
/*     */ public interface CommandBuilderSource<C>
/*     */ {
/*     */   default Command.Builder<C> commandBuilder(String name, Collection<String> aliases, Description description, CommandMeta meta) {
/*  54 */     return decorateBuilder(Command.newBuilder(name, meta, description, aliases.<String>toArray(new String[0])));
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
/*     */   default Command.Builder<C> commandBuilder(String name, Collection<String> aliases, CommandMeta meta) {
/*  74 */     return decorateBuilder(Command.newBuilder(name, meta, Description.empty(), aliases.<String>toArray(new String[0])));
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
/*     */   Command.Builder<C> commandBuilder(String name, CommandMeta meta, Description description, String... aliases) {
/*  96 */     return decorateBuilder(Command.newBuilder(name, meta, description, aliases));
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
/*     */   Command.Builder<C> commandBuilder(String name, CommandMeta meta, String... aliases) {
/* 116 */     return decorateBuilder(Command.newBuilder(name, meta, Description.empty(), aliases));
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
/*     */   Command.Builder<C> commandBuilder(String name, Description description, String... aliases) {
/* 138 */     return decorateBuilder(Command.newBuilder(name, createDefaultCommandMeta(), description, aliases));
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
/*     */   Command.Builder<C> commandBuilder(String name, String... aliases) {
/* 159 */     return decorateBuilder(Command.newBuilder(name, createDefaultCommandMeta(), Description.empty(), aliases));
/*     */   }
/*     */   
/*     */   CommandMeta createDefaultCommandMeta();
/*     */   
/*     */   @API(status = API.Status.INTERNAL)
/*     */   Command.Builder<C> decorateBuilder(Command.Builder<C> paramBuilder);
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\CommandBuilderSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */