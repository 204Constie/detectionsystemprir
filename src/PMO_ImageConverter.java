import java.awt.geom.Point2D.Double;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PMO_ImageConverter implements ImageConverterInterface, PMO_Testable, PMO_LogSource {

	private Set<Integer> framesExpected;
	private CyclicBarrier blockConversion;
	private PMO_Barrier barrier;

	private AtomicInteger threadsLimit;
	private PMO_AtomicCounter threadsCounter;
	private Random rnd = ThreadLocalRandom.current();
	private Set<Integer> framesConverted = Collections.synchronizedSet( new TreeSet<>() );

	public int getFramesConverted() {
		return framesConverted.size();
	}
	
	public void setThreadsLimit( AtomicInteger threadsLimit ) {
		this.threadsLimit = threadsLimit;
	}
	
	public void setThreadsCounters( PMO_AtomicCounter threadsCounter ) {
		this.threadsCounter = threadsCounter;
	}
	
	public void setBarrier( PMO_Barrier barrier ) {
		this.barrier = barrier;
	}
	
	public void setBlockConversionBarrier( CyclicBarrier barrier ) {
		blockConversion = barrier;
	}
	
	@Override
	public Double convert(int frameNumber, int[][] firstImage, int[][] secondImage) {
		assert threadsLimit != null;
		assert threadsCounter != null;
		assert barrier != null;

		int threads = threadsCounter.incAndStoreMax();
		log("Uruchomiono convert dla frameNumber " + frameNumber + " jako " + threads
				+ " wspolbiezny watek obliczeniowy");
		
		if (threads > threadsLimit.get()) {
			PMO_CommonErrorLog.error("Blad: Doszlo do przekroczenia limitu zasobow");
			PMO_CommonErrorLog.error("Blad: Limit " + threadsLimit.get() + " a zmierzono " + threads);
		}

		if (!framesConverted.add(frameNumber)) {
			PMO_CommonErrorLog.error("Blad: ramka " + frameNumber + " ponownie jest konwertowana");
		}

		long conversionTime = PMO_Consts.CONVERSION_MIN_TIME + rnd.nextLong() % PMO_Consts.CONVERSION_RANDOM_TIME;

		Double result = new Double(PMO_ImageGenerator.convert(firstImage), PMO_ImageGenerator.convert(secondImage));

		PMO_TimeHelper.sleep(conversionTime);

		log( "Zakonczono konwersje frameNumber " + frameNumber );
		
		// blokada w celu niemal rownoczesnego przekazania roznych wynikow - ifContains
		if ( barrier.contains(frameNumber) ) {
			barrier.await();
		}
		
		// blokada komunikacji z powodu zmiany limitu
		PMO_ThreadsHelper.wait(blockConversion);
	
		// koniec pracy convert
		threadsCounter.dec();
		return result;
	}
	
	@Override
	public boolean isOK() {
		assert framesExpected != null;
		AtomicBoolean result = new AtomicBoolean(true);
		
		framesExpected.forEach( (f) -> {
			if ( ! framesConverted.contains( f ) ) {
				error( "Blad: Oczekiwano wywolania convert dla frameNumber " + f );
				result.set( false );
			}
		});
		
		return result.get();
	}

}
