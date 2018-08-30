package tests.job.exampleJobs.globalVariable

import testSupport.PipelineSpockTestBase

class GlobalVariableJobTestSpec extends PipelineSpockTestBase {

    def "gobal variable job Jenkinsfile test"() {

        given:
        helper.registerAllowedMethod('doWithProperties', [Properties.class], null)

        when:
        runScript('exampleJobs/globalVariable/Jenkinsfile')

        then:
        printCallStack()
        assertJobStatusSuccess()

        then:
        testNonRegression("Jenkinsfile_should_complete_with_success")
    }
}
