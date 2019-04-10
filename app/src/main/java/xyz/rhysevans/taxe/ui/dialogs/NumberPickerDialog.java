/*
 * Copyright (c) Rhys Evans
 * All Rights Reserved
 */

package xyz.rhysevans.taxe.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.NumberPicker;

import xyz.rhysevans.taxe.R;

/**
 * NumberPickerDialog.java
 *
 * A dialog fragment to show a generic number picker to the user.
 *
 * @author Rhys Evans
 * @author Chris Loftus (https://github.com/chriswloftus/faa_version6/blob/master/app/src/main/java/uk/ac/aber/dcs/cs31620/faaversion6/ui/cats/NumberPickerDialogFragment.java)
 * @version 0.1
 */
public class NumberPickerDialog extends DialogFragment {

    public static final String TAG = DialogFragment.class.getSimpleName();

    public static final String MIN_VALUE_KEY = "MIN_VALUE";
    public static final String MAX_VALUE_KEY = "MAX_VALUE";
    public static final String MESSAGE_KEY = "MESSAGE";
    public static final String SELECTED_NUMBER_KEY = "SELECTED_NUMBER_KEY";

    private NumberPicker.OnValueChangeListener onValueChangeListener;

    private NumberPicker numberPicker;

    /**
     * Default Constructor
     */
    public NumberPickerDialog(){}

    /**
     * Called when the fragment is created
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Link to the parent view (if it implements onValueChangedListener)
        // this way the parent view can be 'informed' of any updates to the number picker
        Context parent = getContext();
        if(parent instanceof NumberPicker.OnValueChangeListener){
            this.onValueChangeListener = (NumberPicker.OnValueChangeListener)parent;
        }
    }

    /**
     * Called when the dialog object is created
     * @param savedInstanceState
     * @return A dialog objeect
     */
    @Override
    @NonNull
    public AlertDialog onCreateDialog(Bundle savedInstanceState){
        // Create a new number picker
        numberPicker = new NumberPicker(getActivity());
        Bundle args = this.getArguments();

        // Get the minimum and maximum values and the message to
        // be displayed from the dialog's arguments
        int minValue = args.getInt(MIN_VALUE_KEY);
        int maxValue = args.getInt(MAX_VALUE_KEY);
        String message = args.getString(MESSAGE_KEY);

        // Set the min and max values
        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);

        // Disable editing values on number picker
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        // Retrieve value from saved instance state, if null just default to min value
        if(savedInstanceState != null){
            numberPicker.setValue(savedInstanceState.getInt(SELECTED_NUMBER_KEY, minValue));
        }

        // Create the dialog itself
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(message);

        // Add behaviour to 'ok' button
        // When users confirms dialog, end activity
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            if(onValueChangeListener != null){
                onValueChangeListener.onValueChange(numberPicker, numberPicker.getValue(), numberPicker.getValue());
            }
        });

        // Do nothing if cancel
        builder.setNegativeButton(android.R.string.cancel, null);

        // Create the dialog
        builder.setView(numberPicker);
        AlertDialog newDialog = builder.create();

        // Change button colors
        newDialog.setOnShowListener(dialog -> {
            Button positiveButton = newDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            Button negativeButton = newDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        });

        return newDialog;
    }


    /**
     * Called when dialog is about to be destroyed to persist the current selection
     * @param outState
     */
    public void onSaveInstanceState(@NonNull Bundle outState){
        outState.putInt(SELECTED_NUMBER_KEY, numberPicker.getValue());
        super.onSaveInstanceState(outState);
    }
}
