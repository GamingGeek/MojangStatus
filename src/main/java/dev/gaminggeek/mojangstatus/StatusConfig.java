package dev.gaminggeek.mojangstatus;

import club.sk1er.vigilance.Vigilant;
import club.sk1er.vigilance.data.Property;
import club.sk1er.vigilance.data.PropertyType;

import java.io.File;

public class StatusConfig extends Vigilant {

    @Property(
        type = PropertyType.SWITCH,
        name = "All Status Messages",
        description = "If enabled, status notifications will be shown",
        category = "Global",
        subcategory = "General"
    )
    public boolean statusMessages = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Status on World Load",
            description = "Check status when a world is loaded",
            category = "Global",
            subcategory = "Status Checks"
    )
    public boolean statusOnWorldLoad = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Status on Startup",
            description = "Check status on startup",
            category = "Global",
            subcategory = "Status Checks"
    )
    public boolean statusOnStartup = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Status on Main Menu",
            description = "Check status when the main menu is shown",
            category = "Global",
            subcategory = "Status Checks"
    )
    public boolean statusOnMainMenu = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Status on Pause",
            description = "Check status when pausing the game",
            category = "Global",
            subcategory = "Status Checks"
    )
    public boolean statusOnPause = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Debug",
            description = "Always shows notifications for all statuses",
            category = "Global",
            subcategory = "Other"
    )
    public boolean debug = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "No Changes",
            description = "Always show notifications for status for some issues or unavailable",
            category = "Global",
            subcategory = "Other"
    )
    public boolean noChanges = false;

    @Property(
        type = PropertyType.SWITCH,
        name = "Minecraft Website",
        description = "Show status notifications about the Minecraft website",
        category = "Services",
        subcategory = "Minecraft"
    )
    public boolean minecraftWebsite = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Minecraft Sessions",
        description = "Show status notifications about Minecraft sessions",
        category = "Services",
        subcategory = "Minecraft"
    )
    public boolean minecraftSessions = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Minecraft Textures",
        description = "Show status notifications about Minecraft textures",
        category = "Services",
        subcategory = "Minecraft"
    )
    public boolean minecraftTextures = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Mojang Website",
            description = "Show status notifications about Mojang's website",
            category = "Services",
            subcategory = "Mojang"
    )
    public boolean mojangWebsite = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Mojang Accounts",
            description = "Show status notifications about Mojang accounts",
            category = "Services",
            subcategory = "Mojang"
    )
    public boolean mojangAccounts = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Mojang Authentication Server",
            description = "Show status notifications about Mojang's authentication server",
            category = "Services",
            subcategory = "Mojang"
    )
    public boolean mojangAuth = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Mojang Session Server",
            description = "Show status notifications about Mojang's session server",
            category = "Services",
            subcategory = "Mojang"
    )
    public boolean mojangSessions = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Mojang API",
            description = "Show status notifications about Mojang's API",
            category = "Services",
            subcategory = "Mojang"
    )
    public boolean mojangAPI = true;

    public StatusConfig() {
        super(new File("./config/mojangstatus.toml"));
        initialize();

    }
}

