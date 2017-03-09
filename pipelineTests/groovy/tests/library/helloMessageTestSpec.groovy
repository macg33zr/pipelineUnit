package tests.library

import testSupport.PipelineSpockTestBase

/**
 * How to unit test some vars DSL like shared code
 */
class helloMessageTestSpec extends PipelineSpockTestBase {

    def "test shared library code"() {

        given:
        def helloMessageBody = {
            message = 'This is a test message'
        }

        when:
        def script = loadScript('pipelineLibrary/vars/helloMessage.groovy')
        script.call(helloMessageBody)

        then:
        printCallStack()
        assertJobStatusSuccess()
    }
}
