package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class m_<T extends Rc> extends Rc {
   protected final Ay<T> y;
   protected final List<T> J;
   private static final long a = kt.a(-817311536253214029L, -1859092136454643001L, MethodHandles.lookup().lookupClass()).a(15652004572926L);

   public m_(Ay<T> var1) {
      this.y = var1;
      this.J = new ArrayList();
   }

   public m_(Ay<T> var1, int var2) {
      this.y = var1;
      this.J = new ArrayList(var2);
   }

   public m_(Ay<T> var1, List<T> var2) {
      this.y = var1;
      this.J = new ArrayList();
      this.J.addAll(var2);
   }

   public static m_<RT> T() {
      return new m_(Ay.K);
   }

   public static m_<mZ> L() {
      return new m_(Ay.m);
   }

   public static Ay<?> J(List<? extends Rc> var0) {
      Ay var1 = Ay.Q;
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Rc var3 = (Rc)var2.next();
         if (var1 == Ay.Q) {
            var1 = var3.b();
         } else if (var1 != var3.b()) {
            return Ay.K;
         }
      }

      return var1;
   }

   public Ay<m_> b() {
      return Ay.P;
   }

   public Ay<T> i() {
      return this.y;
   }

   public boolean E() {
      return this.J.isEmpty();
   }

   public int N() {
      return this.J.size();
   }

   public List<T> N() {
      return Collections.unmodifiableList(this.J);
   }

   public T t(int var1) {
      return (Rc)this.J.get(var1);
   }

   public void E(int var1, T var2) {
      this.R(var2);
      this.J.set(var1, var2);
   }

   public void G(int var1, T var2) {
      this.R(var2);
      this.J.add(var1, var2);
   }

   public void Z(T var1) {
      this.R(var1);
      this.J.add(var1);
   }

   public void o(int var1, Rc var2) {
      this.G(var1, var2);
   }

   public void w(Rc var1) {
      this.Z(var1);
   }

   public void F(int var1) {
      this.J.remove(var1);
   }

   protected void R(T var1) {
      long var2 = a ^ 114227751051074L;
      if (this.y != var1.b()) {
         throw new IllegalArgumentException(MessageFormat.format("Invalid tag type. Expected {0}, got {1}.", this.y.q(), var1.getClass()));
      }
   }

   public void e(Rc var1) {
      long var2 = a ^ 140303920604182L;
      if (this.y == var1.b()) {
         this.J.add(var1);
      } else {
         if (this.y != Ay.K) {
            throw new IllegalArgumentException("Can't add or wrap tag " + var1 + " to list of type " + this.y);
         }

         RT var4 = new RT();
         var4.j("", var1);
         this.J.add(var4);
      }

   }

   private static Rc l(RT var0) {
      if (var0.W.size() == 1) {
         Rc var1 = var0.M("");
         if (var1 != null) {
            return var1;
         }
      }

      return var0;
   }

   public List<? extends Rc> e() {
      if (this.y != Ay.K) {
         return new ArrayList(this.J);
      } else {
         ArrayList var1 = new ArrayList(this.J.size());
         Iterator var2 = this.J.iterator();

         while(var2.hasNext()) {
            Rc var3 = (Rc)var2.next();
            if (var3 instanceof RT) {
               var1.add(l((RT)var3));
            } else {
               var1.add(var3);
            }
         }

         return var1;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         m_ var2 = (m_)var1;
         return Objects.equals(this.y, var2.y) && Objects.equals(this.J, var2.J);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.y, this.J});
   }

   public m_<T> E() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.J.iterator();

      while(var2.hasNext()) {
         Rc var3 = (Rc)var2.next();
         var1.add(var3.X());
      }

      return new m_(this.y, var1);
   }

   public String toString() {
      long var1 = a ^ 68852932736079L;
      return "List(" + this.J + ")";
   }
}
