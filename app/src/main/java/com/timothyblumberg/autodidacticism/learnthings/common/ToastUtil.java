package com.timothyblumberg.autodidacticism.learnthings.common;

import android.view.Gravity;
import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.App;

/**
 * Utility class to show Toast messages.
 */
public final class ToastUtil {

    /**
     * Shows a Toast message for a duration of {@link Toast#LENGTH_SHORT}.
     *
     * @param stringResId the resource ID of the Toast message to display
     */
    public static void showShort(int stringResId) {
        showShort(App.getAppContext().getString(stringResId));
    }

    /**
     * Shows a Toast message for a duration of {@link Toast#LENGTH_SHORT}.
     *
     * @param stringResId the resource ID of the Toast message to display
     * @param formatArgs the arguments to the string resource
     */
    public static void showShort(int stringResId, Object... formatArgs) {
        showShort(App.getAppContext().getString(stringResId, formatArgs));
    }

    /**
     * Shows a Toast message for a duration of {@link Toast#LENGTH_SHORT}.
     *
     * @param message the Toast message to display
     */
    public static void showShort(String message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    /**
     * Shows a Toast message for a duration of {@link Toast#LENGTH_LONG}.
     *
     * @param stringResId the resource ID of the Toast message to display
     */
    public static void showLong(int stringResId) {
        showLong(App.getAppContext().getString(stringResId));
    }

    /**
     * Shows a Toast message for a duration of {@link Toast#LENGTH_LONG}.
     *
     * @param stringResId the resource ID of the Toast message to display
     * @param formatArgs the arguments to the string resource
     */
    public static void showLong(int stringResId, Object... formatArgs) {
        showLong(App.getAppContext().getString(stringResId, formatArgs));
    }

    /**
     * Shows a toast message for a duration of {@link Toast#LENGTH_LONG}.
     *
     * @param message the message to display.
     */
    public static void showLong(String message) {
        showToast(message, Toast.LENGTH_LONG);
    }

    /* Private Methods */

    private static void showToast(String message, int duration) {
        final Toast toast = Toast.makeText(App.getInstance(), message, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}