package com.przypadlo.thserver.model;

import com.przypadlo.thserver.model.Board.Directions;
import com.przypadlo.thserver.model.exception.CannotAttackException;
import com.przypadlo.thserver.model.exception.IncorrectCircleException;
import com.przypadlo.thserver.model.items.WeaponItem;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import com.przypadlo.thserver.model.items.ModifyingItem;
import com.przypadlo.thserver.model.items.UsableItem;

/**
 * Reprezentuje gracza. 
 * 
 * @author mprzypadlo
 */
public class Player {

    private int field = 0;

    private int circle = 0;

    private int hp;

    private int def;

    private Board board;
    
    private int experience = 0;
    
    private int attackPts;
    
    private int mana;
    
    private WeaponItem equippedWeapon;

    private Map<String, Function<Attack, Integer>> attackHandlers;
    
    private Map<String, ModifyingItem> modifiers;
    
    private Map<String, UsableItem> usables;
    
    private Map<String, WeaponItem> weapons;

    /**
     * Konstruktor 
     * 
     * @param board
     *  Instancja obiektu planszy po której poruszał będzie się gracz.
     * @param hp
     *  Wyjściowa liczba punktów zycia.
     * @param def
     *  Wyjściowa liczba punktów obrony.
     * @param attack
     *  Wyjściowa liczba punktów ataku.
     * @param mana
     *  Wyjściowa liczba punktów many.
     * @param defaultWeapon 
     *  Domyślna broń, ktrórą posługuje się gracz.
     */
    public Player(
            Board board, 
            int hp, 
            int def, 
            int attack,
            int mana,
            WeaponItem defaultWeapon
    ) {
        this.board = board;
        this.hp = hp;
        this.def = def;
        this.mana = mana;
        this.attackPts = attack;
        this.equippedWeapon = defaultWeapon;
        inializeInventory();
        renewFieldAndCircle();
    }

    /**
     * Metoda pomocnicza, 
     * Inicializuje inwentarz.
     */
    private void inializeInventory() {
        attackHandlers = new HashMap();
        modifiers = new HashMap();
        usables = new HashMap();
        weapons = new HashMap();
    }

    /**
     * Zmienia planszę po której porusza się gracz. 
     * Zakłada się, że gracze uczestniczący w tej samej grze będą mogli poruszać
     * się po różnych planszach.
     * @param board 
     */
    public void setBoard(Board board) {
        this.board = board;
        renewFieldAndCircle();
    }

    /**
     * Ustawia gracza na polach startowych bierzącej planszy. 
     * 
     * Wywoływana w konstrukorze oraz podczas zmieniania planszy.
     */
    private void renewFieldAndCircle() {
        field = board.getStartingField();
        circle = board.getStartingCircle();
    }

    /**
     * Porusza gracza w prawo o zadaną liczbę pól. 
     * Po wykonaniu gracz znajduje się na tym samym okręgu.
     * @param numberOfFields 
     */
    public void moveRight(int numberOfFields) {
        field = board.calculatePosition(this, Directions.RIGHT, numberOfFields);
    }

    /**
     * Porusza gracza w lewo o zadaną liczbę pól. 
     * Po wykoaniu gracz znajduje się na tym samym okręgu.
     * @param numberOfFields 
     */
    public void moveLeft(int numberOfFields) {
        field = board.calculatePosition(this, Directions.LEFT, numberOfFields);
    }

    /**
     * Zwraca indeks okręgu na którym znajduje się  gracz.
     * @return 
     */
    public int circle() {
        return circle;
    }

    /**
     * Zwraca indeks pola na którym znajduje się gracz.
     * @return 
     */
    public int field() {
        return field;
    }

    /**
     * Przenosi gracza na następny okrąg, na pole wskazane jako parametr.
     * @param fieldNumber 
     */
    public void moveToNextCircle(int fieldNumber) {
        if (circle + 1 >= board.getNumberOfCircles()) {
            throw new IncorrectCircleException();
        }
        circle += 1;
        field = fieldNumber;
    }

    /**
     * Zwraca liczbę punktów życia gracza.
     * @return 
     */
    public int hp() {
        return hp;
    }

    /**
     * Obsługje atak przeciwnika. 
     * 
     * Jeżeli gracz posiada zarejestrowany handler dla 
     * podanego ataku, to obsługą ataku zajmuje się ten właśnie hanlder. 
     * W przeciwnym wypadku wykonany zostanie domyślny kod ataku.
     * 
     * @param attack 
     */
    public void handleAttack(Attack attack) {

        if (attackHandlers.containsKey(attack.name())) {
            hp -= attackHandlers.get(attack.name()).apply(attack);
        } else {
            hp -= attack.value() - def;
        }
    }

