/*     */ package ac.grim.grimac.shaded.kyori.adventure.platform.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.Facet;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.FacetBase;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.Knob;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
/*     */ import java.lang.invoke.MethodHandle;
/*     */ import java.lang.reflect.Method;
/*     */ import net.md_5.bungee.api.chat.BaseComponent;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
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
/*     */ class PaperFacet<V extends CommandSender>
/*     */   extends FacetBase<V>
/*     */ {
/*  47 */   private static final boolean SUPPORTED = Knob.isEnabled("paper", true);
/*  48 */   static final Class<?> NATIVE_COMPONENT_CLASS = MinecraftReflection.findClass(new String[] { String.join(".", new CharSequence[] { "net", "kyori", "adventure", "text", "Component" }) });
/*  49 */   private static final MethodHandle PAPER_ADVENTURE_AS_VANILLA = findAsVanillaMethod();
/*  50 */   private static final Class<?> NATIVE_GSON_COMPONENT_SERIALIZER_CLASS = MinecraftReflection.findClass(new String[] { String.join(".", new CharSequence[] { "net", "kyori", "adventure", "text", "serializer", "gson", "GsonComponentSerializer" }) });
/*  51 */   private static final Class<?> NATIVE_GSON_COMPONENT_SERIALIZER_IMPL_CLASS = MinecraftReflection.findClass(new String[] { String.join(".", new CharSequence[] { "net", "kyori", "adventure", "text", "serializer", "gson", "GsonComponentSerializerImpl" }) });
/*  52 */   private static final MethodHandle NATIVE_GSON_COMPONENT_SERIALIZER_GSON_GETTER = MinecraftReflection.findStaticMethod(NATIVE_GSON_COMPONENT_SERIALIZER_CLASS, "gson", NATIVE_GSON_COMPONENT_SERIALIZER_CLASS, new Class[0]);
/*  53 */   private static final MethodHandle NATIVE_GSON_COMPONENT_SERIALIZER_DESERIALIZE_METHOD = findNativeDeserializeMethod();
/*     */   @Nullable
/*     */   private static MethodHandle findAsVanillaMethod() {
/*     */     try {
/*  57 */       Class<?> paperAdventure = MinecraftReflection.findClass(new String[] { "io.papermc.paper.adventure.PaperAdventure" });
/*  58 */       Method method = paperAdventure.getDeclaredMethod("asVanilla", new Class[] { NATIVE_COMPONENT_CLASS });
/*  59 */       return MinecraftReflection.lookup().unreflect(method);
/*  60 */     } catch (NoSuchMethodException|IllegalAccessException|NullPointerException e) {
/*  61 */       return null;
/*     */     } 
/*     */   }
/*     */   @Nullable
/*     */   private static MethodHandle findNativeDeserializeMethod() {
/*     */     try {
/*  67 */       Method method = NATIVE_GSON_COMPONENT_SERIALIZER_IMPL_CLASS.getDeclaredMethod("deserialize", new Class[] { String.class });
/*  68 */       method.setAccessible(true);
/*  69 */       return MinecraftReflection.lookup().unreflect(method);
/*  70 */     } catch (NoSuchMethodException|IllegalAccessException|NullPointerException e) {
/*  71 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected PaperFacet(@Nullable Class<? extends V> viewerClass) {
/*  76 */     super(viewerClass);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSupported() {
/*  81 */     return (super.isSupported() && SUPPORTED);
/*     */   }
/*     */   
/*     */   static class Title
/*     */     extends SpigotFacet.Message<Player> implements Facet.Title<Player, BaseComponent[], com.destroystokyo.paper.Title.Builder, com.destroystokyo.paper.Title> {
/*  86 */     private static final boolean SUPPORTED = MinecraftReflection.hasClass(new String[] { "com.destroystokyo.paper.Title" });
/*     */     
/*     */     protected Title() {
/*  89 */       super(Player.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isSupported() {
/*  94 */       return (super.isSupported() && SUPPORTED);
/*     */     }
/*     */ 
/*     */     
/*     */     public com.destroystokyo.paper.Title.Builder createTitleCollection() {
/*  99 */       return com.destroystokyo.paper.Title.builder();
/*     */     }
/*     */ 
/*     */     
/*     */     public void contributeTitle(com.destroystokyo.paper.Title.Builder coll, BaseComponent[] title) {
/* 104 */       coll.title(title);
/*     */     }
/*     */ 
/*     */     
/*     */     public void contributeSubtitle(com.destroystokyo.paper.Title.Builder coll, BaseComponent[] subtitle) {
/* 109 */       coll.subtitle(subtitle);
/*     */     }
/*     */ 
/*     */     
/*     */     public void contributeTimes(com.destroystokyo.paper.Title.Builder coll, int inTicks, int stayTicks, int outTicks) {
/* 114 */       if (inTicks > -1) coll.fadeIn(inTicks); 
/* 115 */       if (stayTicks > -1) coll.stay(stayTicks); 
/* 116 */       if (outTicks > -1) coll.fadeOut(outTicks);
/*     */     
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public com.destroystokyo.paper.Title completeTitle(com.destroystokyo.paper.Title.Builder coll) {
/* 122 */       return coll.build();
/*     */     }
/*     */ 
/*     */     
/*     */     public void showTitle(@NotNull Player viewer, com.destroystokyo.paper.Title title) {
/* 127 */       viewer.sendTitle(title);
/*     */     }
/*     */ 
/*     */     
/*     */     public void clearTitle(@NotNull Player viewer) {
/* 132 */       viewer.hideTitle();
/*     */     }
/*     */ 
/*     */     
/*     */     public void resetTitle(@NotNull Player viewer) {
/* 137 */       viewer.resetTitle();
/*     */     }
/*     */   }
/*     */   
/*     */   static class TabList extends CraftBukkitFacet.TabList {
/* 142 */     private static final boolean SUPPORTED = (MinecraftReflection.hasField(CLASS_CRAFT_PLAYER, PaperFacet.NATIVE_COMPONENT_CLASS, new String[] { "playerListHeader" }) && MinecraftReflection.hasField(CLASS_CRAFT_PLAYER, PaperFacet.NATIVE_COMPONENT_CLASS, new String[] { "playerListFooter" }));
/* 143 */     private static final MethodHandle NATIVE_GSON_COMPONENT_SERIALIZER_DESERIALIZE_METHOD_BOUND = createBoundNativeDeserializeMethodHandle();
/*     */     @Nullable
/*     */     private static MethodHandle createBoundNativeDeserializeMethodHandle() {
/* 146 */       if (SUPPORTED) {
/*     */         try {
/* 148 */           return PaperFacet.NATIVE_GSON_COMPONENT_SERIALIZER_DESERIALIZE_METHOD.bindTo(PaperFacet.NATIVE_GSON_COMPONENT_SERIALIZER_GSON_GETTER.invoke());
/* 149 */         } catch (Throwable throwable) {
/* 150 */           Knob.logError(throwable, "Failed to access native GsonComponentSerializer", new Object[0]);
/* 151 */           return null;
/*     */         } 
/*     */       }
/* 154 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isSupported() {
/*     */       // Byte code:
/*     */       //   0: getstatic ac/grim/grimac/shaded/kyori/adventure/platform/bukkit/PaperFacet$TabList.SUPPORTED : Z
/*     */       //   3: ifeq -> 35
/*     */       //   6: aload_0
/*     */       //   7: invokespecial isSupported : ()Z
/*     */       //   10: ifeq -> 35
/*     */       //   13: getstatic ac/grim/grimac/shaded/kyori/adventure/platform/bukkit/PaperFacet$TabList.CLIENTBOUND_TAB_LIST_PACKET_SET_HEADER : Ljava/lang/invoke/MethodHandle;
/*     */       //   16: ifnull -> 25
/*     */       //   19: getstatic ac/grim/grimac/shaded/kyori/adventure/platform/bukkit/PaperFacet$TabList.CLIENTBOUND_TAB_LIST_PACKET_SET_FOOTER : Ljava/lang/invoke/MethodHandle;
/*     */       //   22: ifnonnull -> 31
/*     */       //   25: invokestatic access$200 : ()Ljava/lang/invoke/MethodHandle;
/*     */       //   28: ifnull -> 35
/*     */       //   31: iconst_1
/*     */       //   32: goto -> 36
/*     */       //   35: iconst_0
/*     */       //   36: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #159	-> 0
/*     */       //   #161	-> 25
/*     */       //   #159	-> 36
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	37	0	this	Lac/grim/grimac/shaded/kyori/adventure/platform/bukkit/PaperFacet$TabList;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected Object create117Packet(Player viewer, @Nullable Object header, @Nullable Object footer) throws Throwable {
/* 166 */       if (CLIENTBOUND_TAB_LIST_PACKET_SET_FOOTER == null && CLIENTBOUND_TAB_LIST_PACKET_SET_HEADER == null) {
/* 167 */         return CLIENTBOUND_TAB_LIST_PACKET_CTOR.invoke(PaperFacet
/* 168 */             .PAPER_ADVENTURE_AS_VANILLA.invoke((header == null) ? createMessage(viewer, (Component)Component.empty()) : header), PaperFacet
/* 169 */             .PAPER_ADVENTURE_AS_VANILLA.invoke((footer == null) ? createMessage(viewer, (Component)Component.empty()) : footer));
/*     */       }
/*     */       
/* 172 */       Object packet = CLIENTBOUND_TAB_LIST_PACKET_CTOR.invoke(null, null);
/* 173 */       CLIENTBOUND_TAB_LIST_PACKET_SET_HEADER.invoke(packet, (header == null) ? createMessage(viewer, (Component)Component.empty()) : header);
/* 174 */       CLIENTBOUND_TAB_LIST_PACKET_SET_FOOTER.invoke(packet, (footer == null) ? createMessage(viewer, (Component)Component.empty()) : footer);
/* 175 */       return packet;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Object createMessage(@NotNull Player viewer, @NotNull Component message) {
/*     */       try {
/* 182 */         return NATIVE_GSON_COMPONENT_SERIALIZER_DESERIALIZE_METHOD_BOUND.invoke((String)GsonComponentSerializer.gson().serialize(message));
/* 183 */       } catch (Throwable throwable) {
/* 184 */         Knob.logError(throwable, "Failed to create native Component message", new Object[0]);
/* 185 */         return null;
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\bukkit\PaperFacet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */