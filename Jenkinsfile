/**
 * Helper to validate a pipeline.
 *
 * @param pipelineFile - Pipeline file to validate
 * @return An error count
 */
int validatePipeline(String pipelineFile) {
    Boolean valid = validateDeclarativePipeline(pipelineFile)
    if(!valid) {
        echo "The file ${pipelineFile} is not a valid declarative pipeline"
        return 1
    }
    return 0
}

/**
 * A CI pipeline for Jenkins pipeline jobs. It will validate the jobs and run the unit tests with Gradle
 */
pipeline {

    agent any

    parameters {
        booleanParam(name: 'VALIDATE', defaultValue: true, description: 'Whether to run validation stage')
        string(name: 'GRADLE_TASKS_OPTIONS', defaultValue: 'clean build test -i', description: 'Tasks and options for the gradle command')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
    }

    triggers {
        pollSCM('*/5 * * * *')
    }

    stages {

        stage('Checkout') {
            steps {
                deleteDir()
                checkout scm
            }
        }

        stage('validate') {

            when { expression { return params.VALIDATE } }

            steps {
                script {

                    int validationErrors = 0

                    // Validate the example jobs. This will only work for declarative
                    validationErrors += validatePipeline('exampleJobs/parallel/Jenkinsfile')

                    // Validate this job
                    validationErrors += validatePipeline('Jenkinsfile')

                    // Fail here if any not valid - need to fix this first
                    if(validationErrors > 0) {
                        error("One or more of the pipeline files are not valid. Validation errors: ${validationErrors}")
                    }
                }
            }
        }

        stage('build') {

            steps {
                withEnv(["GRADLE_HOME=${tool name: 'GRADLE_3', type: 'hudson.plugins.gradle.GradleInstallation'}"]) {
                    withEnv(["PATH=${env.PATH}:${env.GRADLE_HOME}/bin"]) {

                        // Checking the env
                        echo "GRADLE_HOME=${env.GRADLE_HOME}"
                        echo "PATH=${env.PATH}"

                        sh "gradle ${params.GRADLE_TASKS_OPTIONS}"
                    }
                }
            }
        }


    }

    post {
        always {
            echo 'pipeline unit tests completed - recording JUnit results'
            junit 'build/reports/**/*.xml'
        }

        success {
            echo 'pipeline unit tests PASSED'
        }

        failure {
            echo 'pipeline unit tests FAILED'
        }

        changed {
            echo 'pipeline unit tests results have CHANGED'
        }

        unstable {
            echo 'pipeline unit tests have gone UNSTABLE'
        }
    }
}