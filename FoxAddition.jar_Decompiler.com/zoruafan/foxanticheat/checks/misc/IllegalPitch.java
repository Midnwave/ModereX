package zoruafan.foxanticheat.checks.misc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.FoliaCompat;

public class IllegalPitch implements Listener {
   private final JavaPlugin plugin;
   private final FilesManager file;
   private Map<UUID, Boolean> kickedPlayers = new HashMap();
   private String path = "misc.modules.illegalpitch";

   public IllegalPitch(JavaPlugin plugin, FilesManager files) {
      this.plugin = plugin;
      this.file = files;
      this.registerPacketListener();
   }

   private void registerPacketListener() {
      ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, ListenerPriority.LOW, new PacketType[]{Client.LOOK, Client.POSITION_LOOK, Client.POSITION}) {
         public void onPacketReceiving(PacketEvent event) {
            if (IllegalPitch.this.file.getChecks().getBoolean(IllegalPitch.this.path + ".enable")) {
               List<String> disabledWorlds = IllegalPitch.this.file.getChecks().getStringList("misc.disabled-worlds");
               Player player = event.getPlayer();
               if (player.isOnline()) {
                  if (!player.hasPermission("foxac.bypass.misc")) {
                     if (disabledWorlds == null || !disabledWorlds.contains(event.getPlayer().getWorld().getName())) {
                        UUID playerUUID = event.getPlayer().getUniqueId();
                        if (!IllegalPitch.this.kickedPlayers.containsKey(playerUUID) || !(Boolean)IllegalPitch.this.kickedPlayers.get(playerUUID)) {
                           Float pitch = (Float)event.getPacket().getFloat().readSafely(1);
                           if (pitch != null) {
                              if (pitch > 90.0F || pitch < -90.0F) {
                                 FoxFlagEvent flagEvent = new FoxFlagEvent(player, "misc", 0, "Invalid Pitch packet!", "Exploits", "Invalid Pitch packet!");
                                 FoliaCompat.runTask(this.plugin, (FA) -> {
                                    Bukkit.getPluginManager().callEvent(flagEvent);
                                    if (!flagEvent.isCancelled()) {
                                       int mode;
                                       try {
                                          mode = IllegalPitch.this.file.getChecks().getInt(IllegalPitch.this.path + ".mode", 2);
                                       } catch (Exception var14) {
                                          mode = 3;
                                       }

                                       event.setCancelled(true);
                                       Location currentLocation = player.getLocation().clone();
                                       if (mode == 1) {
                                          if (currentLocation.getY() > 0.0D) {
                                             Location belowLocation = currentLocation.clone().subtract(0.0D, 1.0D, 0.0D);
                                             Location belowLocation_Fix = currentLocation.clone().subtract(0.0D, 0.1D, 0.0D);
                                             Material belowBlockType = belowLocation.getBlock().getType();
                                             Material belowBlockType_Fix = belowLocation_Fix.getBlock().getType();
                                             if (belowBlockType == Material.AIR) {
                                                currentLocation.subtract(0.0D, 0.1D, 0.0D);
                                                player.teleport(currentLocation);
                                             } else if (belowBlockType_Fix == Material.AIR) {
                                                currentLocation.subtract(0.0D, 0.1D, 0.0D);
                                                player.teleport(currentLocation);
                                             } else if (belowBlockType != Material.AIR && belowBlockType_Fix != Material.AIR) {
                                                player.teleport(currentLocation);
                                             }
                                          }
                                       } else if (mode == 2) {
                                          IllegalPitch.this.kickedPlayers.put(playerUUID, true);

                                          try {
                                             player.teleport(currentLocation);
                                          } catch (IllegalStateException var13) {
                                          }

                                          this.kickPlayer(player);
                                       } else {
                                          IllegalPitch.this.kickedPlayers.put(playerUUID, true);

                                          try {
                                             player.teleport(currentLocation);
                                          } catch (IllegalStateException var12) {
                                          }

                                          this.kickPlayer(player);
                                       }
                                    }

                                 });
                              }

                           }
                        }
                     }
                  }
               }
            }
         }

         private void kickPlayer(Player player) {
            if (player.isOnline()) {
               FoliaCompat.runTask(this.plugin, (FA) -> {
                  this.plugin.getLogger().warning("[IllegalPitch] " + player.getName() + " has been disconnected for IllegalPitch.");
                  player.kickPlayer(IllegalPitch.this.file.getLang("exploits.illegalpitch", player));
               });
            }
         }
      });
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      UUID playerUUID = event.getPlayer().getUniqueId();
      event.getPlayer().getLocation().setPitch(0.0F);
      if (this.kickedPlayers.containsKey(playerUUID) && (Boolean)this.kickedPlayers.get(playerUUID)) {
         Vector direction = player.getLocation().getDirection();
         double x = direction.getX();
         double z = direction.getZ();
         double yaw = Math.atan2(-x, z);
         player.getLocation().setYaw((float)Math.toDegrees(yaw));
         player.getLocation().setPitch(0.0F);
         this.kickedPlayers.put(playerUUID, false);
      }

   }
}
