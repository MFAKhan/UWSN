package UWSN_ResurfacingGA;

//import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
//This projects experiments with resurfacing for a straight line map

import UWSN_Simulator_1.AUV;
import UWSN_Simulator_1.Packet;
import UWSN_Simulator_1.SimulationMap;
import UWSN_Simulator_1.Tour;
import UWSN_Simulator_1.VoIEvaluation;

public class GAforResurfacing {

	private static Random rand = new Random();

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static double ratioDdToDs = 2;
	// naming convention is small letter
	// only final constants are written with captials

	// Distance between consecutive nodes
	public final static double DISTANCE_SCALE = 1000; // Distance between
														// consecutive nodes
	public static double Deployment_Depth = ratioDdToDs * DISTANCE_SCALE; // Distance
																			// between
																			// a
																			// node
																			// from
																			// sea
																			// surface
	public static double AUV_Speed = 2; // AUV speed/velocity is 10 m/s
	public static String DistanceType = "Euclidean";
	public static int NumOfAUVs = 1;
	public static int X_dim = 1; // No. of Nodes Horizontally in Mesh
	public static int Y_dim = 100; // No. of Nodes Vertically in Mesh
	public static int NumOfNodes = X_dim * Y_dim;

	// Equal, Symmetrical, Random
	public static String Packet_Initializtion_Type = "Equal"; // Try Random also
	public static int Expected_Max_Packets_At_Node = 100;
	public static double Packet_Initializtion_Magnitude = 1;
	public static double Packet_Initializtion_DesiredVoIFactor = .5;
	// VoI should be calibrated in accordance with number of packets
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private final int tourTypes = 4 + (X_dim * Y_dim);
	public static int SamplesPerTour = 1;

	public static int getSamplesPerTour() {
		return SamplesPerTour;
	}

	public static void setSamplesPerTour(final int samplesPerTour) {
		SamplesPerTour = samplesPerTour;
	}

	public int TotalSamples = SamplesPerTour * this.tourTypes;
	public static ArrayList<Tour> AllTours = new ArrayList<Tour>();
	public double[][] results = new double[this.tourTypes][1];

	public static String VoICalculationBasis = "Transmit";

	// Retrieve, Transmit
	public static String VoI_Method_For_Fitness_Function = "Retrieve";

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void run() {

		// Initialize Map
		final SimulationMap MyMap = new SimulationMap(X_dim, Y_dim);
		MyMap.InitializeMap("StraightLine", Deployment_Depth, DISTANCE_SCALE);

		// Initialize an AUV for packet initialization measurements
		final AUV Explorer = new AUV(AUV_Speed, DISTANCE_SCALE, 0);

		// Initialize Packets for Nodes on the Map
		final double LastPacketTS = IntializeNodePackets(X_dim, Y_dim, Packet_Initializtion_Type, MyMap, Explorer,
				DistanceType, Packet_Initializtion_Magnitude, Packet_Initializtion_DesiredVoIFactor);
		/*
		 * System.out.println("VoI Map"); MyMap.PrintVoIMap(LastPacketTS);
		 * System.out.println();
		 */
		this.InitializePopulation(MyMap, LastPacketTS);

	}

	public void InitializePopulation(final SimulationMap MyMap, final double LastPacketTS) {
		int NumResurfaceStops = 0;
		for (int i = 0; i < this.tourTypes; i++) {
			Tour T = new Tour("Resurface Randomly 1", NumOfAUVs, NumOfNodes, MyMap, DistanceType, NumResurfaceStops);
			;
			if (i % this.tourTypes == 0) {
				T = new Tour("Resurface Randomly 1", NumOfAUVs, NumOfNodes, MyMap, DistanceType, NumResurfaceStops);
			} else if (i % this.tourTypes == 1) {
				T = new Tour("Resurface Randomly 2", NumOfAUVs, NumOfNodes, MyMap, DistanceType, NumResurfaceStops);
			} else if (i % this.tourTypes == 2) {
				T = new Tour("Resurface At Every Node", NumOfAUVs, NumOfNodes, MyMap, DistanceType, NumResurfaceStops);
			} else if (i % this.tourTypes == 3) {
				T = new Tour("Resurface At Last Node", NumOfAUVs, NumOfNodes, MyMap, DistanceType, NumResurfaceStops);
			} else if (i % this.tourTypes >= 4) {
				NumResurfaceStops++;
				T = new Tour("Resurface After K-Nodes", NumOfAUVs, NumOfNodes, MyMap, DistanceType, NumResurfaceStops);
			}
			final VoIEvaluation X = new VoIEvaluation();
			T.setVoIAccumulatedByTour(X.VoIAllAUVTours(VoICalculationBasis, T, MyMap, LastPacketTS, AUV_Speed,
					DISTANCE_SCALE, DistanceType));
			AllTours.add(T);
			// AllTours.get(i).PrintTour();
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
					for (int i = 0; i < Expected_Max_Packets_At_Node; i++) {
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
					for (int i = 0; i < Expected_Max_Packets_At_Node; i++) {
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
					final int RandNumOfPackets = rand.nextInt(Expected_Max_Packets_At_Node) + 1; // Each
					// node
					// should
					// have
					// at
					// least
					// 1
					// packet
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

		return Decay * 100;
	}

	void analyzeExperiment() {
		// Calculating Averages
		// Initialization
		for (int i = 0; i < this.tourTypes; i++) {
			this.results[i][0] = 0;
		}
		// Summation
		for (int i = 0; i < this.TotalSamples; i++) {
			this.results[i % this.tourTypes][0] += AllTours.get(i).getVoIAccumulatedByTour();
		}
		// Division
		for (int i = 0; i < this.tourTypes; i++) {
			this.results[i][0] = this.results[i][0] / SamplesPerTour;
		}
	}

	void printAverages() {
		System.out.println("\n\nTour Type\t\tAvg. VoI\t");
		int ResurfaceStops = 0;
		for (int i = 0; i < this.tourTypes; i++) {
			if (i % this.tourTypes == 0) {
				System.out.print("Random 1\t");
			} else if (i % this.tourTypes == 1) {
				System.out.print("Random 2\t");
			} else if (i % this.tourTypes == 2) {
				System.out.print("Every Node\t");
			} else if (i % this.tourTypes == 3) {
				System.out.print("Last Node\t");
			} else if (i % this.tourTypes >= 4) {
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
