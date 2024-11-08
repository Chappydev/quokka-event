package com.example.quokka_event.views;

/**
 * Interface to call once viewbutton is clicked to display profile details to admin.
 */
public interface ViewButtonListener {
    /**
     * Send index of the list as a parameter.
     * @param pos
     */
    void viewButtonClick(int pos);
}