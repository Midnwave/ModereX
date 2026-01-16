package com.gmail.olexorus.themis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class NB implements tw<List<bT>> {
   private final tw<List<bT>> R;

   NB() {
      this.R = bT.j.c();
   }

   public List<bT> E(Rc var1, lm<?> var2) {
      if (!(var1 instanceof RT)) {
         return (List)this.R.n(var1, var2);
      } else {
         Map var3 = ((RT)var1).q();
         ArrayList var4 = new ArrayList(var3.size());
         Iterator var5 = var3.entrySet().iterator();

         while(var5.hasNext()) {
            Entry var6 = (Entry)var5.next();
            Iterator var7 = ((List)g9.b.n((Rc)var6.getValue(), var2)).iterator();

            while(var7.hasNext()) {
               String var8 = (String)var7.next();
               var4.add(new bT((String)var6.getKey(), var8, (String)null));
            }
         }

         return var4;
      }
   }

   public Rc L(lm<?> var1, List<bT> var2) {
      return this.R.j(var1, var2);
   }
}
