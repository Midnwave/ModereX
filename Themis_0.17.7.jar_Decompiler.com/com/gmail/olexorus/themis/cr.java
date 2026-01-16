package com.gmail.olexorus.themis;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import org.bukkit.entity.Player;

public class CR {
   public final Player V;
   private static final long a = kt.a(-71988900802207254L, -611292078690258068L, MethodHandles.lookup().lookupClass()).a(154595253738297L);

   public CR(Player var1) {
      this.V = var1;
   }

   public Player z() {
      return this.V;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         CR var2 = (CR)var1;
         return Objects.equals(this.V, var2.V);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.V});
   }

   public String toString() {
      long var1 = a ^ 52494020749812L;
      return "OnlinePlayer{player=" + this.V + '}';
   }
}
