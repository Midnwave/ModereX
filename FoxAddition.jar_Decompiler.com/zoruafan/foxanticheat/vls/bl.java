package zoruafan.foxanticheat.vls;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import zoruafan.foxanticheat.FoxAddition;
import zoruafan.foxanticheat.api.FoxAdditionAPI;
import zoruafan.foxanticheat.manager.FilesManager;

public class bl extends CommandExecutor implements Listener {
   private final JavaPlugin plugin;
   private final boolean decayEnabled;
   private final int decayInterval;
   private final int decayAmount;
   private final CommandExecutor commandExecutor;
   FoxAdditionAPI api = FoxAddition.getAPI();
   private Map<Player, Set<String>> executedThresholds = new HashMap();
   private Timer timer;

   public bl(JavaPlugin plugin, FilesManager file) {
      super(plugin, file);
      this.commandExecutor = new CommandExecutor(plugin, file);
      this.plugin = plugin;
      this.decayEnabled = file.getChecks().getBoolean("blocks.decay.enable", true);
      this.decayInterval = file.getChecks().getInt("blocks.decay.interval", 10);
      this.decayAmount = file.getChecks().getInt("blocks.decay.amount", 4);
      if (this.decayEnabled) {
         this.timer = new Timer();
         this.timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
               bl.this.decayVLS();
            }
         }, 1000L * (long)this.decayInterval, 1000L * (long)this.decayInterval);
      }

   }

   public void flag(Player player, int amount) {
      List<String> commandsToExecute = this.commandExecutor.loadVLSCommands(player, "blocks.commands", amount);
      this.commandExecutor.executeCommands(player, commandsToExecute);
      this.executedThresholds.putAll(this.commandExecutor.getCommands("blocks"));
   }

   private void decayVLS() {
      Iterator var2 = Bukkit.getOnlinePlayers().iterator();

      while(true) {
         Player player;
         int currentVLS;
         do {
            if (!var2.hasNext()) {
               return;
            }

            player = (Player)var2.next();
            currentVLS = this.api.getVLS(player, "blocks");
         } while(currentVLS <= 0);

         int newVLS;
         for(newVLS = 0; newVLS < currentVLS; ++newVLS) {
            ((Set)this.executedThresholds.computeIfAbsent(player, (k) -> {
               return new HashSet();
            })).remove("blocks.commands-" + newVLS);
         }

         newVLS = Math.max(0, currentVLS - this.decayAmount);
         this.api.setVLS(player, "blocks", newVLS);
      }
   }

   @EventHandler
   public void onPluginDisable(PluginDisableEvent event) {
      if (event.getPlugin().equals(this.plugin) && this.timer != null) {
         this.timer.cancel();
      }

   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      this.api.setVLS(player, "blocks", 0);
      this.executedThresholds.remove(player);
   }
}
