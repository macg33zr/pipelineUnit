package tests.job

import spock.lang.Unroll
import testSupport.PipelineSpockTestBase

/**
 * A spock test to test the Jenkinsfile that runs the gradle to run these Spock tests
 */
class JenkinsfileTestSpec extends PipelineSpockTestBase {

    @Unroll
    def "Jenkinsfile should run gradle tests with expected command line validate:#P_VALIDATE gradle: #P_GRADLE_TASKS_OPTIONS"() {

        given:
        addParam('VALIDATE', P_VALIDATE)
        addParam('GRADLE_TASKS_OPTIONS', P_GRADLE_TASKS_OPTIONS)

        and:
        def shellMock = Mock(Closure)
        helper.registerAllowedMethod('sh', [String.class], shellMock)

        when:
        def script = loadScript('Jenkinsfile')
        script.execute()

        then:
        1 * shellMock.call(_) >> { args ->

            def shellCmd = args[0]
            assert shellCmd == GRADLE_EXPECTED_CMD
        }

        then:
        VAL_COUNT * shellMock.call('TODO_VALIDATION_COMMANDS')

        then:
        printCallStack()
        assertJobStatusSuccess()

        then:
        testNonRegression("Jenkinsfile_Should_Run_Gradle_validate_${P_VALIDATE}_gradle_${P_GRADLE_TASKS_OPTIONS}", false)

        where:
        P_VALIDATE          | P_GRADLE_TASKS_OPTIONS | GRADLE_EXPECTED_CMD          | VAL_COUNT
        null                | null                   | 'gradle clean build test -i' | 0
        true                | 'test'                 | 'gradle test'                | 1
        false               | 'build test'           | 'gradle build test'          | 0
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
