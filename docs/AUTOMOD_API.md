# ModereX Automod API Documentation

The ModereX Automod API provides a comprehensive interface for other plugins to interact with ModereX's automatic moderation system. This includes chat filters, spam protection, caps filtering, word filters, and anticheat rule management.

## Getting Started

### Accessing the API

```java
import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.automod.api.AutomodAPI;

// Get the API instance
ModereX moderex = (ModereX) Bukkit.getPluginManager().getPlugin("ModereX");
AutomodAPI api = moderex.getAutomodAPI();
```

### Soft Dependency

Add ModereX as a soft dependency in your `plugin.yml`:

```yaml
softdepend: [ModereX]
```

---

## Spam Protection

### Check Status

```java
// Check if spam protection is enabled
boolean enabled = api.isSpamProtectionEnabled();

// Enable or disable spam protection
api.setSpamProtectionEnabled(true);
```

### Configure Settings

```java
// Get current spam configuration
AutomodAPI.SpamConfig config = api.getSpamConfig();
System.out.println("Message limit: " + config.messageCount());
System.out.println("Time window: " + config.timeWindowSeconds() + "s");
System.out.println("Detect similar: " + config.detectSimilar());
System.out.println("Similarity threshold: " + (config.similarityThreshold() * 100) + "%");

// Set new spam configuration
AutomodAPI.SpamConfig newConfig = new AutomodAPI.SpamConfig(
    5,      // messageCount - messages before flagging
    10,     // timeWindowSeconds - time window
    true,   // detectSimilar - detect duplicate messages
    0.75    // similarityThreshold - 75% similarity
);
api.setSpamConfig(newConfig);
```

### Configuration Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `messageCount` | int | 3 | Messages allowed before flagging |
| `timeWindowSeconds` | int | 5 | Time window in seconds |
| `detectSimilar` | boolean | true | Block similar/duplicate messages |
| `similarityThreshold` | double | 0.8 | Similarity threshold (0.0-1.0) |

---

## Caps Filter

### Check Status

```java
// Check if caps filter is enabled
boolean enabled = api.isCapsFilterEnabled();

// Enable or disable caps filter
api.setCapsFilterEnabled(true);
```

### Configure Settings

```java
// Get current caps configuration
AutomodAPI.CapsConfig config = api.getCapsConfig();
System.out.println("Max caps: " + config.maxPercentage() + "%");
System.out.println("Min length: " + config.minLength() + " chars");

// Set new caps configuration
AutomodAPI.CapsConfig newConfig = new AutomodAPI.CapsConfig(
    60,  // maxPercentage - max 60% caps allowed
    8    // minLength - only check messages 8+ chars
);
api.setCapsConfig(newConfig);
```

### Configuration Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `maxPercentage` | int | 70 | Maximum percentage of caps allowed (0-100) |
| `minLength` | int | 10 | Minimum message length to check |

---

## Link Filter

```java
// Check if link filter is enabled
boolean enabled = api.isLinkFilterEnabled();

// Enable or disable link filter
api.setLinkFilterEnabled(true);
```

---

## Creating Custom Rules

### Simple Rule Creation

```java
// Create a simple word filter rule
String ruleId = api.createWordFilterRule("Swear Filter");

// Create a rule with phrases
List<String> phrases = Arrays.asList("badword1", "badword2", "spam phrase");
String ruleId = api.createWordFilterRule("Custom Filter", phrases, false);

// Create a nickname filter rule
String nicknameRuleId = api.createNicknameRule("Bad Nicknames");

// Create nickname rule with phrases
String nicknameRuleId = api.createNicknameRule("Staff Impersonation",
    Arrays.asList("admin", "owner", "mod", "staff"));
```

### Rule Builder (Recommended)

The `RuleBuilder` provides full control over rule configuration:

