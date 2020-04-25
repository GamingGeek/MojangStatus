package dev.gaminggeek.mojangstatus;

import com.google.gson.JsonArray;
import dev.gaminggeek.mojangstatus.commands.CommandCheckStatus;
import dev.gaminggeek.mojangstatus.commands.CommandMojangStatus;
import dev.gaminggeek.mojangstatus.modcore.ModCoreInstaller;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = MojangStatus.MODID, version = MojangStatus.VERSION)
public class MojangStatus {
    public static final String MODID = "mojang_status";
    public static final String VERSION = "1.0";
    public static StatusCheck check = new StatusCheck();
    public static StatusConfig statusConfig;

    // Stores the last retrieved status for checking if the status has changed
    public static JsonArray lastStatus;


    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModCoreInstaller.initializeModCore(Minecraft.getMinecraft().mcDataDir);

        statusConfig = new StatusConfig();
        statusConfig.preload();

        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new CommandMojangStatus());
        ClientCommandHandler.instance.registerCommand(new CommandCheckStatus());

        lastStatus = check.initStatus();
    }

    @EventHandler
    public void onLaunch(FMLLoadCompleteEvent event) {
        if (statusConfig.statusMessages && statusConfig.statusOnStartup && !statusConfig.statusOnMainMenu) {
            if (statusConfig.debug) System.out.println("Checking status due to startup");
            check.checkStatus(lastStatus);
        }
    }

    @SubscribeEvent
    public void onWorldLoad(Load event) {
        if (statusConfig.statusMessages && statusConfig.statusOnWorldLoad) {
            if (statusConfig.debug) System.out.println("Checking status due to world load");
            check.checkStatus(lastStatus);
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiMainMenu) {
            if (statusConfig.statusMessages && statusConfig.statusOnMainMenu) {
                if (statusConfig.debug) System.out.println("Checking status due to main menu");
                check.checkStatus(lastStatus);
            }
        } else if (event.gui instanceof GuiIngameMenu) {
            if (statusConfig.statusMessages && statusConfig.statusOnPause) {
                if (statusConfig.debug) System.out.println("Checking status due to pause screen");
                check.checkStatus(lastStatus);
            }
        }
    }
}
