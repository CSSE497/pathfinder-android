package xyz.thepathfinder.android;

/**
 * The <tt>AuthenticationListener</tt> interface gives developers access to notifications
 * when the user authentication status is known. These notifications include when the authentication
 * sequence is successful and failed.
 *
 * @author David Robinson
 * @see Authenticator
 */
public abstract class AuthenticationListener implements Listener {

    /**
     * Invoked when the user is successfully authenticated.
     */
    public void authenticationSuccessful() {
    }

    /**
     * Invoked when the authentication sequence fails. This could be for an invalid JWT,
     * an expired JWT, a non-http/https server protocol, or an IOException occurring.
     *
     * @param reason for the authentication failure.
     */
    public void authenticationFailed(String reason) {
    }
}
