package com.example.ben.final_project.Activities;

/**
 * A global delegate for Activities to communicate with their fragments
 */
public interface FragmentsDelegate {
    void onAction(int command, String id);
}
