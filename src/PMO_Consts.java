import java.util.ArrayList;
import java.util.Collection;

public class PMO_Consts {
	
	public static final long CONVERSION_MIN_TIME = 500;
	public static final long CONVERSION_RANDOM_TIME = 150;
	public static final long CONVERSION_WORST_TIME = CONVERSION_MIN_TIME + CONVERSION_RANDOM_TIME;
	public static final int IMAGE_SIZE = 6;
	public static final long ADD_IMAGE_EXEC_TIME = 50;
	
	public static final Collection<String> testClasses = 
			new ArrayList<String>() {{
				add( "PMO_SerialTest");
				add( "PMO_SimpleParallelTest");
				add( "PMO_SimpleParallel2Test");
				add( "PMO_ParallelTest");
				add( "PMO_Parallel2Test");
			}};
}
