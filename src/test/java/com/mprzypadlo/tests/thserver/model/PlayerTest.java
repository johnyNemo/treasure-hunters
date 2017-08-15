package com.mprzypadlo.tests.thserver.model;

import com.przypadlo.thserver.model.Attack;
import com.przypadlo.thserver.model.Board;
import com.przypadlo.thserver.model.Board.Directions;
import com.przypadlo.thserver.model.Item;
import com.przypadlo.thserver.model.Player;
import com.przypadlo.thserver.model.Weapon;
import com.przypadlo.thserver.model.exception.CannotAttackException;
import com.przypadlo.thserver.model.exception.IncorrectCircleException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author mprzypadlo
 */
public class PlayerTest {

    private Player player;

    private Board boardMock;

    private final int initialHP = 100;

    private final int initialDef = 1;

    private final int initialAttack = 10;

    private final int initialMana = 10;

    private Weapon sword;

    @Before
    public void setUp() {
        boardMock = mock(Board.class);
        sword = mock(Weapon.class);
        player = createPlayer();
    }

    private Player createPlayer() {
        return new Player(
                boardMock,
                initialHP,
                initialDef,
                initialAttack,
                initialMana,
                sword
        );
    }

    @Test
    public void testNew_Player_Is_On_Starting_Circle_And_Field() {
        when(boardMock.startingCircle()).thenReturn(1);
        when(boardMock.startingField()).thenReturn(2);
        player = createPlayer();
        assertEquals(1, player.circle());
        assertEquals(2, player.field());
    }

    @Test
    public void testPlayer_Can_Move_Right() {
        int expectedField = 4;
        int numberOfFields = 4;
        setBoardMovementValues(expectedField, Directions.RIGHT, numberOfFields);
        player.moveRight(boardMock, numberOfFields);
        assertEquals(expectedField, player.field());

    }

    @Test
    public void testPlayer_Can_Move_Left() {
        int expectedField = 2;
        int numberOfFields = 3;
        setBoardMovementValues(expectedField, Directions.LEFT, numberOfFields);
        player.moveLeft(boardMock, numberOfFields);
        assertEquals(expectedField, player.field());
    }

    @Test
    public void testPlayer_Moves_To_Next_Circle() {
        when(boardMock.numberOfCircles()).thenReturn(2);
        player.moveToNextCircle(boardMock, 1);

        assertEquals(1, player.circle());
        assertEquals(1, player.field());
    }

    @Test(expected = IncorrectCircleException.class)
    public void testPlayer_Throws_Exception_When_Moving_To_Non_Existing_Circle() {
        when(boardMock.numberOfCircles()).thenReturn(1);
        player.moveToNextCircle(boardMock, 5);
    }

    @Test
    public void testPlayer_Has_Inital_HP() {
        int hp = player.hp();
        assertEquals(initialHP, hp);
    }

    @Test
    public void testPlayer_Has_Initial_Defense() {
        assertEquals(initialDef, player.def());
    }

    @Test
    public void testPlayer_Hanldes_Attack_With_Default_Hanlder() {
        Attack attackMock = createAttackMock("test-attack", 10);
        when(sword.createAttack(player)).thenReturn(attackMock);
        player.handleAttack(attackMock);
        assertEquals(91, player.hp());
    }

    @Test
    public void testPlayer_Use_Registered_Attack_Hanlder_When_Available() {

        player.registerAttackHanlder(
                "test-attack",
                (attack) -> {
                    return 5;
                }
        );

        player.handleAttack(createAttackMock("test-attack", 20));
        assertEquals(95, player.hp());
    }

    @Test
    public void testPlayer_Has_Default_Weapon() {
        when(sword.name()).thenReturn("test-weapon");
        assertEquals("test-weapon", player.equippedWeapon());
    }

    @Test
    public void testPlayer_Attacks() {
        Player attacked = mock(Player.class);
        Attack attack = mock(Attack.class);
        when(sword.createAttack(any(Player.class))).thenReturn(attack);
        player.attack(attacked);
        verify(attacked, times(1)).handleAttack(any(Attack.class));
    }

