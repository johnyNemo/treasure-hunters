
package com.przypadlo.thserver.domain;


public interface Weapon {
    
    public String name();

    public Attack createAttack(Player attacker);
    
    public int range();

}
