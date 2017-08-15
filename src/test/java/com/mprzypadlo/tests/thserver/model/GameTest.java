package com.mprzypadlo.tests.thserver.model;

import com.przypadlo.thserver.model.Board;
import com.przypadlo.thserver.model.Dice;
import com.przypadlo.thserver.model.Field;
import com.przypadlo.thserver.model.Game;
import com.przypadlo.thserver.model.Game.Status;
import com.przypadlo.thserver.model.Item;
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
    
    private Board boardMock;

    @Before
    public void setUp() {
        playerFactoryMock = mock(PlayerFactoryInterface.class);
        diceMock = mock(Dice.class);
        players = new LinkedHashMap();
        boardMock = mock(Board.class);
        game = new Game(boardMock,playerFactoryMock, players, minPlayers, diceMock);
    }

    @Test
    public void Player_Can_Join_Game() {
        game.addPlayer("johny", "wizard");
        assertTrue(players.containsKey("johny"));
    }

    @Test(expected = RuntimeException.class)
    public void Player_Cant_Join_Game_Twice() {
        game.addPlayer("johny", "wizard");
        game.addPlayer("johny", "wizard");
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
        verify(player, times(1)).moveRight(boardMock,6);
    }

    @Test
    public void Game_Allows_Current_Player_To_Move_Left() {
        Player player = mock(Player.class);
        configureGameForMovement(player);

        game.movePlayerLeft("first-player");
        verify(player, times(1)).moveLeft(boardMock,6);
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
        addTwoPlayers();
        System.out.println(players);

        game.attack("first-player", "second-player");
        verify(attacker, times(1)).attack(attackee);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Game_Throws_Exception_On_Incorrect_User_Attack() {
        addTwoPlayers();
        game.attack("non-existing", "second-player");
    }

    @Test(expected = IllegalArgumentException.class)
    public void Game_Throws_Exception_On_Incorrect_User_Is_Attacked() {
        addTwoPlayers();
        game.attack("first-player", "non-existing");
    }
    
    @Test
    public void Game_Allows_Player_To_Pick_Item() {
        Player player = mockBoardForGettingItems();
        addTwoPlayers();
        game.pickItem("first-player", "test-item-name");
        verify(player, times(1)).pickItem(any(Item.class));
    }

    private Player mockBoardForGettingItems() {
        Player p = mock(Player.class);
        when(p.circle()).thenReturn(0); 
        when(p.field()).thenReturn(0);
        Field fieldMock = mock(Field.class);
        Item itemMock = mock(Item.class);
        when(playerFactoryMock.getPlayer(any(String.class))).thenReturn(p);
        when(boardMock.fieldOfPosition(0, 0)).thenReturn(fieldMock); 
        when(fieldMock.getItem("test-item-name")).thenReturn(itemMock);
        return p;
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void Game_Throws_Exception_On_Inccorect_User_Picks_Item() {
        addTwoPlayers();
        game.pickItem("non-existing-user", "item");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void Game_Throws_Exception_On_Incorrect_Item_Name() { 
       
       mockBoardForGettingItems();
       addTwoPlayers();
       game.pickItem("first-player", "non-existing");
    }
    
    @Test 
    public void Game_Allows_Player_To_Use_Items() {
        Player player = mock(Player.class);
        when(playerFactoryMock.getPlayer(anyString())).thenReturn(player);
        addTwoPlayers();
        game.useItem("first-player", "some-item");
        verify(player, times(1)).useItem("some-item");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void Game_Throws_Exception_When_Incorrect_Player_Uses_Item() { 
        addTwoPlayers(); 
        game.useItem("non-existing", "item");
    }
    
    private void addTwoPlayers() {
        game.addPlayer("first-player", "a");
        game.addPlayer("second-player", "b");
    }

    private void configureGameForMovement(Player player) {
        when(diceMock.roll()).thenReturn(6);
        when(playerFactoryMock.getPlayer(any(String.class)))
                .thenReturn(player);

        addTwoPlayers();
    }

    private Player configurePlayerFactoryForAttack(Player attacker) {
        Player attackee = mock(Player.class);
        when(playerFactoryMock.getPlayer("a")).thenReturn(attacker);
        when(playerFactoryMock.getPlayer("b")).thenReturn(attackee);
        return attackee;
    }
}
