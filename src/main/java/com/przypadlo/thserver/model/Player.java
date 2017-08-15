package com.przypadlo.thserver.model;

import com.przypadlo.thserver.model.Board.Directions;
import com.przypadlo.thserver.model.exception.CannotAttackException;
import com.przypadlo.thserver.model.exception.IncorrectCircleException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Set;

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

    private int experience = 0;

    private int attackPts;

    private int mana;

    private Map<String, Function<Attack, Integer>> attackHandlers;

    private Map<String, Item> items;

    private Weapon equippedWeapon;
    
    private Map<String, Weapon> weaponInventory;

    /**
     * Konstruktor
     *
     * @param board Instancja obiektu planszy po której poruszał będzie się
     * gracz.
     * @param hp Wyjściowa liczba punktów zycia.
     * @param def Wyjściowa liczba punktów obrony.
     * @param attack Wyjściowa liczba punktów ataku.
     * @param mana Wyjściowa liczba punktów many.
     * @param initialWeapon
     */
    public Player(
            Board board,
            int hp,
            int def,
            int attack,
            int mana,
            Weapon initialWeapon
    ) {
        this.hp = hp;
        this.def = def;
        this.mana = mana;
        this.attackPts = attack;
        this.circle = board.startingCircle();
        this.field = board.startingField();
        this.equippedWeapon = initialWeapon;
        this.weaponInventory = new HashMap();
        this.attackHandlers = new HashMap();
        this.items = new HashMap();
    }

    public Set items() {
        return items.keySet();
    }

    /**
     * Porusza gracza w prawo o zadaną liczbę pól. Po wykonaniu gracz znajduje
     * się na tym samym okręgu.
     *
     * @param board
     * @param numberOfFields
     */
    public void moveRight(Board board, int numberOfFields) {
        field = board.calculatePosition(this, Directions.RIGHT, numberOfFields);
    }

    /**
     * Porusza gracza w lewo o zadaną liczbę pól. Po wykoaniu gracz znajduje
     * się na tym samym okręgu.
     *
     * @param board
     * @param numberOfFields
     */
    public void moveLeft(Board board, int numberOfFields) {
        field = board.calculatePosition(this, Directions.LEFT, numberOfFields);
    }

    /**
     * Zwraca indeks okręgu na którym znajduje się  gracz.
     *
     * @return
     */
    public int circle() {
        return circle;
    }

    /**
     * Zwraca indeks pola na którym znajduje się gracz.
     *
     * @return
     */
    public int field() {
        return field;
    }

    /**
     * Przenosi gracza na następny okrąg, na pole wskazane jako parametr.
     *
     * @param fieldNumber
     */
    public void moveToNextCircle(Board board, int fieldNumber) {
        if (circle + 1 >= board.numberOfCircles()) {
            throw new IncorrectCircleException();
        }
        circle += 1;
        field = fieldNumber;
    }

    /**
     * Zwraca liczbę punktów życia gracza.
     *
     * @return
     */
    public int hp() {
        return hp;
    }

    /**
     * Obsługje atak przeciwnika.
     *
     * Jeżeli gracz posiada zarejestrowany handler dla podanego ataku, to
     * obsługą ataku zajmuje się ten właśnie hanlder. W przeciwnym wypadku
     * wykonany zostanie domyślny kod ataku.
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
     *
     * @return
     */
    public int def() {
        return def;
    }

    /**
     * Pozwala zarejestrować handler obsługujący atak.
     *
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
     * Atakuje przeciwnika podanego jako parametr.
     *
     * Po każdym wykonanym ataku zwiększa się liczba punktów doświadczenia.
     *
     * @param attacked
     * @throws CannotAttackException
     */
    public void attack(Player attacked) {
        checkAttackPossibility(attacked);
        Attack attack = equippedWeapon.createAttack(this);
        System.out.println(attack);
        attacked.handleAttack(attack);
    }

    /**
     * Metoda pomocnicza, Sprawdza czy można zaatakować przeciwnika. W przypadku
     * braku takiej możliwości żucany jest wyjątek
     *
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
    
    public void pickWeapon(Weapon weapon) { 
        weaponInventory.put(weapon.name(), weapon);
    }
    
    public Set weapons() {
       return weaponInventory.keySet();
    }

    public void pickItem(Item item) {
        items.put(item.name(), item);
    }

    /**
     * Zwraca punkty doświadczenia gracza.
     *
     * @return
     */
    public int experience() {
        return experience;
    }

    /**
     * Zwraca punkty ataku gracza.
     *
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
        Item item = items.get(itemName);
        item.applyTo(this);
        item.decreaseCount();
        
        if (item.count() == 0) { 
            items.remove(item.name());
        }
    }

    public void equipWeapon(String weaponName) {
        weaponInventory.put(equippedWeapon.name(), equippedWeapon);
        equippedWeapon = weaponInventory.get(weaponName);
        weaponInventory.remove(weaponName);        
    }

    public String equippedWeapon() {
        return equippedWeapon.name();
    }
}
