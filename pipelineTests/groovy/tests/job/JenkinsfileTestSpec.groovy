package tests.job

import spock.lang.Unroll
import testSupport.PipelineSpockTestBase

/**
 * A spock test to test the Jenkinsfile that runs the gradle to run these Spock tests
 */
class JenkinsfileTestSpec extends PipelineSpockTestBase {

    def "Jenkinsfile should run gradle tests with expected command line"() {

        given:
        def shellMock = Mock(Closure)
        helper.registerAllowedMethod('sh', [String.class], shellMock)

        when:
        def script = loadScript('Jenkinsfile')
        script.execute()

        then:
        1 * shellMock.call(_) >> { args ->

            def shellCmd = args[0]
            assert shellCmd == 'gradle clean build test -i'
        }

        then:
        printCallStack()
        assertJobStatusSuccess()
    }

    def "Jenkinsfile gradle failure should fail job"() {

        given:
        def shellMock = Mock(Closure)
        helper.registerAllowedMethod('sh', [String.class], shellMock)

        when:
        def script = loadScript('Jenkinsfile')
        script.execute()

        then:
        1 * shellMock.call(_) >> { args ->

            binding.getVariable('currentBuild').result = 'FAILURE'
        }

        then:
        printCallStack()
        assertJobStatusFailure()
    }

    @Unroll
    def "Jenkinsfile cover all build results for post sections - #RESULT"() {

        given:
        def shellMock = Mock(Closure)
        helper.registerAllowedMethod('sh', [String.class], shellMock)

        and:
        binding.getVariable('currentBuild').result = RESULT

        when:
        def script = loadScript('Jenkinsfile')
        script.execute()

        then:
        printCallStack()

        where:
        RESULT      | NONE
        'SUCCESS'   | _
        'FAILURE'   | _
        'ABORTED'   | _
        'UNSTABLE'  | _
    }
}
