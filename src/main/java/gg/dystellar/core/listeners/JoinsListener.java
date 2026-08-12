package gg.dystellar.core.listeners;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.io.netty.NettyUtil;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;

import gg.dystellar.core.DystellarCore;
import gg.dystellar.core.common.User;
import gg.dystellar.core.common.punishments.Punishment;
import gg.dystellar.core.utils.Utils;


public final class JoinsListener {
	public static void register(JavaPlugin plugin) {
		plugin.getEventRegistry().register(EventPriority.FIRST, PlayerConnectEvent.class, e -> onConnect(e));
		plugin.getEventRegistry().register(EventPriority.LAST, PlayerDisconnectEvent.class, e -> onLeave(e));
	}

	private static void onLeave(PlayerDisconnectEvent e) {
		final var p = e.getPlayerRef();
		final var user = User.users.remove(p.getUuid());
		HytaleServer.SCHEDULED_EXECUTOR.execute(() -> {
			try {
				DystellarCore.getApi().saveUser(user);
				DystellarCore.getLog().atInfo().log("The player '" + user.name + "' with an uuid of '" + user.uuid + "' has been saved correctly!");
			} catch (Exception ex) {
				ex.printStackTrace();
				DystellarCore.getLog().atSevere().log("Failed to save " + user.name + "'s data: " + ex.getMessage());
			}
		});
	}

	private static void onConnect(PlayerConnectEvent e) {
		final var p = e.getPlayerRef();

		CompletableFuture.supplyAsync(() -> {
			try {
				final var format = NettyUtil.getRemoteSocketAddress(p.getPacketHandler().getChannel()).toString();
				final var ip = format.substring(1, format.indexOf(':'));
				return DystellarCore.getApi().playerConnected(p.getUuid().toString(), p.getUsername(), ip);
			} catch (Exception ex) {
				ex.printStackTrace();
				p.getPacketHandler().disconnect(Message.raw(ex.getMessage()));
			}
			return null;
		}, HytaleServer.SCHEDULED_EXECUTOR).thenAccept(user -> {
			final var lang = DystellarCore.getInstance().getLang(user.language);
			user.init(p);

			User.users.put(p.getUuid(), user);
			for (Punishment pun : user.punishments) {
				if (!pun.allowJoinMinigames() && !DystellarCore.getInstance().getSetup().allow_banned_players && !pun.isExpired()) {
					Message msg = Message.empty();
					msg.insertAll(
							Arrays.stream(lang.punishMessage)
							.flatMap(c -> Stream.of(
									c.buildMessageNamedParams("title", pun.getTitle(), "reason", pun.getReason(), "expiration", Utils.getTimeFormat(pun.getExpirationDate().orElse(null))),
									Message.raw("\n")))
							.limit(lang.punishMessage.length * 2 - 1)
							.toArray(Message[]::new));
					p.getPacketHandler().disconnect(msg);
				}
			}
		});
	}
	
	/* TODO:
	public void onJoin(PlayerJoinEvent event) {
		User user = User.get(event.getPlayer());
		user.initializeSettingsPanel(event.getPlayer());
		Bukkit.getScheduler().runTaskLater(DystellarCore.getInstance(), () -> {
			if (user.globalTabComplete) DystellarCore.getInstance().sendPluginMessage(event.getPlayer(), DystellarCore.GLOBAL_TAB_REGISTER);
			if (DystellarCore.PACK_ENABLED) {
				DystellarCore.getInstance().sendPluginMessage(event.getPlayer(), DystellarCore.SHOULD_SEND_PACK);
				if (DystellarCore.DEBUG_MODE) Bukkit.getLogger().info("[Debug] Resource pack request sent to proxy.");
			}
		}, 30L);
	}
	*/

	/* TODO:
	public void clicked(InventoryClickEvent event) {
		User u = User.get(event.getWhoClicked().getUniqueId());
		if (u == null) {
			event.setCancelled(true);
			return;
		}
		if (event.getClickedInventory().equals(u.configManager)) {
			event.setCancelled(true);
			ItemStack i = event.getCurrentItem();
			if (i == null || i.getType() == Material.AIR) return;
			if (i.equals(u.globalChatItem)) u.toggleGlobalChat();
			else if (i.equals(u.pmsItem)) u.togglePms();
			else if (i.equals(u.globalTabCompleteItem)) u.toggleGlobalTabComplete();
			else if (i.equals(u.scoreboardEnabledItem)) u.toggleScoreboard();
			Player p = (Player) event.getWhoClicked();
			p.playSound(p.getLocation(), Sound.CLICK, 1.8f, 1.8f);
		}
	}

	public void onLeave(PlayerQuitEvent event) {
		DystellarCore.getAsyncManager().submit(() -> MariaDB.savePlayerToDatabase(users.get(event.getPlayer().getUniqueId())));
	}

	public void onKick(PlayerKickEvent event) {
		DystellarCore.getAsyncManager().submit(() -> MariaDB.savePlayerToDatabase(users.get(event.getPlayer().getUniqueId())));
	}
	*/
}
