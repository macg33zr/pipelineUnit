package tests.job.exampleJobs.parallel

import testSupport.PipelineSpockTestBase

class ParallelJobTestSpec extends PipelineSpockTestBase {

    def "parallel Jenkinsfile test"() {


        when:
        runScript('exampleJobs/parallel/Jenkinsfile')

        then:
        printCallStack()
        assertJobStatusSuccess()
    }
}
