/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.rhysevans.taxe.R;

/**
 * AccountActionsListAdapter.java
 *
 * A customer list adapter for the account actions list
 *
 * @author Rhys Evans
 * @version 0.1
 */
public class AccountActionsListAdapter extends ArrayAdapter<AccountActionModel> {

    /**
     * The application context
     */
    private final Context context;

    /**
     * A list of all the account options
     */
    private final ArrayList<AccountActionModel> optionsModelList;

    /**
     * Constructor
     */
    public AccountActionsListAdapter(Context context, ArrayList<AccountActionModel> optionsModelList){
        super(context, R.layout.list_view_account_action_entry, optionsModelList);

        this.context = context;
        this.optionsModelList = optionsModelList;
    }

    /**
     * Inflate the custom layout to display and return the inflated view
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get the entry view from the inflater
        View entryView = inflater.inflate(R.layout.list_view_account_action_entry, parent, false);

        // Get the icon resource id and string resource id
        ImageView iconView = entryView.findViewById(R.id.account_action_icon);
        TextView textView = entryView.findViewById(R.id.account_action_text);

        // Set the text
        textView.setText(optionsModelList.get(position).getText());
        // Set the icon
        iconView.setImageResource(optionsModelList.get(position).getIcon());

        return entryView;
    }
}
