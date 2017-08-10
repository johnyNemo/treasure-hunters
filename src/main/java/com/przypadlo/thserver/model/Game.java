package com.przypadlo.thserver.model;

import com.przypadlo.thserver.model.Board.Directions;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author mprzypadlo
 */
public class Game {

    public enum Status {
        WAITING_FOR_USERS,
        CURRENT_PLAYER_MOVE
    }

    private final PlayerFactoryInterface playerFactory;

    private final Map<String, Player> players;

    private final int minPlayers;

    private String currentPlayer;

    private Status status = Status.WAITING_FOR_USERS;

    Iterator<String> playerIterator;

    private Dice dice;
    
    private int diceRoll;

    public Game(
            PlayerFactoryInterface playerFactory,
            Map<String, Player> players,
            int minPlayers,
            Dice dice
    ) {
        this.playerFactory = playerFactory;
        this.players = players;
        this.minPlayers = minPlayers;
        this.dice = dice;
    }

    public void addPlayer(String name, String playerClass) {

        if (players.containsKey(name)) {
            throw new RuntimeException("User alread exists.");
        }

        Player p = playerFactory.getPlayer(playerClass);
        players.put(name, p);

        if (players.size() == minPlayers) {
            status = Status.CURRENT_PLAYER_MOVE;
            playerIterator = players.keySet().iterator();
            currentPlayer = playerIterator.next();
            diceRoll = dice.roll();
        }
    }

    public void removePlayer(String playerName) {

        if (!players.containsKey(playerName)) {
            throw new RuntimeException("non-existing-user");
        }

        players.remove(playerName);

        if (players.size() < minPlayers) {
            status = Status.WAITING_FOR_USERS;
        }
    }

    public Status status() {
        return status;
    }

    public String currentPlayer() {
        return currentPlayer;
    }

    public int lastDiceRoll() {
        return diceRoll;
    }
    
    public void movePlayerRight(String playerName) { 
        Player p = players.get(playerName);
        p.moveRight(diceRoll);
        
    }
    
    public void movePlayerLeft(String playerName) { 
        Player p = players.get(playerName);
        p.moveLeft(diceRoll);
    }

}
