package testSupport

/**
 * An exception class to exit a stage due to the when statement
 */
class WhenExitException extends Exception {

    public WhenExitException(String message)
    {
        super(message);
    }
}
