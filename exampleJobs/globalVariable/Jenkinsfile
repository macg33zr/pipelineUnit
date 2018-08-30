/**
 * This is a pipeline to analyse an issue I had with JenkinsPipelineUnit
 * around variables recorded in the callstack.
 *
 * With JenkinsPipelineUnit v1.1 the callstack is like this:
 *
 *    Jenkinsfile.run()
 *       Jenkinsfile.pipeline(groovy.lang.Closure)
 *          Jenkinsfile.agent(groovy.lang.Closure)
 *          Jenkinsfile.stages(groovy.lang.Closure)
 *             Jenkinsfile.stage(One, groovy.lang.Closure)
 *                Jenkinsfile.steps(groovy.lang.Closure)
 *                   Jenkinsfile.script(groovy.lang.Closure)
 *                      Jenkinsfile.doWithProperties({PROP_2=VAL_2, PROP_1=VAL_1})
 *                      Jenkinsfile.echo(props = {PROP_1=VAL_1})
 *             Jenkinsfile.stage(Two, groovy.lang.Closure)
 *                Jenkinsfile.steps(groovy.lang.Closure)
 *                   Jenkinsfile.script(groovy.lang.Closure)
 *                      Jenkinsfile.doWithProperties({PROP_2=VAL_2, PROP_1=VAL_1})
 *                      Jenkinsfile.echo(props = {PROP_2=VAL_2, PROP_1=VAL_1})
 *
 * The props object passed to doWithProperties() in the first stage is not shown as passed!
 *
 */

// A properties object global in the pipeline (this does work., use it a lot.)
Properties props = new Properties()

// Simple pipeline
pipeline {

    agent none

    stages {

        // In Stage One add one value to the property and call a library step with it
        stage('One') {
            steps {
                script {
                    // Add a value to the property
                    props.setProperty('PROP_1', 'VAL_1')

                    // Call a custom library step with the properties containing 1 value
                    doWithProperties(props)

                    // Echo it
                    echo "props = ${props.toString()}"
                }

            }
        }

        // In Stage Two add another value to the property and call a library step with it, it should have 2 values
        stage('Two') {
            steps {
                script {
                    // Add a value to the property
                    props.setProperty('PROP_2', 'VAL_2')

                    // Call a custom library step with the properties containing 2 values
                    doWithProperties(props)

                    // Echo it
                    echo "props = ${props.toString()}"
                }
            }
        }
    }
}