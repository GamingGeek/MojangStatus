package dev.gaminggeek.mojangstatus.commands;

import dev.gaminggeek.mojangstatus.MojangStatus;
import net.modcore.api.commands.DefaultHandler;
import net.modcore.api.commands.Command;
import org.jetbrains.annotations.NotNull;
import net.modcore.api.utils.GuiUtil;

public class CommandMojangStatus extends Command {
    public CommandMojangStatus(@NotNull String name) {
        super(name);
    }

    @DefaultHandler
    public void handle() {
        GuiUtil.open(MojangStatus.statusConfig.gui());
    }
}