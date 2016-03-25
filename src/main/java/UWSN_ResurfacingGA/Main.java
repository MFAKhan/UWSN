package UWSN_ResurfacingGA;

public class Main {

	public static void main(final String[] args) {
		for (int i = 0; i < 128; i++) {
			System.out.println(new ResurfacingSimulation().Run(i));
		}
	}

}
