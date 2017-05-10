pipeline {

    agent any

    parameters {
        booleanParam(name: 'VALIDATE', defaultValue: false, description: 'Whether to run validation stage')
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

        stage('validate') {

            when { expression { return params.VALIDATE } }

            steps {
                sh 'TODO_VALIDATION_COMMANDS'
            }
        }
    }

    post {
        always {
            echo 'pipeline unit tests completed'
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