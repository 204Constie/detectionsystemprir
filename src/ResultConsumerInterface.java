import java.awt.geom.Point2D;

public interface ResultConsumerInterface {
	/**
	 * Metoda pozwala na przekazanie wyniku obliczen. Wynik position to wynik pracy
	 * metody convert, gdy pierwszym z obrazow byl obraz o numerze frameNumber.
	 * Wyniki musza byc dostarczane jednokrotnie i wylacznie z rosnacymi (kolejnymi) 
	 * wartosciami dla frameNumber.
	 * 
	 * @param frameNumber
	 *            numer identyfikacyjny obrazu
	 * @param position
	 *            wynik pracy metody convert
	 */
	public void accept(int frameNumber, Point2D.Double position);
}
