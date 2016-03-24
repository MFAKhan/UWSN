package UWSN_Simulator_1;

//import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;


//This projects experiments with resurfacing for a straight line map

public class Simulation {

	private static Random rand = new Random();  

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static double Ratio_DDtoDS = 2;									//Distance between consecutive nodes
	public static double Distance_Scale = 1000;								//Distance between consecutive nodes
	public static double Deployment_Depth = Ratio_DDtoDS * Distance_Scale;	//Distance between a node from sea surface
	public static double AUV_Speed = 2;										//AUV speed/velocity is 10 m/s
	public static String DistanceType = "Euclidean";
	public static int NumOfAUVs = 1;
	public static int X_dim = 1;					//No. of Nodes Horizontally in Mesh 
	public static int Y_dim = 100;					//No. of Nodes Vertically in Mesh
	public static int NumOfNodes = X_dim * Y_dim;
	
	//Equal, Symmetrical, Random
	public static String Packet_Initializtion_Type = "Equal";  //Try Random also 
	public static int Expected_Max_Packets_At_Node = 100;
	public static double Packet_Initializtion_Magnitude = 1;
	public static double Packet_Initializtion_DesiredVoIFactor = .5;		
	//VoI should be calibrated in accordance with number of packets
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static int TourTypes = 4 + (X_dim * Y_dim);
	public static int SamplesPerTour = 1;
	public static int TotalSamples = SamplesPerTour * TourTypes;
	public static ArrayList<Tour> AllTours = new ArrayList<Tour>();
	public static double [][] Results = new double [TourTypes][1];

	public static String VoICalculationBasis = "Transmit";
		
