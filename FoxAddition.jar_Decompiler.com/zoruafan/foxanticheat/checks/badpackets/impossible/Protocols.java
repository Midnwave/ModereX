package zoruafan.foxanticheat.checks.badpackets.impossible;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.FilesManager;

public class Protocols implements Listener {
   private final JavaPlugin plugin;
   private final FilesManager file;
   private float lastKAid = -1.0F;
   private final ExecutorService mt = Executors.newSingleThreadExecutor();
   boolean detected = false;
   String verbose;
   String type;
   String detailed;
   int vls;
   private String path = "badpackets.modules.impossible.modules.protocols.modules";

   public Protocols(JavaPlugin plugin, FilesManager files) {
      this.plugin = plugin;
      this.file = files;
      this.registerPacketListener();
   }

   private void registerPacketListener() {
      ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, ListenerPriority.HIGH, new PacketType[]{Client.HELD_ITEM_SLOT, Client.KEEP_ALIVE}) {
         public void onPacketReceiving(PacketEvent event) {
            Player player = event.getPlayer();
            if (!player.hasPermission("foxac.bypass.badpackets")) {
               if (event.getPacketType() == Client.HELD_ITEM_SLOT) {
                  if (event.isCancelled()) {
                     return;
                  }

                  int newSlot = (Integer)event.getPacket().getIntegers().readSafely(0);
                  int oldSlot = player.getInventory().getHeldItemSlot();
                  if (newSlot == oldSlot && Protocols.this.file.getChecks().getBoolean(Protocols.this.path + ".a.enable", true)) {
                     Protocols.this.detected = true;
                     Protocols.this.type = "Protocol (A) [BadPackets]";
                     Protocols.this.verbose = "[slot:" + newSlot + "/old:" + oldSlot + "]";
                     Protocols.this.vls = Protocols.this.file.getChecks().getInt(Protocols.this.path + ".a.vls", 1);
                     Protocols.this.detailed = "Equal slot in new and old.";
                  } else if ((newSlot < 0 || newSlot > 9) && Protocols.this.file.getChecks().getBoolean(Protocols.this.path + ".b.enable", true)) {
                     Protocols.this.detected = true;
                     Protocols.this.type = "Protocol (B) [BadPackets]";
                     Protocols.this.verbose = "[slot:" + newSlot + "/old:" + oldSlot + "]";
                     Protocols.this.vls = Protocols.this.file.getChecks().getInt(Protocols.this.path + ".b.vls", 2);
                     Protocols.this.detailed = "Change slot to an invalid slot.";
                  }
               } else if (event.getPacketType() == Client.KEEP_ALIVE) {
                  try {
                     float id = (float)(Integer)event.getPacket().getIntegers().readSafely(0);
                     if (id < 0.0F && Protocols.this.file.getChecks().getBoolean(Protocols.this.path + ".c.enable", true)) {
                        Protocols.this.detected = true;
                        Protocols.this.type = "Protocol (C) [BadPackets]";
                        Protocols.this.verbose = "[ID:" + id + "]";
                        Protocols.this.vls = Protocols.this.file.getChecks().getInt(Protocols.this.path + ".c.vls", 4);
                        Protocols.this.detailed = "Sending an invalid Keep Alive id.";
                     } else if (id == Protocols.this.lastKAid && (double)id != 0.0D && Protocols.this.file.getChecks().getBoolean(Protocols.this.path + ".d.enable", true)) {
                        Protocols.this.detected = true;
                        Protocols.this.type = "Protocol (D) [BadPackets]";
                        Protocols.this.verbose = "[ID:" + id + "]";
                        Protocols.this.vls = Protocols.this.file.getChecks().getInt(Protocols.this.path + ".d.vls", 4);
                        Protocols.this.detailed = "Duplicated Keep Alive id.";
                     }

                     Protocols.this.lastKAid = id;
                  } catch (Exception var5) {
                  }
               }

               if (Protocols.this.detected) {
                  Protocols.this.detected = false;
                  CompletableFuture.runAsync(() -> {
                     FoxFlagEvent flagEvent = new FoxFlagEvent(player, "badpackets", Protocols.this.vls, Protocols.this.detailed, Protocols.this.type, Protocols.this.verbose);
                     Protocols.this.mt.submit(() -> {
                        Bukkit.getPluginManager().callEvent(flagEvent);
                        if (!flagEvent.isCancelled()) {
                           event.setCancelled(true);
                        }

                     });
                  });
               }
            }
         }
      });
   }

   @EventHandler
   public void onPluginDisable(PluginDisableEvent event) {
      if (event.getPlugin().equals(this.plugin)) {
         try {
            this.mt.shutdown();
         } catch (Exception var3) {
         }
      }

   }
}
