import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PMO_ResultListener implements ResultConsumerInterface, PMO_Testable, PMO_LogSource {
	private List<Integer> framesDelivered = Collections.synchronizedList(new ArrayList<>());
	private Map<Integer, Double> resultsDelivered = Collections.synchronizedMap(new TreeMap<>());
	private int framesExpected;
	private Map<Integer, Double> resultsExpected;

	@Override
	public void accept(int frameNumber, Double position) {
		log("Odebrano wynik dla " + frameNumber + " " + position);
		framesDelivered.add(frameNumber);
		resultsDelivered.put(frameNumber, position);
	}

	public void setFramesExpected(int framesExpected) {
		this.framesExpected = framesExpected;
	}

	public void setResultsExpected(Map<Integer, Double> resultsExpected) {
		this.resultsExpected = resultsExpected;
	}

	@Override
	public boolean isOK() {
		if (framesExpected == 0) {
			PMO_CommonErrorLog.internalError("lastFrameNumberExpected not set");
		}
		if (resultsExpected == null) {
			PMO_CommonErrorLog.internalError("resultsExpected not set");
		}

		if (framesDelivered.size() != framesExpected) {
			PMO_CommonErrorLog.error("Blad: dostarczono inna liczbe wynikow niz oczekiwano");
			PMO_CommonErrorLog.error("Blad: oczekiwano " + framesExpected + " a do Listenera dotarlo "
					+ framesDelivered.size());
			return false;
		}

		if (framesDelivered.get(0) != 0) {
			PMO_CommonErrorLog.error("Blad: pierwsza z dostarczonych ramek nie ma numeru 0");
			PMO_CommonErrorLog.error("Blad: pierwsza z dostarczonych ramek to " + framesDelivered.get(0));
			return false;
		}

		for (int i = 0; i < framesExpected; i++) {
			if (framesDelivered.get(i) != i) {
				PMO_CommonErrorLog.error("Jako " + (i + 1) + " ramke odebrano ramke o frameNumber "
						+ framesDelivered.get(i) + " a powinno byc " + i);

				PMO_CommonErrorLog.error(framesDelivered.toString());
				return false;
			}
		}

		PMO_SystemOutRedirect.println("Test odebranych frameNumber zaliczony");

		for (Map.Entry<Integer, Double> entry : resultsExpected.entrySet()) {
			// omijamy wyniki, ktore zostaly wprawdzie wyslane, ale nie
			// powinny zostac dostarczone do Listenera
			if (entry.getKey() > framesExpected)
				continue;

			if (!resultsDelivered.containsKey(entry.getKey())) {
				PMO_CommonErrorLog.error("Blad: Wsrod dostarczonych wynikow brak frameNumber " + entry.getKey());
				return false;
			}

			if (!resultsDelivered.get(entry.getKey()).equals(entry.getValue())) {
				PMO_CommonErrorLog.error("Blad: dla frameNumber " + entry.getKey() + " dostarczono bledny wynik.");
				PMO_CommonErrorLog.error("Blad: dla frameNumber " + entry.getKey() + " oczekiwano " + entry.getValue());
				PMO_CommonErrorLog.error(
						"Blad: dla frameNumber " + entry.getKey() + " jest " + resultsDelivered.get(entry.getKey()));
				return false;
			}
		}

		PMO_SystemOutRedirect.println("Test odebranych wynikow zaliczony");

		return true;
	}

}
