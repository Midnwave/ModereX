/*     */ package ac.grim.grimac.shaded.snakeyaml;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.composer.Composer;
/*     */ import ac.grim.grimac.shaded.snakeyaml.constructor.BaseConstructor;
/*     */ import ac.grim.grimac.shaded.snakeyaml.constructor.Constructor;
/*     */ import ac.grim.grimac.shaded.snakeyaml.emitter.Emitable;
/*     */ import ac.grim.grimac.shaded.snakeyaml.emitter.Emitter;
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.Event;
/*     */ import ac.grim.grimac.shaded.snakeyaml.introspector.BeanAccess;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Node;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Tag;
/*     */ import ac.grim.grimac.shaded.snakeyaml.parser.Parser;
/*     */ import ac.grim.grimac.shaded.snakeyaml.parser.ParserImpl;
/*     */ import ac.grim.grimac.shaded.snakeyaml.reader.StreamReader;
/*     */ import ac.grim.grimac.shaded.snakeyaml.reader.UnicodeReader;
/*     */ import ac.grim.grimac.shaded.snakeyaml.representer.Representer;
/*     */ import ac.grim.grimac.shaded.snakeyaml.resolver.Resolver;
/*     */ import ac.grim.grimac.shaded.snakeyaml.serializer.Serializer;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Yaml
/*     */ {
/*     */   protected final Resolver resolver;
/*     */   private String name;
/*     */   protected BaseConstructor constructor;
/*     */   protected Representer representer;
/*     */   protected DumperOptions dumperOptions;
/*     */   protected LoaderOptions loadingConfig;
/*     */   
/*     */   public Yaml() {
/*  64 */     this((BaseConstructor)new Constructor(new LoaderOptions()), new Representer(new DumperOptions()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Yaml(DumperOptions dumperOptions) {
/*  73 */     this((BaseConstructor)new Constructor(new LoaderOptions()), new Representer(dumperOptions), dumperOptions);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Yaml(LoaderOptions loadingConfig) {
/*  82 */     this((BaseConstructor)new Constructor(loadingConfig), new Representer(new DumperOptions()), new DumperOptions(), loadingConfig);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Yaml(Representer representer) {
/*  92 */     this((BaseConstructor)new Constructor(new LoaderOptions()), representer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Yaml(BaseConstructor constructor) {
/* 101 */     this(constructor, new Representer(new DumperOptions()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Yaml(BaseConstructor constructor, Representer representer) {
/* 111 */     this(constructor, representer, initDumperOptions(representer));
/*     */   }
/*     */   
/*     */   private static DumperOptions initDumperOptions(Representer representer) {
/* 115 */     DumperOptions dumperOptions = new DumperOptions();
/* 116 */     dumperOptions.setDefaultFlowStyle(representer.getDefaultFlowStyle());
/* 117 */     dumperOptions.setDefaultScalarStyle(representer.getDefaultScalarStyle());
/* 118 */     dumperOptions
/* 119 */       .setAllowReadOnlyProperties(representer.getPropertyUtils().isAllowReadOnlyProperties());
/* 120 */     dumperOptions.setTimeZone(representer.getTimeZone());
/* 121 */     return dumperOptions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Yaml(Representer representer, DumperOptions dumperOptions) {
/* 131 */     this((BaseConstructor)new Constructor(new LoaderOptions()), representer, dumperOptions);
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
/*     */   public Yaml(BaseConstructor constructor, Representer representer, DumperOptions dumperOptions) {
/* 143 */     this(constructor, representer, dumperOptions, constructor.getLoadingConfig(), new Resolver());
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
/*     */   public Yaml(BaseConstructor constructor, Representer representer, DumperOptions dumperOptions, LoaderOptions loadingConfig) {
/* 156 */     this(constructor, representer, dumperOptions, loadingConfig, new Resolver());
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
/*     */   public Yaml(BaseConstructor constructor, Representer representer, DumperOptions dumperOptions, Resolver resolver) {
/* 169 */     this(constructor, representer, dumperOptions, new LoaderOptions(), resolver);
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
/*     */   public Yaml(BaseConstructor constructor, Representer representer, DumperOptions dumperOptions, LoaderOptions loadingConfig, Resolver resolver) {
/* 183 */     if (constructor == null) {
/* 184 */       throw new NullPointerException("Constructor must be provided");
/*     */     }
/* 186 */     if (representer == null) {
/* 187 */       throw new NullPointerException("Representer must be provided");
/*     */     }
/* 189 */     if (dumperOptions == null) {
/* 190 */       throw new NullPointerException("DumperOptions must be provided");
/*     */     }
/* 192 */     if (loadingConfig == null) {
/* 193 */       throw new NullPointerException("LoaderOptions must be provided");
/*     */     }
/* 195 */     if (resolver == null) {
/* 196 */       throw new NullPointerException("Resolver must be provided");
/*     */     }
/* 198 */     if (!constructor.isExplicitPropertyUtils()) {
/* 199 */       constructor.setPropertyUtils(representer.getPropertyUtils());
/* 200 */     } else if (!representer.isExplicitPropertyUtils()) {
/* 201 */       representer.setPropertyUtils(constructor.getPropertyUtils());
/*     */     } 
/* 203 */     this.constructor = constructor;
/* 204 */     this.constructor.setAllowDuplicateKeys(loadingConfig.isAllowDuplicateKeys());
/* 205 */     this.constructor.setWrappedToRootException(loadingConfig.isWrappedToRootException());
/* 206 */     if (!dumperOptions.getIndentWithIndicator() && dumperOptions
/* 207 */       .getIndent() <= dumperOptions.getIndicatorIndent()) {
/* 208 */       throw new YAMLException("Indicator indent must be smaller then indent.");
/*     */     }
/* 210 */     representer.setDefaultFlowStyle(dumperOptions.getDefaultFlowStyle());
/* 211 */     representer.setDefaultScalarStyle(dumperOptions.getDefaultScalarStyle());
/* 212 */     representer.getPropertyUtils()
/* 213 */       .setAllowReadOnlyProperties(dumperOptions.isAllowReadOnlyProperties());
/* 214 */     representer.setTimeZone(dumperOptions.getTimeZone());
/* 215 */     this.representer = representer;
/* 216 */     this.dumperOptions = dumperOptions;
/* 217 */     this.loadingConfig = loadingConfig;
/* 218 */     this.resolver = resolver;
/* 219 */     this.name = "Yaml:" + System.identityHashCode(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String dump(Object data) {
/* 229 */     List<Object> list = new ArrayList(1);
/* 230 */     list.add(data);
/* 231 */     return dumpAll(list.iterator());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Node represent(Object data) {
/* 242 */     return this.representer.represent(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String dumpAll(Iterator<? extends Object> data) {
/* 252 */     StringWriter buffer = new StringWriter();
/* 253 */     dumpAll(data, buffer, null);
/* 254 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void dump(Object data, Writer output) {
/* 264 */     List<Object> list = new ArrayList(1);
/* 265 */     list.add(data);
/* 266 */     dumpAll(list.iterator(), output, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void dumpAll(Iterator<? extends Object> data, Writer output) {
/* 276 */     dumpAll(data, output, null);
/*     */   }
/*     */   
/*     */   private void dumpAll(Iterator<? extends Object> data, Writer output, Tag rootTag) {
/* 280 */     Serializer serializer = new Serializer((Emitable)new Emitter(output, this.dumperOptions), this.resolver, this.dumperOptions, rootTag);
/*     */     
/*     */     try {
/* 283 */       serializer.open();
/* 284 */       while (data.hasNext()) {
/* 285 */         Node node = this.representer.represent(data.next());
/* 286 */         serializer.serialize(node);
/*     */       } 
/* 288 */       serializer.close();
/* 289 */     } catch (IOException e) {
/* 290 */       throw new YAMLException(e);
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
/*     */ 
/*     */   
/*     */   public String dumpAs(Object data, Tag rootTag, DumperOptions.FlowStyle flowStyle) {
/* 327 */     DumperOptions.FlowStyle oldStyle = this.representer.getDefaultFlowStyle();
/* 328 */     if (flowStyle != null) {
/* 329 */       this.representer.setDefaultFlowStyle(flowStyle);
/*     */     }
/* 331 */     List<Object> list = new ArrayList(1);
/* 332 */     list.add(data);
/* 333 */     StringWriter buffer = new StringWriter();
/* 334 */     dumpAll(list.iterator(), buffer, rootTag);
/* 335 */     this.representer.setDefaultFlowStyle(oldStyle);
/* 336 */     return buffer.toString();
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
/*     */   public String dumpAsMap(Object data) {
/* 357 */     return dumpAs(data, Tag.MAP, DumperOptions.FlowStyle.BLOCK);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void serialize(Node node, Writer output) {
/* 367 */     Serializer serializer = new Serializer((Emitable)new Emitter(output, this.dumperOptions), this.resolver, this.dumperOptions, null);
/*     */     
/*     */     try {
/* 370 */       serializer.open();
/* 371 */       serializer.serialize(node);
/* 372 */       serializer.close();
/* 373 */     } catch (IOException e) {
/* 374 */       throw new YAMLException(e);
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
/*     */   public List<Event> serialize(Node data) {
/* 386 */     SilentEmitter emitter = new SilentEmitter();
/* 387 */     Serializer serializer = new Serializer(emitter, this.resolver, this.dumperOptions, null);
/*     */     try {
/* 389 */       serializer.open();
/* 390 */       serializer.serialize(data);
/* 391 */       serializer.close();
/* 392 */     } catch (IOException e) {
/* 393 */       throw new YAMLException(e);
/*     */     } 
/* 395 */     return emitter.getEvents();
/*     */   }
/*     */   
/*     */   private static class SilentEmitter
/*     */     implements Emitable {
/* 400 */     private final List<Event> events = new ArrayList<>(100);
/*     */     
/*     */     public List<Event> getEvents() {
/* 403 */       return this.events;
/*     */     }
/*     */ 
/*     */     
/*     */     public void emit(Event event) throws IOException {
/* 408 */       this.events.add(event);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private SilentEmitter() {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T load(String yaml) {
/* 422 */     return (T)loadFromReader(new StreamReader(yaml), Object.class);
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
/*     */   public <T> T load(InputStream io) {
/* 434 */     return (T)loadFromReader(new StreamReader((Reader)new UnicodeReader(io)), Object.class);
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
/*     */   public <T> T load(Reader io) {
/* 446 */     return (T)loadFromReader(new StreamReader(io), Object.class);
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
/*     */   public <T> T loadAs(Reader io, Class<? super T> type) {
/* 459 */     return (T)loadFromReader(new StreamReader(io), type);
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
/*     */   public <T> T loadAs(String yaml, Class<? super T> type) {
/* 473 */     return (T)loadFromReader(new StreamReader(yaml), type);
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
/*     */   public <T> T loadAs(InputStream input, Class<? super T> type) {
/* 486 */     return (T)loadFromReader(new StreamReader((Reader)new UnicodeReader(input)), type);
/*     */   }
/*     */   
/*     */   private Object loadFromReader(StreamReader sreader, Class<?> type) {
/* 490 */     Composer composer = new Composer((Parser)new ParserImpl(sreader, this.loadingConfig), this.resolver, this.loadingConfig);
/*     */     
/* 492 */     this.constructor.setComposer(composer);
/* 493 */     return this.constructor.getSingleData(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<Object> loadAll(Reader yaml) {
/* 504 */     Composer composer = new Composer((Parser)new ParserImpl(new StreamReader(yaml), this.loadingConfig), this.resolver, this.loadingConfig);
/*     */     
/* 506 */     this.constructor.setComposer(composer);
/* 507 */     Iterator<Object> result = new Iterator()
/*     */       {
/*     */         public boolean hasNext() {
/* 510 */           return Yaml.this.constructor.checkData();
/*     */         }
/*     */ 
/*     */         
/*     */         public Object next() {
/* 515 */           return Yaml.this.constructor.getData();
/*     */         }
/*     */ 
/*     */         
/*     */         public void remove() {
/* 520 */           throw new UnsupportedOperationException();
/*     */         }
/*     */       };
/* 523 */     return new YamlIterable(result);
/*     */   }
/*     */   
/*     */   private static class YamlIterable
/*     */     implements Iterable<Object> {
/*     */     private final Iterator<Object> iterator;
/*     */     
/*     */     public YamlIterable(Iterator<Object> iterator) {
/* 531 */       this.iterator = iterator;
/*     */     }
/*     */ 
/*     */     
/*     */     public Iterator<Object> iterator() {
/* 536 */       return this.iterator;
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
/*     */   public Iterable<Object> loadAll(String yaml) {
/* 549 */     return loadAll(new StringReader(yaml));
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
/*     */   public Iterable<Object> loadAll(InputStream yaml) {
/* 561 */     return loadAll((Reader)new UnicodeReader(yaml));
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
/*     */   public Node compose(Reader yaml) {
/* 573 */     Composer composer = new Composer((Parser)new ParserImpl(new StreamReader(yaml), this.loadingConfig), this.resolver, this.loadingConfig);
/*     */     
/* 575 */     return composer.getSingleNode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<Node> composeAll(Reader yaml) {
/* 586 */     final Composer composer = new Composer((Parser)new ParserImpl(new StreamReader(yaml), this.loadingConfig), this.resolver, this.loadingConfig);
/*     */     
/* 588 */     Iterator<Node> result = new Iterator<Node>()
/*     */       {
/*     */         public boolean hasNext() {
/* 591 */           return composer.checkNode();
/*     */         }
/*     */ 
/*     */         
/*     */         public Node next() {
/* 596 */           Node node = composer.getNode();
/* 597 */           if (node != null) {
/* 598 */             return node;
/*     */           }
/* 600 */           throw new NoSuchElementException("No Node is available.");
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*     */         public void remove() {
/* 606 */           throw new UnsupportedOperationException();
/*     */         }
/*     */       };
/* 609 */     return new NodeIterable(result);
/*     */   }
/*     */   
/*     */   private static class NodeIterable
/*     */     implements Iterable<Node> {
/*     */     private final Iterator<Node> iterator;
/*     */     
/*     */     public NodeIterable(Iterator<Node> iterator) {
/* 617 */       this.iterator = iterator;
/*     */     }
/*     */ 
/*     */     
/*     */     public Iterator<Node> iterator() {
/* 622 */       return this.iterator;
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
/*     */   public void addImplicitResolver(Tag tag, Pattern regexp, String first) {
/* 635 */     this.resolver.addImplicitResolver(tag, regexp, first);
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
/*     */   public void addImplicitResolver(Tag tag, Pattern regexp, String first, int limit) {
/* 648 */     this.resolver.addImplicitResolver(tag, regexp, first, limit);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 653 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 663 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setName(String name) {
/* 672 */     this.name = name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<Event> parse(Reader yaml) {
/* 683 */     final ParserImpl parser = new ParserImpl(new StreamReader(yaml), this.loadingConfig);
/* 684 */     Iterator<Event> result = new Iterator<Event>()
/*     */       {
/*     */         public boolean hasNext() {
/* 687 */           return (parser.peekEvent() != null);
/*     */         }
/*     */ 
/*     */         
/*     */         public Event next() {
/* 692 */           Event event = parser.getEvent();
/* 693 */           if (event != null) {
/* 694 */             return event;
/*     */           }
/* 696 */           throw new NoSuchElementException("No Event is available.");
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*     */         public void remove() {
/* 702 */           throw new UnsupportedOperationException();
/*     */         }
/*     */       };
/* 705 */     return new EventIterable(result);
/*     */   }
/*     */   
/*     */   private static class EventIterable
/*     */     implements Iterable<Event> {
/*     */     private final Iterator<Event> iterator;
/*     */     
/*     */     public EventIterable(Iterator<Event> iterator) {
/* 713 */       this.iterator = iterator;
/*     */     }
/*     */ 
/*     */     
/*     */     public Iterator<Event> iterator() {
/* 718 */       return this.iterator;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBeanAccess(BeanAccess beanAccess) {
/* 723 */     this.constructor.getPropertyUtils().setBeanAccess(beanAccess);
/* 724 */     this.representer.getPropertyUtils().setBeanAccess(beanAccess);
/*     */   }
/*     */   
/*     */   public void addTypeDescription(TypeDescription td) {
/* 728 */     this.constructor.addTypeDescription(td);
/* 729 */     this.representer.addTypeDescription(td);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\Yaml.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */