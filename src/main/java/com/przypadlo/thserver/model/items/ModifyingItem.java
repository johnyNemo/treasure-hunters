/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.przypadlo.thserver.model.items;

import com.przypadlo.thserver.model.Player;

/**
 *
 * @author mprzypadlo
 */
public interface ModifyingItem extends Item{
    public void alterPlayer(Player p);
}
