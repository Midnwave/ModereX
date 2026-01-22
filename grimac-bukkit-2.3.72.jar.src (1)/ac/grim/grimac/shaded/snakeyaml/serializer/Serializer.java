/*     */ package ac.grim.grimac.shaded.snakeyaml.serializer;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.DumperOptions;
/*     */ import ac.grim.grimac.shaded.snakeyaml.comments.CommentLine;
/*     */ import ac.grim.grimac.shaded.snakeyaml.emitter.Emitable;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.AliasEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.CommentEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.DocumentEndEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.DocumentStartEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.Event;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.ImplicitTuple;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.MappingEndEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.MappingStartEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.ScalarEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.SequenceEndEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.SequenceStartEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.StreamEndEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.StreamStartEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.AnchorNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.MappingNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Node;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.NodeId;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.NodeTuple;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.ScalarNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.SequenceNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Tag;
/*     */ import ac.grim.grimac.shaded.snakeyaml.resolver.Resolver;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Serializer
/*     */ {
/*     */   private final Emitable emitter;
/*     */   private final Resolver resolver;
/*     */   private final boolean explicitStart;
/*     */   private final boolean explicitEnd;
/*     */   private DumperOptions.Version useVersion;
/*     */   private final Map<String, String> useTags;
/*     */   private final Set<Node> serializedNodes;
/*     */   private final Map<Node, String> anchors;
/*     */   private final AnchorGenerator anchorGenerator;
/*     */   private Boolean closed;
/*     */   private final Tag explicitRoot;
/*     */   
/*     */   public Serializer(Emitable emitter, Resolver resolver, DumperOptions opts, Tag rootTag) {
/*  63 */     if (emitter == null) {
/*  64 */       throw new NullPointerException("Emitter must  be provided");
/*     */     }
/*  66 */     if (resolver == null) {
/*  67 */       throw new NullPointerException("Resolver must  be provided");
/*     */     }
/*  69 */     if (opts == null) {
/*  70 */       throw new NullPointerException("DumperOptions must  be provided");
/*     */     }
/*  72 */     this.emitter = emitter;
/*  73 */     this.resolver = resolver;
/*  74 */     this.explicitStart = opts.isExplicitStart();
/*  75 */     this.explicitEnd = opts.isExplicitEnd();
/*  76 */     if (opts.getVersion() != null) {
/*  77 */       this.useVersion = opts.getVersion();
/*     */     }
/*  79 */     this.useTags = opts.getTags();
/*  80 */     this.serializedNodes = new HashSet<>();
/*  81 */     this.anchors = new HashMap<>();
/*  82 */     this.anchorGenerator = opts.getAnchorGenerator();
/*  83 */     this.closed = null;
/*  84 */     this.explicitRoot = rootTag;
/*     */   }
/*     */   
/*     */   public void open() throws IOException {
/*  88 */     if (this.closed == null)
/*  89 */     { this.emitter.emit((Event)new StreamStartEvent(null, null));
/*  90 */       this.closed = Boolean.FALSE; }
/*  91 */     else { if (Boolean.TRUE.equals(this.closed)) {
/*  92 */         throw new SerializerException("serializer is closed");
/*     */       }
/*  94 */       throw new SerializerException("serializer is already opened"); }
/*     */   
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/*  99 */     if (this.closed == null)
/* 100 */       throw new SerializerException("serializer is not opened"); 
/* 101 */     if (!Boolean.TRUE.equals(this.closed)) {
/* 102 */       this.emitter.emit((Event)new StreamEndEvent(null, null));
/* 103 */       this.closed = Boolean.TRUE;
/*     */       
/* 105 */       this.serializedNodes.clear();
/* 106 */       this.anchors.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void serialize(Node node) throws IOException {
/* 111 */     if (this.closed == null)
/* 112 */       throw new SerializerException("serializer is not opened"); 
/* 113 */     if (this.closed.booleanValue()) {
/* 114 */       throw new SerializerException("serializer is closed");
/*     */     }
/* 116 */     this.emitter
/* 117 */       .emit((Event)new DocumentStartEvent(null, null, this.explicitStart, this.useVersion, this.useTags));
/* 118 */     anchorNode(node);
/* 119 */     if (this.explicitRoot != null) {
/* 120 */       node.setTag(this.explicitRoot);
/*     */     }
/* 122 */     serializeNode(node, null);
/* 123 */     this.emitter.emit((Event)new DocumentEndEvent(null, null, this.explicitEnd));
/* 124 */     this.serializedNodes.clear();
/* 125 */     this.anchors.clear();
/*     */   }
/*     */   
/*     */   private void anchorNode(Node node) {
/* 129 */     if (node.getNodeId() == NodeId.anchor) {
/* 130 */       node = ((AnchorNode)node).getRealNode();
/*     */     }
/* 132 */     if (this.anchors.containsKey(node)) {
/* 133 */       String anchor = this.anchors.get(node);
/* 134 */       if (null == anchor) {
/* 135 */         anchor = this.anchorGenerator.nextAnchor(node);
/* 136 */         this.anchors.put(node, anchor);
/*     */       } 
/*     */     } else {
/* 139 */       SequenceNode seqNode; List<Node> list; MappingNode mnode; List<NodeTuple> map; this.anchors.put(node, 
/* 140 */           (node.getAnchor() != null) ? this.anchorGenerator.nextAnchor(node) : null);
/* 141 */       switch (node.getNodeId()) {
/*     */         case sequence:
/* 143 */           seqNode = (SequenceNode)node;
/* 144 */           list = seqNode.getValue();
/* 145 */           for (Node item : list) {
/* 146 */             anchorNode(item);
/*     */           }
/*     */           break;
/*     */         case mapping:
/* 150 */           mnode = (MappingNode)node;
/* 151 */           map = mnode.getValue();
/* 152 */           for (NodeTuple object : map) {
/* 153 */             Node key = object.getKeyNode();
/* 154 */             Node value = object.getValueNode();
/* 155 */             anchorNode(key);
/* 156 */             anchorNode(value);
/*     */           } 
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void serializeNode(Node node, Node parent) throws IOException {
/* 165 */     if (node.getNodeId() == NodeId.anchor) {
/* 166 */       node = ((AnchorNode)node).getRealNode();
/*     */     }
/* 168 */     String tAlias = this.anchors.get(node);
/* 169 */     if (this.serializedNodes.contains(node)) {
/* 170 */       this.emitter.emit((Event)new AliasEvent(tAlias, null, null));
/*     */     } else {
/* 172 */       ScalarNode scalarNode; Tag detectedTag, defaultTag; ImplicitTuple tuple; ScalarEvent event; SequenceNode seqNode; boolean implicitS; List<Node> list; this.serializedNodes.add(node);
/* 173 */       switch (node.getNodeId()) {
/*     */         case scalar:
/* 175 */           scalarNode = (ScalarNode)node;
/* 176 */           serializeComments(node.getBlockComments());
/* 177 */           detectedTag = this.resolver.resolve(NodeId.scalar, scalarNode.getValue(), true);
/* 178 */           defaultTag = this.resolver.resolve(NodeId.scalar, scalarNode.getValue(), false);
/*     */           
/* 180 */           tuple = new ImplicitTuple(node.getTag().equals(detectedTag), node.getTag().equals(defaultTag));
/*     */           
/* 182 */           event = new ScalarEvent(tAlias, node.getTag().getValue(), tuple, scalarNode.getValue(), null, null, scalarNode.getScalarStyle());
/* 183 */           this.emitter.emit((Event)event);
/* 184 */           serializeComments(node.getInLineComments());
/* 185 */           serializeComments(node.getEndComments());
/*     */           return;
/*     */         case sequence:
/* 188 */           seqNode = (SequenceNode)node;
/* 189 */           serializeComments(node.getBlockComments());
/*     */           
/* 191 */           implicitS = node.getTag().equals(this.resolver.resolve(NodeId.sequence, null, true));
/* 192 */           this.emitter.emit((Event)new SequenceStartEvent(tAlias, node.getTag().getValue(), implicitS, null, null, seqNode
/* 193 */                 .getFlowStyle()));
/* 194 */           list = seqNode.getValue();
/* 195 */           for (Node item : list) {
/* 196 */             serializeNode(item, node);
/*     */           }
/* 198 */           this.emitter.emit((Event)new SequenceEndEvent(null, null));
/* 199 */           serializeComments(node.getInLineComments());
/* 200 */           serializeComments(node.getEndComments());
/*     */           return;
/*     */       } 
/* 203 */       serializeComments(node.getBlockComments());
/* 204 */       Tag implicitTag = this.resolver.resolve(NodeId.mapping, null, true);
/* 205 */       boolean implicitM = node.getTag().equals(implicitTag);
/* 206 */       MappingNode mnode = (MappingNode)node;
/* 207 */       List<NodeTuple> map = mnode.getValue();
/* 208 */       if (mnode.getTag() != Tag.COMMENT) {
/* 209 */         this.emitter.emit((Event)new MappingStartEvent(tAlias, mnode.getTag().getValue(), implicitM, null, null, mnode
/* 210 */               .getFlowStyle()));
/* 211 */         for (NodeTuple row : map) {
/* 212 */           Node key = row.getKeyNode();
/* 213 */           Node value = row.getValueNode();
/* 214 */           serializeNode(key, (Node)mnode);
/* 215 */           serializeNode(value, (Node)mnode);
/*     */         } 
/* 217 */         this.emitter.emit((Event)new MappingEndEvent(null, null));
/* 218 */         serializeComments(node.getInLineComments());
/* 219 */         serializeComments(node.getEndComments());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void serializeComments(List<CommentLine> comments) throws IOException {
/* 226 */     if (comments == null) {
/*     */       return;
/*     */     }
/* 229 */     for (CommentLine line : comments) {
/*     */       
/* 231 */       CommentEvent commentEvent = new CommentEvent(line.getCommentType(), line.getValue(), line.getStartMark(), line.getEndMark());
/* 232 */       this.emitter.emit((Event)commentEvent);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\serializer\Serializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */