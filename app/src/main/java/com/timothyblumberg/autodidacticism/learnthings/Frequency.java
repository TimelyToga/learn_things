package com.timothyblumberg.autodidacticism.learnthings;

/**
 * Created by Tim on 7/26/14.
 */
public class Frequency {

    public int millisecondsTillNext;
    public String itemName;
    public boolean isSelected;

    public Frequency(int millisecondsTillNext, String itemName){
        this.millisecondsTillNext = millisecondsTillNext;
        this.itemName = itemName;
        this.isSelected = false;
    }


}
