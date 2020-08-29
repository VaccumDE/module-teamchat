package bungee.vaccum.module.teamchat;

import bungee.vaccum.api.vBungeeAPI;
import bungee.vaccum.module.teamchat.commands.TeamChatCommand;
import bungee.vaccum.module.teamchat.listener.JoinListener;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class TeamChat extends Plugin {

    private static TeamChat instance;
    private vBungeeAPI bungeeAPI;
    private ArrayList<UUID> teamChatList;

    public static String prefix = "§7» §9TeamChat §8| §7";

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        bungeeAPI = vBungeeAPI.getInstance();

        getProxy().getPluginManager().registerCommand(this, new TeamChatCommand("teamchat"));
        getProxy().getPluginManager().registerCommand(this, new TeamChatCommand("tc"));
        getProxy().getPluginManager().registerListener(this, new JoinListener());

        getProxy().getConsole().sendMessage("§7[" + getDescription().getName() + "] Loading...");
        getProxy().getConsole().sendMessage("§7[" + getDescription().getName() + "] Successfully version " + getDescription().getVersion() + " loaded");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static TeamChat getInstance() {
        return instance;
    }

    public vBungeeAPI getBungeeAPI() { return bungeeAPI; }
}
