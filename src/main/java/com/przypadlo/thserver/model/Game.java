package com.przypadlo.thserver.model;

import java.util.Iterator;
import java.util.LinkedHashMap;
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

    private Iterator<String> playerIterator;

    private final Dice dice;

    private int diceRoll;

    private final Board board;

    public Game(
            Board board,
            PlayerFactoryInterface playerFactory,
            LinkedHashMap<String, Player> players,
            int minPlayers,
            Dice dice
    ) {
        this.playerFactory = playerFactory;
        this.players = players;
        this.minPlayers = minPlayers;
        this.dice = dice;
        this.board = board;
    }

    public void addPlayer(String name, String playerClass) {
        throwExceptionIfPlayerExists(name);
        Player p = playerFactory.getPlayer(playerClass);
        System.out.println(p);
        players.put(name, p);

        if (players.size() == minPlayers) {
            status = Status.CURRENT_PLAYER_MOVE;
            playerIterator = players.keySet().iterator();
            currentPlayer = playerIterator.next();
            diceRoll = dice.roll();
        }
    }

    private void throwExceptionIfPlayerExists(String name) {
        if (players.containsKey(name)) {
            throw new RuntimeException("User alread exists.");
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
        throwExceptionWhenIncorrectPlayer(playerName);
        Player p = players.get(playerName);
        p.moveRight(board, diceRoll);

    }

    public void movePlayerLeft(String playerName) {
        throwExceptionWhenIncorrectPlayer(playerName);
        Player p = players.get(playerName);
        p.moveLeft(board, diceRoll);
    }

    public void attack(String attackerName, String attackeeName) {
        throwExceptionWhenIncorrectPlayer(attackerName);
        throwExceptionIfPlayerDoesNotExists(attackeeName);
        Player attacker = players.get(attackerName);
        Player attackee = players.get(attackeeName);

        attacker.attack(attackee);
    }
    
    public void pickItem(String playerName, String itemName) {
        throwExceptionWhenIncorrectPlayer(playerName);
        Player player = players.get(playerName);
        Item itemToPick = board.fieldOfPosition(
                player.circle(), 
                player.field()
        ).getItem(itemName);
        
        if (itemToPick == null) { 
            throw new IllegalArgumentException("Item " + itemName + "does not exists");
        }
        
        player.pickItem(itemToPick);
    }
    
    public void useItem(String playerName, String itemName) {
        throwExceptionWhenIncorrectPlayer(playerName);
        Player player = players.get(playerName);
        player.useItem(itemName);
    }

    private void throwExceptionIfPlayerDoesNotExists(String playerName) {
        if (!players.containsKey(playerName)) {
            throw new IllegalArgumentException("Player does not exists");
        }
    }

    private void throwExceptionWhenIncorrectPlayer(String playerName) {
        if (!playerName.equals(currentPlayer())) {
            throw new IllegalArgumentException("Given Player is not current");
        }
    }

}
