import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public class PMO_TestHelper {
	public static boolean runTests( Collection<? extends PMO_Testable> tests ) {
		
		AtomicBoolean ab = new AtomicBoolean(true);
		tests.forEach( (t) -> {
			ab.compareAndSet(true, t.isOK());	
		});
		return ab.get();
	}
}
