package com.mprzypadlo.tests.thserver.model;
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
 *
 * @author mprzypadlo
 */
public class GameTest {
    
    private Game game;
    
    PlayerFactoryInterface playerFactoryMock;
    
    private HashMap<String, Player> users;
    
    private final int minPlayers = 2;
       
    @Before
    public void setUp() {
        playerFactoryMock = mock(PlayerFactoryInterface.class);
        users = new HashMap();
        game = new Game(playerFactoryMock, users, minPlayers);
    }
    
    @Test
    public void Player_Can_Join_Game() {
        game.addPlayer(
                "johny", 
                "wizard"
        );
        verify(playerFactoryMock).getPlayer("wizard");
        assertTrue(users.containsKey("johny"));
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
        assertFalse(users.containsKey("johny"));
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
    
    
   
}
