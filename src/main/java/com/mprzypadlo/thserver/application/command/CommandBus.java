package com.mprzypadlo.thserver.application.command;

import com.mprzypadlo.thserver.application.command.exception.CommandHandlerNotFoundException;
import com.mprzypadlo.thserver.application.command.exception.HandlerRegistrationException;
import java.util.Map;

/**
 *
 * @author mprzypadlo
 */
public class CommandBus {
    
    private final  Map<String, CommandHandler> handlers; 
    
    public CommandBus(Map handlers) {
        this.handlers = handlers;
    }
    
    public <T extends Command> void registerHandler(
            String commandName, 
            CommandHandler handler
    ) { 
        
        throwExceptionIfHanlderExists(commandName);
        handlers.put(commandName, handler);
    }
    
    public void dispatch(Command command) throws CommandHandlerNotFoundException{
        String commandName = command.name();
        CommandHandler handler = handlers.get(commandName);
        throwExceptionIfCommandHandlerNotFound(handler);
        handler.handle(command);
    }

    private void throwExceptionIfCommandHandlerNotFound(CommandHandler handler)  {
        if (handler == null) {
            throw new CommandHandlerNotFoundException();
        }
    }

    private void throwExceptionIfHanlderExists(String commandName) throws HandlerRegistrationException {
        if (handlers.containsKey(commandName)) {
            throw new HandlerRegistrationException();
        }
    }
}