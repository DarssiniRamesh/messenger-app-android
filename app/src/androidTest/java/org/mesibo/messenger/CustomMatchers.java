package org.mesibo.messenger;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Custom matchers for Espresso tests
 */
public class CustomMatchers {

    /**
     * Matcher for checking if an EditText has a specific error message
     * @param expectedError The expected error message
     * @return Matcher
     */
    public static Matcher<View> hasErrorText(final String expectedError) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (!(item instanceof EditText)) {
                    return false;
                }
                
                CharSequence error = ((EditText) item).getError();
                if (error == null) {
                    return expectedError == null;
                }
                
                return expectedError.equals(error.toString());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with error: " + expectedError);
            }
        };
    }

    /**
     * Matcher for checking if an ImageView has an image set
     * @return Matcher
     */
    public static Matcher<View> hasImage() {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (!(item instanceof ImageView)) {
                    return false;
                }
                
                ImageView imageView = (ImageView) item;
                return imageView.getDrawable() != null;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has image");
            }
        };
    }

    /**
     * Matcher for checking if a View is enabled
     * @return Matcher
     */
    public static Matcher<View> isEnabled() {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return item.isEnabled();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is enabled");
            }
        };
    }
}