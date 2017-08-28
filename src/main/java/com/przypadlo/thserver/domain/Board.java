
package com.przypadlo.thserver.domain;

/**
 *
 * @author mprzypadlo
 */
public interface Board {

    public enum Directions  { 
        LEFT, RIGHT 
    };
    
    public Integer calculatePosition(Player player, Directions direction, Integer fieldNumber);
    
    public Integer startingCircle();
    
    public Integer startingField();
    
    public Integer numberOfCircles();
    
    public Field fieldOfPosition(int circle, int field);
}
