package UWSN_ResurfacingGA;

public class Main {

	public static void main(final String[] args) {
		for (int i = 0; i < 100; i++) {
			System.out.println(i + "-Nodes:  " + new ResurfacingSimulation().Run(i));
		}
	}

}
