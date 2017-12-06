import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.IntStream;
import java.awt.geom.Point2D.Double;

public class PMO_ImageGenerator {
	private Map<Integer, int[][]> images = new TreeMap<>();
	private Map<Integer, java.lang.Double> values = new TreeMap<>();
	private Map<Integer, Double> expectedResults = new TreeMap<>();
	private Random rnd = new Random();
	private int size;

	public static double convert(int[][] image) {
		int size = image.length;

		double sum = 0;
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				sum += image[i][j] * (i + 1);

		return sum / size;
	}

	public int[][] getImage(int frameNumber) {
		return images.get(frameNumber);
	}
	
	public Set<Integer> getFrameNumbers() {
		return images.keySet();
	}

	private int[][] createImage() {
		int[][] image = new int[size][size];
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				image[i][j] = rnd.nextInt(1000) - 500;
		return image;
	}

	public Map<Integer, Double> getExpectedResults() {
		return expectedResults;
	}

	public PMO_ImageGenerator(int firstFrameNumber, int maxImages, int size) {
		this.size = size;
		IntStream.range(0, maxImages).forEach((i) -> {
			int frameNumber = firstFrameNumber + i;
			int[][] image = createImage();
			images.put(frameNumber, image);
			double value = convert(image);
//			PMO_SystemOutRedirect.println("Image " + frameNumber + " -> " + value);
			values.put(frameNumber, value);
			if (i > 0) {
				expectedResults.put(frameNumber - 1, new Double(values.get(frameNumber - 1), values.get(frameNumber)));
			}
		});
	}
}
