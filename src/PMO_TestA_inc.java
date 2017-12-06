import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class PMO_TestA_inc extends PMO_TestA {
	protected final int IMAGES = 130;
	protected int newThreadsLimit;
	protected int newThreadsDelta;
	protected AtomicBoolean threadsLimitChanged = new AtomicBoolean(false);

	public PMO_TestA_inc(int newThreadsDelta) {
		this.newThreadsLimit = THREADS_LIMIT + newThreadsDelta ;
		this.newThreadsDelta = newThreadsDelta;
	}

	{
		barrier = new CyclicBarrier(THREADS_LIMIT, () -> {
			log("Wszystkie watki konwersji zatrzymano. Ustawiam nowy limit watkow");
			threadsMaxCounter.clear();
			threadsLimit.set(newThreadsLimit);
			system.setThreads(newThreadsLimit);
			converter.setBlockConversionBarrier(null);
			log("Ustawiono nowy limit watkow " + newThreadsLimit);
			log("Do tej pory wykonano " + converter.getFramesConverted() + " konwersji");
			threadsLimitChanged.set(true);
		});
	}

	protected void prepareImageConverter() {
		super.prepareImageConverter();
		converter.setBlockConversionBarrier(barrier);
	}

	public boolean isOK() {
		if ( ! threadsLimitChanged.get() ) {
			error( "Nie udalo sie zmienic limitu watkow");
			return false;
		}
		return super.isOK();
	}
	
	@Override
	public long requiredTime() {
		if ( newThreadsDelta < 0 ) {
			return (IMAGES / newThreadsLimit + 2) * PMO_Consts.CONVERSION_WORST_TIME;			
		} 
		return super.requiredTime();
	}

}
