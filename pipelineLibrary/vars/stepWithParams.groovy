/**
 * Example of a library step with parameters
 *
 * In the job you can call it like this:
 *
 * steps {
 *     stepWithParams("param1 value", "param2 value", 12345)
 * }
 *
 * To mock it in a Unit test of a pipeline add code like this:
 *
 * helper.registerAllowedMethod('stepWithParams', [String.class, String.class, Integer.class], null)
 *
 */

def call(String param1, String param2, Integer param3) {

    echo "param1 = ${param1}"
    echo "param2 = ${param2}"
    echo "param3 = ${param3}"
}