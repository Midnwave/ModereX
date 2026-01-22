/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.event;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.dialog.DialogLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.nbt.api.BinaryTagHolder;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Objects;
/*     */ import java.util.stream.Stream;
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
/*     */ abstract class PayloadImpl
/*     */   implements ClickEvent.Payload
/*     */ {
/*     */   public String toString() {
/*  38 */     return Internals.toString(this);
/*     */   }
/*     */   
/*     */   static final class TextImpl extends PayloadImpl implements ClickEvent.Payload.Text {
/*     */     private final String value;
/*     */     
/*     */     TextImpl(@NotNull String value) {
/*  45 */       this.value = value;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public String value() {
/*  50 */       return this.value;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Stream<? extends ExaminableProperty> examinableProperties() {
/*  55 */       return Stream.of(
/*  56 */           ExaminableProperty.of("value", this.value));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean equals(Object other) {
/*  62 */       if (this == other) return true; 
/*  63 */       if (other == null || getClass() != other.getClass()) return false; 
/*  64 */       TextImpl that = (TextImpl)other;
/*  65 */       return Objects.equals(this.value, that.value);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/*  70 */       return this.value.hashCode();
/*     */     }
/*     */   }
/*     */   
/*     */   static final class IntImpl extends PayloadImpl implements ClickEvent.Payload.Int {
/*     */     private final int integer;
/*     */     
/*     */     IntImpl(int integer) {
/*  78 */       this.integer = integer;
/*     */     }
/*     */ 
/*     */     
/*     */     public int integer() {
/*  83 */       return this.integer;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Stream<? extends ExaminableProperty> examinableProperties() {
/*  88 */       return Stream.of(
/*  89 */           ExaminableProperty.of("integer", this.integer));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean equals(Object other) {
/*  95 */       if (this == other) return true; 
/*  96 */       if (other == null || getClass() != other.getClass()) return false; 
/*  97 */       IntImpl that = (IntImpl)other;
/*  98 */       return Objects.equals(Integer.valueOf(this.integer), Integer.valueOf(that.integer));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 103 */       return this.integer;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class DialogImpl extends PayloadImpl implements ClickEvent.Payload.Dialog {
/*     */     private final DialogLike dialogLike;
/*     */     
/*     */     DialogImpl(@NotNull DialogLike dialogLike) {
/* 111 */       this.dialogLike = dialogLike;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public DialogLike dialog() {
/* 116 */       return this.dialogLike;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Stream<? extends ExaminableProperty> examinableProperties() {
/* 121 */       return Stream.of(
/* 122 */           ExaminableProperty.of("dialog", this.dialogLike));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean equals(Object other) {
/* 128 */       if (this == other) return true; 
/* 129 */       if (other == null || getClass() != other.getClass()) return false; 
/* 130 */       DialogImpl that = (DialogImpl)other;
/* 131 */       return Objects.equals(this.dialogLike, that.dialogLike);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 136 */       return this.dialogLike.hashCode();
/*     */     }
/*     */   }
/*     */   
/*     */   static final class CustomImpl extends PayloadImpl implements ClickEvent.Payload.Custom {
/*     */     private final Key key;
/*     */     private final BinaryTagHolder nbt;
/*     */     
/*     */     CustomImpl(@NotNull Key key, @NotNull BinaryTagHolder nbt) {
/* 145 */       this.key = key;
/* 146 */       this.nbt = nbt;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Key key() {
/* 151 */       return this.key;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public String data() {
/* 156 */       return this.nbt.string();
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public BinaryTagHolder nbt() {
/* 161 */       return this.nbt;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Stream<? extends ExaminableProperty> examinableProperties() {
/* 166 */       return Stream.of(new ExaminableProperty[] {
/* 167 */             ExaminableProperty.of("key", this.key), 
/* 168 */             ExaminableProperty.of("nbt", this.nbt)
/*     */           });
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object other) {
/* 174 */       if (this == other) return true; 
/* 175 */       if (other == null || getClass() != other.getClass()) return false; 
/* 176 */       CustomImpl that = (CustomImpl)other;
/* 177 */       return (Objects.equals(this.key, that.key) && Objects.equals(this.nbt, that.nbt));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 182 */       int result = this.key.hashCode();
/* 183 */       result = 31 * result + this.nbt.hashCode();
/* 184 */       return result;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\event\PayloadImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */