package UWSN_ResurfacingGA;

import org.apache.log4j.BasicConfigurator;

public class Main {

    public static void main(final String[] args) {
        BasicConfigurator.configure();

        GAforResurfacing.getInstance().run();
    }

}
