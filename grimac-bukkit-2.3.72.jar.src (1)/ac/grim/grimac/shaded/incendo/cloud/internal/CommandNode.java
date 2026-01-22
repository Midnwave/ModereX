/*     */ package ac.grim.grimac.shaded.incendo.cloud.internal;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.key.SimpleMutableCloudKeyContainer;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.permission.Permission;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ public final class CommandNode<C>
/*     */ {
/*  69 */   public static final CloudKey<Set<Type>> META_KEY_SENDER_TYPES = CloudKey.cloudKey("senderTypes", new TypeToken<Set<Type>>() {  }
/*     */     );
/*  71 */   public static final CloudKey<Map<Type, Permission>> META_KEY_ACCESS = CloudKey.cloudKey("access", new TypeToken<Map<Type, Permission>>()
/*     */       {
/*     */       
/*     */       });
/*     */   
/*  76 */   private final SimpleMutableCloudKeyContainer nodeMeta = new SimpleMutableCloudKeyContainer(new HashMap<>());
/*  77 */   private final List<CommandNode<C>> children = new LinkedList<>();
/*     */ 
/*     */   
/*     */   private final CommandComponent<C> component;
/*     */   
/*     */   private CommandNode<C> parent;
/*     */   
/*     */   private Command<C> command;
/*     */ 
/*     */   
/*     */   public CommandNode(CommandComponent<C> component) {
/*  88 */     this.component = component;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<CommandNode<C>> children() {
/*  97 */     return Collections.unmodifiableList(this.children);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandNode<C> addChild(CommandComponent<C> component) {
/* 107 */     CommandNode<C> node = new CommandNode(component);
/* 108 */     this.children.add(node);
/* 109 */     return node;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandNode<C> getChild(CommandComponent<C> component) {
/* 119 */     for (CommandNode<C> child : this.children) {
/* 120 */       if (component.equals(child.component())) {
/* 121 */         return child;
/*     */       }
/*     */     } 
/* 124 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean removeChild(CommandNode<C> child) {
/* 134 */     return this.children.remove(child);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLeaf() {
/* 143 */     return this.children.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleMutableCloudKeyContainer nodeMeta() {
/* 152 */     return this.nodeMeta;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandComponent<C> component() {
/* 161 */     return this.component;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Command<C> command() {
/* 170 */     return this.command;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void command(Command<C> command) {
/* 179 */     if (this.command != null) {
/* 180 */       throw new IllegalStateException("Cannot replace owning command");
/*     */     }
/* 182 */     this.command = command;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandNode<C> parent() {
/* 191 */     return this.parent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void parent(CommandNode<C> parent) {
/* 200 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sortChildren() {
/* 207 */     this.children.sort(Comparator.comparing(CommandNode::component));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 212 */     if (this == o) {
/* 213 */       return true;
/*     */     }
/* 215 */     if (o == null || getClass() != o.getClass()) {
/* 216 */       return false;
/*     */     }
/* 218 */     CommandNode<?> node = (CommandNode)o;
/* 219 */     return Objects.equals(component(), node.component());
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 224 */     return Objects.hash(new Object[] { component() });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 229 */     return "Node{value=" + this.component + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\internal\CommandNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */