package me.gipper1998.spleef.file;

import me.gipper1998.spleef.Spleef;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileManager {

    private Spleef main;
    private FileConfiguration dataConfig = null;
    private File dataConfigFile = null;
    private String name;

    public FileManager(Spleef main, String name) {
        this.main = main;
        this.name = name;
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (dataConfigFile == null) {
            dataConfigFile = new File(main.getDataFolder(), name);
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(dataConfigFile);
        InputStream defConfigStream = main.getResource(name);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            dataConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (dataConfig == null) {
            reloadConfig();
        }
        return dataConfig;
    }

    public void saveConfig() {
        if ((dataConfig == null) || (dataConfigFile == null)) {
            return;
        }
        try {
            getConfig().save(dataConfigFile);
        } catch (IOException e) {
            MessageManager.getInstance().sendConsoleMessage("<prefix> &cFile &d" + name + " &cFailed to load, backup previous data from &d" + name + " &cand try again.");
        }
    }

    public void saveDefaultConfig() {
        if (dataConfigFile == null) {
            dataConfigFile = new File(this.main.getDataFolder(), name);
        }
        if (!dataConfigFile.exists()) {
            main.saveResource(name, false);
        }
    }

}