/*     */ package ac.grim.grimac.platform.bukkit.manager;
/*     */ 
/*     */ import ac.grim.grimac.platform.api.manager.ItemResetHandler;
/*     */ import ac.grim.grimac.platform.api.player.PlatformPlayer;
/*     */ import ac.grim.grimac.platform.bukkit.player.BukkitPlatformPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.lang.reflect.Method;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.inventory.EquipmentSlot;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BukkitItemResetHandler
/*     */   implements ItemResetHandler
/*     */ {
/*     */   @NotNull
/*  21 */   private final ItemUsageReset resetItemUsage = createItemUsageResetFunction(); @NotNull
/*  22 */   private final ItemUsageHandGetter itemUsageHandGetter = createItemUsageHandGetterFunction();
/*     */ 
/*     */   
/*     */   public void resetItemUsage(@Nullable PlatformPlayer player) {
/*     */     try {
/*  27 */       if (player != null)
/*  28 */         this.resetItemUsage.accept(((BukkitPlatformPlayer)player).getNative()); 
/*     */     } catch (Throwable $ex) {
/*     */       throw $ex;
/*     */     } 
/*     */   } @Nullable
/*     */   public InteractionHand getItemUsageHand(@Nullable PlatformPlayer platformPlayer) {
/*     */     try {
/*  35 */       return (platformPlayer == null) ? null : 
/*  36 */         this.itemUsageHandGetter.apply(((BukkitPlatformPlayer)platformPlayer).getNative());
/*     */     } catch (Throwable $ex) {
/*     */       throw $ex;
/*     */     } 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private ItemUsageReset createItemUsageResetFunction() {
/*     */     // Byte code:
/*     */     //   0: invokestatic getAPI : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/PacketEventsAPI;
/*     */     //   3: invokevirtual getServerManager : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerManager;
/*     */     //   6: invokeinterface getVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   11: astore_1
/*     */     //   12: aload_1
/*     */     //   13: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion.V_1_17 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   16: invokevirtual isNewerThan : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;)Z
/*     */     //   19: ifeq -> 155
/*     */     //   22: getstatic ac/grim/grimac/platform/bukkit/utils/reflection/PaperUtils.PAPER : Z
/*     */     //   25: ifeq -> 155
/*     */     //   28: aload_1
/*     */     //   29: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion.V_1_19 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   32: invokevirtual isOlderThan : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;)Z
/*     */     //   35: ifeq -> 44
/*     */     //   38: <illegal opcode> accept : ()Lac/grim/grimac/platform/bukkit/manager/BukkitItemResetHandler$ItemUsageReset;
/*     */     //   43: areturn
/*     */     //   44: iconst_0
/*     */     //   45: istore_3
/*     */     //   46: ldc 'org.bukkit.craftbukkit.entity.CraftLivingEntity'
/*     */     //   48: invokestatic forName : (Ljava/lang/String;)Ljava/lang/Class;
/*     */     //   51: astore_2
/*     */     //   52: goto -> 90
/*     */     //   55: astore #4
/*     */     //   57: invokestatic getServer : ()Lorg/bukkit/Server;
/*     */     //   60: invokeinterface getClass : ()Ljava/lang/Class;
/*     */     //   65: invokevirtual getPackageName : ()Ljava/lang/String;
/*     */     //   68: ldc '\.'
/*     */     //   70: invokevirtual split : (Ljava/lang/String;)[Ljava/lang/String;
/*     */     //   73: iconst_3
/*     */     //   74: aaload
/*     */     //   75: astore #5
/*     */     //   77: aload #5
/*     */     //   79: <illegal opcode> makeConcatWithConstants : (Ljava/lang/String;)Ljava/lang/String;
/*     */     //   84: invokestatic forName : (Ljava/lang/String;)Ljava/lang/Class;
/*     */     //   87: astore_2
/*     */     //   88: iconst_1
/*     */     //   89: istore_3
/*     */     //   90: aload_2
/*     */     //   91: ldc 'getHandle'
/*     */     //   93: iconst_0
/*     */     //   94: anewarray java/lang/Class
/*     */     //   97: invokevirtual getMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
/*     */     //   100: astore #4
/*     */     //   102: aload #4
/*     */     //   104: invokevirtual getReturnType : ()Ljava/lang/Class;
/*     */     //   107: iload_3
/*     */     //   108: ifeq -> 116
/*     */     //   111: ldc 'c'
/*     */     //   113: goto -> 118
/*     */     //   116: ldc 'setLivingEntityFlag'
/*     */     //   118: iconst_2
/*     */     //   119: anewarray java/lang/Class
/*     */     //   122: dup
/*     */     //   123: iconst_0
/*     */     //   124: getstatic java/lang/Integer.TYPE : Ljava/lang/Class;
/*     */     //   127: aastore
/*     */     //   128: dup
/*     */     //   129: iconst_1
/*     */     //   130: getstatic java/lang/Boolean.TYPE : Ljava/lang/Class;
/*     */     //   133: aastore
/*     */     //   134: invokevirtual getDeclaredMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
/*     */     //   137: astore #5
/*     */     //   139: aload #5
/*     */     //   141: iconst_1
/*     */     //   142: invokevirtual setAccessible : (Z)V
/*     */     //   145: aload #5
/*     */     //   147: aload #4
/*     */     //   149: <illegal opcode> accept : (Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)Lac/grim/grimac/platform/bukkit/manager/BukkitItemResetHandler$ItemUsageReset;
/*     */     //   154: areturn
/*     */     //   155: aload_1
/*     */     //   156: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion.V_1_8_8 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   159: if_acmpne -> 216
/*     */     //   162: ldc 'org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity'
/*     */     //   164: invokestatic forName : (Ljava/lang/String;)Ljava/lang/Class;
/*     */     //   167: ldc 'getHandle'
/*     */     //   169: iconst_0
/*     */     //   170: anewarray java/lang/Class
/*     */     //   173: invokevirtual getMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
/*     */     //   176: astore_2
/*     */     //   177: aload_2
/*     */     //   178: invokevirtual getReturnType : ()Ljava/lang/Class;
/*     */     //   181: ldc 'bV'
/*     */     //   183: iconst_0
/*     */     //   184: anewarray java/lang/Class
/*     */     //   187: invokevirtual getMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
/*     */     //   190: astore_3
/*     */     //   191: aload_2
/*     */     //   192: invokevirtual getReturnType : ()Ljava/lang/Class;
/*     */     //   195: ldc 'bS'
/*     */     //   197: iconst_0
/*     */     //   198: anewarray java/lang/Class
/*     */     //   201: invokevirtual getMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
/*     */     //   204: astore #4
/*     */     //   206: aload_2
/*     */     //   207: aload_3
/*     */     //   208: aload #4
/*     */     //   210: <illegal opcode> accept : (Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)Lac/grim/grimac/platform/bukkit/manager/BukkitItemResetHandler$ItemUsageReset;
/*     */     //   215: areturn
/*     */     //   216: invokestatic getServer : ()Lorg/bukkit/Server;
/*     */     //   219: invokeinterface getClass : ()Ljava/lang/Class;
/*     */     //   224: invokevirtual getPackageName : ()Ljava/lang/String;
/*     */     //   227: ldc '\.'
/*     */     //   229: invokevirtual split : (Ljava/lang/String;)[Ljava/lang/String;
/*     */     //   232: iconst_3
/*     */     //   233: aaload
/*     */     //   234: astore_2
/*     */     //   235: aload_2
/*     */     //   236: <illegal opcode> makeConcatWithConstants : (Ljava/lang/String;)Ljava/lang/String;
/*     */     //   241: invokestatic forName : (Ljava/lang/String;)Ljava/lang/Class;
/*     */     //   244: ldc 'getHandle'
/*     */     //   246: iconst_0
/*     */     //   247: anewarray java/lang/Class
/*     */     //   250: invokevirtual getMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
/*     */     //   253: astore_3
/*     */     //   254: aload_3
/*     */     //   255: invokevirtual getReturnType : ()Ljava/lang/Class;
/*     */     //   258: aload_2
/*     */     //   259: astore #5
/*     */     //   261: iconst_m1
/*     */     //   262: istore #6
/*     */     //   264: aload #5
/*     */     //   266: invokevirtual hashCode : ()I
/*     */     //   269: lookupswitch default -> 937, -1497224837 -> 520, -1497195046 -> 536, -1497165255 -> 552, -1497135464 -> 568, -1497135463 -> 584, -1497105673 -> 601, -1497075882 -> 618, -1497046091 -> 635, -1497046090 -> 652, -1497046089 -> 670, -1497016300 -> 688, -1496986509 -> 706, -1496986508 -> 724, -1496956718 -> 742, -1496956717 -> 760, -1496956716 -> 778, -1496301316 -> 796, -1496301315 -> 814, -1496301314 -> 832, -1496301313 -> 850, -1496271525 -> 868, -1496271524 -> 886, -1496271523 -> 904, -1496271522 -> 922, -1156393175 -> 488, -1156393174 -> 504
/*     */     //   488: aload #5
/*     */     //   490: ldc 'v1_9_R1'
/*     */     //   492: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   495: ifeq -> 937
/*     */     //   498: iconst_0
/*     */     //   499: istore #6
/*     */     //   501: goto -> 937
/*     */     //   504: aload #5
/*     */     //   506: ldc 'v1_9_R2'
/*     */     //   508: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   511: ifeq -> 937
/*     */     //   514: iconst_1
/*     */     //   515: istore #6
/*     */     //   517: goto -> 937
/*     */     //   520: aload #5
/*     */     //   522: ldc 'v1_10_R1'
/*     */     //   524: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   527: ifeq -> 937
/*     */     //   530: iconst_2
/*     */     //   531: istore #6
/*     */     //   533: goto -> 937
/*     */     //   536: aload #5
/*     */     //   538: ldc 'v1_11_R1'
/*     */     //   540: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   543: ifeq -> 937
/*     */     //   546: iconst_3
/*     */     //   547: istore #6
/*     */     //   549: goto -> 937
/*     */     //   552: aload #5
/*     */     //   554: ldc 'v1_12_R1'
/*     */     //   556: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   559: ifeq -> 937
/*     */     //   562: iconst_4
/*     */     //   563: istore #6
/*     */     //   565: goto -> 937
/*     */     //   568: aload #5
/*     */     //   570: ldc 'v1_13_R1'
/*     */     //   572: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   575: ifeq -> 937
/*     */     //   578: iconst_5
/*     */     //   579: istore #6
/*     */     //   581: goto -> 937
/*     */     //   584: aload #5
/*     */     //   586: ldc 'v1_13_R2'
/*     */     //   588: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   591: ifeq -> 937
/*     */     //   594: bipush #6
/*     */     //   596: istore #6
/*     */     //   598: goto -> 937
/*     */     //   601: aload #5
/*     */     //   603: ldc 'v1_14_R1'
/*     */     //   605: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   608: ifeq -> 937
/*     */     //   611: bipush #7
/*     */     //   613: istore #6
/*     */     //   615: goto -> 937
/*     */     //   618: aload #5
/*     */     //   620: ldc 'v1_15_R1'
/*     */     //   622: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   625: ifeq -> 937
/*     */     //   628: bipush #8
/*     */     //   630: istore #6
/*     */     //   632: goto -> 937
/*     */     //   635: aload #5
/*     */     //   637: ldc 'v1_16_R1'
/*     */     //   639: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   642: ifeq -> 937
/*     */     //   645: bipush #9
/*     */     //   647: istore #6
/*     */     //   649: goto -> 937
/*     */     //   652: aload #5
/*     */     //   654: ldc_w 'v1_16_R2'
/*     */     //   657: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   660: ifeq -> 937
/*     */     //   663: bipush #10
/*     */     //   665: istore #6
/*     */     //   667: goto -> 937
/*     */     //   670: aload #5
/*     */     //   672: ldc_w 'v1_16_R3'
/*     */     //   675: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   678: ifeq -> 937
/*     */     //   681: bipush #11
/*     */     //   683: istore #6
/*     */     //   685: goto -> 937
/*     */     //   688: aload #5
/*     */     //   690: ldc_w 'v1_17_R1'
/*     */     //   693: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   696: ifeq -> 937
/*     */     //   699: bipush #12
/*     */     //   701: istore #6
/*     */     //   703: goto -> 937
/*     */     //   706: aload #5
/*     */     //   708: ldc_w 'v1_18_R1'
/*     */     //   711: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   714: ifeq -> 937
/*     */     //   717: bipush #13
/*     */     //   719: istore #6
/*     */     //   721: goto -> 937
/*     */     //   724: aload #5
/*     */     //   726: ldc_w 'v1_18_R2'
/*     */     //   729: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   732: ifeq -> 937
/*     */     //   735: bipush #14
/*     */     //   737: istore #6
/*     */     //   739: goto -> 937
/*     */     //   742: aload #5
/*     */     //   744: ldc_w 'v1_19_R1'
/*     */     //   747: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   750: ifeq -> 937
/*     */     //   753: bipush #15
/*     */     //   755: istore #6
/*     */     //   757: goto -> 937
/*     */     //   760: aload #5
/*     */     //   762: ldc_w 'v1_19_R2'
/*     */     //   765: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   768: ifeq -> 937
/*     */     //   771: bipush #16
/*     */     //   773: istore #6
/*     */     //   775: goto -> 937
/*     */     //   778: aload #5
/*     */     //   780: ldc_w 'v1_19_R3'
/*     */     //   783: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   786: ifeq -> 937
/*     */     //   789: bipush #17
/*     */     //   791: istore #6
/*     */     //   793: goto -> 937
/*     */     //   796: aload #5
/*     */     //   798: ldc_w 'v1_20_R1'
/*     */     //   801: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   804: ifeq -> 937
/*     */     //   807: bipush #18
/*     */     //   809: istore #6
/*     */     //   811: goto -> 937
/*     */     //   814: aload #5
/*     */     //   816: ldc_w 'v1_20_R2'
/*     */     //   819: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   822: ifeq -> 937
/*     */     //   825: bipush #19
/*     */     //   827: istore #6
/*     */     //   829: goto -> 937
/*     */     //   832: aload #5
/*     */     //   834: ldc_w 'v1_20_R3'
/*     */     //   837: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   840: ifeq -> 937
/*     */     //   843: bipush #20
/*     */     //   845: istore #6
/*     */     //   847: goto -> 937
/*     */     //   850: aload #5
/*     */     //   852: ldc_w 'v1_20_R4'
/*     */     //   855: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   858: ifeq -> 937
/*     */     //   861: bipush #21
/*     */     //   863: istore #6
/*     */     //   865: goto -> 937
/*     */     //   868: aload #5
/*     */     //   870: ldc_w 'v1_21_R1'
/*     */     //   873: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   876: ifeq -> 937
/*     */     //   879: bipush #22
/*     */     //   881: istore #6
/*     */     //   883: goto -> 937
/*     */     //   886: aload #5
/*     */     //   888: ldc_w 'v1_21_R2'
/*     */     //   891: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   894: ifeq -> 937
/*     */     //   897: bipush #23
/*     */     //   899: istore #6
/*     */     //   901: goto -> 937
/*     */     //   904: aload #5
/*     */     //   906: ldc_w 'v1_21_R3'
/*     */     //   909: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   912: ifeq -> 937
/*     */     //   915: bipush #24
/*     */     //   917: istore #6
/*     */     //   919: goto -> 937
/*     */     //   922: aload #5
/*     */     //   924: ldc_w 'v1_21_R4'
/*     */     //   927: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   930: ifeq -> 937
/*     */     //   933: bipush #25
/*     */     //   935: istore #6
/*     */     //   937: iload #6
/*     */     //   939: tableswitch default -> 1176, 0 -> 1056, 1 -> 1062, 2 -> 1068, 3 -> 1074, 4 -> 1080, 5 -> 1086, 6 -> 1086, 7 -> 1092, 8 -> 1098, 9 -> 1104, 10 -> 1104, 11 -> 1104, 12 -> 1104, 13 -> 1110, 14 -> 1116, 15 -> 1122, 16 -> 1128, 17 -> 1134, 18 -> 1140, 19 -> 1146, 20 -> 1152, 21 -> 1158, 22 -> 1164, 23 -> 1170, 24 -> 1170, 25 -> 1170
/*     */     //   1056: ldc_w 'cz'
/*     */     //   1059: goto -> 1193
/*     */     //   1062: ldc_w 'cA'
/*     */     //   1065: goto -> 1193
/*     */     //   1068: ldc_w 'cE'
/*     */     //   1071: goto -> 1193
/*     */     //   1074: ldc_w 'cF'
/*     */     //   1077: goto -> 1193
/*     */     //   1080: ldc_w 'cN'
/*     */     //   1083: goto -> 1193
/*     */     //   1086: ldc_w 'da'
/*     */     //   1089: goto -> 1193
/*     */     //   1092: ldc_w 'dp'
/*     */     //   1095: goto -> 1193
/*     */     //   1098: ldc_w 'dH'
/*     */     //   1101: goto -> 1193
/*     */     //   1104: ldc_w 'clearActiveItem'
/*     */     //   1107: goto -> 1193
/*     */     //   1110: ldc_w 'eR'
/*     */     //   1113: goto -> 1193
/*     */     //   1116: ldc_w 'eS'
/*     */     //   1119: goto -> 1193
/*     */     //   1122: ldc_w 'eZ'
/*     */     //   1125: goto -> 1193
/*     */     //   1128: ldc_w 'ff'
/*     */     //   1131: goto -> 1193
/*     */     //   1134: ldc_w 'fk'
/*     */     //   1137: goto -> 1193
/*     */     //   1140: ldc_w 'fo'
/*     */     //   1143: goto -> 1193
/*     */     //   1146: ldc_w 'fs'
/*     */     //   1149: goto -> 1193
/*     */     //   1152: ldc_w 'ft'
/*     */     //   1155: goto -> 1193
/*     */     //   1158: ldc_w 'fB'
/*     */     //   1161: goto -> 1193
/*     */     //   1164: ldc_w 'fx'
/*     */     //   1167: goto -> 1193
/*     */     //   1170: ldc_w 'fF'
/*     */     //   1173: goto -> 1193
/*     */     //   1176: new java/lang/IllegalStateException
/*     */     //   1179: dup
/*     */     //   1180: aload_1
/*     */     //   1181: invokevirtual getReleaseName : ()Ljava/lang/String;
/*     */     //   1184: <illegal opcode> makeConcatWithConstants : (Ljava/lang/String;)Ljava/lang/String;
/*     */     //   1189: invokespecial <init> : (Ljava/lang/String;)V
/*     */     //   1192: athrow
/*     */     //   1193: iconst_0
/*     */     //   1194: anewarray java/lang/Class
/*     */     //   1197: invokevirtual getMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
/*     */     //   1200: astore #4
/*     */     //   1202: aload_1
/*     */     //   1203: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion.V_1_19 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   1206: invokevirtual isOlderThan : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;)Z
/*     */     //   1209: ifeq -> 1221
/*     */     //   1212: aload #4
/*     */     //   1214: aload_3
/*     */     //   1215: <illegal opcode> accept : (Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)Lac/grim/grimac/platform/bukkit/manager/BukkitItemResetHandler$ItemUsageReset;
/*     */     //   1220: areturn
/*     */     //   1221: aload_3
/*     */     //   1222: invokevirtual getReturnType : ()Ljava/lang/Class;
/*     */     //   1225: ldc 'c'
/*     */     //   1227: iconst_2
/*     */     //   1228: anewarray java/lang/Class
/*     */     //   1231: dup
/*     */     //   1232: iconst_0
/*     */     //   1233: getstatic java/lang/Integer.TYPE : Ljava/lang/Class;
/*     */     //   1236: aastore
/*     */     //   1237: dup
/*     */     //   1238: iconst_1
/*     */     //   1239: getstatic java/lang/Boolean.TYPE : Ljava/lang/Class;
/*     */     //   1242: aastore
/*     */     //   1243: invokevirtual getDeclaredMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
/*     */     //   1246: astore #5
/*     */     //   1248: aload #5
/*     */     //   1250: iconst_1
/*     */     //   1251: invokevirtual setAccessible : (Z)V
/*     */     //   1254: aload_3
/*     */     //   1255: aload #5
/*     */     //   1257: aload #4
/*     */     //   1259: <illegal opcode> accept : (Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)Lac/grim/grimac/platform/bukkit/manager/BukkitItemResetHandler$ItemUsageReset;
/*     */     //   1264: areturn
/*     */     //   1265: astore_1
/*     */     //   1266: aload_1
/*     */     //   1267: athrow
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #41	-> 0
/*     */     //   #42	-> 12
/*     */     //   #43	-> 28
/*     */     //   #44	-> 38
/*     */     //   #48	-> 44
/*     */     //   #50	-> 46
/*     */     //   #55	-> 52
/*     */     //   #51	-> 55
/*     */     //   #52	-> 57
/*     */     //   #53	-> 77
/*     */     //   #54	-> 88
/*     */     //   #57	-> 90
/*     */     //   #58	-> 102
/*     */     //   #59	-> 107
/*     */     //   #58	-> 134
/*     */     //   #61	-> 139
/*     */     //   #63	-> 145
/*     */     //   #70	-> 155
/*     */     //   #71	-> 162
/*     */     //   #72	-> 177
/*     */     //   #73	-> 191
/*     */     //   #75	-> 206
/*     */     //   #87	-> 216
/*     */     //   #88	-> 235
/*     */     //   #89	-> 254
/*     */     //   #90	-> 1056
/*     */     //   #91	-> 1062
/*     */     //   #92	-> 1068
/*     */     //   #93	-> 1074
/*     */     //   #94	-> 1080
/*     */     //   #95	-> 1086
/*     */     //   #96	-> 1092
/*     */     //   #97	-> 1098
/*     */     //   #98	-> 1104
/*     */     //   #99	-> 1110
/*     */     //   #100	-> 1116
/*     */     //   #101	-> 1122
/*     */     //   #102	-> 1128
/*     */     //   #103	-> 1134
/*     */     //   #104	-> 1140
/*     */     //   #105	-> 1146
/*     */     //   #106	-> 1152
/*     */     //   #107	-> 1158
/*     */     //   #108	-> 1164
/*     */     //   #109	-> 1170
/*     */     //   #110	-> 1176
/*     */     //   #89	-> 1197
/*     */     //   #113	-> 1202
/*     */     //   #114	-> 1212
/*     */     //   #116	-> 1221
/*     */     //   #117	-> 1248
/*     */     //   #119	-> 1254
/*     */     //   #39	-> 1265
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   52	3	2	CraftLivingEntity	Ljava/lang/Class;
/*     */     //   77	13	5	nmsPackage	Ljava/lang/String;
/*     */     //   57	33	4	ignored	Ljava/lang/ClassNotFoundException;
/*     */     //   88	67	2	CraftLivingEntity	Ljava/lang/Class;
/*     */     //   46	109	3	obfuscated	Z
/*     */     //   102	53	4	getHandle	Ljava/lang/reflect/Method;
/*     */     //   139	16	5	setLivingEntityFlag	Ljava/lang/reflect/Method;
/*     */     //   177	39	2	getHandle	Ljava/lang/reflect/Method;
/*     */     //   191	25	3	clearActiveItem	Ljava/lang/reflect/Method;
/*     */     //   206	10	4	isUsingItem	Ljava/lang/reflect/Method;
/*     */     //   1248	17	5	setLivingEntityFlag	Ljava/lang/reflect/Method;
/*     */     //   12	1253	1	version	Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   235	1030	2	nmsPackage	Ljava/lang/String;
/*     */     //   254	1011	3	getHandle	Ljava/lang/reflect/Method;
/*     */     //   1202	63	4	clearActiveItem	Ljava/lang/reflect/Method;
/*     */     //   1266	2	1	$ex	Ljava/lang/Throwable;
/*     */     //   0	1268	0	this	Lac/grim/grimac/platform/bukkit/manager/BukkitItemResetHandler;
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   52	3	2	CraftLivingEntity	Ljava/lang/Class<*>;
/*     */     //   88	67	2	CraftLivingEntity	Ljava/lang/Class<*>;
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	43	1265	java/lang/Throwable
/*     */     //   44	154	1265	java/lang/Throwable
/*     */     //   46	52	55	java/lang/ClassNotFoundException
/*     */     //   155	215	1265	java/lang/Throwable
/*     */     //   216	1220	1265	java/lang/Throwable
/*     */     //   1221	1264	1265	java/lang/Throwable
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private ItemUsageHandGetter createItemUsageHandGetterFunction() {
/*     */     // Byte code:
/*     */     //   0: invokestatic getAPI : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/PacketEventsAPI;
/*     */     //   3: invokevirtual getServerManager : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerManager;
/*     */     //   6: invokeinterface getVersion : ()Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   11: astore_1
/*     */     //   12: aload_1
/*     */     //   13: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion.V_1_16_5 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   16: invokevirtual isNewerThanOrEquals : (Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;)Z
/*     */     //   19: ifeq -> 34
/*     */     //   22: getstatic ac/grim/grimac/platform/bukkit/utils/reflection/PaperUtils.PAPER : Z
/*     */     //   25: ifeq -> 34
/*     */     //   28: <illegal opcode> apply : ()Lac/grim/grimac/platform/bukkit/manager/BukkitItemResetHandler$ItemUsageHandGetter;
/*     */     //   33: areturn
/*     */     //   34: aload_1
/*     */     //   35: getstatic ac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion.V_1_8_8 : Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   38: if_acmpne -> 78
/*     */     //   41: ldc 'org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity'
/*     */     //   43: invokestatic forName : (Ljava/lang/String;)Ljava/lang/Class;
/*     */     //   46: ldc 'getHandle'
/*     */     //   48: iconst_0
/*     */     //   49: anewarray java/lang/Class
/*     */     //   52: invokevirtual getMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
/*     */     //   55: astore_2
/*     */     //   56: aload_2
/*     */     //   57: invokevirtual getReturnType : ()Ljava/lang/Class;
/*     */     //   60: ldc 'bS'
/*     */     //   62: iconst_0
/*     */     //   63: anewarray java/lang/Class
/*     */     //   66: invokevirtual getMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
/*     */     //   69: astore_3
/*     */     //   70: aload_3
/*     */     //   71: aload_2
/*     */     //   72: <illegal opcode> apply : (Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)Lac/grim/grimac/platform/bukkit/manager/BukkitItemResetHandler$ItemUsageHandGetter;
/*     */     //   77: areturn
/*     */     //   78: invokestatic getServer : ()Lorg/bukkit/Server;
/*     */     //   81: invokeinterface getClass : ()Ljava/lang/Class;
/*     */     //   86: invokevirtual getPackageName : ()Ljava/lang/String;
/*     */     //   89: ldc '\.'
/*     */     //   91: invokevirtual split : (Ljava/lang/String;)[Ljava/lang/String;
/*     */     //   94: iconst_3
/*     */     //   95: aaload
/*     */     //   96: astore_2
/*     */     //   97: aload_2
/*     */     //   98: <illegal opcode> makeConcatWithConstants : (Ljava/lang/String;)Ljava/lang/String;
/*     */     //   103: invokestatic forName : (Ljava/lang/String;)Ljava/lang/Class;
/*     */     //   106: ldc 'getHandle'
/*     */     //   108: iconst_0
/*     */     //   109: anewarray java/lang/Class
/*     */     //   112: invokevirtual getMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
/*     */     //   115: astore_3
/*     */     //   116: aload_3
/*     */     //   117: invokevirtual getReturnType : ()Ljava/lang/Class;
/*     */     //   120: aload_2
/*     */     //   121: astore #5
/*     */     //   123: iconst_m1
/*     */     //   124: istore #6
/*     */     //   126: aload #5
/*     */     //   128: invokevirtual hashCode : ()I
/*     */     //   131: lookupswitch default -> 797, -1497224837 -> 380, -1497195046 -> 396, -1497165255 -> 412, -1497135464 -> 428, -1497135463 -> 444, -1497105673 -> 461, -1497075882 -> 478, -1497046091 -> 495, -1497046090 -> 512, -1497046089 -> 530, -1497016300 -> 548, -1496986509 -> 566, -1496986508 -> 584, -1496956718 -> 602, -1496956717 -> 620, -1496956716 -> 638, -1496301316 -> 656, -1496301315 -> 674, -1496301314 -> 692, -1496301313 -> 710, -1496271525 -> 728, -1496271524 -> 746, -1496271523 -> 764, -1496271522 -> 782, -1156393175 -> 348, -1156393174 -> 364
/*     */     //   348: aload #5
/*     */     //   350: ldc 'v1_9_R1'
/*     */     //   352: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   355: ifeq -> 797
/*     */     //   358: iconst_0
/*     */     //   359: istore #6
/*     */     //   361: goto -> 797
/*     */     //   364: aload #5
/*     */     //   366: ldc 'v1_9_R2'
/*     */     //   368: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   371: ifeq -> 797
/*     */     //   374: iconst_1
/*     */     //   375: istore #6
/*     */     //   377: goto -> 797
/*     */     //   380: aload #5
/*     */     //   382: ldc 'v1_10_R1'
/*     */     //   384: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   387: ifeq -> 797
/*     */     //   390: iconst_2
/*     */     //   391: istore #6
/*     */     //   393: goto -> 797
/*     */     //   396: aload #5
/*     */     //   398: ldc 'v1_11_R1'
/*     */     //   400: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   403: ifeq -> 797
/*     */     //   406: iconst_3
/*     */     //   407: istore #6
/*     */     //   409: goto -> 797
/*     */     //   412: aload #5
/*     */     //   414: ldc 'v1_12_R1'
/*     */     //   416: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   419: ifeq -> 797
/*     */     //   422: iconst_4
/*     */     //   423: istore #6
/*     */     //   425: goto -> 797
/*     */     //   428: aload #5
/*     */     //   430: ldc 'v1_13_R1'
/*     */     //   432: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   435: ifeq -> 797
/*     */     //   438: iconst_5
/*     */     //   439: istore #6
/*     */     //   441: goto -> 797
/*     */     //   444: aload #5
/*     */     //   446: ldc 'v1_13_R2'
/*     */     //   448: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   451: ifeq -> 797
/*     */     //   454: bipush #6
/*     */     //   456: istore #6
/*     */     //   458: goto -> 797
/*     */     //   461: aload #5
/*     */     //   463: ldc 'v1_14_R1'
/*     */     //   465: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   468: ifeq -> 797
/*     */     //   471: bipush #7
/*     */     //   473: istore #6
/*     */     //   475: goto -> 797
/*     */     //   478: aload #5
/*     */     //   480: ldc 'v1_15_R1'
/*     */     //   482: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   485: ifeq -> 797
/*     */     //   488: bipush #8
/*     */     //   490: istore #6
/*     */     //   492: goto -> 797
/*     */     //   495: aload #5
/*     */     //   497: ldc 'v1_16_R1'
/*     */     //   499: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   502: ifeq -> 797
/*     */     //   505: bipush #9
/*     */     //   507: istore #6
/*     */     //   509: goto -> 797
/*     */     //   512: aload #5
/*     */     //   514: ldc_w 'v1_16_R2'
/*     */     //   517: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   520: ifeq -> 797
/*     */     //   523: bipush #10
/*     */     //   525: istore #6
/*     */     //   527: goto -> 797
/*     */     //   530: aload #5
/*     */     //   532: ldc_w 'v1_16_R3'
/*     */     //   535: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   538: ifeq -> 797
/*     */     //   541: bipush #11
/*     */     //   543: istore #6
/*     */     //   545: goto -> 797
/*     */     //   548: aload #5
/*     */     //   550: ldc_w 'v1_17_R1'
/*     */     //   553: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   556: ifeq -> 797
/*     */     //   559: bipush #12
/*     */     //   561: istore #6
/*     */     //   563: goto -> 797
/*     */     //   566: aload #5
/*     */     //   568: ldc_w 'v1_18_R1'
/*     */     //   571: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   574: ifeq -> 797
/*     */     //   577: bipush #13
/*     */     //   579: istore #6
/*     */     //   581: goto -> 797
/*     */     //   584: aload #5
/*     */     //   586: ldc_w 'v1_18_R2'
/*     */     //   589: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   592: ifeq -> 797
/*     */     //   595: bipush #14
/*     */     //   597: istore #6
/*     */     //   599: goto -> 797
/*     */     //   602: aload #5
/*     */     //   604: ldc_w 'v1_19_R1'
/*     */     //   607: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   610: ifeq -> 797
/*     */     //   613: bipush #15
/*     */     //   615: istore #6
/*     */     //   617: goto -> 797
/*     */     //   620: aload #5
/*     */     //   622: ldc_w 'v1_19_R2'
/*     */     //   625: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   628: ifeq -> 797
/*     */     //   631: bipush #16
/*     */     //   633: istore #6
/*     */     //   635: goto -> 797
/*     */     //   638: aload #5
/*     */     //   640: ldc_w 'v1_19_R3'
/*     */     //   643: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   646: ifeq -> 797
/*     */     //   649: bipush #17
/*     */     //   651: istore #6
/*     */     //   653: goto -> 797
/*     */     //   656: aload #5
/*     */     //   658: ldc_w 'v1_20_R1'
/*     */     //   661: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   664: ifeq -> 797
/*     */     //   667: bipush #18
/*     */     //   669: istore #6
/*     */     //   671: goto -> 797
/*     */     //   674: aload #5
/*     */     //   676: ldc_w 'v1_20_R2'
/*     */     //   679: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   682: ifeq -> 797
/*     */     //   685: bipush #19
/*     */     //   687: istore #6
/*     */     //   689: goto -> 797
/*     */     //   692: aload #5
/*     */     //   694: ldc_w 'v1_20_R3'
/*     */     //   697: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   700: ifeq -> 797
/*     */     //   703: bipush #20
/*     */     //   705: istore #6
/*     */     //   707: goto -> 797
/*     */     //   710: aload #5
/*     */     //   712: ldc_w 'v1_20_R4'
/*     */     //   715: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   718: ifeq -> 797
/*     */     //   721: bipush #21
/*     */     //   723: istore #6
/*     */     //   725: goto -> 797
/*     */     //   728: aload #5
/*     */     //   730: ldc_w 'v1_21_R1'
/*     */     //   733: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   736: ifeq -> 797
/*     */     //   739: bipush #22
/*     */     //   741: istore #6
/*     */     //   743: goto -> 797
/*     */     //   746: aload #5
/*     */     //   748: ldc_w 'v1_21_R2'
/*     */     //   751: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   754: ifeq -> 797
/*     */     //   757: bipush #23
/*     */     //   759: istore #6
/*     */     //   761: goto -> 797
/*     */     //   764: aload #5
/*     */     //   766: ldc_w 'v1_21_R3'
/*     */     //   769: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   772: ifeq -> 797
/*     */     //   775: bipush #24
/*     */     //   777: istore #6
/*     */     //   779: goto -> 797
/*     */     //   782: aload #5
/*     */     //   784: ldc_w 'v1_21_R4'
/*     */     //   787: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   790: ifeq -> 797
/*     */     //   793: bipush #25
/*     */     //   795: istore #6
/*     */     //   797: iload #6
/*     */     //   799: tableswitch default -> 1006, 0 -> 916, 1 -> 922, 2 -> 928, 3 -> 934, 4 -> 934, 5 -> 934, 6 -> 934, 7 -> 934, 8 -> 934, 9 -> 934, 10 -> 934, 11 -> 934, 12 -> 934, 13 -> 940, 14 -> 946, 15 -> 952, 16 -> 958, 17 -> 964, 18 -> 970, 19 -> 976, 20 -> 982, 21 -> 988, 22 -> 994, 23 -> 1000, 24 -> 1000, 25 -> 1000
/*     */     //   916: ldc_w 'cs'
/*     */     //   919: goto -> 1023
/*     */     //   922: ldc_w 'ct'
/*     */     //   925: goto -> 1023
/*     */     //   928: ldc_w 'cx'
/*     */     //   931: goto -> 1023
/*     */     //   934: ldc_w 'isHandRaised'
/*     */     //   937: goto -> 1023
/*     */     //   940: ldc_w 'eL'
/*     */     //   943: goto -> 1023
/*     */     //   946: ldc_w 'eM'
/*     */     //   949: goto -> 1023
/*     */     //   952: ldc_w 'eT'
/*     */     //   955: goto -> 1023
/*     */     //   958: ldc_w 'eZ'
/*     */     //   961: goto -> 1023
/*     */     //   964: ldc_w 'fe'
/*     */     //   967: goto -> 1023
/*     */     //   970: ldc_w 'fi'
/*     */     //   973: goto -> 1023
/*     */     //   976: ldc_w 'fm'
/*     */     //   979: goto -> 1023
/*     */     //   982: ldc_w 'fn'
/*     */     //   985: goto -> 1023
/*     */     //   988: ldc_w 'fv'
/*     */     //   991: goto -> 1023
/*     */     //   994: ldc_w 'fr'
/*     */     //   997: goto -> 1023
/*     */     //   1000: ldc_w 'fz'
/*     */     //   1003: goto -> 1023
/*     */     //   1006: new java/lang/IllegalStateException
/*     */     //   1009: dup
/*     */     //   1010: aload_1
/*     */     //   1011: invokevirtual getReleaseName : ()Ljava/lang/String;
/*     */     //   1014: <illegal opcode> makeConcatWithConstants : (Ljava/lang/String;)Ljava/lang/String;
/*     */     //   1019: invokespecial <init> : (Ljava/lang/String;)V
/*     */     //   1022: athrow
/*     */     //   1023: iconst_0
/*     */     //   1024: anewarray java/lang/Class
/*     */     //   1027: invokevirtual getMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
/*     */     //   1030: astore #4
/*     */     //   1032: aload_3
/*     */     //   1033: invokevirtual getReturnType : ()Ljava/lang/Class;
/*     */     //   1036: aload_2
/*     */     //   1037: astore #6
/*     */     //   1039: iconst_m1
/*     */     //   1040: istore #7
/*     */     //   1042: aload #6
/*     */     //   1044: invokevirtual hashCode : ()I
/*     */     //   1047: lookupswitch default -> 1713, -1497224837 -> 1296, -1497195046 -> 1312, -1497165255 -> 1328, -1497135464 -> 1344, -1497135463 -> 1360, -1497105673 -> 1377, -1497075882 -> 1394, -1497046091 -> 1411, -1497046090 -> 1428, -1497046089 -> 1446, -1497016300 -> 1464, -1496986509 -> 1482, -1496986508 -> 1500, -1496956718 -> 1518, -1496956717 -> 1536, -1496956716 -> 1554, -1496301316 -> 1572, -1496301315 -> 1590, -1496301314 -> 1608, -1496301313 -> 1626, -1496271525 -> 1644, -1496271524 -> 1662, -1496271523 -> 1680, -1496271522 -> 1698, -1156393175 -> 1264, -1156393174 -> 1280
/*     */     //   1264: aload #6
/*     */     //   1266: ldc 'v1_9_R1'
/*     */     //   1268: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1271: ifeq -> 1713
/*     */     //   1274: iconst_0
/*     */     //   1275: istore #7
/*     */     //   1277: goto -> 1713
/*     */     //   1280: aload #6
/*     */     //   1282: ldc 'v1_9_R2'
/*     */     //   1284: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1287: ifeq -> 1713
/*     */     //   1290: iconst_1
/*     */     //   1291: istore #7
/*     */     //   1293: goto -> 1713
/*     */     //   1296: aload #6
/*     */     //   1298: ldc 'v1_10_R1'
/*     */     //   1300: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1303: ifeq -> 1713
/*     */     //   1306: iconst_2
/*     */     //   1307: istore #7
/*     */     //   1309: goto -> 1713
/*     */     //   1312: aload #6
/*     */     //   1314: ldc 'v1_11_R1'
/*     */     //   1316: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1319: ifeq -> 1713
/*     */     //   1322: iconst_3
/*     */     //   1323: istore #7
/*     */     //   1325: goto -> 1713
/*     */     //   1328: aload #6
/*     */     //   1330: ldc 'v1_12_R1'
/*     */     //   1332: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1335: ifeq -> 1713
/*     */     //   1338: iconst_4
/*     */     //   1339: istore #7
/*     */     //   1341: goto -> 1713
/*     */     //   1344: aload #6
/*     */     //   1346: ldc 'v1_13_R1'
/*     */     //   1348: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1351: ifeq -> 1713
/*     */     //   1354: iconst_5
/*     */     //   1355: istore #7
/*     */     //   1357: goto -> 1713
/*     */     //   1360: aload #6
/*     */     //   1362: ldc 'v1_13_R2'
/*     */     //   1364: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1367: ifeq -> 1713
/*     */     //   1370: bipush #6
/*     */     //   1372: istore #7
/*     */     //   1374: goto -> 1713
/*     */     //   1377: aload #6
/*     */     //   1379: ldc 'v1_14_R1'
/*     */     //   1381: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1384: ifeq -> 1713
/*     */     //   1387: bipush #7
/*     */     //   1389: istore #7
/*     */     //   1391: goto -> 1713
/*     */     //   1394: aload #6
/*     */     //   1396: ldc 'v1_15_R1'
/*     */     //   1398: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1401: ifeq -> 1713
/*     */     //   1404: bipush #8
/*     */     //   1406: istore #7
/*     */     //   1408: goto -> 1713
/*     */     //   1411: aload #6
/*     */     //   1413: ldc 'v1_16_R1'
/*     */     //   1415: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1418: ifeq -> 1713
/*     */     //   1421: bipush #9
/*     */     //   1423: istore #7
/*     */     //   1425: goto -> 1713
/*     */     //   1428: aload #6
/*     */     //   1430: ldc_w 'v1_16_R2'
/*     */     //   1433: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1436: ifeq -> 1713
/*     */     //   1439: bipush #10
/*     */     //   1441: istore #7
/*     */     //   1443: goto -> 1713
/*     */     //   1446: aload #6
/*     */     //   1448: ldc_w 'v1_16_R3'
/*     */     //   1451: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1454: ifeq -> 1713
/*     */     //   1457: bipush #11
/*     */     //   1459: istore #7
/*     */     //   1461: goto -> 1713
/*     */     //   1464: aload #6
/*     */     //   1466: ldc_w 'v1_17_R1'
/*     */     //   1469: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1472: ifeq -> 1713
/*     */     //   1475: bipush #12
/*     */     //   1477: istore #7
/*     */     //   1479: goto -> 1713
/*     */     //   1482: aload #6
/*     */     //   1484: ldc_w 'v1_18_R1'
/*     */     //   1487: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1490: ifeq -> 1713
/*     */     //   1493: bipush #13
/*     */     //   1495: istore #7
/*     */     //   1497: goto -> 1713
/*     */     //   1500: aload #6
/*     */     //   1502: ldc_w 'v1_18_R2'
/*     */     //   1505: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1508: ifeq -> 1713
/*     */     //   1511: bipush #14
/*     */     //   1513: istore #7
/*     */     //   1515: goto -> 1713
/*     */     //   1518: aload #6
/*     */     //   1520: ldc_w 'v1_19_R1'
/*     */     //   1523: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1526: ifeq -> 1713
/*     */     //   1529: bipush #15
/*     */     //   1531: istore #7
/*     */     //   1533: goto -> 1713
/*     */     //   1536: aload #6
/*     */     //   1538: ldc_w 'v1_19_R2'
/*     */     //   1541: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1544: ifeq -> 1713
/*     */     //   1547: bipush #16
/*     */     //   1549: istore #7
/*     */     //   1551: goto -> 1713
/*     */     //   1554: aload #6
/*     */     //   1556: ldc_w 'v1_19_R3'
/*     */     //   1559: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1562: ifeq -> 1713
/*     */     //   1565: bipush #17
/*     */     //   1567: istore #7
/*     */     //   1569: goto -> 1713
/*     */     //   1572: aload #6
/*     */     //   1574: ldc_w 'v1_20_R1'
/*     */     //   1577: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1580: ifeq -> 1713
/*     */     //   1583: bipush #18
/*     */     //   1585: istore #7
/*     */     //   1587: goto -> 1713
/*     */     //   1590: aload #6
/*     */     //   1592: ldc_w 'v1_20_R2'
/*     */     //   1595: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1598: ifeq -> 1713
/*     */     //   1601: bipush #19
/*     */     //   1603: istore #7
/*     */     //   1605: goto -> 1713
/*     */     //   1608: aload #6
/*     */     //   1610: ldc_w 'v1_20_R3'
/*     */     //   1613: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1616: ifeq -> 1713
/*     */     //   1619: bipush #20
/*     */     //   1621: istore #7
/*     */     //   1623: goto -> 1713
/*     */     //   1626: aload #6
/*     */     //   1628: ldc_w 'v1_20_R4'
/*     */     //   1631: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1634: ifeq -> 1713
/*     */     //   1637: bipush #21
/*     */     //   1639: istore #7
/*     */     //   1641: goto -> 1713
/*     */     //   1644: aload #6
/*     */     //   1646: ldc_w 'v1_21_R1'
/*     */     //   1649: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1652: ifeq -> 1713
/*     */     //   1655: bipush #22
/*     */     //   1657: istore #7
/*     */     //   1659: goto -> 1713
/*     */     //   1662: aload #6
/*     */     //   1664: ldc_w 'v1_21_R2'
/*     */     //   1667: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1670: ifeq -> 1713
/*     */     //   1673: bipush #23
/*     */     //   1675: istore #7
/*     */     //   1677: goto -> 1713
/*     */     //   1680: aload #6
/*     */     //   1682: ldc_w 'v1_21_R3'
/*     */     //   1685: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1688: ifeq -> 1713
/*     */     //   1691: bipush #24
/*     */     //   1693: istore #7
/*     */     //   1695: goto -> 1713
/*     */     //   1698: aload #6
/*     */     //   1700: ldc_w 'v1_21_R4'
/*     */     //   1703: invokevirtual equals : (Ljava/lang/Object;)Z
/*     */     //   1706: ifeq -> 1713
/*     */     //   1709: bipush #25
/*     */     //   1711: istore #7
/*     */     //   1713: iload #7
/*     */     //   1715: tableswitch default -> 1940, 0 -> 1832, 1 -> 1838, 2 -> 1844, 3 -> 1850, 4 -> 1856, 5 -> 1862, 6 -> 1862, 7 -> 1862, 8 -> 1868, 9 -> 1868, 10 -> 1868, 11 -> 1868, 12 -> 1868, 13 -> 1874, 14 -> 1880, 15 -> 1886, 16 -> 1892, 17 -> 1898, 18 -> 1904, 19 -> 1910, 20 -> 1916, 21 -> 1922, 22 -> 1928, 23 -> 1934, 24 -> 1934, 25 -> 1934
/*     */     //   1832: ldc_w 'ct'
/*     */     //   1835: goto -> 1957
/*     */     //   1838: ldc_w 'cu'
/*     */     //   1841: goto -> 1957
/*     */     //   1844: ldc_w 'cy'
/*     */     //   1847: goto -> 1957
/*     */     //   1850: ldc_w 'cz'
/*     */     //   1853: goto -> 1957
/*     */     //   1856: ldc_w 'cH'
/*     */     //   1859: goto -> 1957
/*     */     //   1862: ldc_w 'cU'
/*     */     //   1865: goto -> 1957
/*     */     //   1868: ldc_w 'getRaisedHand'
/*     */     //   1871: goto -> 1957
/*     */     //   1874: ldc_w 'eM'
/*     */     //   1877: goto -> 1957
/*     */     //   1880: ldc_w 'eN'
/*     */     //   1883: goto -> 1957
/*     */     //   1886: ldc_w 'eU'
/*     */     //   1889: goto -> 1957
/*     */     //   1892: ldc_w 'fa'
/*     */     //   1895: goto -> 1957
/*     */     //   1898: ldc_w 'ff'
/*     */     //   1901: goto -> 1957
/*     */     //   1904: ldc_w 'fj'
/*     */     //   1907: goto -> 1957
/*     */     //   1910: ldc_w 'fn'
/*     */     //   1913: goto -> 1957
/*     */     //   1916: ldc_w 'fo'
/*     */     //   1919: goto -> 1957
/*     */     //   1922: ldc_w 'fw'
/*     */     //   1925: goto -> 1957
/*     */     //   1928: ldc_w 'fs'
/*     */     //   1931: goto -> 1957
/*     */     //   1934: ldc_w 'fA'
/*     */     //   1937: goto -> 1957
/*     */     //   1940: new java/lang/IllegalStateException
/*     */     //   1943: dup
/*     */     //   1944: aload_1
/*     */     //   1945: invokevirtual getReleaseName : ()Ljava/lang/String;
/*     */     //   1948: <illegal opcode> makeConcatWithConstants : (Ljava/lang/String;)Ljava/lang/String;
/*     */     //   1953: invokespecial <init> : (Ljava/lang/String;)V
/*     */     //   1956: athrow
/*     */     //   1957: iconst_0
/*     */     //   1958: anewarray java/lang/Class
/*     */     //   1961: invokevirtual getMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
/*     */     //   1964: astore #5
/*     */     //   1966: aload_3
/*     */     //   1967: aload #4
/*     */     //   1969: aload #5
/*     */     //   1971: <illegal opcode> apply : (Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)Lac/grim/grimac/platform/bukkit/manager/BukkitItemResetHandler$ItemUsageHandGetter;
/*     */     //   1976: areturn
/*     */     //   1977: astore_1
/*     */     //   1978: aload_1
/*     */     //   1979: athrow
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #130	-> 0
/*     */     //   #131	-> 12
/*     */     //   #132	-> 28
/*     */     //   #139	-> 34
/*     */     //   #140	-> 41
/*     */     //   #141	-> 56
/*     */     //   #142	-> 70
/*     */     //   #145	-> 78
/*     */     //   #146	-> 97
/*     */     //   #147	-> 116
/*     */     //   #148	-> 916
/*     */     //   #149	-> 922
/*     */     //   #150	-> 928
/*     */     //   #152	-> 934
/*     */     //   #153	-> 940
/*     */     //   #154	-> 946
/*     */     //   #155	-> 952
/*     */     //   #156	-> 958
/*     */     //   #157	-> 964
/*     */     //   #158	-> 970
/*     */     //   #159	-> 976
/*     */     //   #160	-> 982
/*     */     //   #161	-> 988
/*     */     //   #162	-> 994
/*     */     //   #163	-> 1000
/*     */     //   #164	-> 1006
/*     */     //   #147	-> 1027
/*     */     //   #166	-> 1032
/*     */     //   #167	-> 1832
/*     */     //   #168	-> 1838
/*     */     //   #169	-> 1844
/*     */     //   #170	-> 1850
/*     */     //   #171	-> 1856
/*     */     //   #172	-> 1862
/*     */     //   #173	-> 1868
/*     */     //   #174	-> 1874
/*     */     //   #175	-> 1880
/*     */     //   #176	-> 1886
/*     */     //   #177	-> 1892
/*     */     //   #178	-> 1898
/*     */     //   #179	-> 1904
/*     */     //   #180	-> 1910
/*     */     //   #181	-> 1916
/*     */     //   #182	-> 1922
/*     */     //   #183	-> 1928
/*     */     //   #184	-> 1934
/*     */     //   #185	-> 1940
/*     */     //   #166	-> 1961
/*     */     //   #188	-> 1966
/*     */     //   #128	-> 1977
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   56	22	2	getHandle	Ljava/lang/reflect/Method;
/*     */     //   70	8	3	isUsingItem	Ljava/lang/reflect/Method;
/*     */     //   12	1965	1	version	Lac/grim/grimac/shaded/com/github/retrooper/packetevents/manager/server/ServerVersion;
/*     */     //   97	1880	2	nmsPackage	Ljava/lang/String;
/*     */     //   116	1861	3	getHandle	Ljava/lang/reflect/Method;
/*     */     //   1032	945	4	isUsingItem	Ljava/lang/reflect/Method;
/*     */     //   1966	11	5	getUsingItemHand	Ljava/lang/reflect/Method;
/*     */     //   1978	2	1	$ex	Ljava/lang/Throwable;
/*     */     //   0	1980	0	this	Lac/grim/grimac/platform/bukkit/manager/BukkitItemResetHandler;
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	33	1977	java/lang/Throwable
/*     */     //   34	77	1977	java/lang/Throwable
/*     */     //   78	1976	1977	java/lang/Throwable
/*     */   }
/*     */   
/*     */   private static interface ItemUsageReset {
/*     */     void accept(@NotNull Player param1Player) throws Throwable;
/*     */   }
/*     */   
/*     */   private static interface ItemUsageHandGetter {
/*     */     InteractionHand apply(@NotNull Player param1Player) throws Throwable;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\manager\BukkitItemResetHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */