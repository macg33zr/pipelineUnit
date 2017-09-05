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
        helper.registerAllowedMethod('validateDeclarativePipeline', [String.class], { true } )

        and:
        def shellMock = Mock(Closure)
        helper.registerAllowedMethod('sh', [String.class], shellMock)

        when:
        runScript('Jenkinsfile')

        then:
        1 * shellMock.call(_) >> { List args ->

            // Shell command string comes back as a single element array with JenkinsPipelineUnit 1.1
            // See issue discussion: https://github.com/lesfurets/JenkinsPipelineUnit/issues/59
            println "shellMock args : ${args.toString()}"
            def shellCmd = args[0][0]
            assert shellCmd == GRADLE_EXPECTED_CMD
        }

        then:
        printCallStack()
        assertJobStatusSuccess()

        then:
        testNonRegression("Jenkinsfile_Should_Run_Gradle_validate_${P_VALIDATE}_gradle_${P_GRADLE_TASKS_OPTIONS}")

        where:
        P_VALIDATE          | P_GRADLE_TASKS_OPTIONS | GRADLE_EXPECTED_CMD
        null                | null                   | 'gradle clean build test -i'
        true                | 'test'                 | 'gradle test'
        false               | 'build test'           | 'gradle build test'
    }

    def "Jenkinsfile gradle failure should fail job"() {

        given:
        helper.registerAllowedMethod('validateDeclarativePipeline', [String.class], { true } )

        and:
        def shellMock = Mock(Closure)
        helper.registerAllowedMethod('sh', [String.class], shellMock)

        when:
        runScript('Jenkinsfile')

        then:
        1 * shellMock.call(_) >> { args ->
            println "shellMock args : ${args.toString()}"
            binding.getVariable('currentBuild').result = 'FAILURE'
        }

        then:
        printCallStack()
        assertJobStatusFailure()
    }

    def "Jenkinsfile validation errors should fail the job"() {

        given:
        helper.registerAllowedMethod('validateDeclarativePipeline', [String.class], { false } )

        when:
        runScript('Jenkinsfile')

        then:
        printCallStack()
        assertJobStatusFailure()
    }

    @Unroll
    def "Jenkinsfile cover all build results for post sections - #RESULT"() {

        given:
        helper.registerAllowedMethod('validateDeclarativePipeline', [String.class], { true } )

        and:
        def shellMock = Mock(Closure)
        helper.registerAllowedMethod('sh', [String.class], shellMock)

        and:
        binding.getVariable('currentBuild').result = RESULT

        when:
        runScript('Jenkinsfile')

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
