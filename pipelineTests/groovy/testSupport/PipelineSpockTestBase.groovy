package testSupport

import spock.lang.Specification

/**
 * A base class for Spock testing using the pipeline helper
 */
class PipelineSpockTestBase extends Specification {

    /**
     * Delegate to the test helper
     */
    @Delegate PipelineTestHelper pipelineTestHelper = new PipelineTestHelper()

    /**
     * Do the common setup
     */
    def setup() {
        pipelineTestHelper.setUp()
    }
}
