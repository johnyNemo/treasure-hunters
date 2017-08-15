
package com.przypadlo.thserver.model;


public interface Weapon {
    
    public String name();

    public Attack createAttack(Player attacker);
    
    public int range();

}
