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
        game = new Game(boardMock, playerFactoryMock, players, minPlayers, diceMock);
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
        startGame();
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
        startGame();
        verify(diceMock, times(1)).roll();
    }

    @Test
    public void Game_Allows_Current_Player_To_Move_Right() {
        Player player = configureGameForMovement();
        game.movePlayerRight("first-player");
        verify(player, times(1)).moveRight(boardMock, 6);
    }

    @Test
    public void Game_Allows_Current_Player_To_Move_Left() {
        Player player = configureGameForMovement();
        game.movePlayerLeft("first-player");
        verify(player, times(1)).moveLeft(boardMock, 6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Game_Trhows_Exception_When_Incorrect_Player_Moves_Left() {
        game.movePlayerLeft("non-current-player");
    }

    @Test(expected = IllegalArgumentException.class)
    public void Game_Trhows_Exception_When_Incorrect_Player_Moves_Right() {
        game.movePlayerRight("non-current-player");
    }

    @Test
    public void Game_Allows_Players_To_Attack_Each_Other() {
        Player attacker = mock(Player.class);
        Player attacked = mock(Player.class);

        startGame(attacker, attacked);
        game.attack("first-player", "second-player");
        verify(attacker, times(1)).attack(attacked);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Game_Throws_Exception_On_Incorrect_User_Attack() {
        game.attack("non-existing", "second-player");
    }

    @Test(expected = IllegalArgumentException.class)
    public void Game_Throws_Exception_On_Incorrect_User_Is_Attacked() {
        game.attack("first-player", "non-existing");
    }

    @Test
    public void Game_Allows_Player_To_Pick_Item() {
        Player player = startGame();
        mockBoardForGettingItems(player);
        game.pickItem("first-player", "test-item-name");
        verify(player, times(1)).pickItem(any(Item.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void Game_Throws_Exception_On_Inccorect_User_Picks_Item() {
        startGame();
        game.pickItem("non-existing-user", "item");
    }

    @Test
    public void Game_Allows_Player_To_Use_Items() {
        Player player = startGame();
        game.useItem("first-player", "some-item");
        verify(player, times(1)).useItem("some-item");
    }

    @Test(expected = IllegalArgumentException.class)
    public void Game_Throws_Exception_When_Incorrect_Player_Uses_Item() {
        startGame();
        game.useItem("non-existing", "item");
    }

    @Test
    public void Game_Allows_Players_To_Execute_Actions() {
        Player playerMock = startGame();
        Field fieldMock = createFieldMock();
        game.action("first-player", "test-action");
        verify(fieldMock, times(1)).applyAction(playerMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Game_Throws_Exception_When_Incorrect_Player_Executes_Action() {
        startGame();
        game.action("non-existing-player", "test-action");
    }

    @Test
    public void Game_Updates_Current_Player_After_Action() {
        Player playerMock = startGame();
        createFieldMock(); 
        game.action("first-player", "test-action");
        assertEquals("second-player", game.currentPlayer());
    }

    private Field createFieldMock() {
        Field fieldMock = mock(Field.class);
        when(boardMock.fieldOfPosition(0, 0)).thenReturn(fieldMock);
        return fieldMock;
    }

    private Player startGame() {
        Player player = configurePlayerFactory();
        game.addPlayer("first-player", "a");
        game.addPlayer("second-player", "b");
        return player;
    }

    private Player configurePlayerFactory() {
        Player player = mock(Player.class);
        when(playerFactoryMock.getPlayer(any(String.class)))
                .thenReturn(player);
        return player;
    }

    private void startGame(Player playerOne, Player playerTwo) {
        when(playerFactoryMock.getPlayer("a")).thenReturn(playerOne);
        when(playerFactoryMock.getPlayer("b")).thenReturn(playerTwo);
        game.addPlayer("first-player", "a");
        game.addPlayer("second-player", "b");
    }

    private void mockBoardForGettingItems(Player player) {
        when(player.circle()).thenReturn(0);
        when(player.field()).thenReturn(0);
        configureFieldReturning();
    }

    private void configureFieldReturning() {
        Field fieldMock = mock(Field.class);
        Item itemMock = mock(Item.class);
        when(boardMock.fieldOfPosition(0, 0)).thenReturn(fieldMock);
        when(fieldMock.getItem("test-item-name")).thenReturn(itemMock);
    }

    private Player configureGameForMovement() {
        when(diceMock.roll()).thenReturn(6);
        return startGame();
    }
}
