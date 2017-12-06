import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class PMO_ThreadsHelper {

	public static Thread startThread(Runnable run) {
		Thread th = new Thread(run);
		th.setDaemon(true);
		th.start();
		return th;
	}

	public static void joinThreads(List<Thread> ths) {
		ths.forEach((t) -> {
			try {
				t.join();
			} catch (InterruptedException ie) {
				PMO_SystemOutRedirect.println("Doszlo do wyjatku w trakcie join");
			}
		});
	}

	public static List<Thread> createAndStartThreads(Collection<? extends Runnable> tasks, boolean daemon) {
		List<Thread> result = new ArrayList<>();

		tasks.forEach(t -> {
			result.add(new Thread(t));
		});

		if (daemon) {
			result.forEach(t -> t.setDaemon(true));
		}

		result.forEach(t -> t.start());

		return result;
	}

	public static boolean wait(Object o) {
		if (o != null) {
			synchronized (o) {
				try {
					o.wait();
					return true;
				} catch (InterruptedException ie) {
					PMO_CommonErrorLog.error("Wykryto InterruptedException");
					return false;
				}
			}
		}
		return true;
	}

	public static boolean wait(CyclicBarrier cb) {
		if (cb != null) {
			try {
				cb.await();
				return true;
			} catch (InterruptedException | BrokenBarrierException e) {
				PMO_CommonErrorLog.error("W trakcie await wykryto wyjatek " + e.getMessage());
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public static boolean testIfTrueAndWait(AtomicBoolean o) {
		if (o != null) {
			if (o.get()) {
				return wait(o);
			}
		}
		return true;
	}

	public static void showThreads(Set<Thread> threadSet) {
		threadSet.forEach(PMO_ThreadWatcher::watch);
	}

	public static void showThreads() {
		showThreads(Thread.getAllStackTraces().keySet());
	}

	public static Set<Thread> findThreads(Set<Thread.State> requiredState) {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

		if (requiredState == null) {
			return threadSet;
		}
		return threadSet.stream().filter((th) -> requiredState.contains(th.getState())).collect(Collectors.toSet());
	}

	public static Set<Thread> eliminateThreadsByName(Set<Thread> threads, Set<String> remove) {
		return threads.stream().filter(th -> !remove.contains(th.getName())).collect(Collectors.toSet());
	}

	public static Set<Thread> eliminateThreadsByThread(Set<Thread> threads, Set<Thread> remove) {
		return threads.stream().filter(th -> !remove.contains(th)).collect(Collectors.toSet());
	}
	
	public static Set<Thread> eliminateThreadByClassName( Set<Thread> threads, String className ) {
		return threads.stream().filter( th -> ! thread2String(th).contains(className) ).collect(Collectors.toSet());
	}

	public static String thread2String() {
		return thread2String( Thread.currentThread() );
	}
	
	public static String thread2String(Thread thread) {
		StringBuilder sb = new StringBuilder();

		String threadName = "Thread: " + thread.getName();
		sb.append("Thread > ");
		sb.append(threadName);
		sb.append("\n");

		if (thread.isAlive()) {
			Thread.State state = thread.getState();
			sb.append(threadName);
			sb.append(" State ");
			sb.append(state.name());
			sb.append("\n");
			StackTraceElement[] stet = thread.getStackTrace();
			for (StackTraceElement ste : stet) {
				sb.append(threadName);
				sb.append(" Class: ");
				sb.append(ste.getClassName());
				sb.append(" Method: ");
				sb.append(ste.getMethodName());
				sb.append("@");
				sb.append(ste.getFileName());
				sb.append("@");
				sb.append(ste.getLineNumber());
				sb.append("\n");
			}
		} else {
			sb.append(threadName);
			sb.append("is not alive\n");
		}

		return sb.toString();
	}
	
}
