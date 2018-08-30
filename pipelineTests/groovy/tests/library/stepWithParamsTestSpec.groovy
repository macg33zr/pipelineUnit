package tests.library

import testSupport.PipelineSpockTestBase

class stepWithParamsTestSpec extends PipelineSpockTestBase {

    def "stepWithParams should echo param values"() {

        given:
        def param1 = 'param1 value'
        def param2 = 'param2 value'
        def param3 = 12345


        when:
        def script = loadScript('pipelineLibrary/vars/stepWithParams.groovy')
        script.call(param1, param2, param3)

        then:
        printCallStack()
        assertJobStatusSuccess()

        then:
        testNonRegression("should_echo_values")

    }
}
