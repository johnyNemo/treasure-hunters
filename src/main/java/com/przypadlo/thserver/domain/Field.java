package com.przypadlo.thserver.domain;

public interface Field {
    
    public Item getItem(String itemName);
    
    public void applyAction(Player player);
    
}
