package testSupport

import com.lesfurets.jenkins.unit.BasePipelineTest


class PipelineTestHelper extends BasePipelineTest {

    /**
     * Override the setup for our purposes
     */
    @Override
    void setUp() {

        helper.scriptRoots = ['']
        helper.scriptExtension = ''

        super.setUp()

        registerDeclarativeMethods()

        registerScriptedMethods()

        setJobVariables()

    }

    /**
     * Declarative pipeline methods not in the base
     *
     * See here:
     * https://www.cloudbees.com/sites/default/files/declarative-pipeline-refcard.pdf
     */
    void registerDeclarativeMethods() {

        // For execution of the pipeline
        helper.registerAllowedMethod('execute', [], {})

        helper.registerAllowedMethod('pipeline', [Closure.class], null)
        helper.registerAllowedMethod('options', [Closure.class], null)
        helper.registerAllowedMethod('environment', [Closure.class], { Closure c ->

            def envBefore = [env: binding.getVariable('env')]
            println "Env section - original env vars: ${envBefore.toString()}"
            c.resolveStrategy = Closure.DELEGATE_FIRST
            c.delegate = envBefore
            c()

            def envNew = envBefore.env
            envBefore.each { k, v ->
                if (k != 'env') {
                    envNew["$k"] = v
                }

            }
            println "Env section - env vars set to: ${envNew.toString()}"
            binding.setVariable('env', envNew)
        })

        helper.registerAllowedMethod('triggers', [Closure.class], null)
        helper.registerAllowedMethod('pollSCM', [String.class], null)
        helper.registerAllowedMethod('cron', [String.class], null)

        helper.registerAllowedMethod('parameters', [Closure.class], null)
        helper.registerAllowedMethod('string', [Map.class], null)

        helper.registerAllowedMethod('agent', [Closure.class], null)
        helper.registerAllowedMethod('label', [String.class], null)
        helper.registerAllowedMethod('docker', [String.class], null)
        helper.registerAllowedMethod('image', [String.class], null)
        helper.registerAllowedMethod('args', [String.class], null)
        helper.registerAllowedMethod('dockerfile', [Closure.class], null)
        helper.registerAllowedMethod('dockerfile', [Boolean.class], null)

        helper.registerAllowedMethod('timestamps', [], null)


        helper.registerAllowedMethod('tools', [Closure.class], null)

        helper.registerAllowedMethod('stages', [Closure.class], null)

        /**
         * Skip processing stage if abort / fail set
         */
        helper.registerAllowedMethod('stage', [String.class, Closure.class], { String stgName, Closure body ->
            def status = binding.getVariable('currentBuild').result
            switch (status) {
                case 'FAILURE':
                case 'ABORTED':
                    println "Stage ${stgName} skipped - job status: ${status}"
                    break
                default:
                    return body()
            }

        })

        helper.registerAllowedMethod('steps', [Closure.class], null)
        helper.registerAllowedMethod('script', [Closure.class], null)

        helper.registerAllowedMethod('when', [Closure.class], null)
        helper.registerAllowedMethod('expression', [Closure.class], null)




        helper.registerAllowedMethod('post', [Closure.class], null)

        /**
         * Handling the post sections
         */
        def postResultEmulator = { String section, Closure c ->

            def currentBuild = binding.getVariable('currentBuild')

            switch (section) {
                case 'always':
                case 'changed': // How to handle changed? It may happen so just run it..
                    return c.call()
                    break
                case 'success':
                    if(currentBuild.result == 'SUCCESS') { return c.call() }
                    else { println "post ${section} skipped as not SUCCESS"; return null}
                    break
                case 'unstable':
                    if(currentBuild.result == 'UNSTABLE') { return c.call() }
                    else { println "post ${section} skipped as SUCCESS"; return null}
                    break
                case 'failure':
                    if(currentBuild.result == 'FAILURE') { return c.call() }
                    else { println "post ${section} skipped as not FAILURE"; return null}
                    break
                case 'aborted':
                    if(currentBuild.result == 'ABORTED') { return c.call() }
                    else { println "post ${section} skipped as not ABORTED"; return null}
                    break
                default:
                    assert false, "post section ${section} is not recognised. Check pipeline syntax."
                    break
            }
        }
        helper.registerAllowedMethod('always', [Closure.class], postResultEmulator.curry('always'))
        helper.registerAllowedMethod('changed', [Closure.class], postResultEmulator.curry('changed'))
        helper.registerAllowedMethod('success', [Closure.class], postResultEmulator.curry('success'))
        helper.registerAllowedMethod('unstable', [Closure.class], postResultEmulator.curry('unstable'))
        helper.registerAllowedMethod('failure', [Closure.class], postResultEmulator.curry('failure'))
    }

