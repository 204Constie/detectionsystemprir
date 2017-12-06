
public interface MotionDetectionSystemInterface {
	/**
	 * Metoda ustawia liczbe watkow, ktorej system moze uzywac do obliczen. Metoda
	 * zostanie wykonan przed umieszczeniem w systemie pierwszego obrazy. Uwaga: w
	 * trakcie pracy systemu mozliwe jest ponowne wykonanie tej metody. W przypadku
	 * zwiekszenia limitu nowe watki powinny zostac mozliwie szybko zagospodarowane.
	 * W przypadku zmiejszenia limitu oczekuje sie wygaszania nadmiarowych watkow w
	 * miare konczenia sie wykonywanych przez nie prac.
	 * 
	 * @param threads
	 *            dozwolona liczba watkow
	 */
	public void setThreads(int threads);

	/**
	 * Metoda umozliwia przekazanie referencji do obiektu odpowiedzialnego za
	 * przetwarzanie obrazu.
	 * 
	 * @param ici
	 *            referencja do odbiektu przetwarzajacego obraz
	 */
	public void setImageConverter(ImageConverterInterface ici);

	/**
	 * Rejestracja obiektu nasluchujacego. Do tego obiektu nalezy przekazywac dane.
	 * Dane musza byc przekazane wg. naturalnej kolejnosci ramek (rosnaca wartosc
	 * frameNumber). Metoda zostanie jednokrotnie wykonana przed umieszczeniem w
	 * systemie pierwszego obrazu.
	 * 
	 * @param rci
	 *            konsument wynikow przetwarzania ramek
	 */
	public void setResultListener(ResultConsumerInterface rci);

	/**
	 * Metoda przekazujaca do obraz do przetworzenia. Obrazy maja przydzielone
	 * kolejne numery frameNumber od 0 wlacznie. Obrazy moga byc przekazywane w
	 * dowolnej kolejnosci. Wszystkie obrazy maja ten sam rozmiar.
	 * 
	 * @param frameNumber
	 *            numer identyfikujacy obraz (numer ramki filmu, z ktorego obraz
	 *            pochodzi)
	 * @param image
	 *            obraz do przetworzenia.
	 */
	public void addImage(int frameNumber, int[][] image);
}
