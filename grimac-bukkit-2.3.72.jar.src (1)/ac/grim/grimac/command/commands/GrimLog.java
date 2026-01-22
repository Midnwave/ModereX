/*    */ package ac.grim.grimac.command.commands;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.command.BuildableCommand;
/*    */ import ac.grim.grimac.manager.init.start.SuperDebug;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.IntegerParser;
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*    */ import ac.grim.grimac.utils.common.GrimArguments;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.net.HttpURLConnection;
/*    */ import java.net.URL;
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.util.function.Consumer;
/*    */ 
/*    */ public class GrimLog
/*    */   implements BuildableCommand
/*    */ {
/*    */   public static void sendLogAsync(Sender sender, String log, Consumer<String> consumer, String type) {
/* 25 */     String success = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("upload-log", "%prefix% &fUploaded debug to: %url%");
/* 26 */     String failure = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("upload-log-upload-failure", "%prefix% &cSomething went wrong while uploading this log, see console for more information.");
/* 27 */     String uploading = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("upload-log-start", "%prefix% &fUploading log... please wait");
/* 28 */     uploading = MessageUtil.replacePlaceholders(sender, uploading);
/* 29 */     sender.sendMessage(MessageUtil.miniMessage(uploading));
/* 30 */     GrimAPI.INSTANCE.getScheduler().getAsyncScheduler().runNow(GrimAPI.INSTANCE.getGrimPlugin(), () -> {
/*    */           try {
/*    */             sendLog(sender, log, success, failure, consumer, type);
/* 33 */           } catch (Exception e) {
/*    */             String message = MessageUtil.replacePlaceholders(sender, failure);
/*    */             sender.sendMessage(MessageUtil.miniMessage(message));
/*    */             LogUtil.error("Failed to send log", e);
/*    */           } 
/*    */         });
/*    */   }
/*    */   
/*    */   private static void sendLog(Sender sender, String log, String success, String failure, Consumer<String> consumer, String type) throws IOException {
/* 42 */     URL mUrl = new URL(GrimArguments.PASTE_URL + "data/post");
/* 43 */     HttpURLConnection urlConn = (HttpURLConnection)mUrl.openConnection();
/*    */     try {
/* 45 */       urlConn.setDoOutput(true);
/* 46 */       urlConn.setRequestMethod("POST");
/* 47 */       urlConn.addRequestProperty("User-Agent", "GrimAC/" + GrimAPI.INSTANCE.getExternalAPI().getGrimVersion());
/* 48 */       urlConn.addRequestProperty("Content-Type", type);
/* 49 */       urlConn.setRequestProperty("Content-Length", Integer.toString(log.length()));
/* 50 */       OutputStream stream = urlConn.getOutputStream(); 
/* 51 */       try { stream.write(log.getBytes(StandardCharsets.UTF_8));
/* 52 */         if (stream != null) stream.close();  } catch (Throwable throwable) { if (stream != null)
/* 53 */           try { stream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  int response = urlConn.getResponseCode();
/* 54 */       if (response == 201) {
/* 55 */         String responseURL = urlConn.getHeaderField("Location");
/* 56 */         String message = success.replace("%url%", GrimArguments.PASTE_URL + GrimArguments.PASTE_URL);
/* 57 */         consumer.accept(message);
/* 58 */         message = MessageUtil.replacePlaceholders(sender, message);
/* 59 */         sender.sendMessage(MessageUtil.miniMessage(message));
/*    */       } else {
/* 61 */         String message = MessageUtil.replacePlaceholders(sender, failure);
/* 62 */         sender.sendMessage(MessageUtil.miniMessage(message));
/* 63 */         LogUtil.error("Returned response code " + response + ": " + urlConn.getResponseMessage());
/*    */       } 
/*    */     } finally {
/* 66 */       urlConn.disconnect();
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void register(CommandManager<Sender> commandManager) {
/* 78 */     Command<Sender> command = commandManager.commandBuilder("grim", new String[] { "grimac" }).literal("log", new String[] { "logs" }).permission("grim.log").required("flagId", IntegerParser.integerParser()).handler(this::handleLog).manager(commandManager).build();
/* 79 */     commandManager
/* 80 */       .command(command)
/* 81 */       .command(commandManager.commandBuilder("gl", new String[0]).proxies(command));
/*    */   }
/*    */   
/*    */   private void handleLog(CommandContext<Sender> context) {
/* 85 */     Sender sender = (Sender)context.sender();
/* 86 */     int flagId = ((Integer)context.get("flagId")).intValue();
/*    */     
/* 88 */     StringBuilder builder = SuperDebug.getFlag(flagId);
/* 89 */     if (builder == null) {
/* 90 */       sender.sendMessage(MessageUtil.getParsedComponent(sender, "upload-log-not-found", "%prefix% &cUnable to find that log"));
/*    */       return;
/*    */     } 
/* 93 */     sendLogAsync(sender, builder.toString(), string -> {  }"text/yaml");
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\commands\GrimLog.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */