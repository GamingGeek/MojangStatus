package dev.gaminggeek.mojangstatus.commands;

import dev.gaminggeek.mojangstatus.MojangStatus;
import net.modcore.api.commands.DefaultHandler;
import net.modcore.api.commands.Command;
import org.jetbrains.annotations.NotNull;

public class CommandCheckStatus extends Command {
    public CommandCheckStatus(@NotNull String name) {
        super(name);
    }

    @DefaultHandler
    public void handle() {
        MojangStatus.check.checkStatus(MojangStatus.lastStatus, true);
    }
}
