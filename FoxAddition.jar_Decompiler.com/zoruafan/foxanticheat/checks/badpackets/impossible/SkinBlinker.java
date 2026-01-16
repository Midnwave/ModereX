package zoruafan.foxanticheat.checks.badpackets.impossible;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.hooks.GeyserManager;

public class SkinBlinker {
   private final JavaPlugin plugin;
   private final FilesManager file;
   private final boolean spammerToggle;
   private final long spammerInterval;
   private long spammerLatest = 0L;
   private final Map<Player, Integer> changeCount = new HashMap();
   private int skin = -1;
   private boolean hasBeenDetected = false;
   private final HashMap<Player, Long> cooldowns = new HashMap();
   private final long cooldownTime = 250L;
   private boolean useFloodGate;
   private GeyserManager floodgate;
   private Timer timer;
   private final ExecutorService mt = Executors.newSingleThreadExecutor();
   String verbose;
   String type;
   private String path = "badpackets.modules.impossible.modules.skinblinker";

   public SkinBlinker(JavaPlugin plugin, FilesManager files) {
      this.plugin = plugin;
      this.file = files;
      this.spammerToggle = this.file.getChecks().getBoolean(this.path + ".spammer.enable", true);
      this.spammerInterval = this.file.getChecks().getLong(this.path + ".spammer.interval", 1000L);
      if (this.spammerToggle) {
         this.timer = new Timer();
         this.timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
               SkinBlinker.this.decayVLS();
            }
         }, this.spammerInterval, this.spammerInterval);
      }

      this.registerPacketListener();
   }

   private void registerPacketListener() {
      ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, ListenerPriority.HIGH, new PacketType[]{Client.SETTINGS}) {
         public void onPacketReceiving(PacketEvent event) {
            Player player = event.getPlayer();
            if (player.isOnline()) {
               if (!SkinBlinker.this.useFloodGate || !SkinBlinker.this.floodgate.isBedrock(player)) {
                  if (!player.hasPermission("foxac.bypass.badpackets")) {
                     List<String> disabledWorlds = SkinBlinker.this.file.getChecks().getStringList("badpackets.disabled-worlds");
                     if (disabledWorlds == null || !disabledWorlds.contains(player.getWorld().getName())) {
                        try {
                           int field = (Integer)event.getPacket().getIntegers().readSafely(1);
                           if (SkinBlinker.this.skin == -1) {
                              SkinBlinker.this.skin = field;
                           }

                           int currentCount = (Integer)SkinBlinker.this.changeCount.getOrDefault(player, 0);
                           SkinBlinker.this.changeCount.put(player, currentCount + 1);
                           int max = SkinBlinker.this.file.getChecks().getInt(SkinBlinker.this.path + ".spammer.max", 5);
                           long currentTime = System.currentTimeMillis();
                           long intervalNow = currentTime - SkinBlinker.this.spammerLatest;
                           if (SkinBlinker.this.spammerToggle && SkinBlinker.this.skin != field && intervalNow <= SkinBlinker.this.spammerInterval && currentCount >= max && SkinBlinker.this.spammerLatest != 0L) {
                              SkinBlinker.this.spammerLatest = 0L;
                              SkinBlinker.this.verbose = "[count:" + currentCount + "/" + max + "] [interval:" + intervalNow + "/" + SkinBlinker.this.spammerInterval + "] ";
                              SkinBlinker.this.hasBeenDetected = true;
                              SkinBlinker.this.type = "Spammer";
                           } else if (player.isSprinting() && this.isEnabled("sprinting") && SkinBlinker.this.skin != field) {
                              SkinBlinker.this.verbose = "[in:sprint] ";
                              SkinBlinker.this.hasBeenDetected = true;
                              SkinBlinker.this.type = "Actions";
                           } else if ((player.isSneaking() || player.isSleeping() && this.isEnabled("sneaking")) && SkinBlinker.this.skin != field) {
                              SkinBlinker.this.verbose = "[in:sneak] ";
                              SkinBlinker.this.hasBeenDetected = true;
                              SkinBlinker.this.type = "Actions";
                           } else if (player.isBlocking() && this.isEnabled("blocking") && SkinBlinker.this.skin != field) {
                              SkinBlinker.this.verbose = "[in:blocking] ";
                              SkinBlinker.this.hasBeenDetected = true;
                              SkinBlinker.this.type = "Actions";
                           } else {
                              SkinBlinker.this.hasBeenDetected = false;
                              SkinBlinker.this.verbose = "";
                              SkinBlinker.this.type = "";
                           }

                           if (SkinBlinker.this.hasBeenDetected) {
                              if (!player.isOnline()) {
                                 return;
                              }

                              if (SkinBlinker.this.file.getChecks().getBoolean(SkinBlinker.this.path + ".cancel")) {
                                 event.setCancelled(true);
                              }

                              if (SkinBlinker.this.cooldowns.containsKey(player) && System.currentTimeMillis() < (Long)SkinBlinker.this.cooldowns.get(player)) {
                                 return;
                              }

                              SkinBlinker.this.cooldowns.put(player, System.currentTimeMillis() + 250L);
                              CompletableFuture.runAsync(() -> {
                                 SkinBlinker.this.mt.submit(() -> {
                                    FoxFlagEvent flagEvent = new FoxFlagEvent(player, "badpackets", SkinBlinker.this.file.getChecks().getInt(SkinBlinker.this.path + "." + SkinBlinker.this.type.toLowerCase() + ".vls", 2), SkinBlinker.this.verbose + " Skin value: value:" + SkinBlinker.this.skin + "/" + field + "]", "SkinBlinker (" + SkinBlinker.this.type + ") [BadPackets]", SkinBlinker.this.verbose + "[value:" + SkinBlinker.this.skin + "/" + field + "]");
                                    Bukkit.getPluginManager().callEvent(flagEvent);
                                 });
                              });
                              SkinBlinker.this.hasBeenDetected = false;
                           }

                           if (SkinBlinker.this.spammerLatest == 0L) {
                              SkinBlinker.this.spammerLatest = System.currentTimeMillis();
                           }

                           SkinBlinker.this.skin = field;
                        } catch (Exception var11) {
                        }

                     }
                  }
               }
            }
         }

         private boolean isEnabled(String value) {
            return SkinBlinker.this.file.getChecks().getBoolean(SkinBlinker.this.path + ".actions." + value, true);
         }
      });
   }

   private void decayVLS() {
      Iterator var2 = Bukkit.getOnlinePlayers().iterator();

      while(var2.hasNext()) {
         Player player = (Player)var2.next();
         int currentCount = (Integer)this.changeCount.getOrDefault(player, 0);
         if (currentCount > 0) {
            this.changeCount.put(player, 0);
         }
      }

   }

   @EventHandler
   public void onPluginDisable(PluginDisableEvent event) {
      if (event.getPlugin().equals(this.plugin) && this.timer != null) {
         this.timer.cancel();
      }

      if (event.getPlugin().equals(this.plugin)) {
         try {
            this.mt.shutdown();
         } catch (Exception var3) {
         }
      }

   }
}
