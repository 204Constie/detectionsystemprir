import java.util.concurrent.atomic.AtomicBoolean;

public class PMO_ImageProvider implements PMO_RunnableAndTestable, PMO_LogSource {
	
	private int frameNumber;
	private int[][] image;
	private AtomicBoolean imageProvided = new AtomicBoolean(false);
	private MotionDetectionSystemInterface system;
	private long execTime;

	public PMO_ImageProvider( MotionDetectionSystemInterface system, int frameNumber, int[][] image ) {
		this.frameNumber = frameNumber;
		this.image = image;
		this.system = system;
	}
	
	@Override
	public void run() {
		log("Za chwile zostanie przekazany obrazek numer " + frameNumber );
		execTime = PMO_TimeHelper.executionTime(() -> {
			system.addImage(frameNumber, image);
		});
		
		log("Obrazek numer " + frameNumber + " zostal przekazany w czasie " + execTime + "msec");
		imageProvided.set(true);
	}

	@Override
	public boolean isOK() {
		
		if ( ! imageProvided.get() ) {
			error( "Blad: Obrazka " + frameNumber + " nie udalo sie dostarczyc");
			return false;
		}
		
		if ( execTime > PMO_Consts.ADD_IMAGE_EXEC_TIME ) {
			error( "Blad: Przekroczono limit czasu na dodanie obrazka " + frameNumber );
			return false;
		}
		
		return true;
	}

	
}
