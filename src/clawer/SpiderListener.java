package clawer;

/**
 * Listener of Spider on page processing. Used for monitor and such on.
 *
 */
public interface SpiderListener {

    public void onSuccess(Request request);

    public void onError(Request request);
}