```java
// Create a comprehensive word filter rule
String ruleId = api.createRule("Advertising Filter")
    .type(AutomodAPI.RuleType.WORD_FILTER)
    .description("Blocks server advertisements and self-promotion")
    .phrases(Arrays.asList("join my server", "discord.gg/", "play."))
    .exclusions(Arrays.asList("play.minecraft", "official"))
    .filterMode(AutomodAPI.FilterMode.CONTAINS_PHRASE)
    .flagAction(AutomodAPI.FlagAction.BLOCK)
    .enabled(true)
    .applyToNicknames(false)
    .autoPunishment(new AutomodAPI.PunishmentConfig(
        AutomodAPI.AutoPunishmentType.MUTE,
        1800000,  // 30 minutes
        2,        // After 2 violations
        600000    // Within 10 minutes
    ))
    .build();

// Create a nickname-only filter
String nicknameRuleId = api.createRule("Inappropriate Names")
    .nicknameOnly(true)
    .description("Blocks inappropriate nicknames")
    .phrases(Arrays.asList("inappropriate", "offensive"))
    .filterMode(AutomodAPI.FilterMode.CONTAINS_PHRASE)
    .flagAction(AutomodAPI.FlagAction.BLOCK)
    .build();

// Create a regex-based filter
String regexRuleId = api.createRule("IP Address Filter")
    .description("Blocks IP addresses in chat")
    .addPhrase("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")
    .filterMode(AutomodAPI.FilterMode.REGEX)
    .flagAction(AutomodAPI.FlagAction.BLOCK)
    .build();
```

### RuleBuilder Methods

| Method | Description |
|--------|-------------|
| `type(RuleType)` | Set rule type (WORD_FILTER or NICKNAME) |
| `description(String)` | Set rule description |
| `phrases(List<String>)` | Set all phrases to filter |
| `addPhrase(String)` | Add a single phrase |
| `exclusions(List<String>)` | Set exclusion phrases |
| `addExclusion(String)` | Add a single exclusion |
| `filterMode(FilterMode)` | Set how phrases are matched |
| `flagAction(FlagAction)` | Set action when triggered |
| `enabled(boolean)` | Enable/disable rule |
| `exactMatch(boolean)` | Match whole message only |
| `applyToNicknames(boolean)` | Also apply to nicknames |
| `nicknameOnly(boolean)` | ONLY apply to nicknames |
| `autoPunishment(PunishmentConfig)` | Configure auto-punishment |
| `build()` | Create and save the rule |

### FilterMode Options

| Mode | Description |
|------|-------------|
| `CONTAINS_PHRASE` | Match if message contains phrase anywhere |
| `EXACT_MESSAGE` | Match only if entire message equals phrase |
| `REGEX` | Use regular expression matching |

### FlagAction Options

| Action | Description |
|--------|-------------|
| `BLOCK` | Block the message entirely |
| `WARN` | Allow but warn the player |
| `MODIFY` | Modify the message (censor) |
| `LOG_ONLY` | Log only, no action |

### Manage Rules

```java
// Get a rule by ID
AutomodAPI.RuleInfo rule = api.getRule("123");

// Get all rules
List<AutomodAPI.RuleInfo> allRules = api.getAllRules();

// Get rules by type
List<AutomodAPI.RuleInfo> wordFilters = api.getRulesByType(AutomodAPI.RuleType.WORD_FILTER);
List<AutomodAPI.RuleInfo> anticheatRules = api.getRulesByType(AutomodAPI.RuleType.ANTICHEAT);

// Enable or disable a rule
api.setRuleEnabled("123", true);

// Delete a rule (built-in rules cannot be deleted)
boolean deleted = api.deleteRule("123");
```

### Update Rule Properties

```java
// Update rule name (custom rules only)
api.setRuleName("ruleId", "New Rule Name");

// Update rule description
api.setRuleDescription("ruleId", "This rule filters spam phrases");

// Change filter mode
api.setRuleFilterMode("ruleId", AutomodAPI.FilterMode.REGEX);

// Set whether rule applies to nicknames
api.setRuleApplyToNicknames("ruleId", true);

// Change the action taken when triggered
api.setRuleFlagAction("ruleId", AutomodAPI.FlagAction.WARN);
```

