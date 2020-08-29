package bungee.vaccum.module.teamchat.listener;

import bungee.vaccum.api.cloud.CloudHandler;
import bungee.vaccum.api.cloud.TeamChatManager;
import bungee.vaccum.module.teamchat.TeamChat;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class JoinListener implements Listener {

    TeamChat teamChat = TeamChat.getInstance();
    CloudHandler cloudHandler = teamChat.getBungeeAPI().getCloudHandler();
    TeamChatManager teamChatManager = teamChat.getBungeeAPI().getTeamChatManager();

    private final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);

    @EventHandler
    public void onConnect(PostLoginEvent event) throws Exception {
        ProxiedPlayer proxiedPlayer = event.getPlayer();

        if(teamChatManager.hasPlayerTeamChatAutoLogin(proxiedPlayer)) {
            if(!proxiedPlayer.hasPermission("vaccum.teamchat.use"))
                teamChatManager.setPlayerTeamChatAutoLogin(proxiedPlayer, false);
            else {
                for(UUID uuid : teamChatManager.getOnlineTeamChatPlayers()) {
                    ICloudPlayer cloudPlayer = playerManager.getOnlinePlayer(uuid);

                    if(cloudPlayer != null)
                        cloudPlayer.getPlayerExecutor().sendChatMessage(TeamChat.prefix + cloudHandler.getDisplayName(proxiedPlayer) + " §ahat sich eingeloggt.");
                }
                proxiedPlayer.sendMessage(TeamChat.prefix + "Du wurdest eingeloggt.");
                teamChatManager.setTeamChatStatus(proxiedPlayer, true);
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) throws Exception {
        ProxiedPlayer proxiedPlayer = event.getPlayer();

        if(teamChatManager.isPlayerLoggedIntoTeamChat(proxiedPlayer)) {
            teamChatManager.setTeamChatStatus(proxiedPlayer, false);

            for(UUID uuid : teamChatManager.getOnlineTeamChatPlayers()) {
                ICloudPlayer cloudPlayer = playerManager.getOnlinePlayer(uuid);

                if(cloudPlayer != null) {
                    cloudPlayer.getPlayerExecutor().sendChatMessage(TeamChat.prefix + cloudHandler.getDisplayName(proxiedPlayer) + " §ahat sich ausgeloggt.");
                }
            }
        }
    }

}
