package com.github.aaqib786.bottomnavigationaction;

public interface SelectionCallback {
    void onTabSelected(Item item, int containerResID);

    void onActionSelected(Item item, int containerResID);
}
