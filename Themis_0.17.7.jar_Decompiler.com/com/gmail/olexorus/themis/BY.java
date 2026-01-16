package com.gmail.olexorus.themis;

import java.util.Map.Entry;

class bY implements vo {
   private X k;
   final Entry V;
   final String Y;

   bY(Entry var1, String var2) {
      this.V = var1;
      this.Y = var2;
   }

   public String N() {
      return (String)this.V.getKey();
   }

   public X n() {
      if (this.k == null) {
         this.k = h.E().k(this.Y);
      }

      return this.k;
   }

   public boolean equals(Object var1) {
      return var1 instanceof vo ? ((vo)var1).N().equals(this.N()) : false;
   }
}
