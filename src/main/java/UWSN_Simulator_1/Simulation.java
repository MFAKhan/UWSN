package UWSN_Simulator_1;

//import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
//This projects experiments with resurfacing for a straight line map

public class Simulation {

	private static Random rand = new Random();

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Distance between consecutive nodes in meters scale
	public final static double DISTANCE_SCALE = 1000;
	public final static double RATIO_DD_TO_DS = 2;
	// Distance between a node from sea surface
	public final static double DEPLOYMENT_DEPTH = RATIO_DD_TO_DS * DISTANCE_SCALE;
	// AUV speed/velocity is 2 m/s
	public final static double AUV_SPEED = 2;
	public final static String DISTANCE_TYPE = "Euclidean";
	public final static int NUM_OF_AUVS = 1;
	public final static int X_DIM = 1; // No. of Nodes Horizontally in Mesh
	public final static int Y_DIM = 10; // No. of Nodes Vertically in Mesh
	public final static int NUM_OF_NODES = X_DIM * Y_DIM;

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Options Packet_Initializtion_Type : Equal, Symmetrical, Random
	public final static String PACKET_INITIALIZATION_TYPE = "Equal";
	public final static int EXPECTED_MAX_PACKETS_AT_NODE = 100;
	public final static double PACKET_INITIALIZATION_MAGNITUDE = 1;
	// VoI should be calibrated in accordance with number of packets
	public final static double PACKET_INITIALIZATION_DESIRED_VOI_FACTOR = .5;

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public final static int TOUR_TYPES = 3 + ((X_DIM * Y_DIM) + 1);
	public final static int SAMPLES_PER_TOUR = 10;
	public final static int TOTAL_SAMPLES = SAMPLES_PER_TOUR * TOUR_TYPES;
	public static ArrayList<Tour> AllTours = new ArrayList<Tour>();
	public double[][] results = new double[TOUR_TYPES][1];

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Options VoICalculationBasis : Transmit, Retrieve
	public final static String VoICalculationBasis = "Transmit";

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public Simulation() {
	}

	public void Run() {
		for (int i = 0; i < SAMPLES_PER_TOUR; i++) {
			this.SingleSimulationRun();
		}
		this.AnalyzeExperiment();
		this.PrintAverages();
	}

	public void SingleSimulationRun() {

		// Initialize Map
		final SimulationMap MyMap = new SimulationMap(X_DIM, Y_DIM);
		MyMap.initializeMap("StraightLine", DEPLOYMENT_DEPTH, DISTANCE_SCALE);

		// Initialize an AUV for packet initialization measurements
		final AUV Explorer = new AUV(AUV_SPEED, DISTANCE_SCALE, 0);

		// Initialize Packets for Nodes on the Map
		final double LastPacketTS = IntializeNodePackets(X_DIM, Y_DIM, PACKET_INITIALIZATION_TYPE, MyMap, Explorer,
				DISTANCE_TYPE, PACKET_INITIALIZATION_MAGNITUDE, PACKET_INITIALIZATION_DESIRED_VOI_FACTOR);
		/*
		 * System.out.println("VoI Map"); MyMap.PrintVoIMap(LastPacketTS);
		 * System.out.println();
		 */
		this.InitializePopulation(MyMap, LastPacketTS);

	}

	public void InitializePopulation(final SimulationMap MyMap, final double LastPacketTS) {
		int NumResurfaceStops = -1;
		for (int i = 0; i < TOUR_TYPES; i++) {
			Tour T = new Tour("Resurface Randomly", NUM_OF_AUVS, NUM_OF_NODES, MyMap, DISTANCE_TYPE, NumResurfaceStops,
					AUV_SPEED, DISTANCE_SCALE, LastPacketTS, null);
			if (i % TOUR_TYPES == 0) {
				T = new Tour("Resurface Randomly", NUM_OF_AUVS, NUM_OF_NODES, MyMap, DISTANCE_TYPE, NumResurfaceStops,
						AUV_SPEED, DISTANCE_SCALE, LastPacketTS, null);
			} else if (i % TOUR_TYPES == 1) {
				T = new Tour("Resurface At Every Node", NUM_OF_AUVS, NUM_OF_NODES, MyMap, DISTANCE_TYPE,
						NumResurfaceStops, AUV_SPEED, DISTANCE_SCALE, LastPacketTS, null);
			} else if (i % TOUR_TYPES == 2) {
				T = new Tour("Resurface At Last Node", NUM_OF_AUVS, NUM_OF_NODES, MyMap, DISTANCE_TYPE,
						NumResurfaceStops, AUV_SPEED, DISTANCE_SCALE, LastPacketTS, null);
			} else if (i % TOUR_TYPES >= 2) {
				NumResurfaceStops++;
				T = new Tour("Resurface After K-Nodes", NUM_OF_AUVS, NUM_OF_NODES, MyMap, DISTANCE_TYPE,
						NumResurfaceStops, AUV_SPEED, DISTANCE_SCALE, LastPacketTS, null);
			}
			final VoIEvaluation X = new VoIEvaluation();
			T.setVoIAccumulatedByTour(X.VoIAllAUVTours(VoICalculationBasis, T, MyMap, LastPacketTS, AUV_SPEED,
					DISTANCE_SCALE, DISTANCE_TYPE));
			AllTours.add(T);
			AllTours.get(i).PrintTour();
		}
	}

