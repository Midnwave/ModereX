/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.body.DialogBody;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.input.Input;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTByte;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtDecoder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtEncoder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import java.util.List;
/*     */ import org.jspecify.annotations.NullMarked;
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
/*     */ @NullMarked
/*     */ public final class CommonDialogData
/*     */ {
/*     */   private final Component title;
/*     */   private final Component externalTitle;
/*     */   private final boolean canCloseWithEscape;
/*     */   private final boolean pause;
/*     */   private final DialogAction afterAction;
/*     */   private final List<DialogBody> body;
/*     */   private final List<Input> inputs;
/*     */   
/*     */   public CommonDialogData(Component title, Component externalTitle, boolean canCloseWithEscape, boolean pause, DialogAction afterAction, List<DialogBody> body, List<Input> inputs) {
/*  48 */     if (pause && !afterAction.isWillUnpause()) {
/*  49 */       throw new IllegalArgumentException("Dialogs that pause the game must use after_action values that unpause it after user action!");
/*     */     }
/*     */     
/*  52 */     this.title = title;
/*  53 */     this.externalTitle = externalTitle;
/*  54 */     this.canCloseWithEscape = canCloseWithEscape;
/*  55 */     this.pause = pause;
/*  56 */     this.afterAction = afterAction;
/*  57 */     this.body = body;
/*  58 */     this.inputs = inputs;
/*     */   }
/*     */   
/*     */   public static CommonDialogData decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/*  62 */     Component title = (Component)compound.getOrThrow("title", (NbtDecoder)AdventureSerializer.serializer(wrapper), wrapper);
/*  63 */     Component externalTitle = (Component)compound.getOrNull("external_title", (NbtDecoder)AdventureSerializer.serializer(wrapper), wrapper);
/*  64 */     boolean canCloseWithEscape = compound.getBooleanOr("can_close_with_escape", true);
/*  65 */     boolean pause = compound.getBooleanOr("pause", true);
/*  66 */     DialogAction afterAction = (DialogAction)compound.getOr("after_action", DialogAction::decode, DialogAction.CLOSE, wrapper);
/*  67 */     List<DialogBody> body = compound.getListOrEmpty("body", DialogBody::decode, wrapper);
/*  68 */     List<Input> inputs = compound.getListOrEmpty("inputs", Input::decode, wrapper);
/*  69 */     return new CommonDialogData(title, externalTitle, canCloseWithEscape, pause, afterAction, body, inputs);
/*     */   }
/*     */   
/*     */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, CommonDialogData data) {
/*  73 */     compound.set("title", data.title, (NbtEncoder)AdventureSerializer.serializer(wrapper), wrapper);
/*  74 */     if (data.externalTitle != null) {
/*  75 */       compound.set("external_title", data.externalTitle, (NbtEncoder)AdventureSerializer.serializer(wrapper), wrapper);
/*     */     }
/*  77 */     if (!data.canCloseWithEscape) {
/*  78 */       compound.setTag("can_close_with_escape", (NBT)new NBTByte(false));
/*     */     }
/*  80 */     if (!data.pause) {
/*  81 */       compound.setTag("pause", (NBT)new NBTByte(false));
/*     */     }
/*  83 */     if (data.afterAction != DialogAction.CLOSE) {
/*  84 */       compound.set("after_action", data.afterAction, DialogAction::encode, wrapper);
/*     */     }
/*  86 */     if (!data.body.isEmpty()) {
/*  87 */       compound.setCompactList("body", data.body, DialogBody::encode, wrapper);
/*     */     }
/*  89 */     if (!data.inputs.isEmpty()) {
/*  90 */       compound.setList("inputs", data.inputs, Input::encode, wrapper);
/*     */     }
/*     */   }
/*     */   
/*     */   public Component getTitle() {
/*  95 */     return this.title;
/*     */   }
/*     */   
/*     */   public Component getExternalTitle() {
/*  99 */     return this.externalTitle;
/*     */   }
/*     */   
/*     */   public boolean isCanCloseWithEscape() {
/* 103 */     return this.canCloseWithEscape;
/*     */   }
/*     */   
/*     */   public boolean isPause() {
/* 107 */     return this.pause;
/*     */   }
/*     */   
/*     */   public DialogAction getAfterAction() {
/* 111 */     return this.afterAction;
/*     */   }
/*     */   
/*     */   public List<DialogBody> getBody() {
/* 115 */     return this.body;
/*     */   }
/*     */   
/*     */   public List<Input> getInputs() {
/* 119 */     return this.inputs;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\CommonDialogData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */