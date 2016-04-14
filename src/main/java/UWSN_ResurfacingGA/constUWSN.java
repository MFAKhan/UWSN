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
	public final double RATIO_DD_TO_DS = 1.0;
	// Distance between a node from sea surface
	public final double DEPLOYMENT_DEPTH = constUWSN.RATIO_DD_TO_DS * constUWSN.DISTANCE_SCALE;
	// AUV speed/velocity is 2 m/s
	public final double AUV_SPEED = 2;
	public final String DISTANCE_TYPE = "Euclidean";
	public final int NUM_OF_AUVS = 1;
	public final int X_DIM = 1; // No. of Nodes Horizontally in Mesh
	public final int Y_DIM = 10; // No. of Nodes Vertically in Mesh
	public final int NUM_OF_NODES = constUWSN.X_DIM * constUWSN.Y_DIM;

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Options Packet_Initializtion_Type : Equal, Symmetrical, Random
	public final String PACKET_INITIALIZATION_TYPE = "Random Number of Packets - Fixed VoI Magnitude";
	public final int EXPECTED_MAX_PACKETS_AT_NODE = 5;
	public final double PACKET_INITIALIZATION_MAGNITUDE = 1;
	// VoI should be calibrated in accordance with number of packets
	public final double PACKET_INITIALIZATION_DESIRED_VOI_FACTOR = .05;

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public final int TOUR_TYPES = (2 * constUWSN.NUM_OF_NODES) + 4;
	// public final int TOUR_TYPES = constUWSN.NUM_OF_NODES;
	public final int SAMPLES_PER_TOUR = 5;
	public final int TOTAL_SAMPLES = constUWSN.SAMPLES_PER_TOUR * constUWSN.TOUR_TYPES;

	public double[][] results = new double[constUWSN.TOUR_TYPES][2];
	public double[][] allExperiments = new double[constUWSN.SAMPLES_PER_TOUR][4];

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Options VoICalculationBasis : Transmit, Retrieve
	public final String VoICalculationBasis = "Transmit";

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
