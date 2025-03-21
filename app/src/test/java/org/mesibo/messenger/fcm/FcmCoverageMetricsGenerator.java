package org.mesibo.messenger.fcm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that includes all the test classes for FCM components.
 * This helps ensure that all FCM tests are run when generating coverage metrics.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    MesiboGcmListenerServiceTest.class,
    MesiboRegistrationIntentServiceTest.class,
    MesiboJobIntentServiceTest.class
})
public class FcmCoverageMetricsGenerator {
    // This class doesn't need any code as it's just a test suite definition
}