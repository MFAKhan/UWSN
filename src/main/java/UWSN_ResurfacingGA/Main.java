package UWSN_ResurfacingGA;

public class Main {

	public static void main(final String[] args) {
		final ResurfacingSimulation R = new ResurfacingSimulation();
		for (int i = 1; i < 100; i++) {
			System.out.println(R.Run(i));
		}
	}

}
