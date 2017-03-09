# pipelineUnit

This project demonstrates how to write unit tests for Jenkins pipeline code including declarative pipelines, scripted pipelines and shared library code under the /vars area. The framework does not strictly validate the pipeline syntax but it will emulate the pipeline using the JenkinsPipelineUnit framework and can validate any groovy script sections in the pipeline and logic. It may not be complete for all pipeline syntax.

It is using Spock, Gradle and Groovy and the JenkinsPipelineUnit framework available here: https://github.com/lesfurets/JenkinsPipelineUnit

The unit test is actually testing the Jenkinsfile that builds this Gradle project on Jenkins.



