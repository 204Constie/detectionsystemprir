import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PMO_TestThreadsKeeper {
	private Set<Thread> myThreads;

	{
		myThreads = Collections.synchronizedSet(new HashSet<>(PMO_ThreadsHelper.findThreads(null)));
	}

	public void addThread() {
		addThread(Thread.currentThread());
	}

	public void addThread(Thread thread) {
		PMO_Log.log("Adding thread " + thread.getName());
		myThreads.add(thread);
	}

	public void removeThread() {
		Thread thread = Thread.currentThread();
		PMO_Log.log("Removing " + thread.getName());
		myThreads.remove(thread);
	}

	public Set<Thread> getThreads() {
		return myThreads;
	}
}
