package dev.gaminggeek.mojangstatus.modcore;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion(ForgeVersion.mcVersion)
public class FMLLoadingPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        int initialize = ModCoreInstaller.initialize(Launch.minecraftHome, "1.8.9");

        if (ModCoreInstaller.isErrored() || initialize != 0 && initialize != -1) {
            System.out.println("Failed to load Sk1er Modcore - " + initialize + " - " + ModCoreInstaller.getError());
        }

        if (ModCoreInstaller.isIsRunningModCore())  // If true the classes are loaded
            return new String[]{"club.sk1er.mods.core.forge.ClassTransformer"};

        return new String[]{};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
