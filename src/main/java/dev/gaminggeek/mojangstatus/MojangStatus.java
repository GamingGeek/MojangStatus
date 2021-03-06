package dev.gaminggeek.mojangstatus;

import com.google.gson.JsonObject;
import dev.gaminggeek.mojangstatus.commands.CommandCheckStatus;
import dev.gaminggeek.mojangstatus.commands.CommandMojangStatus;
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
import net.modcore.api.ModCoreAPI;

import static org.koin.core.context.ContextFunctionsKt.startKoin;

@Mod(name = "Mojang Status", modid = MojangStatus.MODID, version = MojangStatus.VERSION)
public class MojangStatus {
    public static final String MODID = "mojang_status";
    public static final String VERSION = "1.8";
    public static StatusCheck check = new StatusCheck();
    public static StatusConfig statusConfig;

    // Stores the last retrieved status for checking if the status has changed
    public static JsonObject lastStatus = new JsonObject();

    @EventHandler
    public void init(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        statusConfig = new StatusConfig();
        statusConfig.preload();

        ModCoreAPI.getCommandRegistry().registerCommand(new CommandMojangStatus("mojangstatus"));
        ModCoreAPI.getCommandRegistry().registerCommand(new CommandCheckStatus("checkstatus"));

        check.checkStatus(lastStatus, true);
    }

    @EventHandler
    public void onLaunch(FMLLoadCompleteEvent event) {
        if (statusConfig.statusMessages && statusConfig.statusOnStartup && !statusConfig.statusOnMainMenu) {
            check.checkStatus(lastStatus, true);
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (statusConfig.statusMessages && statusConfig.statusOnWorldLoad) {
            check.checkStatus(lastStatus, true);
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiMainMenu) {
            if (statusConfig.statusMessages && statusConfig.statusOnMainMenu) {
                check.checkStatus(lastStatus, true);
            }
        } else if (event.gui instanceof GuiIngameMenu) {
            if (statusConfig.statusMessages && statusConfig.statusOnPause) {
                check.checkStatus(lastStatus, true);
            }
        }
    }
}
