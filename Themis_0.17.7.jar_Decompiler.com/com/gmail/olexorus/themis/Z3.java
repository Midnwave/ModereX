package com.gmail.olexorus.themis;

import io.netty.buffer.ByteBuf;

public class z3 implements uV {
   public void x(Object var1, Object var2) {
      if (lC.u(var1)) {
         if (CE.s() && var2 instanceof ByteBuf) {
            ((ByteBuf)var2).retain();
         }

         lC.A(var1, var2);
      } else {
         ((ByteBuf)var2).release();
      }

   }
}
