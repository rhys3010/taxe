/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.account;

/**
 * AccountActionModel.java
 *
 * A model class for an account action object, used to populate
 * list view in user's account overview
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class AccountActionModel {

    /**
     * Icon resource id
     */
    private int icon;

    /**
     * The action to be taken
     */
    private int text;

    /**
     * Constructor
     * @param icon
     * @param text
     */
    public AccountActionModel(int icon, int text){
        this.icon = icon;
        this.text = text;
    }

    /**
     * Gets the icon resource ID
     * @return
     */
    public int getIcon() {
        return icon;
    }

    /**
     * Sets the icon resource ID
     * @param icon
     */
    public void setIcon(int icon) {
        this.icon = icon;
    }

    /**
     * Gets the action description
     * @return
     */
    public int getText() {
        return text;
    }

    /**
     * Sets the action description
     * @param text
     */
    public void setText(int text) {
        this.text = text;
    }
}
