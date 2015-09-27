package fr.esgi.twitterc.client;

/**
 * Twitter client specific exceptions.
 */
public class TwitterClientException extends Exception {

    /**
     * Constructor with message.
     *
     * @param message The message.
     */
    public TwitterClientException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     *
     * @param message The message.
     * @param cause The cause.
     */
    public TwitterClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
