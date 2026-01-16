package com.gmail.olexorus.themis;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

final class nf extends n9 implements Gg<Integer, z0<? extends Entity, ? extends ConcurrentLinkedQueue<z0<? extends Integer, ? extends BoundingBox>>>> {
   final aE O;
   final int I;

   nf(aE var1, int var2) {
      super(1);
      this.O = var1;
      this.I = var2;
   }

   public final z0<Entity, ConcurrentLinkedQueue<z0<Integer, BoundingBox>>> d(Integer var1) {
      Integer var2 = (Integer)var1[0];
      return new z0(wd.p(((Player)this.O.O()).getWorld(), this.I), new ConcurrentLinkedQueue());
   }
}
