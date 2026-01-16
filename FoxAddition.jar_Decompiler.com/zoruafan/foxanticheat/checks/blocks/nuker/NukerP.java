package zoruafan.foxanticheat.checks.blocks.nuker;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.ChecksManager;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.FoliaCompat;
import zoruafan.foxanticheat.manager.hooks.GeyserManager;
import zoruafan.foxanticheat.manager.hooks.VeinMinerManager;

public class NukerP extends ChecksManager implements Listener {
   private final JavaPlugin plugin;
   private final FilesManager file;
   private Map<Player, Long> lBT = new HashMap();
   private boolean usFg;
   private GeyserManager fG;
   private boolean uVM;
   private VeinMinerManager vM;
   private final String p = "blocks.modules.nuker.target";
   private int ms = 200;

   public NukerP(JavaPlugin plugin, FilesManager file, boolean uVm, boolean uFg) {
      super(file);
      this.plugin = plugin;
      this.file = file;
      this.ms = file.getChecks().getInt("blocks.modules.nuker.target.force_cancel.ms", 215);
      this.usFg = uFg;
      if (this.usFg) {
         try {
            this.fG = new GeyserManager(file);
         } catch (Exception var7) {
            this.fG = null;
            this.usFg = false;
         }
      } else {
         this.fG = null;
      }

      this.usFg = false;
      this.uVM = uVm;
      if (this.uVM) {
         try {
            this.vM = new VeinMinerManager(plugin);
         } catch (Exception var6) {
         }
      } else {
         this.vM = null;
      }

      this.registerPacketListener();
   }

   private void registerPacketListener() {
      ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, ListenerPriority.HIGH, new PacketType[]{Client.BLOCK_DIG}) {
         public void onPacketReceiving(PacketEvent event) {
            Player e = event.getPlayer();
            if (!NukerP.this.iAD(e, "blocks")) {
               if (NukerP.this.usFg && NukerP.this.fG != null) {
                  if (!NukerP.this.fG.isAllowDetect_Mode(e, "blocks.modules.nuker.target.detect")) {
                     return;
                  }
               } else if (!NukerP.this.file.getChecks().getBoolean("blocks.modules.nuker.target.detect.java", true)) {
                  return;
               }

               if (event.getPlayer().getType() == EntityType.PLAYER) {
                  PlayerDigType dT = (PlayerDigType)event.getPacket().getPlayerDigTypes().readSafely(0);
                  if (dT == PlayerDigType.STOP_DESTROY_BLOCK) {
                     BlockPosition bP = (BlockPosition)event.getPacket().getBlockPositionModifier().readSafely(0);
                     int x = bP.getX();
                     int y = bP.getY();
                     int z = bP.getZ();
                     Block b = e.getWorld().getBlockAt(x, y, z);
                     if (!NukerP.this.file.getChecks().getBoolean("blocks.modules.nuker.target.blocklist.enable", false) || NukerP.this.isAllowed(e, b.getType())) {
                        if (!NukerP.this.uVM || NukerP.this.vM == null || !NukerP.this.vM.isInVeinMiner(e, b)) {
                           Location tL = this.gPTL(e);
                           if (tL != null) {
                              Block tB = tL.getBlock();
                              if (b != null && tB != null) {
                                 double d = tB.getLocation().distance(b.getLocation());
                                 double dX = (double)Math.abs(tB.getX() - b.getX());
                                 double dZ = (double)Math.abs(tB.getZ() - b.getZ());
                                 double ds = Math.sqrt(dX * dX + dZ * dZ);
                                 if (!b.equals(tB) && d >= 0.3D && ds >= 1.42D) {
                                    if (!e.isOnline()) {
                                       return;
                                    }

                                    FoliaCompat.runTask(this.plugin, (FA) -> {
                                       Material bType = b.getType();
                                       Material tBType = tB.getType();
                                       if (bType != null && tBType != null) {
                                          FoxFlagEvent flagEvent = new FoxFlagEvent(e, "blocks", NukerP.this.file.getChecks().getInt("blocks.modules.nuker.target.vls", 5), "Trying to break blocks without target it.", "Nuker (Target)", "[T1=" + b.getType() + "] [T2=" + tB.getType() + "]");
                                          Bukkit.getPluginManager().callEvent(flagEvent);
                                          if (!flagEvent.isCancelled()) {
                                             event.setCancelled(true);
                                             if (NukerP.this.file.getChecks().getBoolean("blocks.modules.nuker.target.force_cancel.enable", true)) {
                                                NukerP.this.startCooldown(e);
                                             }
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
         }

         private Location gPTL(Player player) {
            BlockIterator iterator = new BlockIterator(player, 100);

            while(iterator.hasNext()) {
               Block block = iterator.next();
               if (block != null) {
                  World world = block.getWorld();
                  if (world != null && block.getType() != Material.AIR && block.getType().isSolid()) {
                     return block.getLocation();
                  }
               }
            }

            return null;
         }
      });
   }

   private boolean isAllowed(Player e, Material block) {
      List<String> list = this.file.getChecks().getStringList("blocks.modules.nuker.target.blocklist.list");
      String type = this.file.getConfig().getString("blocks.modules.nuker.target.blocklist.type", "whitelist");
      if (type.equalsIgnoreCase("whitelist")) {
         return list.contains(block.toString().toUpperCase());
      } else if (type.equalsIgnoreCase("blacklist")) {
         return !list.contains(block.toString().toUpperCase());
      } else {
         this.plugin.getLogger().severe("[NUKER] Invalid option in 'type'. Use 'whitelist'/'blacklist' for this option.");
         this.plugin.getLogger().severe("[NUKER] The nuker checker don't works correctly.");
         return false;
      }
   }

   private void startCooldown(Player player) {
      this.lBT.put(player, System.currentTimeMillis() + (long)this.ms);
   }

   @EventHandler
   public void onBlockBreak(BlockBreakEvent event) {
      Player e = event.getPlayer();
      if (this.lBT.containsKey(e)) {
         long lastBreakTime = (Long)this.lBT.get(e);
         long currentTime = System.currentTimeMillis();
         if (currentTime - lastBreakTime < (long)this.ms) {
            this.startCooldown(e);
            event.setCancelled(true);
         } else {
            this.lBT.remove(e);
         }
      }
   }
}
