package com.blockforge.moderex.replay;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;

public class ReplaySnapshot {

    private final long timestamp;
    private final double x, y, z;
    private final float yaw, pitch;
    private final boolean sneaking;
    private final boolean sprinting;
    private final boolean swimming;
    private final boolean gliding;
    private final boolean onGround;
    private final ItemStack[] armor;
    private final ItemStack mainHand;
    private final ItemStack offHand;
    private final int heldSlot;
    private final String worldName;
    private final ActionType action;
    private final String actionData;

    public enum ActionType {
        NONE,
        ATTACK,
        INTERACT,
        PLACE_BLOCK,
        BREAK_BLOCK,
        DROP_ITEM,
        CHAT,
        COMMAND,
        DAMAGE_RECEIVED,
        DAMAGE_DEALT,
        // Enhanced action types for detailed logging
        SWING_ARM,
        SNEAK_START,
        SNEAK_END,
        SPRINT_START,
        SPRINT_END,
        JUMP,
        INVENTORY_OPEN,
        INVENTORY_CLOSE,
        ITEM_PICKUP,
        ITEM_USE,
        BOW_SHOOT,
        CONSUME_ITEM,
        FISH_CAST,
        FISH_REEL,
        SHIELD_BLOCK,
        DEATH,
        RESPAWN,
        TELEPORT,
        PORTAL_ENTER,
        // 1.21.11+ Spear actions (safewrapped - only logged if spear exists)
        SPEAR_JAB,
        SPEAR_CHARGE,
        // Equipment changes
        ARMOR_EQUIP,
        OFFHAND_SWAP,
        // Crossbow
        CROSSBOW_SHOOT,
        // Consumables and special items
        WIND_CHARGE_THROW,
        ENDER_PEARL_THROW,
        TRIDENT_THROW,
        EGG_THROW,
        SNOWBALL_THROW,
        POTION_THROW,
        FIREWORK_USE,
        RIPTIDE_USE,
        SHIELD_BLOCK_START,
        SHIELD_BLOCK_END,
        // Entity interactions
        ENTITY_SPAWN,
        ENTITY_KILL,
        ENTITY_INTERACT,
        ENTITY_MOUNT,
        ENTITY_DISMOUNT
    }

    private ReplaySnapshot(Builder builder) {
        this.timestamp = builder.timestamp;
        this.x = builder.x;
        this.y = builder.y;
        this.z = builder.z;
        this.yaw = builder.yaw;
        this.pitch = builder.pitch;
        this.sneaking = builder.sneaking;
        this.sprinting = builder.sprinting;
        this.swimming = builder.swimming;
        this.gliding = builder.gliding;
        this.onGround = builder.onGround;
        this.armor = builder.armor;
        this.mainHand = builder.mainHand;
        this.offHand = builder.offHand;
        this.heldSlot = builder.heldSlot;
        this.worldName = builder.worldName;
        this.action = builder.action;
        this.actionData = builder.actionData;
    }

    public long getTimestamp() { return timestamp; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public boolean isSneaking() { return sneaking; }
    public boolean isSprinting() { return sprinting; }
    public boolean isSwimming() { return swimming; }
    public boolean isGliding() { return gliding; }
    public boolean isOnGround() { return onGround; }
    public ItemStack[] getArmor() { return armor; }
    public ItemStack getMainHand() { return mainHand; }
    public ItemStack getOffHand() { return offHand; }
    public int getHeldSlot() { return heldSlot; }
    public String getWorldName() { return worldName; }
    public ActionType getAction() { return action; }
    public String getActionData() { return actionData; }

    public Location toLocation(org.bukkit.World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    // Use a delimiter that won't appear in base64 or normal data
    private static final String FIELD_DELIMITER = "|||";

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp).append(FIELD_DELIMITER);
        sb.append(String.format("%.3f,%.3f,%.3f,%.2f,%.2f", x, y, z, yaw, pitch)).append(FIELD_DELIMITER);
        sb.append(worldName).append(FIELD_DELIMITER);
        sb.append(encodeFlags()).append(FIELD_DELIMITER);
        sb.append(heldSlot).append(FIELD_DELIMITER);
        sb.append(action.ordinal()).append(FIELD_DELIMITER);
        // Base64 encode actionData to avoid any delimiter issues
        sb.append(actionData != null ? Base64.getEncoder().encodeToString(actionData.getBytes(java.nio.charset.StandardCharsets.UTF_8)) : "").append(FIELD_DELIMITER);
        // Serialize equipment
        sb.append(serializeItem(mainHand)).append(FIELD_DELIMITER);
        sb.append(serializeItem(offHand)).append(FIELD_DELIMITER);
        sb.append(serializeArmor(armor));
        return sb.toString();
    }

    private int encodeFlags() {
        int flags = 0;
        if (sneaking) flags |= 1;
        if (sprinting) flags |= 2;
        if (swimming) flags |= 4;
        if (gliding) flags |= 8;
        if (onGround) flags |= 16;
        return flags;
    }

