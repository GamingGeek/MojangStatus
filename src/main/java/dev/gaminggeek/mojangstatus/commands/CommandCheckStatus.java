package dev.gaminggeek.mojangstatus.commands;

import club.sk1er.mods.core.command.ModCoreCommand;
import dev.gaminggeek.mojangstatus.MojangStatus;
import net.minecraft.command.ICommandSender;

public class CommandCheckStatus extends ModCoreCommand {
    @Override
    public String getCommandName() {
        return "checkstatus";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        MojangStatus.check.checkStatus(MojangStatus.lastStatus, true);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
