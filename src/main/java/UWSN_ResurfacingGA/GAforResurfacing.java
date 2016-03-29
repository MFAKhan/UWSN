package UWSN_ResurfacingGA;

import java.util.ArrayList;
import java.util.Random;

import UWSN_Simulator_1.AUV;
import UWSN_Simulator_1.Packet;
import UWSN_Simulator_1.SimulationMap;
import UWSN_Simulator_1.Tour;
import UWSN_Simulator_1.VoIEvaluation;

public class GAforResurfacing {

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
	public final static String PACKET_INITIALIZATION_TYPE = "Random";
	public final static int EXPECTED_MAX_PACKETS_AT_NODE = 500;
	public final static double PACKET_INITIALIZATION_MAGNITUDE = 1;
	// VoI should be calibrated in accordance with number of packets
	public final static double PACKET_INITIALIZATION_DESIRED_VOI_FACTOR = .5;

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public final static int TOUR_TYPES = (2 * NUM_OF_NODES) + 4;
	public final static int SAMPLES_PER_TOUR = 1;
	public final static int TOTAL_SAMPLES = SAMPLES_PER_TOUR * TOUR_TYPES;
	public ArrayList<Tour> AllTours = new ArrayList<Tour>();
	public double[][] results = new double[TOUR_TYPES][1];

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Options VoICalculationBasis : Transmit, Retrieve
	public final static String VoICalculationBasis = "Transmit";

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public GAforResurfacing() {
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
		MyMap.InitializeMap("StraightLine", DEPLOYMENT_DEPTH, DISTANCE_SCALE);

		// Initialize an AUV for packet initialization measurements
		final AUV Explorer = new AUV(AUV_SPEED, DISTANCE_SCALE, 0);

		// Initialize Packets for Nodes on the Map
		final double LastPacketTS = IntializeNodePackets(X_DIM, Y_DIM, PACKET_INITIALIZATION_TYPE, MyMap, Explorer,
				DISTANCE_TYPE, PACKET_INITIALIZATION_MAGNITUDE, PACKET_INITIALIZATION_DESIRED_VOI_FACTOR);
		/*
		 * System.out.println("VoI Map"); MyMap.PrintVoIMap(LastPacketTS);
		 * System.out.println();
		 */
		this.CreateAndEvaluateTours(MyMap, LastPacketTS);

	}

