package gg.dystellar.core.commands;

import java.awt.Color;
import java.time.format.DateTimeFormatter;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import gg.dystellar.core.common.User;

/**
 * Command that lets you see your or other people's punishments.
 */
public class PunishmentsCommand extends AbstractTargetPlayerCommand {
	private final static Color DARK_AQUA = new Color(0x00AAAA);

    public PunishmentsCommand() {
		super("punishments", "See a player's punishments");
		this.requirePermission("dystellar.punishments");
    }

	@Override
	protected void execute(CommandContext ctx, Ref<EntityStore> r, Ref<EntityStore> r2, PlayerRef p, World w, Store<EntityStore> store) {
		final var user = User.getUser(p).get();

		p.sendMessage(Message.raw(p.getUsername() + "'s Punishments:").color(Color.YELLOW));
		for (final var pun : user.punishments) {
			p.sendMessage(Message.raw("---------------------------"));
			p.sendMessage(Message.join(Message.raw("ID").color(DARK_AQUA), Message.raw(": " + pun.getId()).color(Color.WHITE)));
			p.sendMessage(Message.join(Message.raw("Type").color(DARK_AQUA), Message.raw(": " + pun.getType()).color(Color.WHITE)));
			p.sendMessage(Message.join(Message.raw("Creation Date").color(DARK_AQUA), Message.raw(": " + pun.getCreationDate().format(DateTimeFormatter.BASIC_ISO_DATE)).color(Color.WHITE)));
			p.sendMessage(Message.join(Message.raw("Expiration Date").color(DARK_AQUA), Message.raw(": " + pun.getExpirationDate().map(d -> d.format(DateTimeFormatter.BASIC_ISO_DATE)).orElse("Never")).color(Color.WHITE)));
			p.sendMessage(Message.join(Message.raw("Is Expired").color(DARK_AQUA), Message.raw(": " + (pun.isExpired() ? "Yes" : "No")).color(Color.WHITE)));
			p.sendMessage(Message.join(Message.raw("Reason").color(DARK_AQUA), Message.raw(": " + pun.getReason()).color(Color.WHITE)));
			p.sendMessage(Message.raw("---------------------------"));
		}
	}
}
