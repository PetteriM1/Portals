package me.petterim1.portals;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

public class Main extends PluginBase {

    private static final int configVersion = 2;

    public static Config config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        if (config.getInt("configVersion") != configVersion) {
            getLogger().warning("Outdated config file, trying to update it automatically");
            config.set("configVersion", configVersion);
            config.set("resetPosition", false);
            config.save();
            config = getConfig();
        }
        getServer().getScheduler().scheduleDelayedRepeatingTask(this, new Task(), 2, 2);
    }
}

class Task extends Thread {

    @Override
    public void run() {
        for (Player p : Server.getInstance().getOnlinePlayers().values()) {
            if (Main.config.getSections("portals").size() > 0) {
                Main.config.getSections("portals").forEach((s, o) -> {
                    ConfigSection c = (ConfigSection) o;
                    if (p.getLevel().getName().equals(c.getString("world"))) {
                        if (c.getString("rotation").equals("x")) {
                            if (Math.round(p.x * 2) / 2.0 == c.getInt("x") && Math.round(p.z) <= c.getInt("z") + c.getInt("width") && Math.round(p.z) >= c.getInt("z")) {
                                if (p.y >= c.getDouble("y") && p.y <= c.getDouble("y") + c.getDouble("height")) {
                                    if (Main.config.getBoolean("resetPosition")) {
                                        p.teleport(p.getLevel().getSafeSpawn());
                                    }
                                    Server.getInstance().dispatchCommand(p, c.getString("command"));
                                }
                            }
                        } else if (c.getString("rotation").equals("z")) {
                            if (Math.round(p.z * 2) / 2.0 == c.getInt("z") && Math.round(p.x) <= c.getInt("x") + c.getInt("width") && Math.round(p.x) >= c.getInt("x")) {
                                if (p.y >= c.getDouble("y") && p.y <= c.getDouble("y") + c.getDouble("height")) {
                                    if (Main.config.getBoolean("resetPosition")) {
                                        p.teleport(p.getLevel().getSafeSpawn());
                                    }
                                    Server.getInstance().dispatchCommand(p, c.getString("command"));
                                }
                            }
                        } else {
                            Server.getInstance().getLogger().error("Unknown portal rotation: " + c.getString("rotation"));
                        }
                    }
                });
            }
        }
    }
}