	public void CreateAndEvaluateTours(final SimulationMap MyMap, final double LastPacketTS) {
		// add GA tour here
		Tour T;
		VoIEvaluation X;
		for (int NumResurfaceStops = 0; NumResurfaceStops < NUM_OF_NODES; NumResurfaceStops++) {
			T = new Tour("Resurface After K-Nodes", NUM_OF_AUVS, NUM_OF_NODES, MyMap, DISTANCE_TYPE, NumResurfaceStops,
					AUV_SPEED, DISTANCE_SCALE, LastPacketTS, null);
			X = new VoIEvaluation();
			T.setVoIAccumulatedByTour(X.VoIAllAUVTours(VoICalculationBasis, T, MyMap, LastPacketTS, AUV_SPEED,
					DISTANCE_SCALE, DISTANCE_TYPE));
			this.AllTours.add(T);
		}
		T = new Tour("Resurface Tour Using GA1", NUM_OF_AUVS, NUM_OF_NODES, MyMap, DISTANCE_TYPE, -1, AUV_SPEED,
				DISTANCE_SCALE, LastPacketTS, null);
		X = new VoIEvaluation();
		T.setVoIAccumulatedByTour(X.VoIAllAUVTours(VoICalculationBasis, T, MyMap, LastPacketTS, AUV_SPEED,
				DISTANCE_SCALE, DISTANCE_TYPE));
		this.AllTours.add(T);
		for (int NumResurfaceStops = 0; NumResurfaceStops < NUM_OF_NODES; NumResurfaceStops++) {
			T = new Tour("Reversal : Resurface After K-Nodes", NUM_OF_AUVS, NUM_OF_NODES, MyMap, DISTANCE_TYPE,
					NumResurfaceStops, AUV_SPEED, DISTANCE_SCALE, LastPacketTS, null);
			X = new VoIEvaluation();
			T.setVoIAccumulatedByTour(X.VoIAllAUVTours(VoICalculationBasis, T, MyMap, LastPacketTS, AUV_SPEED,
					DISTANCE_SCALE, DISTANCE_TYPE));
			this.AllTours.add(T);
		}
		T = new Tour("Reversal : Resurface Tour Using GA1", NUM_OF_AUVS, NUM_OF_NODES, MyMap, DISTANCE_TYPE, -1,
				AUV_SPEED, DISTANCE_SCALE, LastPacketTS, null);
		X = new VoIEvaluation();
		T.setVoIAccumulatedByTour(X.VoIAllAUVTours(VoICalculationBasis, T, MyMap, LastPacketTS, AUV_SPEED,
				DISTANCE_SCALE, DISTANCE_TYPE));
		this.AllTours.add(T);
		T = new Tour("Resurface Tour Using GA2", NUM_OF_AUVS, NUM_OF_NODES, MyMap, DISTANCE_TYPE, -1, AUV_SPEED,
				DISTANCE_SCALE, LastPacketTS, null);
		X = new VoIEvaluation();
		T.setVoIAccumulatedByTour(X.VoIAllAUVTours(VoICalculationBasis, T, MyMap, LastPacketTS, AUV_SPEED,
				DISTANCE_SCALE, DISTANCE_TYPE));
		this.AllTours.add(T);
		T = new Tour("Resurface Tour Using GA3", NUM_OF_AUVS, NUM_OF_NODES, MyMap, DISTANCE_TYPE, -1, AUV_SPEED,
				DISTANCE_SCALE, LastPacketTS, null);
		X = new VoIEvaluation();
		T.setVoIAccumulatedByTour(X.VoIAllAUVTours(VoICalculationBasis, T, MyMap, LastPacketTS, AUV_SPEED,
				DISTANCE_SCALE, DISTANCE_TYPE));
		this.AllTours.add(T);
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
						M.Nodes.get(Xloc).get(Yloc).AcquirePacket(NewPacket);
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
						M.Nodes.get(Xloc).get(Yloc).AcquirePacket(NewPacket);
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
					// Each node should have at least 1 packet
					final int RandNumOfPackets = rand.nextInt(EXPECTED_MAX_PACKETS_AT_NODE) + 1;
					for (int i = 0; i < RandNumOfPackets; i++) {
						TS += 10;
						final Packet NewPacket = new Packet("Normal", Magnitude, Decay, LatestTS);
						M.Nodes.get(Xloc).get(Yloc).AcquirePacket(NewPacket);
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
				for (int i = 0; i < M.Nodes.get(Xloc).get(Yloc).Packets.size(); i++) {
					final String PacketClass = M.Nodes.get(Xloc).get(Yloc).Packets.get(i).PacketDataClass;
					if (PacketClass == "Normal") {
						M.Nodes.get(Xloc).get(Yloc).Packets.get(i).VoIDecay = Decay;
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

		final double TimeForAverageTourLength = M.AverageTourLength(DistanceType) / A.AUVvelocity;
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
			this.results[i % TOUR_TYPES][0] += this.AllTours.get(i).getVoIAccumulatedByTour();
		}
		// Division
		for (int i = 0; i < TOUR_TYPES; i++) {
			this.results[i][0] = this.results[i][0] / SAMPLES_PER_TOUR;
		}
	}

	void PrintAverages() {
		System.out.println("Tour Type\tAvg. VoI\t");
		for (int i = 0; i < TOUR_TYPES; i++) {
			System.out.print(this.AllTours.get(i).NameOfPlanner + "\t");
			for (int j = 0; j < 1; j++) {
				System.out.print(String.format("%8.2f", this.results[i][j]));
			}
			System.out.println();
		}
	}

}
