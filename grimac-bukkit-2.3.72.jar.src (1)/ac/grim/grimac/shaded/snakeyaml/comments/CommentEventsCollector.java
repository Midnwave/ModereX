/*     */ package ac.grim.grimac.shaded.snakeyaml.comments;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.CommentEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.Event;
/*     */ import ac.grim.grimac.shaded.snakeyaml.parser.Parser;
/*     */ import java.util.AbstractQueue;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CommentEventsCollector
/*     */ {
/*     */   private List<CommentLine> commentLineList;
/*     */   private final Queue<Event> eventSource;
/*     */   private final CommentType[] expectedCommentTypes;
/*     */   
/*     */   public CommentEventsCollector(final Parser parser, CommentType... expectedCommentTypes) {
/*  43 */     this.eventSource = new AbstractQueue<Event>()
/*     */       {
/*     */         public boolean offer(Event e)
/*     */         {
/*  47 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */         
/*     */         public Event poll() {
/*  52 */           return parser.getEvent();
/*     */         }
/*     */ 
/*     */         
/*     */         public Event peek() {
/*  57 */           return parser.peekEvent();
/*     */         }
/*     */ 
/*     */         
/*     */         public Iterator<Event> iterator() {
/*  62 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */         
/*     */         public int size() {
/*  67 */           throw new UnsupportedOperationException();
/*     */         }
/*     */       };
/*     */     
/*  71 */     this.expectedCommentTypes = expectedCommentTypes;
/*  72 */     this.commentLineList = new ArrayList<>();
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
/*     */   public CommentEventsCollector(Queue<Event> eventSource, CommentType... expectedCommentTypes) {
/*  84 */     this.eventSource = eventSource;
/*  85 */     this.expectedCommentTypes = expectedCommentTypes;
/*  86 */     this.commentLineList = new ArrayList<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isEventExpected(Event event) {
/*  96 */     if (event == null || !event.is(Event.ID.Comment)) {
/*  97 */       return false;
/*     */     }
/*  99 */     CommentEvent commentEvent = (CommentEvent)event;
/* 100 */     for (CommentType type : this.expectedCommentTypes) {
/* 101 */       if (commentEvent.getCommentType() == type) {
/* 102 */         return true;
/*     */       }
/*     */     } 
/* 105 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommentEventsCollector collectEvents() {
/* 116 */     collectEvents(null);
/* 117 */     return this;
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
/*     */   public Event collectEvents(Event event) {
/* 129 */     if (event != null) {
/* 130 */       if (isEventExpected(event)) {
/* 131 */         this.commentLineList.add(new CommentLine((CommentEvent)event));
/*     */       } else {
/* 133 */         return event;
/*     */       } 
/*     */     }
/* 136 */     while (isEventExpected(this.eventSource.peek())) {
/* 137 */       this.commentLineList.add(new CommentLine((CommentEvent)this.eventSource.poll()));
/*     */     }
/* 139 */     return null;
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
/*     */   public Event collectEventsAndPoll(Event event) {
/* 152 */     Event nextEvent = collectEvents(event);
/* 153 */     return (nextEvent != null) ? nextEvent : this.eventSource.poll();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<CommentLine> consume() {
/*     */     try {
/* 163 */       return this.commentLineList;
/*     */     } finally {
/* 165 */       this.commentLineList = new ArrayList<>();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 175 */     return this.commentLineList.isEmpty();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\comments\CommentEventsCollector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */