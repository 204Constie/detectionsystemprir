
public interface PMO_LogSource {
	default void log(String txt) {
		PMO_Log.log(txt);
	}

	default void error(String txt) {
		PMO_CommonErrorLog.error(txt);
	}
}
