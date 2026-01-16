package com.gmail.olexorus.themis;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

class ru implements Iterator<Be<R, C, V>> {
   Iterator<Entry<R, Map<C, V>>> U;
   Iterator<Entry<C, V>> r;
   private Entry<R, Map<C, V>> b;
   private Entry<C, V> I;
   private Be<R, C, V> v;
   final li G;

   ru(li var1) {
      this.G = var1;
      this.U = li.b(this.G).entrySet().iterator();
      this.r = null;
      this.v = this.G();
   }

   private Be<R, C, V> G() {
      if (this.r == null || !this.r.hasNext()) {
         if (!this.U.hasNext()) {
            return null;
         }

         this.b = (Entry)this.U.next();
         this.r = ((Map)this.b.getValue()).entrySet().iterator();
      }

      if (!this.r.hasNext()) {
         return null;
      } else {
         this.I = (Entry)this.r.next();
         return new lx(this.G, this.b, this.I);
      }
   }

   public boolean hasNext() {
      return this.v != null;
   }

   public Be<R, C, V> C() {
      Be var1 = this.v;
      this.v = this.G();
      return var1;
   }

   public void remove() {
      this.r.remove();
   }
}
