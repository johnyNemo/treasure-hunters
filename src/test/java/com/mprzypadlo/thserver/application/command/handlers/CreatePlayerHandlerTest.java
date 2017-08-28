package com.mprzypadlo.thserver.application.command.handlers;

import com.mprzypadlo.thserver.application.Message;
import com.mprzypadlo.thserver.application.MessageBuilder;
import com.mprzypadlo.thserver.application.command.commands.CreatePlayer;
import com.przypadlo.thserver.model.game.Game;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.mprzypadlo.thserver.application.MessageBus;

public class CreatePlayerHandlerTest {

    private CreatePlayerHandler handler;

    private Game gameMock;

    private MessageBus connectionsMock;

    private MessageBuilder builderMock;

    @Before
    public void setUp() {
        gameMock = mock(Game.class);
        builderMock = mock(MessageBuilder.class);
        connectionsMock = mock(MessageBus.class);
        handler = new CreatePlayerHandler(
                gameMock,
                connectionsMock,
                builderMock
        );
    }

    @Test
    public void CreatePlayer_Executes_Domain_Logic() {
        configureMessageBuilderMock();
        handleCommand();
        verify(gameMock, times(1)).addPlayer("player-one", "warrior");
    }

    @Test
    public void CreatePlayer_Sends_Message_On_Success() {
        Message message = configureMessageBuilderMock();
        handleCommand();
        verify(connectionsMock, times(1)).notifyAll(message);
    }
    
    @Test
    public void CreatePlayer_Sends_Message_On_Failure() {
        Message message = configureMessageBuilderMock();
        doThrow(RuntimeException.class)
                .when(gameMock)
                .addPlayer(anyString(), anyString());
        handleCommand();
        verify(connectionsMock, times(1)).notify(10, message);
    }

    private Message configureMessageBuilderMock() {
        Message message = mock(Message.class);
        when(builderMock.message(anyString()))
                .thenReturn(builderMock);
        when(builderMock.contentField(anyString(), anyString()))
                .thenReturn(builderMock);
        when(builderMock.getMessage())
                .thenReturn(message);
        return message;
    }

    private void handleCommand() {
        CreatePlayer command = createCreatePlayerCommand(
                "player-one",
                "warrior",
                10
        );
        handler.handle(command);
    }

    private CreatePlayer createCreatePlayerCommand(
            String playerName,
            String playerClass,
            int sourceId
    ) {
        CreatePlayer command = mock(CreatePlayer.class);
        when(command.playerName()).thenReturn(playerName);
        when(command.playerClass()).thenReturn(playerClass);
        when(command.sourceId()).thenReturn(sourceId);
        return command;
    }
}
