package testSupport

import com.lesfurets.jenkins.unit.RegressionTest
import spock.lang.Specification

/**
 * A base class for Spock testing using the pipeline helper
 */
class PipelineSpockTestBase extends Specification  implements RegressionTest {

    /**
     * Delegate to the test helper
     */
    @Delegate PipelineTestHelper pipelineTestHelper

    /**
     * Do the common setup
     */
    def setup() {

        // Set callstacks path for RegressionTest
        callStackPath = 'pipelineTests/groovy/tests/callstacks/'

        // Create and config the helper
        pipelineTestHelper = new PipelineTestHelper()
        pipelineTestHelper.setUp()
    }
}
