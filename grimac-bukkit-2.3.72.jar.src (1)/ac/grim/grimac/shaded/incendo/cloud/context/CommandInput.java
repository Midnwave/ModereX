/*     */ package ac.grim.grimac.shaded.incendo.cloud.context;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.ByteRange;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.DoubleRange;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.FloatRange;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.IntRange;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.LongRange;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.ShortRange;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.StringTokenizer;
/*     */ import org.apiguardian.api.API;
/*     */ import org.checkerframework.dataflow.qual.Pure;
/*     */ import org.checkerframework.dataflow.qual.SideEffectFree;
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
/*     */ @API(status = API.Status.EXPERIMENTAL)
/*     */ public interface CommandInput
/*     */ {
/*  48 */   public static final List<String> BOOLEAN_STRICT = Collections.unmodifiableList(Arrays.asList(new String[] { "TRUE", "FALSE" }));
/*  49 */   public static final List<String> BOOLEAN_LIBERAL = Collections.unmodifiableList(Arrays.asList(new String[] { "TRUE", "YES", "ON", "FALSE", "NO", "OFF" }));
/*  50 */   public static final List<String> BOOLEAN_LIBERAL_TRUE = Collections.unmodifiableList(Arrays.asList(new String[] { "TRUE", "YES", "ON" }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static CommandInput of(String input) {
/*  59 */     return new CommandInputImpl(input);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static CommandInput of(Iterable<String> input) {
/*  69 */     return new CommandInputImpl(String.join(" ", (Iterable)input));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static CommandInput empty() {
/*  78 */     return new CommandInputImpl("");
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
/*     */   @Pure
/*     */   default int length() {
/* 105 */     return input().length();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default int remainingLength() {
/* 114 */     return length() - cursor();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default int remainingTokens() {
/* 123 */     int count = (new StringTokenizer(remainingInput(), " ")).countTokens();
/*     */     
/* 125 */     if (remainingInput().endsWith(" ")) {
/* 126 */       return count + 1;
/*     */     }
/* 128 */     return count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default String remainingInput() {
/* 137 */     return input().substring(cursor());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default String readInput() {
/* 146 */     return input().substring(0, cursor());
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
/*     */   @SideEffectFree
/*     */   default boolean hasRemainingInput() {
/* 166 */     return (cursor() < length());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean isEmpty() {
/* 175 */     return isEmpty(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean isEmpty(boolean ignoreWhitespace) {
/* 185 */     return !hasRemainingInput(ignoreWhitespace);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean hasRemainingInput(boolean ignoreWhitespace) {
/* 195 */     if (!hasRemainingInput()) {
/* 196 */       return false;
/*     */     }
/*     */     
/* 199 */     if (ignoreWhitespace) {
/* 200 */       return hasNonWhitespace();
/*     */     }
/*     */     
/* 203 */     return true;
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
/*     */   @SideEffectFree
/*     */   default String peekString(int chars) {
/* 231 */     String remainingInput = remainingInput();
/* 232 */     if (chars > remainingInput.length()) {
/* 233 */       throw new CursorOutOfBoundsException(
/* 234 */           cursor() + chars, 
/* 235 */           length());
/*     */     }
/*     */     
/* 238 */     return remainingInput.substring(0, chars);
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
/*     */   default String read(int chars) {
/* 250 */     String readString = peekString(chars);
/* 251 */     moveCursor(chars);
/* 252 */     return readString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default char peek() {
/* 262 */     if (cursor() >= input().length()) {
/* 263 */       throw new CursorOutOfBoundsException(
/* 264 */           cursor(), 
/* 265 */           length());
/*     */     }
/*     */     
/* 268 */     return input().charAt(cursor());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default char read() {
/* 277 */     char readChar = peek();
/* 278 */     moveCursor(1);
/* 279 */     return readChar;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String peekString() {
/* 288 */     if (!hasRemainingInput()) {
/* 289 */       return "";
/*     */     }
/*     */     
/* 292 */     String remainingInput = remainingInput();
/* 293 */     int indexOfWhitespace = remainingInput.indexOf(' ');
/* 294 */     if (indexOfWhitespace == -1) {
/* 295 */       return remainingInput;
/*     */     }
/*     */     
/* 298 */     StringBuilder builder = new StringBuilder();
/* 299 */     for (int i = 0; i < remainingInput.length(); i++) {
/* 300 */       char currentChar = remainingInput.charAt(i);
/* 301 */       if (Character.isWhitespace(currentChar)) {
/*     */         
/* 303 */         if (builder.length() == 0) {
/*     */           continue;
/*     */         }
/*     */         
/*     */         break;
/*     */       } 
/*     */       
/* 310 */       builder.append(currentChar);
/*     */       continue;
/*     */     } 
/* 313 */     return builder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String readStringSkipWhitespace(boolean preserveSingleSpace) {
/* 324 */     String readString = readString();
/* 325 */     skipWhitespace(preserveSingleSpace);
/* 326 */     return readString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String readStringSkipWhitespace() {
/* 336 */     return readStringSkipWhitespace(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String readString() {
/* 345 */     return skipWhitespace().readUntil(' ');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String readUntil(char separator) {
/* 355 */     if (!hasRemainingInput()) {
/* 356 */       return "";
/*     */     }
/*     */     
/* 359 */     String remainingInput = remainingInput();
/* 360 */     int indexOfWhitespace = remainingInput.indexOf(separator);
/* 361 */     if (indexOfWhitespace == -1) {
/* 362 */       moveCursor(remainingLength());
/* 363 */       return remainingInput;
/*     */     } 
/*     */ 
/*     */     
/* 367 */     return read(indexOfWhitespace);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String readUntilAndSkip(char separator) {
/* 378 */     String readString = readUntil(separator);
/* 379 */     if (readString.isEmpty() || !hasRemainingInput()) {
/* 380 */       return readString;
/*     */     }
/* 382 */     char readChar = read();
/* 383 */     if (readChar != separator) {
/* 384 */       moveCursor(-1);
/*     */     }
/* 386 */     return readString;
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
/*     */   default CommandInput skipWhitespace(int maxSpaces, boolean preserveSingleSpace) {
/* 398 */     if (preserveSingleSpace && remainingLength() == 1 && peek() == ' ') {
/* 399 */       return this;
/*     */     }
/* 401 */     for (int i = 0; i < maxSpaces && hasRemainingInput() && Character.isWhitespace(peek()); i++) {
/* 402 */       read();
/*     */     }
/* 404 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default CommandInput skipWhitespace(int maxSpaces) {
/* 414 */     return skipWhitespace(maxSpaces, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default CommandInput skipWhitespace(boolean preserveSingleSpace) {
/* 424 */     return skipWhitespace(2147483647, preserveSingleSpace);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default CommandInput skipWhitespace() {
/* 433 */     return skipWhitespace(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean hasNonWhitespace() {
/* 442 */     return remainingInput().chars().anyMatch(c -> !Character.isWhitespace(c));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean isValidByte(byte min, byte max) {
/*     */     try {
/* 455 */       byte parsedByte = Byte.parseByte(peekString());
/* 456 */       return (parsedByte >= min && parsedByte <= max);
/* 457 */     } catch (NumberFormatException ignored) {
/* 458 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean isValidByte(ByteRange range) {
/* 470 */     return isValidByte(range.minByte(), range.maxByte());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default byte readByte() {
/* 481 */     return Byte.parseByte(readString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean isValidShort(short min, short max) {
/*     */     try {
/* 494 */       short parsedShort = Short.parseShort(peekString());
/* 495 */       return (parsedShort >= min && parsedShort <= max);
/* 496 */     } catch (NumberFormatException ignored) {
/* 497 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean isValidShort(ShortRange range) {
/* 509 */     return isValidShort(range.minShort(), range.maxShort());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default short readShort() {
/* 520 */     return Short.parseShort(readString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean isValidInteger(int min, int max) {
/*     */     try {
/* 533 */       int parsedInteger = Integer.parseInt(peekString());
/* 534 */       return (parsedInteger >= min && parsedInteger <= max);
/* 535 */     } catch (NumberFormatException ignored) {
/* 536 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean isValidInteger(IntRange range) {
/* 548 */     return isValidInteger(range.minInt(), range.maxInt());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default int readInteger() {
/* 559 */     return Integer.parseInt(readString());
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
/*     */   default int readInteger(int radix) {
/* 571 */     return Integer.parseInt(readString(), radix);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean isValidLong(long min, long max) {
/*     */     try {
/* 584 */       long parsedLong = Long.parseLong(peekString());
/* 585 */       return (parsedLong >= min && parsedLong <= max);
/* 586 */     } catch (NumberFormatException ignored) {
/* 587 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean isValidLong(LongRange range) {
/* 599 */     return isValidLong(range.minLong(), range.maxLong());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default long readLong() {
/* 610 */     return Long.parseLong(readString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean isValidDouble(double min, double max) {
/*     */     try {
/* 623 */       double parsedDouble = Double.parseDouble(peekString());
/* 624 */       return (parsedDouble >= min && parsedDouble <= max);
/* 625 */     } catch (NumberFormatException ignored) {
/* 626 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean isValidDouble(DoubleRange range) {
/* 638 */     return isValidDouble(range.minDouble(), range.maxDouble());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default double readDouble() {
/* 649 */     return Double.parseDouble(readString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean isValidFloat(float min, float max) {
/*     */     try {
/* 662 */       float parsedFloat = Float.parseFloat(peekString());
/* 663 */       return (parsedFloat >= min && parsedFloat <= max);
/* 664 */     } catch (NumberFormatException ignored) {
/* 665 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean isValidFloat(FloatRange range) {
/* 677 */     return isValidFloat(range.minFloat(), range.maxFloat());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default float readFloat() {
/* 688 */     return Float.parseFloat(readString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SideEffectFree
/*     */   default boolean isValidBoolean(boolean liberal) {
/* 699 */     if (liberal) {
/* 700 */       return BOOLEAN_LIBERAL.contains(peekString().toUpperCase(Locale.ROOT));
/*     */     }
/* 702 */     return BOOLEAN_STRICT.contains(peekString().toUpperCase(Locale.ROOT));
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
/*     */   default boolean readBoolean() {
/* 714 */     return BOOLEAN_LIBERAL_TRUE.contains(readString().toUpperCase(Locale.ROOT));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String lastRemainingToken() {
/* 725 */     String remainingInput = remainingInput();
/* 726 */     if (remainingInput.isEmpty() || remainingInput.endsWith(" ")) {
/* 727 */       return "";
/*     */     }
/*     */     
/* 730 */     int lastSpace = remainingInput.lastIndexOf(' ');
/* 731 */     if (lastSpace == -1) {
/* 732 */       return remainingInput;
/*     */     }
/* 734 */     return remainingInput.substring(lastSpace + 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default char lastRemainingCharacter() {
/* 744 */     String lastToken = lastRemainingToken();
/* 745 */     if (lastToken.isEmpty()) {
/* 746 */       throw new CursorOutOfBoundsException(cursor(), length());
/*     */     }
/* 748 */     return lastToken.charAt(lastToken.length() - 1);
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
/*     */   default String difference(CommandInput that, boolean includeTrailingWhitespace) {
/* 767 */     if (!input().equals(that.input())) {
/* 768 */       return input();
/*     */     }
/* 770 */     String difference = input().substring(cursor(), that.cursor());
/* 771 */     if (!includeTrailingWhitespace && difference.endsWith(" ")) {
/* 772 */       return difference.substring(0, difference.length() - 1);
/*     */     }
/* 774 */     return difference;
/*     */   }
/*     */   @Pure
/*     */   String input();
/*     */   @SideEffectFree
/*     */   int cursor();
/*     */   CommandInput appendString(String paramString);
/*     */   void moveCursor(int paramInt);
/*     */   CommandInput cursor(int paramInt);
/*     */   
/*     */   default String difference(CommandInput that) {
/* 785 */     return difference(that, false);
/*     */   }
/*     */   
/*     */   CommandInput copy();
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static class CursorOutOfBoundsException
/*     */     extends NoSuchElementException
/*     */   {
/*     */     CursorOutOfBoundsException(int cursor, int length) {
/* 795 */       super(
/* 796 */           String.format("Cursor exceeds input length (%d > %d)", new Object[] {
/*     */               
/* 798 */               Integer.valueOf(cursor), 
/* 799 */               Integer.valueOf(length - 1)
/*     */             }));
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\context\CommandInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */