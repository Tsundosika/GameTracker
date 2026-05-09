package at.tsundosika.gametracker.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "gametracker")
public class GametrackerConfig implements ConfigData {

    public boolean hudEnabled = true;

    @ConfigEntry.Gui.Excluded
    public int hudX = 4;

    @ConfigEntry.Gui.Excluded
    public int hudY = 4;

    public boolean showPerMode = true;
}