    @Test(expected = CannotAttackException.class)
    public void testPlayer_Throws_Exception_When_Player_On_Other_Circle() {
        
        Player attacked = mock(Player.class); 
        when(attacked.circle()).thenReturn(2);
        player.attack(attacked);
    }

    @Test(expected = CannotAttackException.class)
    public void testPlayer_Throws_Exception_When_Oponent_To_Far() {
        Player attacked = mock(Player.class);
        when(sword.range()).thenReturn(1);
        when(attacked.field()).thenReturn(3);
        player.attack(attacked);
    }

    @Test
    public void Player_Can_Pick_Items() {
        Item item = mock(Item.class);
        when(item.name()).thenReturn("test-item");
        player.pickItem(item);

        assertTrue(player.items().contains("test-item"));
    }
    
    @Test 
    public void Player_Uses_Items() { 
        Item itemMock = mock(Item.class); 
        when(itemMock.name()).thenReturn("test-item");
        
        player.pickItem(itemMock);
        player.useItem("test-item");
        
        verify(itemMock, times(1)).applyTo(player);
    }

    @Test
    public void Player_Decrease_Item_Count_When_Used() {
        Item itemMock = mock(Item.class);
        when(itemMock.name()).thenReturn("test-item");
        player.pickItem(itemMock);
        player.useItem("test-item");
        verify(itemMock, times(1)).decreaseCount();
    }

    @Test
    public void testPlayer_Removes_Item_When_Run_Out_Of_It() {
        Item item = mock(Item.class);
        when(item.name()).thenReturn("mana-potion");
        when(item.count()).thenReturn(0);
        player.pickItem(item);
        player.useItem("mana-potion");
        assertEquals(0, player.items().size());
    }

    @Test
    public void testPlayer_Picks_Up_Weapons() {
        Weapon weapon = mock(Weapon.class);
        when(weapon.name()).thenReturn("test-weapon-2");
        player.pickWeapon(weapon);
        assertTrue(player.weapons().contains("test-weapon-2"));
    }

    @Test
    public void testPlayer_Equips_Weapons() {
        Weapon weapon = mock(Weapon.class); 
        when(weapon.name()).thenReturn("swordy");
        player.pickWeapon(weapon);
        player.equipWeapon("swordy");
        
        assertEquals("swordy", player.equippedWeapon());
    }

    @Test
    public void testEquipped_Weapon_Is_Removed_From_Inventory() {
        Weapon weapon = mock(Weapon.class); 
        when(weapon.name()).thenReturn("swordy"); 
        player.pickWeapon(weapon); 
        player.equipWeapon("swordy");
        
        assertFalse(player.weapons().contains("swordy")); 
    }

    @Test
    public void testPreviously_Equipped_Weapon_Is_Added_To_Inventory() {
        Weapon weapon = mock(Weapon.class); 
        when(weapon.name()).thenReturn("swordy"); 
        when(sword.name()).thenReturn("andrzej");
        player.pickWeapon(weapon);
        player.equipWeapon("swordy");
        
        assertTrue(player.weapons().contains("andrzej"));
    }

    private Attack createAttackMock(String attackName, int value, int exp) {
        Attack attackMock = createAttackMock(attackName, value);
        when(attackMock.experienceIncrease()).thenReturn(exp);
        return attackMock;
    }

    private Attack createAttackMock(String attackName, int value) {
        Attack attackMock = mock(Attack.class);
        when(attackMock.name()).thenReturn(attackName);
        when(attackMock.value()).thenReturn(value);
        return attackMock;
    }

    private void setBoardMovementValues(int expectedPosition, Directions expectedDirection, int numberOfFields) {
        when(
                boardMock.calculatePosition(
                        player,
                        expectedDirection,
                        numberOfFields)
        ).thenReturn(expectedPosition);

    }
}
