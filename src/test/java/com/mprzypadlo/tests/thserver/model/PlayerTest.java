package com.mprzypadlo.tests.thserver.model;

import com.przypadlo.thserver.model.Attack;
import com.przypadlo.thserver.model.Board;
import com.przypadlo.thserver.model.Board.Directions;
import com.przypadlo.thserver.model.Player;
import com.przypadlo.thserver.model.exception.CannotAttackException;
import com.przypadlo.thserver.model.exception.IncorrectCircleException;
import com.przypadlo.thserver.model.items.WeaponItem;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.przypadlo.thserver.model.items.ModifyingItem;
import com.przypadlo.thserver.model.items.UsableItem;

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

    private WeaponItem sword;

    private boolean equippedWeaponAskedForAttack;

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

    @Before
    public void setUp() {
        boardMock = mock(Board.class);
        sword = mock(WeaponItem.class);
        player = createPlayer();
    }

    @Test
    public void testNew_Player_Is_On_Starting_Circle_And_Field() {
        when(boardMock.getStartingCircle()).thenReturn(1);
        when(boardMock.getStartingField()).thenReturn(2);

        player = createPlayer();
        assertEquals(1, player.circle());
        assertEquals(2, player.field());
    }

    @Test
    public void testPlayer_Can_Move_Right() {
        int expectedField = 4;
        int numberOfFields = 4;
        setBoardMovementValues(expectedField, Directions.RIGHT, numberOfFields);
        player.moveRight(numberOfFields);
        assertEquals(expectedField, player.field());

    }

    @Test
    public void testPlayer_Can_Move_Left() {
        int expectedField = 2;
        int numberOfFields = 3;
        setBoardMovementValues(expectedField, Directions.LEFT, numberOfFields);
        player.moveLeft(numberOfFields);
        assertEquals(expectedField, player.field());
    }

    @Test
    public void testPlayer_Moves_To_Next_Circle() {
        when(boardMock.getNumberOfCircles()).thenReturn(2);
        player.moveToNextCircle(1);

        assertEquals(1, player.circle());
        assertEquals(1, player.field());
    }

    @Test(expected = IncorrectCircleException.class)
    public void testPlayer_Throws_Exception_When_Moving_To_Non_Existing_Circle() {
        when(boardMock.getNumberOfCircles()).thenReturn(1);
        player.moveToNextCircle(5);
    }

    @Test
    public void testSetBoard_Sets_New_Circle_And_Field() {
        Board newBoardMock = mock(Board.class);
        when(newBoardMock.getStartingCircle()).thenReturn(1);
        when(newBoardMock.getStartingField()).thenReturn(5);

        player.setBoard(newBoardMock);

        assertEquals(1, player.circle());
        assertEquals(5, player.field());
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
        when(sword.name()).thenReturn("sword");
        String weapon = player.equippedWeapon();
        assertEquals("sword", weapon);
    }

    @Test
    public void testPlayer_Attacks() {
        Attack expectedAttack = createAttackMock("sword-attack", 10, 4);
        Player attackedPlayer = createPlayer();

        when(sword.createAttack(player)).thenReturn(expectedAttack);
        player.attack(attackedPlayer);
        assertEquals(91, attackedPlayer.hp());
        assertEquals(4, player.experience());
    }

    @Test(expected = CannotAttackException.class)
    public void testPlayer_Throws_Exception_When_Player_On_Other_Circle() {
        Attack expectedAttack = createAttackMock("sword-attack", 10, 4);
        when(sword.createAttack(player)).thenReturn(expectedAttack);
        Player attackedPlayer = mock(Player.class);
        when(attackedPlayer.circle()).thenReturn(1);

        player.attack(attackedPlayer);
    }

    @Test(expected = CannotAttackException.class)
    public void testPlayer_Throws_Exception_When_Oponent_To_Far() {
        Attack expectedAttack = createAttackMock("sword-attack", 10, 4);
        when(sword.createAttack(player)).thenReturn(expectedAttack);
        Player attackedPlayer = mock(Player.class);
        when(attackedPlayer.circle()).thenReturn(0);
        when(attackedPlayer.field()).thenReturn(1);
        when(sword.range()).thenReturn(0);
        
        player.attack(attackedPlayer);
    }

    @Test
    public void testPlayer_Picks_Modifying_Items() {
        ModifyingItem item = mock(ModifyingItem.class);
        when(item.name()).thenReturn("ring");
        player.pickItem(item);
        verify(item, times(1)).alterPlayer(player);
    }

    @Test
    public void testPlayer_Picks_One_Usable_Item() {
        UsableItem item = mock(UsableItem.class);
        player.pickItem(item);
        assertTrue(player.usableItems().contains(item));
    }

    @Test
    public void testPlayer_Picks_Two_Usable_Items_Of_Same_Name() {
        UsableItem itemOne = mock(UsableItem.class);
        UsableItem itemTwo = mock(UsableItem.class);
        when(itemOne.name()).thenReturn("mana-potion");
        when(itemTwo.name()).thenReturn("mana-potion");
        player.pickItem(itemOne);
        player.pickItem(itemTwo);
        verify(itemOne, times(1)).increaseCount();
        assertTrue(player.usableItems().contains(itemOne));
    }

    @Test
    public void testPlayer_Can_Use_Usable_Items() {
        UsableItem item = mock(UsableItem.class);
        when(item.name()).thenReturn("mana-potion");

        player.pickItem(item);
        player.useItem("mana-potion");
        verify(item, times(1)).apply(player);
    }

    @Test
    public void testPlayer_Decrease_Item_Count_When_Used() {
        UsableItem item = mock(UsableItem.class);
        when(item.name()).thenReturn("mana-potion");

        player.pickItem(item);
        player.useItem("mana-potion");
        verify(item, times(1)).decreaseCount();
    }

    @Test
    public void testPlayer_Removes_Item_When_Run_Out_Of_It() {
        UsableItem item = mock(UsableItem.class);
        when(item.name()).thenReturn("mana-potion");
        when(item.count()).thenReturn(0);
        player.pickItem(item);
        player.useItem("mana-potion");
        assertEquals(0, player.usableItems().size());
    }

    @Test
    public void testPlayer_Picks_Up_Weapons() {
        WeaponItem mace = mock(WeaponItem.class);
        when(mace.name()).thenReturn("mace-of-destruction");

        player.pickItem(mace);
        assertEquals(true, player.weapons().contains(mace));
    }

    @Test
    public void testPlayer_Equips_Weapons() {
        WeaponItem mace = mock(WeaponItem.class);
        when(mace.name()).thenReturn("mace-of-destruction");
        player.pickItem(mace);
        player.equipWeapon("mace-of-destruction");
        assertEquals("mace-of-destruction", player.equippedWeapon());
    }

    @Test
    public void testEquipped_Weapon_Is_Removed_From_Inventory() {
        WeaponItem mace = mock(WeaponItem.class);
        when(mace.name()).thenReturn("mace-of-destruction");
        player.pickItem(mace);
        player.equipWeapon("mace-of-destruction");
        assertEquals(false, player.weapons().contains(mace));
    }

    @Test
    public void testPreviously_Equipped_Weapon_Is_Added_To_Inventory() {
        WeaponItem mace = mock(WeaponItem.class);
        when(mace.name()).thenReturn("mace-of-destruction");
        player.pickItem(mace);
        player.equipWeapon("mace-of-destruction");
        assertEquals(true, player.weapons().contains(sword));
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
