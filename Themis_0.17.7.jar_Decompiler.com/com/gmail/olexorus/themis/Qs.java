package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class QS extends lm<QS> {
   private static final List<Entry<String, MI>> i;
   private static final Map<String, MI> U;
   private static final Map<MI, String> e;
   private int D;
   private List<Gf> S;
   private static final long a = kt.a(-6446135112102597209L, 3557938725490139694L, MethodHandles.lookup().lookupClass()).a(119974968945244L);

   public void t() {
      if (this.I.R(zZ.V_1_7_10)) {
         this.D = this.f();
      } else {
         this.D = this.Q();
      }

      this.x();
   }

   protected void x() {
      long var1 = a ^ 119027858106935L;
      int var3;
      if (this.I.i(zZ.V_1_17)) {
         var3 = this.Q();
      } else {
         var3 = this.f();
      }

      this.S = new ArrayList(var3);

      for(int var4 = 0; var4 < var3; ++var4) {
         MI var5;
         if (this.I.i(zZ.V_1_20_5)) {
            var5 = (MI)this.e(NE::L);
         } else if (this.I.i(zZ.V_1_16)) {
            var5 = NE.s(this.R().toString());
         } else {
            String var6 = this.m(64);
            var5 = (MI)U.get(var6);
            if (var5 == null) {
               var5 = NE.s(var6);
            }

            if (var5 == null) {
               throw new IllegalStateException("Can't find attribute for name " + var6 + " (version: " + this.I.name() + ")");
            }
         }

         double var17 = this.o();
         int var8;
         if (this.I.R(zZ.V_1_7_10)) {
            var8 = this.x();
         } else {
            var8 = this.Q();
         }

         ArrayList var9 = new ArrayList(var8);

         for(int var10 = 0; var10 < var8; ++var10) {
            al var11;
            UUID var12;
            if (this.I.i(zZ.V_1_21)) {
               var11 = this.R();
               var12 = lE.v(var11);
            } else {
               var12 = this.V();
               var11 = new al(var12.toString());
            }

            double var13 = this.o();
            byte var15 = this.M();
            ON var16 = ON.VALUES[var15];
            var9.add(new lE(var11, var12, var13, var16));
         }

         this.S.add(new Gf(var5, var17, var9));
      }

   }

   public void d() {
      if (this.I.R(zZ.V_1_7_10)) {
         this.L(this.D);
      } else {
         this.E(this.D);
      }

      if (this.I.i(zZ.V_1_17)) {
         this.E(this.S.size());
      } else {
         this.L(this.S.size());
      }

      Iterator var1 = this.S.iterator();

      while(var1.hasNext()) {
         Gf var2 = (Gf)var1.next();
         if (this.I.i(zZ.V_1_20_5)) {
            this.E(var2.g().f(this.I.u()));
         } else if (this.I.i(zZ.V_1_16)) {
            this.T(var2.g().v(this.I.u()));
         } else {
            String var3 = (String)e.get(var2.g());
            this.I(var3 != null ? var3 : var2.g().f().toString());
         }

         this.v(Gf.s(var2));
         if (this.I.R(zZ.V_1_7_10)) {
            this.f(Gf.s(var2).size());
         } else {
            this.E(Gf.s(var2).size());
         }

         Iterator var5 = Gf.s(var2).iterator();

         while(var5.hasNext()) {
            lE var4 = (lE)var5.next();
            if (this.I.i(zZ.V_1_21)) {
               this.T(lE.B(var4));
            } else {
               this.y(lE.G(var4));
            }

            this.v(lE.P(var4));
            this.u(lE.p(var4).ordinal());
         }
      }

   }

   public void x(QS var1) {
      this.D = var1.D;
      this.S = var1.S;
   }

   private static String lambda$static$0(String var0, String var1) {
      return var0;
   }

   static {
      long var0 = a ^ 42469411846532L;
      i = Collections.unmodifiableList(Arrays.asList(new SimpleEntry("generic.maxHealth", NE.r), new SimpleEntry("Max Health", NE.r), new SimpleEntry("zombie.spawnReinforcements", NE.b), new SimpleEntry("Spawn Reinforcements Chance", NE.b), new SimpleEntry("horse.jumpStrength", NE.X), new SimpleEntry("Jump Strength", NE.X), new SimpleEntry("generic.followRange", NE.rH), new SimpleEntry("Follow Range", NE.rH), new SimpleEntry("generic.knockbackResistance", NE.d), new SimpleEntry("Knockback Resistance", NE.d), new SimpleEntry("generic.movementSpeed", NE.m), new SimpleEntry("Movement Speed", NE.m), new SimpleEntry("generic.flyingSpeed", NE.p), new SimpleEntry("Flying Speed", NE.p), new SimpleEntry("generic.attackDamage", NE.v), new SimpleEntry("generic.attackKnockback", NE.P), new SimpleEntry("generic.attackSpeed", NE.rX), new SimpleEntry("generic.armorToughness", NE.z), new SimpleEntry("generic.armor", NE.t), new SimpleEntry("generic.luck", NE.Y)));
      U = (Map)i.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
      e = (Map)i.stream().collect(Collectors.toMap(Entry::getValue, Entry::getKey, QS::lambda$static$0));
   }
}
