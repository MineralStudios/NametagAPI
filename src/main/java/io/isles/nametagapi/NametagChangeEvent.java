package io.isles.nametagapi;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;

/**
 * This event is fired whenever a nametag changes via NametagAPI.
 * 
 * @author Levi Webb (Original)
 * @author Hyphenical Technologies (Modifiers)
 */
public class NametagChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final String playerName;
    private String oldPrefix, oldSuffix, newPrefix, newSuffix;
    @Getter
    private NametagChangeType type;
    @Getter
    private NametagChangeReason reason;
    @Getter
    @Setter
    private boolean cancelled;

    /**
     * <b><i>NOTICE:</i></b> This is meant to be an internal event. Manually
     * firing this event is unnecessary as this library handles it.
     * 
     * <br>
     * <br>
     * 
     * Constructs a new NametagChangeEvent with the given properties.
     * 
     * @param playerName The player this event is associated with.
     * @param oldPrefix  The current prefix for the player.
     * @param oldSuffix  The current suffix for the player.
     * @param newPrefix  The prefix to set for the player.
     * @param newSuffix  The suffix to set for the player.
     * @param type       The type of nametag change.
     * @param reason     The reason why the nametag is being changed.
     */
    public NametagChangeEvent(String playerName, String oldPrefix, String oldSuffix, String newPrefix, String newSuffix,
            NametagChangeType type, NametagChangeReason reason) {
        this.playerName = playerName;
        this.oldPrefix = oldPrefix;
        this.oldSuffix = oldSuffix;
        this.newPrefix = newPrefix;
        this.newSuffix = newSuffix;
        this.type = type;
        this.reason = reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Returns the prefix that the player currently has.
     * 
     * @return the current prefix.
     */
    public String getCurrentPrefix() {
        return oldPrefix;
    }

    /**
     * Returns the suffix that the player currently has.
     * 
     * @return The current suffix.
     */
    public String getCurrentSuffix() {
        return oldSuffix;
    }

    /**
     * Returns the prefix that is going to be set if this event is not
     * cancelled.
     * 
     * @return The prefix to set.
     */
    public String getPrefix() {
        return newPrefix;
    }

    /**
     * Returns the suffix that is going to be set if this event is not
     * cancelled.
     * 
     * @return The suffix to set.
     */
    public String getSuffix() {
        return newSuffix;
    }

    /**
     * Sets the prefix to set if this event is not cancelled.
     * 
     * @param prefix The prefix to set.
     */
    public void setPrefix(String prefix) {
        newPrefix = prefix;
    }

    /**
     * Sets the suffix to set if this event is not cancelled.
     * 
     * @param suffix The suffix to set.
     */
    public void setSuffix(String suffix) {
        newSuffix = suffix;
    }

    /**
     * Get the event's {@code HandlerList}.
     * 
     * @return HandlerList of current handlers for this event.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /** Represents the type of change a player's nametag can undergo. */
    public enum NametagChangeType {
        /**
         * The current prefix and suffix is completely overwritten by the new
         * ones.
         */
        HARD,

        /**
         * The current prefix and suffix is set only if the new prefix/suffix is
         * not null/empty. Null and empty prefixes and suffixes will not be set.
         */
        SOFT
    }

    /** Represents the reason or cause for the change of a player's nametag. */
    public enum NametagChangeReason {
        /** Set the player's nametag prefix. */
        SET_PREFIX,

        /** Set the player's nametag suffix. */
        SET_SUFFIX,

        /** The nametag is being set back to vanilla. */
        VANILLA,

        /** Custom nametag change reason. */
        CUSTOM;
    }

}