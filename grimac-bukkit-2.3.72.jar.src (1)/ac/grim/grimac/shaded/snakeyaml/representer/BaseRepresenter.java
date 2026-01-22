/*     */ package ac.grim.grimac.shaded.snakeyaml.representer;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.DumperOptions;
/*     */ import ac.grim.grimac.shaded.snakeyaml.introspector.PropertyUtils;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.AnchorNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.MappingNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Node;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.NodeTuple;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.ScalarNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.SequenceNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Tag;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class BaseRepresenter
/*     */ {
/*  42 */   protected final Map<Class<?>, Represent> representers = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Represent nullRepresenter;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   protected final Map<Class<?>, Represent> multiRepresenters = new LinkedHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  57 */   protected DumperOptions.ScalarStyle defaultScalarStyle = DumperOptions.ScalarStyle.PLAIN;
/*     */ 
/*     */ 
/*     */   
/*  61 */   protected DumperOptions.FlowStyle defaultFlowStyle = DumperOptions.FlowStyle.AUTO;
/*     */ 
/*     */ 
/*     */   
/*  65 */   protected final Map<Object, Node> representedObjects = new IdentityHashMap<Object, Node>() {
/*     */       private static final long serialVersionUID = -5576159264232131854L;
/*     */       
/*     */       public Node put(Object key, Node value) {
/*  69 */         return (Node)super.put(key, new AnchorNode(value));
/*     */       }
/*     */     };
/*     */   
/*     */   protected Object objectToRepresent;
/*     */   
/*     */   private PropertyUtils propertyUtils;
/*     */   
/*     */   private boolean explicitPropertyUtils = false;
/*     */ 
/*     */   
/*     */   public Node represent(Object data) {
/*  81 */     Node node = representData(data);
/*  82 */     this.representedObjects.clear();
/*  83 */     this.objectToRepresent = null;
/*  84 */     return node;
/*     */   }
/*     */   protected final Node representData(Object data) {
/*     */     Node node;
/*  88 */     this.objectToRepresent = data;
/*     */     
/*  90 */     if (this.representedObjects.containsKey(this.objectToRepresent)) {
/*  91 */       node = this.representedObjects.get(this.objectToRepresent);
/*  92 */       return node;
/*     */     } 
/*     */ 
/*     */     
/*  96 */     if (data == null) {
/*  97 */       node = this.nullRepresenter.representData(null);
/*  98 */       return node;
/*     */     } 
/*     */ 
/*     */     
/* 102 */     Class<?> clazz = data.getClass();
/* 103 */     if (this.representers.containsKey(clazz)) {
/* 104 */       Represent representer = this.representers.get(clazz);
/* 105 */       node = representer.representData(data);
/*     */     } else {
/*     */       
/* 108 */       for (Class<?> repr : this.multiRepresenters.keySet()) {
/* 109 */         if (repr != null && repr.isInstance(data)) {
/* 110 */           Represent representer = this.multiRepresenters.get(repr);
/* 111 */           node = representer.representData(data);
/* 112 */           return node;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 117 */       if (this.multiRepresenters.containsKey(null)) {
/* 118 */         Represent representer = this.multiRepresenters.get(null);
/* 119 */         node = representer.representData(data);
/*     */       } else {
/* 121 */         Represent representer = this.representers.get(null);
/* 122 */         node = representer.representData(data);
/*     */       } 
/*     */     } 
/* 125 */     return node;
/*     */   }
/*     */   
/*     */   protected Node representScalar(Tag tag, String value, DumperOptions.ScalarStyle style) {
/* 129 */     if (style == null) {
/* 130 */       style = this.defaultScalarStyle;
/*     */     }
/* 132 */     return (Node)new ScalarNode(tag, value, null, null, style);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Node representScalar(Tag tag, String value) {
/* 137 */     return representScalar(tag, value, this.defaultScalarStyle);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Node representSequence(Tag tag, Iterable<?> sequence, DumperOptions.FlowStyle flowStyle) {
/* 142 */     int size = 10;
/* 143 */     if (sequence instanceof List) {
/* 144 */       size = ((List)sequence).size();
/*     */     }
/* 146 */     List<Node> value = new ArrayList<>(size);
/* 147 */     SequenceNode node = new SequenceNode(tag, value, flowStyle);
/* 148 */     this.representedObjects.put(this.objectToRepresent, node);
/* 149 */     DumperOptions.FlowStyle bestStyle = DumperOptions.FlowStyle.FLOW;
/* 150 */     for (Object item : sequence) {
/* 151 */       Node nodeItem = representData(item);
/* 152 */       if (!(nodeItem instanceof ScalarNode) || !((ScalarNode)nodeItem).isPlain()) {
/* 153 */         bestStyle = DumperOptions.FlowStyle.BLOCK;
/*     */       }
/* 155 */       value.add(nodeItem);
/*     */     } 
/* 157 */     if (flowStyle == DumperOptions.FlowStyle.AUTO) {
/* 158 */       if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
/* 159 */         node.setFlowStyle(this.defaultFlowStyle);
/*     */       } else {
/* 161 */         node.setFlowStyle(bestStyle);
/*     */       } 
/*     */     }
/* 164 */     return (Node)node;
/*     */   }
/*     */   
/*     */   protected Node representMapping(Tag tag, Map<?, ?> mapping, DumperOptions.FlowStyle flowStyle) {
/* 168 */     List<NodeTuple> value = new ArrayList<>(mapping.size());
/* 169 */     MappingNode node = new MappingNode(tag, value, flowStyle);
/* 170 */     this.representedObjects.put(this.objectToRepresent, node);
/* 171 */     DumperOptions.FlowStyle bestStyle = DumperOptions.FlowStyle.FLOW;
/* 172 */     for (Map.Entry<?, ?> entry : mapping.entrySet()) {
/* 173 */       Node nodeKey = representData(entry.getKey());
/* 174 */       Node nodeValue = representData(entry.getValue());
/* 175 */       if (!(nodeKey instanceof ScalarNode) || !((ScalarNode)nodeKey).isPlain()) {
/* 176 */         bestStyle = DumperOptions.FlowStyle.BLOCK;
/*     */       }
/* 178 */       if (!(nodeValue instanceof ScalarNode) || !((ScalarNode)nodeValue).isPlain()) {
/* 179 */         bestStyle = DumperOptions.FlowStyle.BLOCK;
/*     */       }
/* 181 */       value.add(new NodeTuple(nodeKey, nodeValue));
/*     */     } 
/* 183 */     if (flowStyle == DumperOptions.FlowStyle.AUTO) {
/* 184 */       if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
/* 185 */         node.setFlowStyle(this.defaultFlowStyle);
/*     */       } else {
/* 187 */         node.setFlowStyle(bestStyle);
/*     */       } 
/*     */     }
/* 190 */     return (Node)node;
/*     */   }
/*     */   
/*     */   public void setDefaultScalarStyle(DumperOptions.ScalarStyle defaultStyle) {
/* 194 */     this.defaultScalarStyle = defaultStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DumperOptions.ScalarStyle getDefaultScalarStyle() {
/* 203 */     if (this.defaultScalarStyle == null) {
/* 204 */       return DumperOptions.ScalarStyle.PLAIN;
/*     */     }
/* 206 */     return this.defaultScalarStyle;
/*     */   }
/*     */   
/*     */   public void setDefaultFlowStyle(DumperOptions.FlowStyle defaultFlowStyle) {
/* 210 */     this.defaultFlowStyle = defaultFlowStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DumperOptions.FlowStyle getDefaultFlowStyle() {
/* 219 */     return this.defaultFlowStyle;
/*     */   }
/*     */   
/*     */   public void setPropertyUtils(PropertyUtils propertyUtils) {
/* 223 */     this.propertyUtils = propertyUtils;
/* 224 */     this.explicitPropertyUtils = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final PropertyUtils getPropertyUtils() {
/* 233 */     if (this.propertyUtils == null) {
/* 234 */       this.propertyUtils = new PropertyUtils();
/*     */     }
/* 236 */     return this.propertyUtils;
/*     */   }
/*     */   
/*     */   public final boolean isExplicitPropertyUtils() {
/* 240 */     return this.explicitPropertyUtils;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\representer\BaseRepresenter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */