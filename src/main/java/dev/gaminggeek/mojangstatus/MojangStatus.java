package dev.gaminggeek.mojangstatus;

import com.google.gson.JsonObject;
import dev.gaminggeek.mojangstatus.commands.CommandCheckStatus;
import dev.gaminggeek.mojangstatus.commands.CommandMojangStatus;
import gg.essential.api.EssentialAPI;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(name = "MojangStatus", modid = MojangStatus.MODID, version = MojangStatus.VERSION)
public class MojangStatus {
    public static final String MODID = "mojang_status";
    public static final String VERSION = "2.0.0";
    private static final StatusCheck statusCheck = new StatusCheck();
    private static StatusConfig statusConfig;

    // Stores the last retrieved status for checking if the status has changed
    public static JsonObject lastStatus = new JsonObject();

    @EventHandler
    public void init(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        statusConfig = new StatusConfig();
        statusConfig.preload();

        EssentialAPI.getCommandRegistry().registerCommand(new CommandMojangStatus("mojangstatus"));
        EssentialAPI.getCommandRegistry().registerCommand(new CommandCheckStatus("checkstatus"));

        statusCheck.checkStatus(lastStatus, true);
    }

    @EventHandler
    public void onLaunch(FMLLoadCompleteEvent event) {
        if (statusConfig.statusMessages && statusConfig.statusOnStartup && !statusConfig.statusOnMainMenu) {
            statusCheck.checkStatus(lastStatus, true);
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (statusConfig.statusMessages && statusConfig.statusOnWorldLoad) {
            statusCheck.checkStatus(lastStatus, true);
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiMainMenu) {
            if (statusConfig.statusMessages && statusConfig.statusOnMainMenu) {
                statusCheck.checkStatus(lastStatus, true);
            }
        } else if (event.gui instanceof GuiIngameMenu) {
            if (statusConfig.statusMessages && statusConfig.statusOnPause) {
                statusCheck.checkStatus(lastStatus, true);
            }
        }
    }

    public static StatusCheck getStatusCheck() {
        return statusCheck;
    }

    public static StatusConfig getStatusConfig() {
        return statusConfig;
    }
}
