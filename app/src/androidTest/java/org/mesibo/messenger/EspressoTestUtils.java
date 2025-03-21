package org.mesibo.messenger;

import android.view.View;
import android.widget.EditText;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Matcher;

/**
 * Utility class for Espresso tests
 */
public class EspressoTestUtils {

    /**
     * Custom ViewAction to set text in an EditText without the keyboard showing up
     * @param value Text to set
     * @return ViewAction
     */
    public static ViewAction setTextInTextView(final String value) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(EditText.class);
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((EditText) view).setText(value);
            }

            @Override
            public String getDescription() {
                return "Set text in TextView";
            }
        };
    }

    /**
     * Custom ViewAction to click a specific position in a view
     * @param percentageX X position as percentage of view width (0-1)
     * @param percentageY Y position as percentage of view height (0-1)
     * @return ViewAction
     */
    public static ViewAction clickXYPosition(final float percentageX, final float percentageY) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Click at position " + percentageX + ", " + percentageY + " of the view";
            }

            @Override
            public void perform(UiController uiController, View view) {
                float[] coordinates = new float[2];
                coordinates[0] = view.getWidth() * percentageX;
                coordinates[1] = view.getHeight() * percentageY;
                float[] precision = new float[2];
                precision[0] = 1f;
                precision[1] = 1f;
                
                // Send a click
                uiController.loopMainThreadUntilIdle();
                androidx.test.espresso.action.MotionEvents.sendDown(uiController, coordinates, precision).down;
                uiController.loopMainThreadForAtLeast(200);
                androidx.test.espresso.action.MotionEvents.sendUp(uiController, coordinates);
            }
        };
    }

    /**
     * Custom ViewAction to wait for a specific duration
     * @param millis Time to wait in milliseconds
     * @return ViewAction
     */
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
}