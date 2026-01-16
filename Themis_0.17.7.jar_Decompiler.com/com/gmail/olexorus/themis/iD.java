package com.gmail.olexorus.themis;

import java.util.Objects;

public abstract class id implements GL {
   protected final z2 f;

   protected id(z2 var1) {
      this.f = var1;
   }

   public al f() {
      if (this.f != null) {
         return this.f.m();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public int f(vL var1) {
      if (this.f != null) {
         return this.f.U(var1);
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public boolean z() {
      return this.f != null;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof id)) {
         return false;
      } else {
         id var2 = (id)var1;
         return this.f != null && var2.f != null ? this.f.m().equals(var2.f.m()) : false;
      }
   }

   public int hashCode() {
      return this.f != null ? Objects.hashCode(this.f.m()) : super.hashCode();
   }

   public String toString() {
      return this.getClass().getSimpleName() + "[" + (this.f == null ? this.hashCode() : this.f.m()) + ']';
   }
}
