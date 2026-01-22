/*     */ package ac.grim.grimac.shaded.snakeyaml.composer;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.DumperOptions;
/*     */ import ac.grim.grimac.shaded.snakeyaml.LoaderOptions;
/*     */ import ac.grim.grimac.shaded.snakeyaml.comments.CommentEventsCollector;
/*     */ import ac.grim.grimac.shaded.snakeyaml.comments.CommentLine;
/*     */ import ac.grim.grimac.shaded.snakeyaml.comments.CommentType;
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.Mark;
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.AliasEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.Event;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.MappingStartEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.NodeEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.ScalarEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.SequenceStartEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.MappingNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Node;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.NodeId;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.NodeTuple;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.ScalarNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.SequenceNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Tag;
/*     */ import ac.grim.grimac.shaded.snakeyaml.parser.Parser;
/*     */ import ac.grim.grimac.shaded.snakeyaml.resolver.Resolver;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Composer
/*     */ {
/*     */   protected final Parser parser;
/*     */   private final Resolver resolver;
/*     */   private final Map<String, Node> anchors;
/*     */   private final Set<Node> recursiveNodes;
/*  62 */   private int nonScalarAliasesCount = 0;
/*     */   
/*     */   private final LoaderOptions loadingConfig;
/*     */   private final CommentEventsCollector blockCommentsCollector;
/*     */   private final CommentEventsCollector inlineCommentsCollector;
/*  67 */   private int nestingDepth = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int nestingDepthLimit;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Composer(Parser parser, Resolver resolver, LoaderOptions loadingConfig) {
/*  78 */     if (parser == null) {
/*  79 */       throw new NullPointerException("Parser must be provided");
/*     */     }
/*  81 */     if (resolver == null) {
/*  82 */       throw new NullPointerException("Resolver must be provided");
/*     */     }
/*  84 */     if (loadingConfig == null) {
/*  85 */       throw new NullPointerException("LoaderOptions must be provided");
/*     */     }
/*  87 */     this.parser = parser;
/*  88 */     this.resolver = resolver;
/*  89 */     this.anchors = new HashMap<>();
/*  90 */     this.recursiveNodes = new HashSet<>();
/*  91 */     this.loadingConfig = loadingConfig;
/*  92 */     this.blockCommentsCollector = new CommentEventsCollector(parser, new CommentType[] { CommentType.BLANK_LINE, CommentType.BLOCK });
/*     */     
/*  94 */     this.inlineCommentsCollector = new CommentEventsCollector(parser, new CommentType[] { CommentType.IN_LINE });
/*  95 */     this.nestingDepthLimit = loadingConfig.getNestingDepthLimit();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean checkNode() {
/* 105 */     if (this.parser.checkEvent(Event.ID.StreamStart)) {
/* 106 */       this.parser.getEvent();
/*     */     }
/*     */     
/* 109 */     return !this.parser.checkEvent(Event.ID.StreamEnd);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Node getNode() {
/* 119 */     this.blockCommentsCollector.collectEvents();
/* 120 */     if (this.parser.checkEvent(Event.ID.StreamEnd)) {
/* 121 */       List<CommentLine> commentLines = this.blockCommentsCollector.consume();
/* 122 */       Mark startMark = ((CommentLine)commentLines.get(0)).getStartMark();
/* 123 */       List<NodeTuple> children = Collections.emptyList();
/* 124 */       MappingNode mappingNode = new MappingNode(Tag.COMMENT, false, children, startMark, null, DumperOptions.FlowStyle.BLOCK);
/* 125 */       mappingNode.setBlockComments(commentLines);
/* 126 */       return (Node)mappingNode;
/*     */     } 
/*     */     
/* 129 */     this.parser.getEvent();
/*     */     
/* 131 */     Node node = composeNode(null);
/*     */     
/* 133 */     this.blockCommentsCollector.collectEvents();
/* 134 */     if (!this.blockCommentsCollector.isEmpty()) {
/* 135 */       node.setEndComments(this.blockCommentsCollector.consume());
/*     */     }
/* 137 */     this.parser.getEvent();
/* 138 */     this.anchors.clear();
/* 139 */     this.recursiveNodes.clear();
/* 140 */     return node;
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
/*     */   public Node getSingleNode() {
/* 153 */     this.parser.getEvent();
/*     */     
/* 155 */     Node document = null;
/* 156 */     if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
/* 157 */       document = getNode();
/*     */     }
/*     */     
/* 160 */     if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
/* 161 */       Event event = this.parser.getEvent();
/* 162 */       Mark contextMark = (document != null) ? document.getStartMark() : null;
/* 163 */       throw new ComposerException("expected a single document in the stream", contextMark, "but found another document", event
/* 164 */           .getStartMark());
/*     */     } 
/*     */     
/* 167 */     this.parser.getEvent();
/* 168 */     return document;
/*     */   }
/*     */   private Node composeNode(Node parent) {
/*     */     Node node;
/* 172 */     this.blockCommentsCollector.collectEvents();
/* 173 */     if (parent != null) {
/* 174 */       this.recursiveNodes.add(parent);
/*     */     }
/*     */     
/* 177 */     if (this.parser.checkEvent(Event.ID.Alias)) {
/* 178 */       AliasEvent event = (AliasEvent)this.parser.getEvent();
/* 179 */       String anchor = event.getAnchor();
/* 180 */       if (!this.anchors.containsKey(anchor)) {
/* 181 */         throw new ComposerException(null, null, "found undefined alias " + anchor, event
/* 182 */             .getStartMark());
/*     */       }
/* 184 */       node = this.anchors.get(anchor);
/* 185 */       if (!(node instanceof ScalarNode)) {
/* 186 */         this.nonScalarAliasesCount++;
/* 187 */         if (this.nonScalarAliasesCount > this.loadingConfig.getMaxAliasesForCollections()) {
/* 188 */           throw new YAMLException("Number of aliases for non-scalar nodes exceeds the specified max=" + this.loadingConfig
/*     */               
/* 190 */               .getMaxAliasesForCollections());
/*     */         }
/*     */       } 
/* 193 */       if (this.recursiveNodes.remove(node)) {
/* 194 */         node.setTwoStepsConstruction(true);
/*     */       }
/*     */       
/* 197 */       this.blockCommentsCollector.consume();
/* 198 */       this.inlineCommentsCollector.collectEvents().consume();
/*     */     } else {
/* 200 */       NodeEvent event = (NodeEvent)this.parser.peekEvent();
/* 201 */       String anchor = event.getAnchor();
/* 202 */       increaseNestingDepth();
/*     */       
/* 204 */       if (this.parser.checkEvent(Event.ID.Scalar)) {
/* 205 */         node = composeScalarNode(anchor, this.blockCommentsCollector.consume());
/* 206 */       } else if (this.parser.checkEvent(Event.ID.SequenceStart)) {
/* 207 */         node = composeSequenceNode(anchor);
/*     */       } else {
/* 209 */         node = composeMappingNode(anchor);
/*     */       } 
/* 211 */       decreaseNestingDepth();
/*     */     } 
/* 213 */     this.recursiveNodes.remove(parent);
/* 214 */     return node;
/*     */   }
/*     */   protected Node composeScalarNode(String anchor, List<CommentLine> blockComments) {
/*     */     Tag nodeTag;
/* 218 */     ScalarEvent ev = (ScalarEvent)this.parser.getEvent();
/* 219 */     String tag = ev.getTag();
/* 220 */     boolean resolved = false;
/*     */     
/* 222 */     if (tag == null || tag.equals("!")) {
/* 223 */       nodeTag = this.resolver.resolve(NodeId.scalar, ev.getValue(), ev
/* 224 */           .getImplicit().canOmitTagInPlainScalar());
/* 225 */       resolved = true;
/*     */     } else {
/* 227 */       nodeTag = new Tag(tag);
/* 228 */       if (nodeTag.isCustomGlobal() && 
/* 229 */         !this.loadingConfig.getTagInspector().isGlobalTagAllowed(nodeTag)) {
/* 230 */         throw new ComposerException(null, null, "Global tag is not allowed: " + tag, ev
/* 231 */             .getStartMark());
/*     */       }
/*     */     } 
/*     */     
/* 235 */     ScalarNode scalarNode = new ScalarNode(nodeTag, resolved, ev.getValue(), ev.getStartMark(), ev.getEndMark(), ev.getScalarStyle());
/* 236 */     if (anchor != null) {
/* 237 */       scalarNode.setAnchor(anchor);
/* 238 */       this.anchors.put(anchor, scalarNode);
/*     */     } 
/* 240 */     scalarNode.setBlockComments(blockComments);
/* 241 */     scalarNode.setInLineComments(this.inlineCommentsCollector.collectEvents().consume());
/* 242 */     return (Node)scalarNode;
/*     */   }
/*     */   protected Node composeSequenceNode(String anchor) {
/*     */     Tag nodeTag;
/* 246 */     SequenceStartEvent startEvent = (SequenceStartEvent)this.parser.getEvent();
/* 247 */     String tag = startEvent.getTag();
/*     */ 
/*     */     
/* 250 */     boolean resolved = false;
/* 251 */     if (tag == null || tag.equals("!")) {
/* 252 */       nodeTag = this.resolver.resolve(NodeId.sequence, null, startEvent.getImplicit());
/* 253 */       resolved = true;
/*     */     } else {
/* 255 */       nodeTag = new Tag(tag);
/* 256 */       if (nodeTag.isCustomGlobal() && 
/* 257 */         !this.loadingConfig.getTagInspector().isGlobalTagAllowed(nodeTag)) {
/* 258 */         throw new ComposerException(null, null, "Global tag is not allowed: " + tag, startEvent
/* 259 */             .getStartMark());
/*     */       }
/*     */     } 
/* 262 */     ArrayList<Node> children = new ArrayList<>();
/*     */     
/* 264 */     SequenceNode node = new SequenceNode(nodeTag, resolved, children, startEvent.getStartMark(), null, startEvent.getFlowStyle());
/* 265 */     if (startEvent.isFlow()) {
/* 266 */       node.setBlockComments(this.blockCommentsCollector.consume());
/*     */     }
/* 268 */     if (anchor != null) {
/* 269 */       node.setAnchor(anchor);
/* 270 */       this.anchors.put(anchor, node);
/*     */     } 
/* 272 */     while (!this.parser.checkEvent(Event.ID.SequenceEnd)) {
/* 273 */       this.blockCommentsCollector.collectEvents();
/* 274 */       if (this.parser.checkEvent(Event.ID.SequenceEnd)) {
/*     */         break;
/*     */       }
/* 277 */       children.add(composeNode((Node)node));
/*     */     } 
/* 279 */     if (startEvent.isFlow()) {
/* 280 */       node.setInLineComments(this.inlineCommentsCollector.collectEvents().consume());
/*     */     }
/* 282 */     Event endEvent = this.parser.getEvent();
/* 283 */     node.setEndMark(endEvent.getEndMark());
/* 284 */     this.inlineCommentsCollector.collectEvents();
/* 285 */     if (!this.inlineCommentsCollector.isEmpty()) {
/* 286 */       node.setInLineComments(this.inlineCommentsCollector.consume());
/*     */     }
/* 288 */     return (Node)node;
/*     */   }
/*     */   protected Node composeMappingNode(String anchor) {
/*     */     Tag nodeTag;
/* 292 */     MappingStartEvent startEvent = (MappingStartEvent)this.parser.getEvent();
/* 293 */     String tag = startEvent.getTag();
/*     */     
/* 295 */     boolean resolved = false;
/* 296 */     if (tag == null || tag.equals("!")) {
/* 297 */       nodeTag = this.resolver.resolve(NodeId.mapping, null, startEvent.getImplicit());
/* 298 */       resolved = true;
/*     */     } else {
/* 300 */       nodeTag = new Tag(tag);
/* 301 */       if (nodeTag.isCustomGlobal() && 
/* 302 */         !this.loadingConfig.getTagInspector().isGlobalTagAllowed(nodeTag)) {
/* 303 */         throw new ComposerException(null, null, "Global tag is not allowed: " + tag, startEvent
/* 304 */             .getStartMark());
/*     */       }
/*     */     } 
/*     */     
/* 308 */     List<NodeTuple> children = new ArrayList<>();
/*     */     
/* 310 */     MappingNode node = new MappingNode(nodeTag, resolved, children, startEvent.getStartMark(), null, startEvent.getFlowStyle());
/* 311 */     if (startEvent.isFlow()) {
/* 312 */       node.setBlockComments(this.blockCommentsCollector.consume());
/*     */     }
/* 314 */     if (anchor != null) {
/* 315 */       node.setAnchor(anchor);
/* 316 */       this.anchors.put(anchor, node);
/*     */     } 
/* 318 */     while (!this.parser.checkEvent(Event.ID.MappingEnd)) {
/* 319 */       this.blockCommentsCollector.collectEvents();
/* 320 */       if (this.parser.checkEvent(Event.ID.MappingEnd)) {
/*     */         break;
/*     */       }
/* 323 */       composeMappingChildren(children, node);
/*     */     } 
/* 325 */     if (startEvent.isFlow()) {
/* 326 */       node.setInLineComments(this.inlineCommentsCollector.collectEvents().consume());
/*     */     }
/* 328 */     Event endEvent = this.parser.getEvent();
/* 329 */     node.setEndMark(endEvent.getEndMark());
/* 330 */     this.inlineCommentsCollector.collectEvents();
/* 331 */     if (!this.inlineCommentsCollector.isEmpty()) {
/* 332 */       node.setInLineComments(this.inlineCommentsCollector.consume());
/*     */     }
/* 334 */     return (Node)node;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void composeMappingChildren(List<NodeTuple> children, MappingNode node) {
/* 344 */     Node itemKey = composeKeyNode(node);
/* 345 */     if (itemKey.getTag().equals(Tag.MERGE)) {
/* 346 */       node.setMerged(true);
/*     */     }
/* 348 */     Node itemValue = composeValueNode(node);
/* 349 */     children.add(new NodeTuple(itemKey, itemValue));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Node composeKeyNode(MappingNode node) {
/* 359 */     return composeNode((Node)node);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Node composeValueNode(MappingNode node) {
/* 369 */     return composeNode((Node)node);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void increaseNestingDepth() {
/* 376 */     if (this.nestingDepth > this.nestingDepthLimit) {
/* 377 */       throw new YAMLException("Nesting Depth exceeded max " + this.nestingDepthLimit);
/*     */     }
/* 379 */     this.nestingDepth++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void decreaseNestingDepth() {
/* 386 */     if (this.nestingDepth > 0) {
/* 387 */       this.nestingDepth--;
/*     */     } else {
/* 389 */       throw new YAMLException("Nesting Depth cannot be negative");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\composer\Composer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */