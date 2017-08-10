
package com.przypadlo.thserver.model.items;

import com.przypadlo.thserver.model.Player;

/**
 *
 * @author mprzypadlo
 */
public interface UsableItem extends Item{
    public int count();
    public void increaseCount();
    public void decreaseCount();
    public int apply(Player p);
}
