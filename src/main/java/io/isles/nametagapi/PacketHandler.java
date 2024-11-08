package io.isles.nametagapi;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;

/**
 * A small wrapper for the PacketPlayOutScoreboardTeam packet.
 * <p>
 * Source: https://github.com/sgtcaze/NametagEdit/blob/master/src
 * /main/java/ca/wacos/nametagedit/PacketPlayOut.java
 * </p>
 * 
 * @author sgtcaze (Original)
 * @author Hyphenical Technologies (Modifiers)
 * @author Jaiden (Removed Reflection to support 1.8 MineralSpigot)
 */
class PacketHandler {

	private final PacketPlayOutScoreboardTeam packet;

	public PacketHandler(String name, String prefix, String suffix, Collection<String> players, int paramInteger) {

		this.packet = new PacketPlayOutScoreboardTeam();
		packet.setA(name);
		packet.setH(paramInteger);

		if (paramInteger == 0 || paramInteger == 2) {
			packet.setB(name);
			packet.setC(prefix);
			packet.setD(suffix);
			packet.setI(1);
		}

		if (paramInteger == 0)
			this.addAll(players);
	}

	public PacketHandler(String name, Collection<String> players, int paramInt) {
		this.packet = new PacketPlayOutScoreboardTeam();

		if (paramInt != 3 && paramInt != 4)
			throw new IllegalArgumentException(
					"Method must be join or leave for player constructor");

		if (players == null || players.isEmpty())
			players = new ArrayList<String>();

		packet.setA(name);
		packet.setH(paramInt);
		this.addAll(players);
	}

	public void sendToPlayer(Player bukkitPlayer) {
		if (bukkitPlayer instanceof CraftPlayer craftPlayer)
			craftPlayer.getHandle().playerConnection.sendPacket(packet);
	}

	@SuppressWarnings("all")
	private void addAll(Collection<String> col) {
		packet.getG().addAll(col);
	}
}