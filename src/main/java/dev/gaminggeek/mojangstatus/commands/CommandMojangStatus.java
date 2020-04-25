package dev.gaminggeek.mojangstatus.commands;

import club.sk1er.mods.core.ModCore;
import club.sk1er.mods.core.command.ModCoreCommand;
import dev.gaminggeek.mojangstatus.MojangStatus;
import net.minecraft.command.ICommandSender;

public class CommandMojangStatus extends ModCoreCommand {
    @Override
    public String getCommandName() {
        return "mojangstatus";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        ModCore.getInstance().getGuiHandler().open(MojangStatus.statusConfig.gui());
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