### Manage Phrases

```java
// Set all phrases for a rule
List<String> newPhrases = Arrays.asList("word1", "word2", "phrase here");
api.setRulePhrases("ruleId", newPhrases);

// Add a single phrase
api.addPhrase("ruleId", "new bad word");

// Remove a phrase
api.removePhrase("ruleId", "word to remove");

// Set exclusion phrases (words that bypass the filter)
List<String> exclusions = Arrays.asList("assessment", "classic");
api.setRuleExclusions("ruleId", exclusions);
```

### RuleInfo Record

```java
public record RuleInfo(
    String id,           // Unique rule ID
    String name,         // Display name
    String description,  // Rule description
    RuleType type,       // SPAM_PROTECTION, CAPS_FILTER, WORD_FILTER, etc.
    boolean enabled,     // Is rule active?
    boolean builtIn,     // Is this a built-in rule?
    List<String> phrases,    // Blacklisted phrases
    List<String> exclusions, // Exclusion phrases
    boolean exactMatch   // Match mode
) {}
```

---

## Auto-Punishment

### Configure Auto-Punishment

```java
// Create punishment configuration
AutomodAPI.PunishmentConfig punishment = new AutomodAPI.PunishmentConfig(
    AutomodAPI.AutoPunishmentType.MUTE,  // type: WARN, MUTE, KICK, BAN
    3600000,  // durationMs: 1 hour (-1 for permanent)
    3,        // triggerCount: violations before punishment
    300000,   // timeWindowMs: 5 minutes
    "Automod: Repeated violations"  // reason (optional)
);

// Apply to a rule
api.setAutoPunishment("ruleId", punishment);

// Get current punishment config
AutomodAPI.PunishmentConfig current = api.getAutoPunishment("ruleId");
if (current != null) {
    System.out.println("Type: " + current.type());
    System.out.println("Triggers after: " + current.triggerCount() + " violations");
}

// Disable auto-punishment
api.setAutoPunishment("ruleId", null);
```

### Punishment Types

| Type | Description |
|------|-------------|
| `WARN` | Issue a warning |
| `MUTE` | Temporarily mute the player |
| `KICK` | Kick from server |
| `BAN` | Ban from server |

---

## Message Processing

### Check Messages

```java
// Check if a message would be filtered (doesn't actually send it)
AutomodAPI.FilterResult result = api.checkMessage(player, "test message here");

if (result.blocked()) {
    System.out.println("Message would be blocked: " + result.reason());
} else if (result.modified()) {
    System.out.println("Message would be modified to: " + result.modifiedMessage());
} else {
    System.out.println("Message is allowed");
}
```

### Check Nicknames

```java
// Check if a nickname would be filtered
AutomodAPI.FilterResult result = api.checkNickname(player, "BadNickname");

if (result.blocked()) {
    player.sendMessage("That nickname is not allowed!");
}
```

### FilterResult Record

```java
public record FilterResult(
    boolean blocked,        // Was the message blocked?
    boolean modified,       // Was the message modified?
    String modifiedMessage, // The modified message (if modified)
    String reason           // Reason for blocking/modifying
) {
    // Check if message was allowed through
    public boolean allowed() {
        return !blocked && !modified;
    }
}
```

---

## Anticheat Rules

ModereX automatically creates automod rules for each anticheat check when an anticheat plugin is detected (Grim, Vulcan, Matrix, etc.).

### Get Anticheat Rules

```java
// Get all anticheat rules
List<AutomodAPI.RuleInfo> acRules = api.getAnticheatRules();

// Get rules for a specific anticheat
List<AutomodAPI.RuleInfo> grimRules = api.getAnticheatRules("grim");
List<AutomodAPI.RuleInfo> vulcanRules = api.getAnticheatRules("vulcan");
```

