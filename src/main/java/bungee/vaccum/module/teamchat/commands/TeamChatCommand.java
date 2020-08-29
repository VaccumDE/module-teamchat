package bungee.vaccum.module.teamchat.commands;

import bungee.vaccum.api.cloud.CloudHandler;
import bungee.vaccum.api.cloud.TeamChatManager;
import bungee.vaccum.api.vBungeeAPI;
import bungee.vaccum.module.teamchat.TeamChat;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class TeamChatCommand extends Command {

    TeamChat teamChat = TeamChat.getInstance();
    vBungeeAPI bungeeAPI = teamChat.getBungeeAPI();
    CloudHandler cloudHandler = bungeeAPI.getCloudHandler();
    TeamChatManager teamChatManager = bungeeAPI.getTeamChatManager();

    private final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);

    public TeamChatCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

            if(proxiedPlayer.hasPermission("vaccum.teamchat.use")) {
                if(args.length >= 1) {
                    TeamChat.getInstance().getProxy().getScheduler().runAsync(TeamChat.getInstance(), () -> {
                        try {
                            if (args[0].equalsIgnoreCase("login")) {
                                if (!teamChatManager.isPlayerLoggedIntoTeamChat(proxiedPlayer)) {
                                    for(UUID uuid : teamChatManager.getOnlineTeamChatPlayers()) {
                                        ICloudPlayer cloudPlayer = playerManager.getOnlinePlayer(uuid);

                                        if(cloudPlayer != null)
                                            cloudPlayer.getPlayerExecutor().sendChatMessage(TeamChat.prefix + cloudHandler.getDisplayName(proxiedPlayer) + " §ahat sich eingeloggt.");
                                    }

                                    teamChatManager.setTeamChatStatus(proxiedPlayer, true);
                                    proxiedPlayer.sendMessage(TeamChat.prefix + "§aDu wurdest nun eingeloggt.");
                                } else
                                    proxiedPlayer.sendMessage(TeamChat.prefix + "§cDu bist bereits eingeloggt");

                            } else if(args[0].equalsIgnoreCase("logout")) {
                                if(teamChatManager.isPlayerLoggedIntoTeamChat(proxiedPlayer)) {
                                    teamChatManager.setTeamChatStatus(proxiedPlayer, false);

                                    for(UUID uuid : teamChatManager.getOnlineTeamChatPlayers()) {
                                        ICloudPlayer cloudPlayer = playerManager.getOnlinePlayer(uuid);

                                        if(cloudPlayer != null) {
                                            cloudPlayer.getPlayerExecutor().sendChatMessage(TeamChat.prefix + cloudHandler.getDisplayName(proxiedPlayer) + " §ahat sich ausgeloggt.");
                                        }
                                    }
                                    proxiedPlayer.sendMessage(TeamChat.prefix + "§cDu wurdest ausgeloggt.");
                                } else
                                    proxiedPlayer.sendMessage(TeamChat.prefix + "§cDu bist bereits ausgeloggt.");
                            } else if(args[0].equalsIgnoreCase("togglelogin")) {
                                if(teamChatManager.hasPlayerTeamChatAutoLogin(proxiedPlayer)) {
                                    teamChatManager.setPlayerTeamChatAutoLogin(proxiedPlayer, false);
                                    proxiedPlayer.sendMessage(TeamChat.prefix + "Dein Auto-Login wurde §cdeaktiviert.");
                                } else {
                                    teamChatManager.setPlayerTeamChatAutoLogin(proxiedPlayer, true);
                                    proxiedPlayer.sendMessage(TeamChat.prefix + "Dein Auto-Login wurde §aaktiviert.");
                                }
                            } else {
                                if(teamChatManager.getOnlineTeamChatPlayers().contains(proxiedPlayer.getUniqueId())) {
                                    StringBuilder message = new StringBuilder(args[0]);
                                    for(int i = 1; i < args.length; i++) {
                                        if(!args[i].isEmpty()) {
                                            message.append(" ").append(args[i]);
                                        }
                                    }

                                    for(UUID uuid : teamChatManager.getOnlineTeamChatPlayers()) {
                                        ICloudPlayer cloudPlayer = playerManager.getOnlinePlayer(uuid);

                                        if(cloudPlayer != null) {
                                            cloudPlayer.getPlayerExecutor().sendChatMessage(TeamChat.prefix + cloudHandler.getDisplayName(proxiedPlayer) + " §e-> §f" + message);
                                        }
                                    }
                                } else
                                    proxiedPlayer.sendMessage(TeamChat.prefix + "§cDu bist nicht eingeloggt.");
                            }
                        } catch (Exception exception) { exception.printStackTrace(); }
                    });
                } else {
                    proxiedPlayer.sendMessage(TeamChat.prefix + "§cFalsche Eingabe");
                }
            } else
                proxiedPlayer.sendMessage(TeamChat.prefix + "§cDazu hast du keine Berechtigung!");
        }
    }
}
