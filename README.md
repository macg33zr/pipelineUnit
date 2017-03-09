# pipelineUnit

This project demonstrates how to write unit tests for Jenkins pipeline code including declarative pipelines, scripted pipelines and shared library code under the /vars area. The framework does not strictly validate the pipeline syntax but it will emulate the pipeline using the JenkinsPipelineUnit framework and can validate any groovy script sections in the pipeline and logic. It may not be complete for all pipeline syntax.

It is using Spock, Gradle and Groovy and the JenkinsPipelineUnit framework available here: https://github.com/lesfurets/JenkinsPipelineUnit

The unit test is actually testing the Jenkinsfile that builds this Gradle project on Jenkins.

The below is an example of a unit test output from Jenkins console:

```
tests.job.JenkinsfileTestSpec > Jenkinsfile should run gradle tests with expected command line STANDARD_OUT
    post failure skipped as not FAILURE
    post unstable skipped as SUCCESS
    >>>>>> pipeline call stack -------------------------------------------------
       Jenkinsfile.run()
          Jenkinsfile.pipeline(groovy.lang.Closure)
             Jenkinsfile.agent(groovy.lang.Closure)
             Jenkinsfile.options(groovy.lang.Closure)
                Jenkinsfile.logRotator({numToKeepStr=10})
                Jenkinsfile.buildDiscarder(null)
                Jenkinsfile.timestamps()
             Jenkinsfile.triggers(groovy.lang.Closure)
                Jenkinsfile.pollSCM(*/5 * * * *)
             Jenkinsfile.stages(groovy.lang.Closure)
                Jenkinsfile.stage(Checkout, groovy.lang.Closure)
                   Jenkinsfile.steps(groovy.lang.Closure)
                      Jenkinsfile.deleteDir()
                      Jenkinsfile.checkout(groovy.lang.Closure)
                Jenkinsfile.stage(build, groovy.lang.Closure)
                   Jenkinsfile.steps(groovy.lang.Closure)
                      Jenkinsfile.tool({name=GRADLE_3, type=hudson.plugins.gradle.GradleInstallation})
                      Jenkinsfile.withEnv([GRADLE_HOME=GRADLE_3_HOME], groovy.lang.Closure)
                         Jenkinsfile.withEnv([PATH=/some/path:GRADLE_3_HOME/bin], groovy.lang.Closure)
                            Jenkinsfile.echo(GRADLE_HOME=GRADLE_3_HOME)
                            Jenkinsfile.echo(PATH=/some/path:GRADLE_3_HOME/bin)
                            Jenkinsfile.sh(gradle clean build test -i)
                Jenkinsfile.stage(validate, groovy.lang.Closure)
                   Jenkinsfile.steps(groovy.lang.Closure)
                      Jenkinsfile.echo(TODO: syntactic validation of Jenkinsfiles)
             Jenkinsfile.post(groovy.lang.Closure)
                Jenkinsfile.always(groovy.lang.Closure)
                   Jenkinsfile.echo(pipeline unit tests completed)
                Jenkinsfile.success(groovy.lang.Closure)
                   Jenkinsfile.echo(pipeline unit tests PASSED)
                Jenkinsfile.failure(groovy.lang.Closure)
                Jenkinsfile.changed(groovy.lang.Closure)
                   Jenkinsfile.echo(pipeline unit tests results have CHANGED)
                Jenkinsfile.unstable(groovy.lang.Closure)
       Jenkinsfile.execute()
```


