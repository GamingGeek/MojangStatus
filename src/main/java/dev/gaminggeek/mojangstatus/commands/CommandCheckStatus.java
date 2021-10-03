package dev.gaminggeek.mojangstatus.commands;

import dev.gaminggeek.mojangstatus.MojangStatus;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import org.jetbrains.annotations.NotNull;

public class CommandCheckStatus extends Command {
    public CommandCheckStatus(@NotNull String name) {
        super(name);
    }

    @DefaultHandler
    public void handle() {
        MojangStatus.getStatusCheck().checkStatus(MojangStatus.lastStatus, true);
    }
}
