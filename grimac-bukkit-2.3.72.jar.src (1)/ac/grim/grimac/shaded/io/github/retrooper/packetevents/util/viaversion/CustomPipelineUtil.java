/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.viaversion;
/*     */ 
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.SpigotReflectionUtil;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CustomPipelineUtil
/*     */ {
/*     */   private static Method DECODE_METHOD;
/*     */   private static Method ENCODE_METHOD;
/*     */   private static Method MTM_DECODE;
/*     */   private static Method MTM_ENCODE;
/*     */   
/*     */   public static void init() {
/*  35 */     Class<?> channelHandlerContextClass = SpigotReflectionUtil.getNettyClass("channel.ChannelHandlerContext");
/*     */     try {
/*  37 */       DECODE_METHOD = SpigotReflectionUtil.BYTE_TO_MESSAGE_DECODER.getDeclaredMethod("decode", new Class[] { channelHandlerContextClass, SpigotReflectionUtil.BYTE_BUF_CLASS, List.class });
/*  38 */       DECODE_METHOD.setAccessible(true);
/*  39 */     } catch (NoSuchMethodException e) {
/*  40 */       e.printStackTrace();
/*     */     } 
/*     */     try {
/*  43 */       ENCODE_METHOD = SpigotReflectionUtil.MESSAGE_TO_BYTE_ENCODER.getDeclaredMethod("encode", new Class[] { channelHandlerContextClass, Object.class, SpigotReflectionUtil.BYTE_BUF_CLASS });
/*  44 */       ENCODE_METHOD.setAccessible(true);
/*  45 */     } catch (NoSuchMethodException e) {
/*  46 */       e.printStackTrace();
/*     */     } 
/*     */     
/*     */     try {
/*  50 */       Class<?> messageToMessageDecoderClass = SpigotReflectionUtil.getNettyClass("handler.codec.MessageToMessageDecoder");
/*  51 */       MTM_DECODE = messageToMessageDecoderClass.getDeclaredMethod("decode", new Class[] { channelHandlerContextClass, Object.class, List.class });
/*  52 */       MTM_DECODE.setAccessible(true);
/*  53 */     } catch (NoSuchMethodException e) {
/*  54 */       e.printStackTrace();
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/*  59 */       Class<?> messageToMessageEncoderClass = SpigotReflectionUtil.getNettyClass("handler.codec.MessageToMessageEncoder");
/*  60 */       MTM_ENCODE = messageToMessageEncoderClass.getDeclaredMethod("encode", new Class[] { channelHandlerContextClass, Object.class, List.class });
/*  61 */       MTM_ENCODE.setAccessible(true);
/*  62 */     } catch (NoSuchMethodException e) {
/*  63 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static List<Object> callDecode(Object decoder, Object ctx, Object input) throws InvocationTargetException {
/*  68 */     List<Object> output = new ArrayList();
/*     */     try {
/*  70 */       DECODE_METHOD.invoke(decoder, new Object[] { ctx, input, output });
/*  71 */     } catch (IllegalAccessException e) {
/*  72 */       e.printStackTrace();
/*     */     } 
/*  74 */     return output;
/*     */   }
/*     */   
/*     */   public static void callEncode(Object encoder, Object ctx, Object msg, Object output) throws InvocationTargetException {
/*     */     try {
/*  79 */       ENCODE_METHOD.invoke(encoder, new Object[] { ctx, msg, output });
/*  80 */     } catch (IllegalAccessException e) {
/*  81 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static List<Object> callMTMEncode(Object encoder, Object ctx, Object msg) {
/*  86 */     List<Object> output = new ArrayList();
/*     */     try {
/*  88 */       MTM_ENCODE.invoke(encoder, new Object[] { ctx, msg, output });
/*  89 */     } catch (IllegalAccessException|InvocationTargetException e) {
/*  90 */       e.printStackTrace();
/*     */     } 
/*  92 */     return output;
/*     */   }
/*     */   
/*     */   public static List<Object> callMTMDecode(Object decoder, Object ctx, Object msg) throws InvocationTargetException {
/*  96 */     List<Object> output = new ArrayList();
/*     */     try {
/*  98 */       MTM_DECODE.invoke(decoder, new Object[] { ctx, msg, output });
/*  99 */     } catch (IllegalAccessException e) {
/* 100 */       e.printStackTrace();
/*     */     } 
/* 102 */     return output;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\viaversion\CustomPipelineUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */