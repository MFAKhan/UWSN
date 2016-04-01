package UWSN_ResurfacingGA;

/**
 * Contains the constants that are used for the simulation setup
 *
 *
 */
public interface constUWSN {

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Distance between consecutive nodes in meters scale
    public final double DISTANCE_SCALE = 1000;
    public final double RATIO_DD_TO_DS = 2;
    // Distance between a node from sea surface
    public final double DEPLOYMENT_DEPTH =
            constUWSN.RATIO_DD_TO_DS * constUWSN.DISTANCE_SCALE;
    // AUV speed/velocity is 2 m/s
    public final double AUV_SPEED = 2;
    public final String DISTANCE_TYPE = "Euclidean";
    public final int NUM_OF_AUVS = 1;
    public final int X_DIM = 1; // No. of Nodes Horizontally in Mesh
    public final int Y_DIM = 100; // No. of Nodes Vertically in Mesh
    public final int NUM_OF_NODES = constUWSN.X_DIM * constUWSN.Y_DIM;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Options Packet_Initializtion_Type : Equal, Symmetrical, Random
    public final String PACKET_INITIALIZATION_TYPE = "Random";
    public final int EXPECTED_MAX_PACKETS_AT_NODE = 500;
    public final double PACKET_INITIALIZATION_MAGNITUDE = 1;
    // VoI should be calibrated in accordance with number of packets
    public final double PACKET_INITIALIZATION_DESIRED_VOI_FACTOR = .5;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public final int TOUR_TYPES = (2 * constUWSN.NUM_OF_NODES) + 4;
    public final int SAMPLES_PER_TOUR = 1;
    public final int TOTAL_SAMPLES =
            constUWSN.SAMPLES_PER_TOUR * constUWSN.TOUR_TYPES;

    public double[][] results = new double[constUWSN.TOUR_TYPES][1];

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Options VoICalculationBasis : Transmit, Retrieve
    public final String VoICalculationBasis = "Transmit";

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
