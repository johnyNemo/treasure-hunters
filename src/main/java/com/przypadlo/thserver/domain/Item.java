/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.przypadlo.thserver.domain;

/**
 *
 * @author marek
 */
public interface Item {
    
    public String name();
    
    public void decreaseCount();
    
    public int count();

    public void applyTo(Player player);
    
}
