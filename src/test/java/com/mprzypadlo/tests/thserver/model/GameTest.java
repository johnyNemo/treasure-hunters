package com.mprzypadlo.tests.thserver.model;

import com.przypadlo.thserver.model.Dice;
import com.przypadlo.thserver.model.Game;
import com.przypadlo.thserver.model.Game.Status;
import com.przypadlo.thserver.model.Player;
import com.przypadlo.thserver.model.PlayerFactoryInterface;
import java.util.LinkedHashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class GameTest {

    private Game game;

    PlayerFactoryInterface playerFactoryMock;

    Dice diceMock;

    private LinkedHashMap<String, Player> players;

    private final int minPlayers = 2;

    @Before
    public void setUp() {
        System.out.println("Before each test?!");
        playerFactoryMock = mock(PlayerFactoryInterface.class);
        diceMock = mock(Dice.class);
        players = new LinkedHashMap();
        System.out.println("Players size: " + players.size());
        game = new Game(playerFactoryMock, players, minPlayers, diceMock);
    }

    @Test
    public void Player_Can_Join_Game() {
        game.addPlayer("johny", "wizard");
        assertTrue(players.containsKey("johny"));
    }

    @Test(expected = RuntimeException.class)
    public void Player_Cant_Join_Game_Twice() {
        game.addPlayer("johny", "wizard");
        game.addPlayer("johny", "wizzard");
    }

    @Test
    public void Player_Can_Abbandon_Game() {
        game.addPlayer("johny", "wizzard");
        game.removePlayer("johny");
        assertFalse(players.containsKey("johny"));
    }

    @Test
    public void Game_Waits_For_Enough_Users() {
        assertEquals(Status.WAITING_FOR_USERS, game.status());
    }

    @Test
    public void Game_Changes_Status_When_Min_Users_Joined() {
        game.addPlayer("a", "a1");
        game.addPlayer("b", "b1");
        assertEquals(Status.CURRENT_PLAYER_MOVE, game.status());
    }

    @Test
    public void Game_Changes_Status_When_Not_Enought_Users() {
        game.addPlayer("a", "a1");
        game.addPlayer("b", "b1");
        game.removePlayer("a");
        assertEquals(Status.WAITING_FOR_USERS, game.status());
    }

    @Test(expected = RuntimeException.class)
    public void Game_Throws_Exception_When_Deleting_Non_Existing_Player() {
        game.removePlayer("non-existing");
    }

    @Test
    public void Game_Sets_Current_User_After_Status_Change() {
        game.addPlayer("a", "a1");
        game.addPlayer("b", "b1");
        assertEquals("a", game.currentPlayer());
    }

    @Test
    public void Game_Rolls_Dice_On_Status_Change() {
        when(diceMock.roll()).thenReturn(6);

        game.addPlayer("first-player", "wizzard");
        game.addPlayer("second-player", "wizzard");

        assertEquals(6, game.lastDiceRoll());
    }

    @Test
    public void Game_Allows_Current_Player_To_Move_Right() {
        Player player = mock(Player.class);
        configureGameForMovement(player);

        game.movePlayerRight("first-player");
        verify(player, times(1)).moveRight(6);
    }

    @Test
    public void Game_Allows_Current_Player_To_Move_Left() {
        Player player = mock(Player.class);
        configureGameForMovement(player);

        game.movePlayerLeft("first-player");
        verify(player, times(1)).moveLeft(6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Game_Trhows_Exception_When_Incorrect_Player_Moves_Left() {
        configureGameForMovement(mock(Player.class));
        game.movePlayerLeft("non-current-player");
    }

    @Test(expected = IllegalArgumentException.class)
    public void Game_Trhows_Exception_When_Incorrect_Player_Moves_Right() {
        configureGameForMovement(mock(Player.class));
        game.movePlayerRight("non-current-player");
    }

    @Test
    public void Game_Allows_Players_To_Attack_Each_Other() {
        Player attacker = mock(Player.class);
        Player attackee = configurePlayerFactoryForAttack(attacker);

        addTwoUsersToGame();
        System.out.println(players);

        game.attack("first-player", "second-player");
        verify(attacker, times(1)).attack(attackee);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Game_Throws_Exception_On_Incorrect_User_Attack() {

        addTwoUsersToGame();
        game.attack("non-existing", "second-player");
    }

    @Test(expected = IllegalArgumentException.class)
    public void Game_Throws_Exception_On_Incorrect_User_Is_Attacked() {
        addTwoUsersToGame();
        game.attack("first-player", "non-existing");
    }

    private void addTwoUsersToGame() {
        game.addPlayer("first-player", "a");
        game.addPlayer("second-player", "b");
    }

    private void configureGameForMovement(Player player) {
        when(diceMock.roll()).thenReturn(6);
        when(playerFactoryMock.getPlayer(any(String.class)))
                .thenReturn(player);

        addTwoUsersToGame();
    }

    private Player configurePlayerFactoryForAttack(Player attacker) {
        Player attackee = mock(Player.class);
        when(playerFactoryMock.getPlayer("a")).thenReturn(attacker);
        when(playerFactoryMock.getPlayer("b")).thenReturn(attackee);
        return attackee;
    }
}
