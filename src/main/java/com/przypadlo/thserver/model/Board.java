
package com.przypadlo.thserver.model;

/**
 *
 * @author mprzypadlo
 */
public interface Board {

    public enum Directions  { 
        LEFT, RIGHT 
    };
    
    public Integer calculatePosition(Player player, Directions direction, Integer fieldNumber);
    
    public Integer getStartingCircle();
    
    public Integer getStartingField();
    
    public Integer getNumberOfCircles();
}
