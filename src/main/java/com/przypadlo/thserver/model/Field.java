package com.przypadlo.thserver.model;

public interface Field {
    
    public Item getItem(String itemName);
    
    public void applyAction(Player player);
    
}
