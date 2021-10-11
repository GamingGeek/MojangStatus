package dev.gaminggeek.mojangstatus.commands;

import dev.gaminggeek.mojangstatus.MojangStatus;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import gg.essential.api.utils.GuiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CommandMojangStatus extends Command {
    public CommandMojangStatus(@NotNull String name) {
        super(name);
    }

    @DefaultHandler
    public void handle() {
        GuiUtil.open(Objects.requireNonNull(MojangStatus.getStatusConfig().gui()));
    }
}