package GA_UWSN.GA3;

import java.util.Comparator;

class GA3_Individual_Comparator implements Comparator<GA3_Individual> {

	@Override
	public int compare(final GA3_Individual e1, final GA3_Individual e2) {
		if (e1.getFitnessValue() < e2.getFitnessValue()) {
			return -1;
		} else {
			return 1;
		}
	}
}