package com.devcomrade.banbot.handler.command;

import com.devcomrade.banbot.components.BotIdentity;
import com.devcomrade.banbot.handler.action.BotAction;
import com.devcomrade.banbot.handler.command.handler.fallbacks.FallbackCommandDispatcher;
import com.devcomrade.banbot.handler.context.CommandContext;
import com.devcomrade.banbot.handler.context.UpdateContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandFacade {
    private final CommandDispatcher commandDispatcher;
    private final FallbackCommandDispatcher fallbackCommandDispatcher;
    private final BotIdentity botIdentity;

    public List<BotAction> handleCommand(Message message, UpdateContext updateContext) {
        String command = null;
        if (message.getReplyToMessage() != null) {
            // Команды для установки слов
            command = fallbackCommandDispatcher.dispatch(message, updateContext.chat());
        } else if (message.getNewChatMembers() != null && !message.getNewChatMembers().isEmpty()) {
            if (message.getNewChatMembers().stream().anyMatch(u -> botIdentity.username().equals(u.getUserName()))) {
                command = "bot_added_to_chat";
            }
        } else if (message.getText() != null && message.getText().startsWith("/")) {
            command = parseCommand(message.getText(), botIdentity.username());
        }

        var ctx = new CommandContext(message, command, message.getText(), updateContext);

        return commandDispatcher.dispatch(ctx);
    }

    private String parseCommand(String message, String botName) {
        if (message == null || message.trim().isEmpty()) {
            return null;
        }

        // The first token is command
        var tokens = message.trim().split("\\s+", 2);
        if (tokens.length == 0) {
            return null;
        }

        var commandToken = tokens[0];

        // Check if it's a command (starts with /)
        if (!commandToken.startsWith("/")) {
            return null;
        }

        // Remove / from command
        var command = commandToken.substring(1);

        // Remove bot name from command
        if (botName != null && !botName.isEmpty()) {
            command = command.replace("@" + botName, "");
        }

        return command;
    }
}
