
public class PMO_TestC extends PMO_TestA {
	protected PMO_ImageGenerator generator2 = new PMO_ImageGenerator(110, IMAGES, PMO_Consts.IMAGE_SIZE);

	protected void createImageProviders() {
		generator2.getFrameNumbers().forEach(fn -> {
			providers.add(new PMO_ImageProvider(system, fn, generator2.getImage(fn)));
		});

		super.createImageProviders();
	}

	@Override
	public boolean isOK() {
		int conversionsExpected = 2 * IMAGES - 2;
		if (converter.getFramesConverted() != conversionsExpected) {
			error("Blad: nie wykonano wszystkich mozliwych konwersji");
			error("Blad: oczekiwano " + conversionsExpected + " a wykonano " + converter.getFramesConverted());
			return false;
		} else {
			PMO_SystemOutRedirect.println( "OK. Wykonano " + conversionsExpected + " konwersji");
		}

		return super.isOK();
	}

	@Override
	public long requiredTime() {
		return 2 * super.requiredTime();
	}

}
