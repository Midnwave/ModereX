package com.gmail.olexorus.themis;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public class c7<CEC extends c7, I extends bJ> {
   private final Bd Q;
   private final AM L;
   protected final I q;
   private final List<String> M;
   private final int p;
   private final Map<String, Object> U;
   private final Map<String, String> k;
   private final wi n;

   c7(Bd var1, AM var2, I var3, List<String> var4, int var5, Map<String, Object> var6) {
      this.Q = var1;
      this.n = var1.d.C;
      this.L = var2;
      this.q = var3;
      this.M = var4;
      this.p = var5;
      this.U = var6;
      this.k = var2.W();
   }

   public String D() {
      return !this.M.isEmpty() ? (String)this.M.remove(0) : null;
   }

   public String d() {
      return !this.M.isEmpty() ? (String)this.M.remove(this.M.size() - 1) : null;
   }

   public String S() {
      return !this.M.isEmpty() ? (String)this.M.get(0) : null;
   }

   public String C() {
      return !this.M.isEmpty() ? (String)this.M.get(this.M.size() - 1) : null;
   }

   public boolean G() {
      return this.Q.z.length - 1 == this.p;
   }

   public boolean S() {
      return this.L.z();
   }

   public boolean r(String var1) {
      return this.k.containsKey(var1);
   }

   public String d(String var1, String var2) {
      return (String)this.k.getOrDefault(var1, var2);
   }

   public Integer i(String var1, Integer var2) {
      return Oa.L((String)this.k.get(var1), var2);
   }

   public Double q(String var1, Number var2) {
      return Oa.Q((String)this.k.get(var1), var2 != null ? var2.doubleValue() : null);
   }

   public <T extends Annotation> String t(Class<T> var1, int var2) {
      return this.n.I().L(this.L.G(), var1, var2);
   }

   public <T extends Annotation> boolean k(Class<T> var1) {
      return this.n.I().T(this.L.G(), var1);
   }

   public Bd S() {
      return this.Q;
   }

   AM k() {
      return this.L;
   }

   public Parameter s() {
      return this.L.G();
   }

   public I q() {
      return this.q;
   }

   public List<String> e() {
      return this.M;
   }
}
