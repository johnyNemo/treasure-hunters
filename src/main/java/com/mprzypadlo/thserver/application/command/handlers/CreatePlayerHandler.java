package com.mprzypadlo.thserver.application.command.handlers;

import com.mprzypadlo.thserver.application.command.Command;
import com.mprzypadlo.thserver.application.command.CommandHandler;
import com.mprzypadlo.thserver.application.command.commands.CreatePlayer;
import com.przypadlo.thserver.model.Game;


public class CreatePlayerHandler implements CommandHandler {
    
    private Game game;
    
    public CreatePlayerHandler(
            Game game
    ) {
        this.game = game;
    }

    @Override
    public void handle(Command command) {
        CreatePlayer createPlayerCommand = (CreatePlayer) command;
        game.addPlayer(
                createPlayerCommand.playerName(),
                createPlayerCommand.playerClass()
        );
    }
    
}
