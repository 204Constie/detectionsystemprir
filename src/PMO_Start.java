import java.util.function.Supplier;

public class PMO_Start {
	private static boolean runTest(PMO_TestInterface test) {

		showAndLog("--------------------------------------------------------");
		showAndLog("--------------------- faza testu -----------------------");
		showAndLog("--------------------------------------------------------");

		long timeToFinish = test.requiredTime();
		Thread th = test.startTest();

		PMO_SystemOutRedirect.println("timeToFinish = " + timeToFinish);
		PMO_SystemOutRedirect.println("Maksymalny czas oczekiwania to " + (timeToFinish / 1000) + " sekund");

		long beforeJoin = java.lang.System.currentTimeMillis();
		try {
			th.join(timeToFinish);
		} catch (InterruptedException e) {
		}
		long remainingTime = timeToFinish - java.lang.System.currentTimeMillis() + beforeJoin;
		if (remainingTime > 0) {
			PMO_SystemOutRedirect.println("Dodatkowy czas: " + remainingTime + " msec");
			PMO_TimeHelper.sleep(remainingTime);
		}

		PMO_SystemOutRedirect.println("Zakonczyl sie czas oczekiwania na join()");

		if (th.isAlive()) {
			PMO_SystemOutRedirect.println("BLAD: Test nie zostal ukonczony na czas");
			PMO_ThreadWatcher.watch(th);
			return false;
		} else {
			PMO_SystemOutRedirect.println("Uruchamiam test");
			return test.isOK();
		}

	}

	private static void showAndLog(String txt) {
		PMO_SystemOutRedirect.println(txt);
		PMO_Log.log(txt);
	}

	private static void showTest(String txt) {
		showAndLog("+---------------+");
		showAndLog("|               |");
		showAndLog("+-- " + txt + " --+");
		showAndLog("|               |");
		showAndLog("+---------------+");
	}

	private static void shutdown() {
		java.lang.System.out.println("HALT");
		Runtime.getRuntime().halt(0);
		java.lang.System.out.println("EXIT");
		java.lang.System.exit(0);
	}

	private static boolean executeTest(String testName, Supplier<? extends PMO_TestA> testSupplier, int repetitions) {
		boolean result = true;

		for (int i = 0; (i < repetitions) && (result); i++) {
			showTest(testName);
			result &= runTest(testSupplier.get());
			result &= PMO_CommonErrorLog.isStateOK();
		}

		return result;
	}

	public static void main(String[] args) {
		PMO_SystemOutRedirect.startRedirectionToNull();

		PMO_UncaughtException uncaughtExceptionsLog = new PMO_UncaughtException();

		boolean result = true;

		result &= executeTest("Test A", () -> new PMO_TestA(), 3);
		if (result)
			result &= executeTest("Test Ainc + 3 ", () -> new PMO_TestA_inc(3), 3);
		if (result)
			result &= executeTest("Test Ainc - 3 ", () -> new PMO_TestA_inc(-3), 3);
		if (result)
			result &= executeTest("Test B", () -> new PMO_TestB(), 5);
		if (result)
			result &= executeTest("Test C", () -> new PMO_TestC(), 5);

		if (!result) {
			PMO_SystemOutRedirect.println("--- log bledow ---");
			PMO_CommonErrorLog.getErrorLog(0).forEach(PMO_SystemOutRedirect::println);
		}

		if (!uncaughtExceptionsLog.isEmpty()) {
			PMO_SystemOutRedirect.println("--- log wyjatkow ---");
			PMO_CommonErrorLog.error(uncaughtExceptionsLog.toString());
			PMO_SystemOutRedirect.println(uncaughtExceptionsLog.toString());
		}

		if (!result) {
			PMO_Log.showLog();
		}

		PMO_Verdict.show(result);

		shutdown();
	}
}