### Configure Anticheat Thresholds

```java
// Rule ID format: ac_[anticheat]_[checkname]
// Example: ac_grim_reach, ac_grim_speed, ac_vulcan_killaura

// Set threshold for auto-punishment
api.setAnticheatThreshold(
    "ac_grim_reach",  // ruleId
    15,               // alertThreshold - alerts before action
    60                // timeWindowSeconds - reset after 60s
);

// Enable the rule
api.setRuleEnabled("ac_grim_reach", true);

// Configure auto-punishment for anticheat violations
AutomodAPI.PunishmentConfig acPunishment = new AutomodAPI.PunishmentConfig(
    AutomodAPI.AutoPunishmentType.BAN,
    604800000,  // 7 days
    1,          // trigger after 1 threshold breach
    3600000     // 1 hour window
);
api.setAutoPunishment("ac_grim_reach", acPunishment);
```

---

## Utility Methods

### Reload Rules

```java
// Reload all automod rules from database
api.reload();
```

### Clear Player Violations

```java
// Clear violation history for a player
api.clearPlayerViolations(player);

// Or by UUID
api.clearPlayerViolations(playerUuid);
```

---

## Events

ModereX fires Bukkit events that your plugin can listen to. Import from `com.blockforge.moderex.automod.api.AutomodEvent`.

### MessageBlockedEvent

Fired when a message is blocked by automod. Cancellable - cancel to allow the message through.

```java
@EventHandler
public void onMessageBlocked(AutomodEvent.MessageBlockedEvent event) {
    Player player = event.getPlayer();
    String message = event.getMessage();
    String ruleName = event.getRuleName();
    String reason = event.getReason();

    // Allow VIP players to bypass
    if (player.hasPermission("vip.bypass")) {
        event.setCancelled(true);
    }

    getLogger().info(player.getName() + " blocked by " + ruleName + ": " + message);
}
```

### MessageModifiedEvent

Fired when a message is modified (e.g., caps lowered). Cancellable - cancel to use original message.

```java
@EventHandler
public void onMessageModified(AutomodEvent.MessageModifiedEvent event) {
    String original = event.getOriginalMessage();
    String modified = event.getModifiedMessage();

    // Custom modification
    event.setModifiedMessage(modified + " [edited]");

    // Or cancel to use original
    // event.setCancelled(true);
}
```

### ViolationEvent

Fired when a violation is recorded against a player.

```java
@EventHandler
public void onViolation(AutomodEvent.ViolationEvent event) {
    Player player = event.getPlayer();
    int count = event.getViolationCount();
    int threshold = event.getTriggerThreshold();

    if (event.willTriggerPunishment()) {
        getLogger().warning(player.getName() + " will be punished!");
    }
}
```

### AutoPunishmentEvent

Fired when auto-punishment is about to be applied. Cancellable.

```java
@EventHandler
public void onAutoPunishment(AutomodEvent.AutoPunishmentEvent event) {
    Player player = event.getPlayer();
    AutomodAPI.AutoPunishmentType type = event.getPunishmentType();

    // Prevent bans from automod
    if (type == AutomodAPI.AutoPunishmentType.BAN) {
        event.setCancelled(true);
        // Apply mute instead
    }
}
```

### AnticheatAlertEvent

Fired when an anticheat alert is processed through automod.

```java
@EventHandler
public void onAnticheatAlert(AutomodEvent.AnticheatAlertEvent event) {
    String anticheat = event.getAnticheat();  // "Grim"
    String check = event.getCheckName();       // "Reach"
    int violations = event.getViolations();
    double vl = event.getVlLevel();

    getLogger().info(event.getPlayer().getName() + " flagged " +
                     anticheat + ":" + check + " (VL: " + vl + ")");
}
```

### NicknameBlockedEvent

Fired when a nickname is blocked. Cancellable.

