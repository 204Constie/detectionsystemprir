import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PMO_TestA implements PMO_TestInterface, PMO_LogSource {

	protected CyclicBarrier barrier;
	protected MotionDetectionSystemInterface system;
	protected AtomicBoolean runCompleted = new AtomicBoolean(false);
	protected PMO_ImageGenerator generator;
	protected final int IMAGES = 100;
	protected final int THREADS_LIMIT = 10;
	protected List<PMO_ImageProvider> providers = new ArrayList<>();
	protected PMO_ImageConverter converter = new PMO_ImageConverter();
	protected AtomicInteger threadsLimit = new AtomicInteger(THREADS_LIMIT);
	protected PMO_AtomicCounter threadsMaxCounter = PMO_CountersFactory.prepareCommonMaxStorageCounter();
	protected PMO_AtomicCounter threadsCounter = PMO_CountersFactory.prepareCounterWithMaxStorageSet();
	protected PMO_ResultListener listener = new PMO_ResultListener();
	protected PMO_Barrier myBarrier = new PMO_Barrier();

	{
		system = (MotionDetectionSystemInterface) PMO_GeneralPurposeFabric.fabric("MotionDetectionSystem",
				"MotionDetectionSystemInterface");
	}

	protected void runCompleted() {
		runCompleted.set(true);
	}

	protected void createImages(int images) {
		generator = new PMO_ImageGenerator(0, images, PMO_Consts.IMAGE_SIZE);
	}

	protected void createImageProviders() {
		generator.getFrameNumbers().forEach(fn -> {
			providers.add(new PMO_ImageProvider(system, fn, generator.getImage(fn)));
		});
	}

	protected void prepareImageConverter() {
		converter.setThreadsCounters(threadsCounter);
		converter.setThreadsLimit(threadsLimit);
	}

	protected void prepareResultListener() {
		listener.setFramesExpected(IMAGES - 1);
		listener.setResultsExpected(generator.getExpectedResults());
	}

	protected void setListenerAndConverter() {
		try {
			system.setImageConverter(converter);
			system.setResultListener(listener);
		} catch (Exception e) {
			error("W trakcie wykonywania metod setImageConverter/setResultListener doszlo do wyjatku");
			error(e.toString());
		}
	}

	private void setThreadsLimit() {
		try {
			system.setThreads(THREADS_LIMIT);
		} catch (Exception e) {
			error("W trakcie wykonywania metody setThreads doszlo do wyjatku");
			error(e.toString());
		}
	}

	protected void runProviders() {
		PMO_ThreadsHelper.joinThreads(PMO_ThreadsHelper.createAndStartThreads(providers, true));
		log( "Do systemu dostarczono obrazki");
	}

	@Override
	public void run() {

		createImages(IMAGES);
		createImageProviders();
		prepareMyBarrier();
		prepareImageConverter();
		prepareResultListener();
		setMyBarrier();
		setThreadsLimit();
		setListenerAndConverter();

		runProviders();
		runCompleted();
	}


	private void setMyBarrier() {
		converter.setBarrier(myBarrier);
	}

	protected void prepareMyBarrier() {
	}

	@Override
	public long requiredTime() {
		return (IMAGES / THREADS_LIMIT + 2) * PMO_Consts.CONVERSION_WORST_TIME;
	}

	@Override
	public boolean isOK() {

		if (runCompleted.get()) {
			PMO_SystemOutRedirect.println("Metoda run() test zakonczyla prace");
		} else {
			error("Blad: Test nie ma sensu - metoda run() nie zakonczyla pracy");
			return false;
		}

		boolean result = true;

		if (threadsMaxCounter.get() < threadsLimit.get()) {
			error("Blad: system dziala nieefektywnie. Nie uzywa dostepnych watkow");
			error("Blad: system dziala nieefektywnie. Oczekiwano " + threadsLimit.get() + " jest "
					+ threadsMaxCounter.get());
			result = false;
		} else {
			PMO_SystemOutRedirect.println( "OK: Obliczenia wykonywane wspolbieznie z uzyciem " + threadsMaxCounter.get() + " watkow");
		}

		if (threadsCounter.get() != 0) {
			error("Blad: Liczba uruchomien i zakonczen convert jest rozna?");
			result = false;
		}

		result &= listener.isOK();

		result &= PMO_TestHelper.runTests(providers);

		return result;
	}

}
