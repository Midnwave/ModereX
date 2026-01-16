package zoruafan.foxanticheat.manager.hooks;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message.MentionType;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.MessageAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import zoruafan.foxanticheat.manager.FilesManager;

public class DiscordSRVManager {
   private final JavaPlugin plugin;
   private final HashMap<Player, Long> cooldowns;
   private final long cooldownTime;
   private final FilesManager file;
   private final String p = "hooks.discordsrv";

   public DiscordSRVManager(JavaPlugin plugin, long cooldownTime, FilesManager files) {
      this.plugin = plugin;
      this.cooldowns = new HashMap();
      this.cooldownTime = cooldownTime;
      this.file = files;
   }

   public void sendMessageToDiscord(String type, Player player, int vls, String detailed) {
      DiscordSRV discordSRV = DiscordSRV.getPlugin();
      if (!this.hasCooldown(player)) {
         String channel = this.file.getConfig().getString("hooks.discordsrv.channel");
         boolean showTitle = this.file.getConfig().getBoolean("hooks.discordsrv.show.title");
         boolean showUUID = this.file.getConfig().getBoolean("hooks.discordsrv.show.uuid");
         boolean showInformation = this.file.getConfig().getBoolean("hooks.discordsrv.show.information");
         boolean showLocation = this.file.getConfig().getBoolean("hooks.discordsrv.show.location");
         String authorLang = this.file.getConfig().getString("hooks.discordsrv.messages.author");
         String playerLang = this.file.getConfig().getString("hooks.discordsrv.messages.player");
         String uuidLang = this.file.getConfig().getString("hooks.discordsrv.messages.uuid");
         String checkLang = this.file.getConfig().getString("hooks.discordsrv.messages.check");
         String vlsLang = this.file.getConfig().getString("hooks.discordsrv.messages.vls");
         String locationLang = this.file.getConfig().getString("hooks.discordsrv.messages.location");
         String detailedLang = this.file.getConfig().getString("hooks.discordsrv.messages.information");
         String contentLang = this.file.getConfig().getString("hooks.discordsrv.messages.content");
         String playerName = player.getName();
         String vlsString = String.valueOf(vls);
         String skinUrl = "https://crafatar.com/avatars/" + player.getUniqueId().toString();
         EmbedBuilder embedBuilder = new EmbedBuilder();
         embedBuilder.setAuthor(authorLang);
         if (showTitle) {
            embedBuilder.setTitle("FoxAddition", "https://www.spigotmc.org/resources/111260/");
         }

         embedBuilder.addField(playerLang, "`" + playerName + "`", true);
         if (showUUID) {
            UUID playerUUID = player.getUniqueId();
            String uuidString = String.valueOf(playerUUID);
            embedBuilder.addField(uuidLang, "`" + uuidString + "`", true);
         }

         embedBuilder.addField(checkLang, "`" + type + "`", true);
         embedBuilder.addField(vlsLang, "`" + vlsString + "`", true);
         if (showLocation) {
            Location playerPosition = player.getLocation();
            World playerWorld_U = player.getWorld();
            String playerWorld = playerWorld_U.getName();
            double x = playerPosition.getX();
            double y = playerPosition.getY();
            double z = playerPosition.getZ();
            String locX = String.format("%.1f", x);
            String locY = String.format("%.1f", y);
            String locZ = String.format("%.1f", z);
            embedBuilder.addField(locationLang, String.valueOf(playerWorld) + ": `" + locX + "`, `" + locY + "`, `" + locZ + "`", false);
         }

         if (showInformation) {
            embedBuilder.addField(detailedLang, detailed, false);
         }

         embedBuilder.setColor(5793266);
         embedBuilder.setThumbnail(skinUrl);
         MessageEmbed embed = embedBuilder.build();
         TextChannel textChannel = discordSRV.getJda().getTextChannelById(channel);
         if (textChannel != null) {
            try {
               Set<MentionType> allowedMentions = (Set)Arrays.asList(MentionType.USER, MentionType.CHANNEL, MentionType.EMOTE, MentionType.EVERYONE, MentionType.ROLE, MentionType.HERE).stream().collect(Collectors.toSet());
               ((MessageAction)textChannel.sendMessage(contentLang).embed(embed).allowedMentions(allowedMentions)).queue();
            } catch (Exception var35) {
               textChannel.sendMessage(embed).queue();
            }
         } else {
            this.plugin.getLogger().warning("[DISCORDSRV] Channel with ID " + channel + " not found.");
         }

         this.setCooldown(player);
      }
   }

   private boolean hasCooldown(Player player) {
      return this.cooldowns.containsKey(player) && System.currentTimeMillis() < (Long)this.cooldowns.get(player);
   }

   private void setCooldown(Player player) {
      this.cooldowns.put(player, System.currentTimeMillis() + this.cooldownTime);
   }
}
