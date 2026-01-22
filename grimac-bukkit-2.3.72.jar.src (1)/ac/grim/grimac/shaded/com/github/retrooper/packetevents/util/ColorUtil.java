/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*     */ 
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
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
/*     */ public class ColorUtil
/*     */ {
/*     */   public static String toString(NamedTextColor color) {
/*  26 */     String prefix = "§";
/*  27 */     if (color == null) {
/*  28 */       return prefix + "f";
/*     */     }
/*  30 */     switch (color.toString()) {
/*     */       case "black":
/*  32 */         return prefix + "0";
/*     */       case "dark_blue":
/*  34 */         return prefix + "1";
/*     */       case "dark_green":
/*  36 */         return prefix + "2";
/*     */       case "dark_aqua":
/*  38 */         return prefix + "3";
/*     */       case "dark_red":
/*  40 */         return prefix + "4";
/*     */       case "dark_purple":
/*  42 */         return prefix + "5";
/*     */       case "gold":
/*  44 */         return prefix + "6";
/*     */       case "gray":
/*  46 */         return prefix + "7";
/*     */       case "dark_gray":
/*  48 */         return prefix + "8";
/*     */       case "blue":
/*  50 */         return prefix + "9";
/*     */       case "green":
/*  52 */         return prefix + "a";
/*     */       case "aqua":
/*  54 */         return prefix + "b";
/*     */       case "red":
/*  56 */         return prefix + "c";
/*     */       case "light_purple":
/*  58 */         return prefix + "d";
/*     */       case "yellow":
/*  60 */         return prefix + "e";
/*     */       case "white":
/*  62 */         return prefix + "f";
/*     */     } 
/*  64 */     return prefix + "f";
/*     */   }
/*     */ 
/*     */   
/*     */   public static int getId(NamedTextColor color) {
/*  69 */     if (color == null) {
/*  70 */       return -1;
/*     */     }
/*  72 */     switch (color.toString()) {
/*     */       case "black":
/*  74 */         return 0;
/*     */       case "dark_blue":
/*  76 */         return 1;
/*     */       case "dark_green":
/*  78 */         return 2;
/*     */       case "dark_aqua":
/*  80 */         return 3;
/*     */       case "dark_red":
/*  82 */         return 4;
/*     */       case "dark_purple":
/*  84 */         return 5;
/*     */       case "gold":
/*  86 */         return 6;
/*     */       case "gray":
/*  88 */         return 7;
/*     */       case "dark_gray":
/*  90 */         return 8;
/*     */       case "blue":
/*  92 */         return 9;
/*     */       case "green":
/*  94 */         return 10;
/*     */       case "aqua":
/*  96 */         return 11;
/*     */       case "red":
/*  98 */         return 12;
/*     */       case "light_purple":
/* 100 */         return 13;
/*     */       case "yellow":
/* 102 */         return 14;
/*     */       case "white":
/* 104 */         return 15;
/*     */     } 
/* 106 */     return 15;
/*     */   }
/*     */ 
/*     */   
/*     */   public static NamedTextColor fromId(int id) {
/* 111 */     if (id < 0) {
/* 112 */       return null;
/*     */     }
/* 114 */     switch (id) {
/*     */       case 0:
/* 116 */         return NamedTextColor.BLACK;
/*     */       case 1:
/* 118 */         return NamedTextColor.DARK_BLUE;
/*     */       case 2:
/* 120 */         return NamedTextColor.DARK_GREEN;
/*     */       case 3:
/* 122 */         return NamedTextColor.DARK_AQUA;
/*     */       case 4:
/* 124 */         return NamedTextColor.DARK_RED;
/*     */       case 5:
/* 126 */         return NamedTextColor.DARK_PURPLE;
/*     */       case 6:
/* 128 */         return NamedTextColor.GOLD;
/*     */       case 7:
/* 130 */         return NamedTextColor.GRAY;
/*     */       case 8:
/* 132 */         return NamedTextColor.DARK_GRAY;
/*     */       case 9:
/* 134 */         return NamedTextColor.BLUE;
/*     */       case 10:
/* 136 */         return NamedTextColor.GREEN;
/*     */       case 11:
/* 138 */         return NamedTextColor.AQUA;
/*     */       case 12:
/* 140 */         return NamedTextColor.RED;
/*     */       case 13:
/* 142 */         return NamedTextColor.LIGHT_PURPLE;
/*     */       case 14:
/* 144 */         return NamedTextColor.YELLOW;
/*     */       case 15:
/* 146 */         return NamedTextColor.WHITE;
/*     */     } 
/* 148 */     return NamedTextColor.WHITE;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\ColorUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */