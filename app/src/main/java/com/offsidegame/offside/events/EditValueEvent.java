package com.offsidegame.offside.events;

import android.app.Dialog;

/**
 * Created by user on 12/7/2017.
 */

public class EditValueEvent {
    private String dialogTitle;
    private String dialogInstructions;
    private String currentValue;
    private String key;

    public static String updateGroupName = "UpdateGroupName";
    public static String updatePlayerName = "UpdatePlayerName";



    public EditValueEvent(String dialogTitle, String dialogInstructions, String currentValue, String key) {

        this.dialogTitle = dialogTitle;
        this.dialogInstructions = dialogInstructions;
        this.currentValue = currentValue;
        this.key = key;
    }



    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public String getDialogInstructions() {
        return dialogInstructions;
    }

    public void setDialogInstructions(String dialogInstructions) {
        this.dialogInstructions = dialogInstructions;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
