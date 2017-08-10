package com.mprzypadlo.tests.thserver.model;

import com.przypadlo.thserver.model.Board.Directions;
import com.przypadlo.thserver.model.Dice;
import com.przypadlo.thserver.model.Game;
import com.przypadlo.thserver.model.Game.Status;
import com.przypadlo.thserver.model.Player;
import com.przypadlo.thserver.model.PlayerFactoryInterface;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * Tests for Game class.
 * @author mprzypadlo
 */
public class GameTest {

    /**
     * Class under test.
     * This is a line that will cause conflict, end everything will be good.
     */
    private Game game;

    PlayerFactoryInterface playerFactoryMock;

    Dice diceMock;

    private HashMap<String, Player> players;

    private final int minPlayers = 2;

    @Before
    public void setUp() {
        playerFactoryMock = mock(PlayerFactoryInterface.class);
        diceMock = mock(Dice.class);
        players = new HashMap();
        game = new Game(playerFactoryMock, players, minPlayers, diceMock);
    }

    @Test
    public void Player_Can_Join_Game() {
        game.addPlayer(
                "johny",
                "wizard"
        );
        verify(playerFactoryMock).getPlayer("wizard");
        assertTrue(players.containsKey("johny"));
    }

    @Test(expected = RuntimeException.class)
    public void Player_Cant_Join_Game_Twice() {
        game.addPlayer("johny", "wizard");
        game.addPlayer("johny", "lizzard");
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
    
    @Test
    public void Game_Does_Not_Allow_Different_Player_To_Move_Left() { 
        game.movePlayerRight("non-existing-player");
    }

    private void configureGameForMovement(Player player) {
        when(diceMock.roll()).thenReturn(6);
        when(playerFactoryMock.getPlayer(any(String.class)))
                .thenReturn(player);
        
        game.addPlayer("first-player", "a");
        game.addPlayer("second-player", "b");
    }
    
    

}
