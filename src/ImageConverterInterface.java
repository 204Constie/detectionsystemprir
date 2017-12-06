import java.awt.geom.Point2D;

public interface ImageConverterInterface {
	/**
	 * Metoda dokonujaca przetworzenia pary kolejnych ramek, z ktorych pierwsza ma
	 * numer identyfikacyjny frameNumber.
	 * 
	 * @param frameNumber
	 *            numer identyfikacyjny pierwszego z obrazow
	 * @param firstImage
	 *            pierwszy obraz do przetworzenia
	 * @param secondImage
	 *            drugi obraz do przetworzenia
	 * @return wynik przetworzenia pary obrazow.
	 */
	public Point2D.Double convert(int frameNumber, int[][] firstImage, int[][] secondImage);
}
