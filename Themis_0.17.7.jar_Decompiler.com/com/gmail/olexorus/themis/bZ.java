package com.gmail.olexorus.themis;

import java.util.AbstractList;
import java.util.List;

public abstract class BZ<E> extends AbstractList<E> implements List<E>, Tz {
   protected BZ() {
   }

   public abstract E set(int var1, E var2);

   public abstract E v(int var1);

   public abstract void add(int var1, E var2);

   public abstract int z();
}