	//Retrieve, Transmit
	public static String VoI_Method_For_Fitness_Function = "Retrieve";
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) {
		for (int i = 0; i < SamplesPerTour; i++) {
			Run();
		}
		AnalyzeExperiment();
		PrintAverages();
	}
	
	public static void Run() {
		
		//Initialize Map
		SimulationMap MyMap = new SimulationMap(X_dim, Y_dim);
		MyMap.InitializeMap("StraightLine", Deployment_Depth, Distance_Scale);
		
		//Initialize an AUV for packet initialization measurements
		AUV Explorer = new AUV (AUV_Speed, Distance_Scale, 0);
		
		//Initialize Packets for Nodes on the Map
		double LastPacketTS = 
				IntializeNodePackets
				(
				X_dim, Y_dim, 
				Packet_Initializtion_Type, MyMap, Explorer, DistanceType, 
				Packet_Initializtion_Magnitude, Packet_Initializtion_DesiredVoIFactor
				);
		/*
		System.out.println("VoI Map");
		MyMap.PrintVoIMap(LastPacketTS);
		System.out.println();
		*/
		InitializePopulation(MyMap, LastPacketTS);

	}
	
	public static void InitializePopulation(SimulationMap MyMap, double LastPacketTS){
		int NumResurfaceStops = 0;
		for (int i = 0; i < TourTypes; i++){
			Tour T = new Tour("Resurface Randomly 1", NumOfAUVs, NumOfNodes, MyMap, DistanceType, NumResurfaceStops);;
			if (i % TourTypes == 0){
				T = new Tour("Resurface Randomly 1", NumOfAUVs, NumOfNodes, MyMap, DistanceType, NumResurfaceStops);
			}
			else if (i % TourTypes == 1){
				T = new Tour("Resurface Randomly 2", NumOfAUVs, NumOfNodes, MyMap, DistanceType, NumResurfaceStops);
			}
			else if (i % TourTypes == 2) {
				T = new Tour("Resurface At Every Node", NumOfAUVs, NumOfNodes, MyMap, DistanceType, NumResurfaceStops);
			}
			else if (i % TourTypes == 3) {
				T = new Tour("Resurface At Last Node", NumOfAUVs, NumOfNodes, MyMap, DistanceType, NumResurfaceStops);
			}
			else if (i % TourTypes >= 4) {
				NumResurfaceStops++;
				T = new Tour("Resurface After K-Nodes", NumOfAUVs, NumOfNodes, MyMap, DistanceType, NumResurfaceStops);
			}
			VoIEvaluation X;
			X = new VoIEvaluation();
			T.setVoIAccumulatedByTour(X.VoIAllAUVTours(VoICalculationBasis, T, MyMap, LastPacketTS, AUV_Speed, Distance_Scale, DistanceType));
			AllTours.add(T);
			//AllTours.get(i).PrintTour();
		}
	}
	
	public static void ExterminatePopulation(){
		AllTours.clear();
	}

	public static double IntializeNodePackets(int x, int y, String Method, SimulationMap M, AUV A, String DistanceType, double Magnitude, double DesiredVoIFactor) {
		double Decay = -1;				//to be set yet
		double LatestTS = 0;
		//Equal Initialization
		if (Method == "Equal") {		
			for (int Xloc = 0; Xloc < x; Xloc++) {
				for (int Yloc = 0; Yloc < y; Yloc++) {
					LatestTS = 0;
					for (int i = 0; i < Expected_Max_Packets_At_Node; i++) {
						LatestTS += 1;
						Packet NewPacket = new Packet("Normal", Magnitude, Decay, LatestTS); 
						M.Nodes.get(Xloc).get(Yloc).AcquirePacket(NewPacket);
					}
				}
			}
		}
		//Symmetrical Initialization
		else if (Method == "Symmetrical") {		
			for (int Xloc = 0; Xloc < x; Xloc++) {
				for (int Yloc = 0; Yloc < y; Yloc++) {
					LatestTS = 5 * Math.pow(Xloc, 2) * Math.pow(Yloc, 2);
					for (int i = 0; i < Expected_Max_Packets_At_Node; i++) {
						LatestTS += 1;
						Packet NewPacket = new Packet("Normal", Magnitude, Decay, LatestTS); 
						M.Nodes.get(Xloc).get(Yloc).AcquirePacket(NewPacket);
					}
				}
			}
		}		
		//Random Initialization
		else if (Method == "Random") {
			int TS = 0;
			LatestTS = 0;
			for (int Xloc = 0; Xloc < x; Xloc++) {
				for (int Yloc = 0; Yloc < y; Yloc++) {
					TS = 0;
					int RandNumOfPackets = rand.nextInt(Expected_Max_Packets_At_Node) + 1; //Each node should have at least 1 packet
					for (int i = 0; i < RandNumOfPackets; i++) {
						TS += 10;
						Packet NewPacket = new Packet("Normal", Magnitude, Decay, LatestTS); 
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
					String PacketClass = M.Nodes.get(Xloc).get(Yloc).Packets.get(i).PacketDataClass;
					if (PacketClass == "Normal") {
						M.Nodes.get(Xloc).get(Yloc).Packets.get(i).VoIDecay = Decay;
					}
				}
			}
		}	
		return LatestTS;
	}

	private static double DetermineDecayConstant(SimulationMap M, AUV A, double Magnitude, double DesiredVoIFactor, String DistanceType, Double LatestTS) {
		// t = S / V
		// VoI = A * e^-(Bt) => B = ln(VoI/A) * -1/t
		double Decay = 0;
		//double OffsetTime = LatestTS;
		double OffsetTime = 0;

		double TimeForAverageTourLength = M.AverageTourLength(DistanceType) / A.AUVvelocity;
		Decay = Math.log((DesiredVoIFactor * Magnitude) / Magnitude) * - (1 / (TimeForAverageTourLength + OffsetTime));

		return Decay*100;
	}

	private static void AnalyzeExperiment() {
		//Calculating Averages
		//Initialization
		for (int i = 0; i < TourTypes; i++) {
			Results [i][0] = 0;
		}	
		//Summation
		for (int i = 0; i < TotalSamples; i++) {
			Results [i % TourTypes][0] += AllTours.get(i).getVoIAccumulatedByTour();
		}
		//Division
		for (int i = 0; i < TourTypes; i++) {
			Results [i][0] = Results [i][0] / SamplesPerTour;
		}
	}

	private static void PrintAverages() {
		System.out.println("\n\nTour Type\t\tAvg. VoI\t");
		int ResurfaceStops = 0;
		for (int i = 0; i < TourTypes; i++){
			if (i % TourTypes == 0) {
				System.out.print("Random 1\t");
			}
			else if (i % TourTypes == 1) {
				System.out.print("Random 2\t");
			}
			else if (i % TourTypes == 2) {
				System.out.print("Every Node\t");
			}
			else if (i % TourTypes == 3) {
				System.out.print("Last Node\t");
			}
			else if (i % TourTypes >= 4) {
				ResurfaceStops++;
				System.out.print(ResurfaceStops + " Nodes\t\t");
			}
			for (int j = 0; j < 1; j++) {
				System.out.print("\t"+String.format( "%8.2f", Results[i][j]));
			}
			System.out.println();
		}
	}

}