```java
@EventHandler
public void onNicknameBlocked(AutomodEvent.NicknameBlockedEvent event) {
    Player player = event.getPlayer();
    String nickname = event.getNickname();

    player.sendMessage("Nickname '" + nickname + "' is not allowed!");
}
```

---

## Complete Example

```java
import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.automod.api.AutomodAPI;
import com.blockforge.moderex.automod.api.AutomodEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class MyPlugin extends JavaPlugin implements Listener {

    private AutomodAPI automodAPI;

    @Override
    public void onEnable() {
        // Check if ModereX is available
        if (Bukkit.getPluginManager().getPlugin("ModereX") == null) {
            getLogger().warning("ModereX not found! Automod features disabled.");
            return;
        }

        // Get the API
        ModereX moderex = (ModereX) Bukkit.getPluginManager().getPlugin("ModereX");
        automodAPI = moderex.getAutomodAPI();

        // Configure automod
        setupAutomod();

        // Register event listeners
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void setupAutomod() {
        // Configure spam protection
        automodAPI.setSpamProtectionEnabled(true);
        automodAPI.setSpamConfig(new AutomodAPI.SpamConfig(4, 8, true, 0.7));

        // Configure caps filter
        automodAPI.setCapsFilterEnabled(true);
        automodAPI.setCapsConfig(new AutomodAPI.CapsConfig(65, 12));

        // Create a custom word filter
        String ruleId = automodAPI.createWordFilterRule(
            "Server Advertising",
            Arrays.asList("join my server", "play.otherserver.com", "discord.gg"),
            false
        );

        // Add auto-punishment
        automodAPI.setAutoPunishment(ruleId, new AutomodAPI.PunishmentConfig(
            AutomodAPI.AutoPunishmentType.MUTE,
            1800000,  // 30 minutes
            2,        // After 2 violations
            600000    // Within 10 minutes
        ));

        // Configure anticheat rules (if Grim is detected)
        if (automodAPI.getAnticheatRules("grim").size() > 0) {
            // Enable reach detection with auto-ban
            automodAPI.setRuleEnabled("ac_grim_reach", true);
            automodAPI.setAnticheatThreshold("ac_grim_reach", 20, 120);
            automodAPI.setAutoPunishment("ac_grim_reach", new AutomodAPI.PunishmentConfig(
                AutomodAPI.AutoPunishmentType.BAN,
                604800000,  // 7 days
                1,
                3600000
            ));
        }

        getLogger().info("Automod configured successfully!");
    }

    @EventHandler
    public void onMessageBlocked(AutomodEvent.MessageBlockedEvent event) {
        // Log blocked messages
        getLogger().info("[Automod] " + event.getPlayer().getName() +
                        " blocked by " + event.getRuleName() +
                        ": " + event.getMessage());
    }

    @EventHandler
    public void onAutoPunishment(AutomodEvent.AutoPunishmentEvent event) {
        // Notify staff about auto-punishments
        String message = "[Automod] " + event.getPlayer().getName() +
                        " auto-" + event.getPunishmentType().name().toLowerCase() +
                        " for: " + event.getReason();

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("myplugin.staff")) {
                staff.sendMessage(message);
            }
        }
    }
}
```

---

## Rule Types Reference

| Type | Description | Built-in |
|------|-------------|----------|
| `SPAM_PROTECTION` | Blocks rapid/duplicate messages | Yes |
| `CAPS_FILTER` | Converts excessive caps to lowercase | Yes |
| `LINK_FILTER` | Blocks URLs and IP addresses | Yes |
| `AFK_KICK` | Kicks inactive players | Yes |
| `WORD_FILTER` | Custom phrase filtering | No |
| `NICKNAME` | Nickname moderation | No |
| `ANTICHEAT` | Per-check anticheat rules | Auto-generated |

---

## Support

- GitHub Issues: [https://github.com/Midnwave/ModereX/issues](https://github.com/Midnwave/ModereX/issues)
- Documentation: [https://github.com/Midnwave/ModereX/wiki](https://github.com/Midnwave/ModereX/wiki)
