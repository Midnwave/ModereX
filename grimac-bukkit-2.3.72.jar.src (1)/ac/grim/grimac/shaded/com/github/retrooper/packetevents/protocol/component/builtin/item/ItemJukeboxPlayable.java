/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.jukebox.IJukeboxSong;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.jukebox.JukeboxSong;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.jukebox.JukeboxSongs;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MaybeMappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Objects;
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
/*     */ public class ItemJukeboxPlayable
/*     */ {
/*     */   private MaybeMappedEntity<IJukeboxSong> song;
/*     */   @Obsolete
/*     */   private boolean showInTooltip;
/*     */   
/*     */   public ItemJukeboxPlayable(MaybeMappedEntity<IJukeboxSong> song) {
/*  43 */     this(song, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public ItemJukeboxPlayable(@Nullable JukeboxSong song, @Nullable ResourceLocation songKey, boolean showInTooltip) {
/*  55 */     this((IJukeboxSong)song, songKey, showInTooltip);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ItemJukeboxPlayable(@Nullable IJukeboxSong song, @Nullable ResourceLocation songKey, boolean showInTooltip) {
/*  67 */     this(new MaybeMappedEntity((MappedEntity)song, songKey), showInTooltip);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public ItemJukeboxPlayable(MaybeMappedEntity<IJukeboxSong> song, boolean showInTooltip) {
/*  78 */     this.song = song;
/*  79 */     this.showInTooltip = showInTooltip;
/*     */   }
/*     */   
/*     */   public static ItemJukeboxPlayable read(PacketWrapper<?> wrapper) {
/*  83 */     MaybeMappedEntity<IJukeboxSong> song = MaybeMappedEntity.read(wrapper, (IRegistry)JukeboxSongs.getRegistry(), IJukeboxSong::read);
/*  84 */     boolean showInTooltip = (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5) || wrapper.readBoolean());
/*  85 */     return new ItemJukeboxPlayable(song, showInTooltip);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ItemJukeboxPlayable jukeboxPlayable) {
/*  89 */     MaybeMappedEntity.write(wrapper, jukeboxPlayable.song, IJukeboxSong::write);
/*  90 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5)) {
/*  91 */       wrapper.writeBoolean(jukeboxPlayable.showInTooltip);
/*     */     }
/*     */   }
/*     */   
/*     */   public MaybeMappedEntity<IJukeboxSong> getSongHolder() {
/*  96 */     return this.song;
/*     */   }
/*     */   
/*     */   public void setSongHolder(MaybeMappedEntity<IJukeboxSong> songHolder) {
/* 100 */     this.song = songHolder;
/*     */   }
/*     */   @Nullable
/*     */   public IJukeboxSong getJukeboxSong() {
/* 104 */     return (IJukeboxSong)this.song.getValue();
/*     */   }
/*     */   
/*     */   public void setJukeboxSong(@Nullable IJukeboxSong song) {
/* 108 */     this.song = new MaybeMappedEntity((MappedEntity)song);
/*     */   }
/*     */   
/*     */   public void setJukeboxSong(@Nullable JukeboxSong song) {
/* 112 */     setJukeboxSong((IJukeboxSong)song);
/*     */   }
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public JukeboxSong getSong() {
/* 117 */     IJukeboxSong song = getJukeboxSong();
/* 118 */     if (song == null)
/* 119 */       return null; 
/* 120 */     if (song instanceof JukeboxSong) {
/* 121 */       return (JukeboxSong)song;
/*     */     }
/* 123 */     return (JukeboxSong)song.copy(null);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setSong(JukeboxSong song) {
/* 128 */     setJukeboxSong((IJukeboxSong)song);
/*     */   }
/*     */   @Nullable
/*     */   public ResourceLocation getSongKey() {
/* 132 */     return this.song.getName();
/*     */   }
/*     */   
/*     */   public void setSongKey(ResourceLocation songKey) {
/* 136 */     this.song = new MaybeMappedEntity(songKey);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public boolean isShowInTooltip() {
/* 144 */     return this.showInTooltip;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public void setShowInTooltip(boolean showInTooltip) {
/* 152 */     this.showInTooltip = showInTooltip;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 157 */     if (this == obj) return true; 
/* 158 */     if (!(obj instanceof ItemJukeboxPlayable)) return false; 
/* 159 */     ItemJukeboxPlayable that = (ItemJukeboxPlayable)obj;
/* 160 */     if (this.showInTooltip != that.showInTooltip) return false; 
/* 161 */     if (!Objects.equals(this.song, that.song)) return false; 
/* 162 */     return Objects.equals(this.song, that.song);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 167 */     return Objects.hash(new Object[] { this.song, this.song, Boolean.valueOf(this.showInTooltip) });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemJukeboxPlayable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */