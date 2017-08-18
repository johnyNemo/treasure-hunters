package com.mprzypadlo.thserver.application.command.handlers;

import com.mprzypadlo.thserver.application.command.commands.CreatePlayer;
import com.przypadlo.thserver.model.Game;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreatePlayerHandlerTest {

    private CreatePlayerHandler handler;

    private Game gameMock;

    @Before
    public void setUp() {
        gameMock = mock(Game.class);
        handler = new CreatePlayerHandler(gameMock);
    }

    @Test
    public void CreatePlayer_Executes_Domain_Logic() {
        CreatePlayer command = createCreatePlayerCommand();
        handler.handle(command);
        verify(gameMock, times(1)).addPlayer("player-one", "warrior");
    }
        
    private CreatePlayer createCreatePlayerCommand() {
        CreatePlayer command = mock(CreatePlayer.class);
        when(command.playerName()).thenReturn("player-one");
        when(command.playerClass()).thenReturn("warrior");
        return command;
    }
}
