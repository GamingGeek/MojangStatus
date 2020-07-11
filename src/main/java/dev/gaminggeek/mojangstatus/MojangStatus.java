package dev.gaminggeek.mojangstatus;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.gaminggeek.mojangstatus.commands.CommandCheckStatus;
import dev.gaminggeek.mojangstatus.commands.CommandMojangStatus;
import dev.gaminggeek.mojangstatus.modcore.ModCoreInstaller;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(name = "Mojang Status", modid = MojangStatus.MODID, version = MojangStatus.VERSION)
public class MojangStatus {
    public static final String MODID = "mojang_status";
    public static final String VERSION = "1.7.1";
    public static StatusCheck check = new StatusCheck();
    public static StatusConfig statusConfig;

    // Stores the last retrieved status for checking if the status has changed
    public static JsonObject lastStatus = new JsonObject();


    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModCoreInstaller.initializeModCore(Minecraft.getMinecraft().mcDataDir);

        statusConfig = new StatusConfig();
        statusConfig.preload();

        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new CommandMojangStatus());
        ClientCommandHandler.instance.registerCommand(new CommandCheckStatus());

        check.checkStatus(lastStatus, true);
    }

    @EventHandler
    public void onLaunch(FMLLoadCompleteEvent event) {
        if (statusConfig.statusMessages && statusConfig.statusOnStartup && !statusConfig.statusOnMainMenu) {
            check.checkStatus(lastStatus, true);
        }
    }

    @SubscribeEvent
    public void onWorldLoad(Load event) {
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
