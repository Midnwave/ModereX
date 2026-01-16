package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;

final class lb {
   private final CharSequence k;
   private int s;
   private static final long a = kt.a(1523380781098127401L, 5328060505834479488L, MethodHandles.lookup().lookupClass()).a(51352222988337L);

   lb(CharSequence var1) {
      this.k = var1;
   }

   public char z() {
      return this.k.charAt(this.s);
   }

   public char y(int var1) {
      return this.k.charAt(this.s + var1);
   }

   public char N() {
      return this.k.charAt(this.s++);
   }

   public boolean n() {
      ++this.s;
      return this.p();
   }

   public boolean p() {
      return this.s < this.k.length();
   }

   public boolean g(int var1) {
      return this.s + var1 < this.k.length();
   }

   public CharSequence C(char var1) {
      long var2 = a ^ 62862265713779L;
      var1 = Character.toLowerCase(var1);
      int var4 = -1;

      for(int var5 = this.s; var5 < this.k.length(); ++var5) {
         if (this.k.charAt(var5) == '\\') {
            ++var5;
         } else if (Character.toLowerCase(this.k.charAt(var5)) == var1) {
            var4 = var5;
            break;
         }
      }

      if (var4 == -1) {
         throw this.K("No occurrence of " + var1 + " was found");
      } else {
         CharSequence var6 = this.k.subSequence(this.s, var4);
         this.s = var4 + 1;
         return var6;
      }
   }

   public lb X(char var1) {
      long var2 = a ^ 23987133867507L;
      this.L();
      if (!this.p()) {
         throw this.K("Expected character '" + var1 + "' but got EOF");
      } else if (this.z() != var1) {
         throw this.K("Expected character '" + var1 + "' but got '" + this.z() + "'");
      } else {
         this.N();
         return this;
      }
   }

   public boolean N(char var1) {
      this.L();
      if (this.p() && this.z() == var1) {
         this.n();
         return true;
      } else {
         return false;
      }
   }

   public lb L() {
      while(this.p() && Character.isWhitespace(this.z())) {
         this.n();
      }

      return this;
   }

   public WG K(String var1) {
      return new WG(var1, this.k, this.s);
   }
}
