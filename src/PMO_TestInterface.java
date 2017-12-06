
public interface PMO_TestInterface extends PMO_RunnableAndTestable {
	
	/**
	 * Metoda zwraca informacje o ilosci msec potrzebnych do zrealizowania testu.
	 * 
	 * @return czas potrzebny do wykonania testu
	 */
	public long requiredTime();
	
	/**
	 * Metoda uruchamia test jako watek
	 * @return
	 */
	default Thread startTest() {
		return PMO_ThreadsHelper.startThread(this);
	}
}
