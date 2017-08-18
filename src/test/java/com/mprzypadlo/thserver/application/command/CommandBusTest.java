package com.mprzypadlo.thserver.application.command;
import com.mprzypadlo.thserver.application.command.exception.CommandHandlerNotFoundException;
import com.mprzypadlo.thserver.application.command.exception.HandlerRegistrationException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandBusTest {
    
    private CommandBus bus;
    
    private Map commandHanlders;
    
    @Before
    public void setUp() {
        commandHanlders = new HashMap();
        bus = new CommandBus(commandHanlders);        
    }
    
    @Test
    public void CommandBus_Registers_Hanlders() {
        CommandHandler handlerMock = mock(CommandHandler.class);
        bus.registerHandler("create-player", handlerMock);
        assertTrue(commandHanlders.containsKey("create-player"));
    }
    
    @Test(expected = HandlerRegistrationException.class)
    public void CommendBus_Cant_Register_Two_Hanlders_For_The_Same_Command() {
        CommandHandler handlerMockOne = mock(CommandHandler.class);
        CommandHandler handlerMockTwo = mock(CommandHandler.class);
        bus.registerHandler("create-player", handlerMockOne); 
        bus.registerHandler("create-player", handlerMockTwo);        
    }
    
    @Test
    public void CommandBus_Executes_Hanlder() {
        CommandHandler hanlderMock = mock(CommandHandler.class);
        Command commandMock = createCommand("create-player");
        bus.registerHandler("create-player", hanlderMock);
        bus.dispatch(commandMock);
        verify(hanlderMock, times(1)).handle(commandMock);
    }
    
    @Test(expected = CommandHandlerNotFoundException.class)
    public void CommndBus_Throws_Exception_When_Handler_Not_Found() {
        bus.dispatch(mock(Command.class));
    }

    private Command createCommand(String name) {
        Command commandMock = mock(Command.class);
        when(commandMock.name()).thenReturn(name);
        return commandMock;
    }

}
