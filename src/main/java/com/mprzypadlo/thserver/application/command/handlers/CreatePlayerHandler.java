package com.mprzypadlo.thserver.application.command.handlers;

import com.mprzypadlo.thserver.application.Message;
import com.mprzypadlo.thserver.application.MessageBuilder;
import com.mprzypadlo.thserver.application.command.Command;
import com.mprzypadlo.thserver.application.command.CommandHandler;
import com.mprzypadlo.thserver.application.command.commands.CreatePlayer;
import com.przypadlo.thserver.model.game.Game;
import com.mprzypadlo.thserver.application.MessageBus;

public class CreatePlayerHandler implements CommandHandler {

    private final Game game;

    private final MessageBus connections;

    private final MessageBuilder messageBuilder;

    private CreatePlayer createPlayerCommand;

    public CreatePlayerHandler(
            Game game,
            MessageBus connections,
            MessageBuilder messageBuilder
    ) {
        this.game = game;
        this.connections = connections;
        this.messageBuilder = messageBuilder;
    }

    @Override
    public void handle(Command command) {
        try {
            tryCreatePlayer(command);
        } catch (RuntimeException ex) {
            notifyError(ex, command.sourceId());
        }
    }

    private void tryCreatePlayer(Command command) {
        createPlayerCommand = (CreatePlayer) command;
        addPlayerToGame();
        notifySuccess();
    }

    private void notifyError(RuntimeException ex, int sourceId) {
        Message errorMessage = createErrorMessage(ex);
        connections.notify(sourceId, errorMessage);
    }

    private Message createErrorMessage(RuntimeException ex) {
        return messageBuilder
                .message("player-addition-error")
                .contentField("error-message", ex.getMessage())
                .getMessage();
    }

    private void addPlayerToGame() {
        game.addPlayer(
                createPlayerCommand.playerName(),
                createPlayerCommand.playerClass()
        );
    }

    private void notifySuccess() {
        Message successMessage = createSuccessMessage();
        connections.notifyAll(
                successMessage
        );
    }

    private Message createSuccessMessage() {
        return messageBuilder
                .message("player-added")
                .contentField("player-name", createPlayerCommand.playerName())
                .contentField("player-class", createPlayerCommand.playerClass())
                .getMessage();
    }
}