    /**
     * Zwraca liczbę punktów obrony gracza.
     * @return 
     */
    public int def() {
        return def;
    }

    /**
     * Pozwala zarejestrować handler obsługujący atak.
     * @param name
     * @param handler 
     */
    public void registerAttackHanlder(
            String name,
            Function<Attack, Integer> handler
    ) {
        attackHandlers.put(name, handler);
    }
    
    /**
     * Zwraca nazwę aktualnie używanej broni.
     * 
     * @return 
     */
    public String equippedWeapon() {
        return equippedWeapon.name();
    }
    
    /**
     * Atakuje przeciwnika podanego jako parametr.
     * 
     * Po każdym wykonanym ataku zwiększa się liczba punktów 
     * doświadczenia.
     * @param player 
     * @throws CannotAttackException
     */
    public void attack(Player player) throws CannotAttackException {
        
        checkAttackPossibility(player);
        
        Attack attack = equippedWeapon.createAttack(this);
        player.handleAttack(attack);
        experience += attack.experienceIncrease();
    }

    /**
     * Metoda pomocnicza, 
     * Sprawdza czy można zaatakować przeciwnika.
     * W przypadku braku takiej możliwości żucany jest wyjątek
     * @param player 
     * @throws CannotAttackException
     */
    private void checkAttackPossibility(Player player) throws CannotAttackException {
        if (circle != player.circle()) {
            throw new CannotAttackException();
        }
        
        int distance = Math.abs(player.field() - field);
        
        if (equippedWeapon.range() < distance) {
            throw new CannotAttackException();
        }
    }
    
    /**
     * Wprowadza do inventarza przedmiot zminiający 
     * statystyki gracza. 
     * Przedmiot działa na gracza zaraz po podniesieniu.
     * @param item 
     */
    public void pickItem(ModifyingItem item) {
        item.alterPlayer(this);
        modifiers.put(item.name(), item);
    }
    
    /**
     * Wprowadza do inwentarza przedmiot, który można użyć później. 
     * 
     * Jeżeli w inwentarzu znajduje się już przedmiot takiej samej nazwie jak 
     * przedmiot wprowadzany, przedmiot nie zostanie dodany, zwiększy się jedynie
     * licznik przedmiotu już wprowadzonego.
     * @param item 
     */
    public void pickItem(UsableItem item) {
        if (usables.containsKey(item.name())) {
            usables.get(item.name()).increaseCount();
        } else {
            usables.put(item.name(), item);
        }
    }
    
    /**
     * Wprowadza do inventarza broń.
     * @param item 
     */
    public void pickItem(WeaponItem item) {
        weapons.put(item.name(), item);
    }
    
    /**
     * Zwraca kolekcję przedmiotów.
     * @return 
     */
    public Collection<UsableItem> usableItems() {
        return usables.values();
    }
    
    /**
     * Zwraca kolekcję przedmiotów modyfikujących gracza.
     * @return 
     */
    public Collection<ModifyingItem> modifyingItems() {
        return  modifiers.values();
    }
    
    /**
     * Zwraca kolekcję broni podanaej przez gracza.
     * @return 
     */
    public Collection<WeaponItem> weapons() {
        return weapons.values();
    }
    
    /**
     * Zwraca punkty doświadczenia gracza.
     * @return 
     */
    public int experience() {
        return experience;
    }
    
    /**
     * Zwraca punkty ataku gracza.
     * @return 
     */
    public int attackPoints() {
        return attackPts;
    }
    
    public void increaseDefence(int defenceIncrease) {
        def += defenceIncrease;
    }
    
    public void increaseAttack(int attackIncrease) {
        attackPts += attackIncrease;
    }
    
    public void increaseMana(int manaIncrease) {
        mana += manaIncrease;
    }
    
    public void useItem(String itemName) {
        UsableItem item = usables.get(itemName);
        item.apply(this);
        item.decreaseCount();
        
        if (item.count() == 0) {
            usables.remove(item.name());
        }
    }
    
    public void equipWeapon(String weaponName) {
        WeaponItem weaponToEquip = weapons.get(weaponName);
        weapons.put(equippedWeapon.name(), equippedWeapon);
        equippedWeapon = weaponToEquip;
        weapons.remove(weaponName);
    }
}
