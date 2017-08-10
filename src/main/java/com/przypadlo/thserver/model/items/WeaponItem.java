package com.przypadlo.thserver.model.items;

import com.przypadlo.thserver.model.Attack;
import com.przypadlo.thserver.model.Player;

/**
 *
 * @author mprzypadlo
 */
public interface WeaponItem extends Item {
    
    public Attack createAttack(Player attacker);
    
    public int range();
    
}
