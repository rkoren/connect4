import java.util.function.Consumer;

/** Async is used to allow computation to be deferred until
 *  later events can provide the answer. */
public interface Async<T> {
	/** Will be called on the event thread
	 *  and must call callback exactly once on the event thread. */
	void async(Consumer<T> callback);
}
