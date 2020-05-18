package me.petterim1.portals;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

public class Main extends PluginBase {

    private static final int configVersion = 2;

    static ConfigSection portals;

    static boolean resetPosition;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Config config = getConfig();
        if (config.getInt("configVersion") != configVersion) {
            getLogger().warning("Outdated config file, trying to update it automatically");
            config.set("configVersion", configVersion);
            config.set("resetPosition", false);
            config.save(false);
            config = getConfig();
        }
        resetPosition = config.getBoolean("resetPosition");
        portals = config.getSections("portals");
        if (portals.size() > 0) {
            getServer().getScheduler().scheduleDelayedRepeatingTask(this, new Task(), 2, 2);
        } else {
            getLogger().warning("No portals found from the config");
        }
    }
}

class Task implements Runnable {

    @Override
    public void run() {
        for (Player p : Server.getInstance().getOnlinePlayers().values()) {
            Main.portals.forEach((s, o) -> {
                ConfigSection c = (ConfigSection) o;
                if (p.getLevel().getName().equals(c.getString("world"))) {
                    String rotation = c.getString("rotation");
                    if (rotation.equals("x")) {
                        int z = p.getFloorZ();
                        if (p.getFloorX() == c.getInt("x") && z <= c.getInt("z") + c.getInt("width") && z >= c.getInt("z")) {
                            if (p.y >= c.getDouble("y") && p.y <= c.getDouble("y") + c.getDouble("height")) {
                                if (Main.resetPosition) {
                                    p.teleport(p.getLevel().getSafeSpawn());
                                }
                                String cmd = c.getString("command");
                                boolean console = false;
                                if (cmd.startsWith("%consolecommand%")) {
                                    cmd = cmd.replace("%consolecommand%", "").replace("%player%", "\"" + p.getName() + "\"");
                                    console = true;
                                }
                                Server.getInstance().dispatchCommand(console ? Server.getInstance().getConsoleSender() : p, cmd);
                            }
                        }
                    } else if (rotation.equals("z")) {
                        int x = p.getFloorX();
                        if (p.getFloorZ() == c.getInt("z") && x <= c.getInt("x") + c.getInt("width") && x >= c.getInt("x")) {
                            if (p.y >= c.getDouble("y") && p.y <= c.getDouble("y") + c.getDouble("height")) {
                                if (Main.resetPosition) {
                                    p.teleport(p.getLevel().getSafeSpawn());
                                }
                                String cmd = c.getString("command");
                                boolean console = false;
                                if (cmd.startsWith("%consolecommand%")) {
                                    cmd = cmd.replace("%consolecommand%", "").replace("%player%", "\"" + p.getName() + "\"");
                                    console = true;
                                }
                                Server.getInstance().dispatchCommand(console ? Server.getInstance().getConsoleSender() : p, cmd);
                            }
                        }
                    } else {
                        Server.getInstance().getLogger().error("Invalid portal rotation: " + c.getString("rotation"));
                    }
                }
            });
        }
    }
}
