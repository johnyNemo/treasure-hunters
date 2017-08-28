package com.mprzypadlo.thserver.application.command.commands;

import com.mprzypadlo.thserver.application.command.Command;

public abstract class CreatePlayer implements Command {

    @Override
    public String name() {
        return "asdf";
    }
    
    @Override
    public abstract int sourceId();
    
    public abstract String playerName(); 

    public abstract String playerClass();
    
}