    /**
     * Serialize an ItemStack to a Base64 string.
     */
    private static String serializeItem(ItemStack item) {
        if (item == null) return "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Deserialize an ItemStack from a Base64 string.
     */
    private static ItemStack deserializeItem(String data) {
        if (data == null || data.isEmpty()) return null;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Serialize armor array to a Base64 string.
     */
    private static String serializeArmor(ItemStack[] armor) {
        if (armor == null) return "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(armor.length);
            for (ItemStack item : armor) {
                dataOutput.writeObject(item);
            }
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Deserialize armor array from a Base64 string.
     */
    private static ItemStack[] deserializeArmor(String data) {
        if (data == null || data.isEmpty()) return null;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            int length = dataInput.readInt();
            ItemStack[] armor = new ItemStack[length];
            for (int i = 0; i < length; i++) {
                armor[i] = (ItemStack) dataInput.readObject();
            }
            dataInput.close();
            return armor;
        } catch (Exception e) {
            return null;
        }
    }

    public static ReplaySnapshot deserialize(String data) {
        // Try new format first (with ||| delimiter)
        if (data.contains(FIELD_DELIMITER)) {
            return deserializeNewFormat(data);
        }
        // Fall back to old format (with ; delimiter) for backwards compatibility
        return deserializeOldFormat(data);
    }

    private static ReplaySnapshot deserializeNewFormat(String data) {
        String[] parts = data.split("\\|\\|\\|", -1);
        if (parts.length < 6) return null;

        try {
            long timestamp = Long.parseLong(parts[0]);
            String[] pos = parts[1].split(",");
            double x = Double.parseDouble(pos[0]);
            double y = Double.parseDouble(pos[1]);
            double z = Double.parseDouble(pos[2]);
            float yaw = Float.parseFloat(pos[3]);
            float pitch = Float.parseFloat(pos[4]);
            String worldName = parts[2];
            int flags = Integer.parseInt(parts[3]);
            int heldSlot = Integer.parseInt(parts[4]);
            ActionType action = ActionType.values()[Integer.parseInt(parts[5])];

            // Decode base64 actionData
            String actionData = null;
            if (parts.length > 6 && !parts[6].isEmpty()) {
                try {
                    actionData = new String(Base64.getDecoder().decode(parts[6]), java.nio.charset.StandardCharsets.UTF_8);
                } catch (Exception e) {
                    actionData = parts[6]; // Fallback if not base64
                }
            }

            // Deserialize equipment
            ItemStack mainHand = parts.length > 7 ? deserializeItem(parts[7]) : null;
            ItemStack offHand = parts.length > 8 ? deserializeItem(parts[8]) : null;
            ItemStack[] armor = parts.length > 9 ? deserializeArmor(parts[9]) : null;

            return new Builder()
                    .timestamp(timestamp)
                    .position(x, y, z, yaw, pitch)
                    .world(worldName)
                    .sneaking((flags & 1) != 0)
                    .sprinting((flags & 2) != 0)
                    .swimming((flags & 4) != 0)
                    .gliding((flags & 8) != 0)
                    .onGround((flags & 16) != 0)
                    .heldSlot(heldSlot)
                    .action(action, actionData)
                    .mainHand(mainHand)
                    .offHand(offHand)
                    .armor(armor)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    private static ReplaySnapshot deserializeOldFormat(String data) {
        String[] parts = data.split(";", 7);
        if (parts.length < 6) return null;

        try {
            long timestamp = Long.parseLong(parts[0]);
            String[] pos = parts[1].split(",");
            double x = Double.parseDouble(pos[0]);
            double y = Double.parseDouble(pos[1]);
            double z = Double.parseDouble(pos[2]);
            float yaw = Float.parseFloat(pos[3]);
            float pitch = Float.parseFloat(pos[4]);
            String worldName = parts[2];
            int flags = Integer.parseInt(parts[3]);
            int heldSlot = Integer.parseInt(parts[4]);
            ActionType action = ActionType.values()[Integer.parseInt(parts[5])];
            String actionData = parts.length > 6 ? parts[6] : null;

            return new Builder()
                    .timestamp(timestamp)
                    .position(x, y, z, yaw, pitch)
                    .world(worldName)
                    .sneaking((flags & 1) != 0)
                    .sprinting((flags & 2) != 0)
                    .swimming((flags & 4) != 0)
                    .gliding((flags & 8) != 0)
                    .onGround((flags & 16) != 0)
                    .heldSlot(heldSlot)
                    .action(action, actionData)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    public static class Builder {
        private long timestamp = System.currentTimeMillis();
        private double x, y, z;
        private float yaw, pitch;
        private boolean sneaking, sprinting, swimming, gliding, onGround;
        private ItemStack[] armor;
        private ItemStack mainHand, offHand;
        private int heldSlot;
        private String worldName;
        private ActionType action = ActionType.NONE;
        private String actionData;

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder position(double x, double y, double z, float yaw, float pitch) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            return this;
        }

        public Builder position(Location loc) {
            this.x = loc.getX();
            this.y = loc.getY();
            this.z = loc.getZ();
            this.yaw = loc.getYaw();
            this.pitch = loc.getPitch();
            this.worldName = loc.getWorld().getName();
            return this;
        }

        public Builder world(String worldName) {
            this.worldName = worldName;
            return this;
        }

        public Builder sneaking(boolean sneaking) {
            this.sneaking = sneaking;
            return this;
        }

        public Builder sprinting(boolean sprinting) {
            this.sprinting = sprinting;
            return this;
        }

        public Builder swimming(boolean swimming) {
            this.swimming = swimming;
            return this;
        }

        public Builder gliding(boolean gliding) {
            this.gliding = gliding;
            return this;
        }

        public Builder onGround(boolean onGround) {
            this.onGround = onGround;
            return this;
        }

        public Builder armor(ItemStack[] armor) {
            this.armor = armor != null ? armor.clone() : null;
            return this;
        }

        public Builder mainHand(ItemStack mainHand) {
            this.mainHand = mainHand != null ? mainHand.clone() : null;
            return this;
        }

        public Builder offHand(ItemStack offHand) {
            this.offHand = offHand != null ? offHand.clone() : null;
            return this;
        }

        public Builder heldSlot(int slot) {
            this.heldSlot = slot;
            return this;
        }

        public Builder action(ActionType action, String data) {
            this.action = action;
            this.actionData = data;
            return this;
        }

        public ReplaySnapshot build() {
            return new ReplaySnapshot(this);
        }
    }
}