    /**
     * Scripted pipeline methods not in the base
     */
    void registerScriptedMethods() {

        /**
         * In minutes:
         * timeout(20) {}
         */
        helper.registerAllowedMethod('timeout', [Integer.class, Closure.class], null)

        helper.registerAllowedMethod('waitUntil', [Closure.class], null)
        helper.registerAllowedMethod('writeFile', [Map.class], null)
        helper.registerAllowedMethod('build', [Map.class], null)
        helper.registerAllowedMethod('tool', [Map.class], { t -> "${t.name}_HOME" })

        helper.registerAllowedMethod('withCredentials', [Map.class, Closure.class], null)
        helper.registerAllowedMethod('withCredentials', [List.class, Closure.class], null)
        helper.registerAllowedMethod('usernamePassword', [Map.class], { creds -> return creds })

        helper.registerAllowedMethod('deleteDir', [], null)
        helper.registerAllowedMethod('pwd', [], { 'workspaceDirMocked' })

        helper.registerAllowedMethod('stash', [Map.class], null)
        helper.registerAllowedMethod('unstash', [Map.class], null)

        helper.registerAllowedMethod('checkout', [Closure.class], null)

        helper.registerAllowedMethod('withEnv', [List.class, Closure.class], { List list, Closure c ->

            list.each {
                //def env = helper.get
                def item = it.split('=')
                assert item.size() == 2, "withEnv list does not look right: ${list.toString()}"
                addEnvVar(item[0], item[1])
                c.delegate = binding
                c.call()
            }
        })


    }

    /**
     * Variables that Jenkins expects
     */
    void setJobVariables() {

        /**
         * Job params - may need to override in specific tests
         */
        binding.setVariable('params', [])

        /**
         * Env passed in from Jenkins - may need to override in specific tests
         */
        binding.setVariable('env', [BUILD_NUMBER: '1234', PATH: '/some/path'])

        /**
         * The currentBuild in the job
         */
        binding.setVariable('currentBuild', new Expando(result: 'SUCCESS', displayName: 'Build #1234'))

        /**
         * agent any
         * agent none
         */
        binding.setVariable('any', {})
        binding.setVariable('none', {})

        /**
         * checkout scm
         */
        binding.setVariable('scm', {})

        /**
         * PATH
         */
        binding.setVariable('PATH', '/some/path')
    }

    /**
     * Prettier print of call stack to whatever taste
     */
    @Override
    void printCallStack() {
        println '>>>>>> pipeline call stack -------------------------------------------------'
        super.printCallStack()
        println ''
    }

    /**
     * Helper for adding a params value in tests
     */
    void addParam(String name, String val, Boolean overWrite = false) {
        Map params = binding.getVariable('params') as Map
        if (params == null) {
            params = [:]
            binding.setVariable('params', params)
        }
        if (params[name] == null || overWrite) {
            params[name] = val
        }
    }

    /**
     * Helper for adding a environment value in tests
     */
    void addEnvVar(String name, String val) {
        Map env = binding.getVariable('env') as Map
        if (env == null) {
            env = [:]
            binding.setVariable('env', env)
        }
        env[name] = val
    }

}