	public static void ExterminatePopulation() {
		AllTours.clear();
	}

	public static double IntializeNodePackets(final int x, final int y, final String Method, final SimulationMap M,
			final AUV A, final String DistanceType, final double Magnitude, final double DesiredVoIFactor) {
		double Decay = -1; // to be set yet
		double LatestTS = 0;
		// Equal Initialization
		if (Method == "Equal") {
			for (int Xloc = 0; Xloc < x; Xloc++) {
				for (int Yloc = 0; Yloc < y; Yloc++) {
					LatestTS = 0;
					for (int i = 0; i < EXPECTED_MAX_PACKETS_AT_NODE; i++) {
						LatestTS += 1;
						final Packet NewPacket = new Packet("Normal", Magnitude, Decay, LatestTS);
						M.nodes.get(Xloc).get(Yloc).acquirePacket(NewPacket);
					}
				}
			}
		}
		// Symmetrical Initialization
		else if (Method == "Symmetrical") {
			for (int Xloc = 0; Xloc < x; Xloc++) {
				for (int Yloc = 0; Yloc < y; Yloc++) {
					LatestTS = 5 * Math.pow(Xloc, 2) * Math.pow(Yloc, 2);
					for (int i = 0; i < EXPECTED_MAX_PACKETS_AT_NODE; i++) {
						LatestTS += 1;
						final Packet NewPacket = new Packet("Normal", Magnitude, Decay, LatestTS);
						M.nodes.get(Xloc).get(Yloc).acquirePacket(NewPacket);
					}
				}
			}
		}
		// Random Initialization
		else if (Method == "Random") {
			int TS = 0;
			LatestTS = 0;
			for (int Xloc = 0; Xloc < x; Xloc++) {
				for (int Yloc = 0; Yloc < y; Yloc++) {
					TS = 0;
					final int RandNumOfPackets = rand.nextInt(EXPECTED_MAX_PACKETS_AT_NODE) + 1;
					for (int i = 0; i < RandNumOfPackets; i++) {
						TS += 10;
						final Packet NewPacket = new Packet("Normal", Magnitude, Decay, LatestTS);
						M.nodes.get(Xloc).get(Yloc).acquirePacket(NewPacket);
						if (LatestTS < TS) {
							LatestTS = TS;
						}
					}
				}
			}
		}
		// Setting up decays
		Decay = DetermineDecayConstant(M, A, Magnitude, DesiredVoIFactor, DistanceType, LatestTS);
		for (int Xloc = 0; Xloc < x; Xloc++) {
			for (int Yloc = 0; Yloc < y; Yloc++) {
				for (int i = 0; i < M.nodes.get(Xloc).get(Yloc).packets.size(); i++) {
					final String PacketClass = M.nodes.get(Xloc).get(Yloc).packets.get(i).PacketDataClass;
					if (PacketClass == "Normal") {
						M.nodes.get(Xloc).get(Yloc).packets.get(i).VoIDecay = Decay;
					}
				}
			}
		}
		return LatestTS;
	}

	private static double DetermineDecayConstant(final SimulationMap M, final AUV A, final double Magnitude,
			final double DesiredVoIFactor, final String DistanceType, final Double LatestTS) {
		// t = S / V
		// VoI = A * e^-(Bt) => B = ln(VoI/A) * -1/t
		double Decay = 0;
		// double OffsetTime = LatestTS;
		final double OffsetTime = 0;

		final double TimeForAverageTourLength = M.averageTourLength(DistanceType) / A.AUVvelocity;
		Decay = Math.log((DesiredVoIFactor * Magnitude) / Magnitude) * -(1 / (TimeForAverageTourLength + OffsetTime));

		return Decay;
	}

	void AnalyzeExperiment() {
		// Calculating Averages
		// Initialization
		for (int i = 0; i < TOUR_TYPES; i++) {
			this.results[i][0] = 0;
		}
		// Summation
		for (int i = 0; i < TOTAL_SAMPLES; i++) {
			this.results[i % TOUR_TYPES][0] += AllTours.get(i).getVoIAccumulatedByTour();
		}
		// Division
		for (int i = 0; i < TOUR_TYPES; i++) {
			this.results[i][0] = this.results[i][0] / SAMPLES_PER_TOUR;
		}
	}

	void PrintAverages() {
		System.out.println("\n\nTour Type\t\tAvg. VoI\t");
		int ResurfaceStops = -1;
		for (int i = 0; i < TOUR_TYPES; i++) {
			if (i % TOUR_TYPES == 0) {
				System.out.print("Random 1\t");
			} else if (i % TOUR_TYPES == 1) {
				System.out.print("Random 2\t");
			} else if (i % TOUR_TYPES == 2) {
				System.out.print("Every Node\t");
			} else if (i % TOUR_TYPES == 3) {
				System.out.print("Last Node\t");
			} else if (i % TOUR_TYPES >= 4) {
				ResurfaceStops++;
				System.out.print(ResurfaceStops + " Nodes\t\t");
			}
			for (int j = 0; j < 1; j++) {
				System.out.print("\t" + String.format("%8.2f", this.results[i][j]));
			}
			System.out.println();
		}
	}

}
