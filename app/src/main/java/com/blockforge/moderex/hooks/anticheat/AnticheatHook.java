package com.blockforge.moderex.hooks.anticheat;

import com.blockforge.moderex.ModereX;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public abstract class AnticheatHook {

    protected final ModereX plugin;
    protected final String anticheatName;
    protected boolean enabled = false;
    protected Consumer<AnticheatAlert> alertHandler;

    public AnticheatHook(ModereX plugin, String anticheatName) {
        this.plugin = plugin;
        this.anticheatName = anticheatName;
    }

    public abstract boolean hook();

    public abstract void unhook();

    public String getName() {
        return anticheatName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setAlertHandler(Consumer<AnticheatAlert> handler) {
        this.alertHandler = handler;
    }

    protected void handleAlert(AnticheatAlert alert) {
        if (alertHandler != null) {
            alertHandler.accept(alert);
        }
    }

    protected boolean isPluginAvailable(String pluginName) {
        return plugin.getServer().getPluginManager().getPlugin(pluginName) != null;
    }

    public static class AnticheatAlert {
        private final String anticheat;
        private final Player player;
        private final String checkName;
        private final String checkType;
        private final int violations;
        private final double vlLevel;
        private final String details;

        public AnticheatAlert(String anticheat, Player player, String checkName,
                              String checkType, int violations, double vlLevel, String details) {
            this.anticheat = anticheat;
            this.player = player;
            this.checkName = checkName;
            this.checkType = checkType;
            this.violations = violations;
            this.vlLevel = vlLevel;
            this.details = details;
        }

        public String getAnticheat() {
            return anticheat;
        }

        public Player getPlayer() {
            return player;
        }

        public String getCheckName() {
            return checkName;
        }

        public String getCheckType() {
            return checkType;
        }

        public int getViolations() {
            return violations;
        }

        public double getVlLevel() {
            return vlLevel;
        }

        public String getDetails() {
            return details;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s failed %s (%s) VL: %.1f - %s",
                    anticheat, player.getName(), checkName, checkType, vlLevel, details);
        }
    }
}
