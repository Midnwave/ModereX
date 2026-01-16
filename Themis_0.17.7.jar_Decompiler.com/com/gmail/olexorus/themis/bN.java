package com.gmail.olexorus.themis;

public interface bn extends Vj {
   bn L = new Bk((byte)0);
   bn Q = new Bk((byte)1);

   static bn y(byte var0) {
      if (var0 == 0) {
         return L;
      } else {
         return (bn)(var0 == 1 ? Q : new Bk(var0));
      }
   }

   default uX<bn> y() {
      return tq.i;
   }

   byte e();
}
