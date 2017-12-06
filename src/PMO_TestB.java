
public class PMO_TestB extends PMO_TestA {
	protected void prepareMyBarrier() {
		// blokada w trakcie przetwarzania ramek 12-15 i 55
		myBarrier.register(12);
		myBarrier.register(13);
		myBarrier.register(14);
		myBarrier.register(15);
		myBarrier.register(55);
	}
	
	@Override
	public long requiredTime() {
		return ( IMAGES / (THREADS_LIMIT - 5 ) + 2) * PMO_Consts.CONVERSION_WORST_TIME;
	}
}
