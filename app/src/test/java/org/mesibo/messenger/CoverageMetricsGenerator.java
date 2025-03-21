package org.mesibo.messenger;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that includes all the test classes for core components.
 * This helps ensure that all tests are run when generating coverage metrics.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    NotifyUserTest.class,
    UIManagerTest.class,
    MesiboFileTransferHelperTest.class,
    MainApplicationTest.class
})
public class CoverageMetricsGenerator {
    // This class doesn't need any code as it's just a test suite definition
}