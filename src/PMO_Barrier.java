import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class PMO_Barrier implements PMO_LogSource {
	private int registered;
	private int arrived;
	private Runnable barrierAction;
	private Set<Integer> usersToBeServed = Collections.synchronizedSet(new TreeSet<>());

	public PMO_Barrier() {
	}

	public PMO_Barrier(Runnable barrierAction) {
		this.barrierAction = barrierAction;
	}

	synchronized public void register( int userID ) {
		log( "Bariera bedzie blokowac prace " + userID );
		registered++;
		usersToBeServed.add(userID);
	}

	synchronized public void deregister( int userID ) {
		registered--;
		usersToBeServed.remove(userID);
		log( "Bariera przestaje blokowac prace " + userID );
	}
	
	public boolean contains( int userID ) {
		return usersToBeServed.contains( userID );
	}

	public void await() {
		if (registered == 0)
			return;
		
		log( "Wywolano await()" );

		synchronized (this) {
			arrived++;
			if (arrived < registered) {
				try {
					log( "Za moment await->wait()" );
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				if (barrierAction != null) {
					barrierAction.run();
				}
				arrived = 0;
				PMO_SystemOutRedirect.println( "PMO_Barrier notifyAll");
				log( "Za moment await->notifyAll()" );
				notifyAll();
			}
		}
	}
}
