package UWSN_Simulator_1;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Tour implements Comparable<Tour> {

	public String NameOfPlanner;	
	
	public int NumOfAUVs = -1;
	public int NumOfNodes = -1;	//Not Initialized
	public int AUVTourMaxLength_1 = -1;
	public int AUVTourMaxLength_2 = -1;
	
	public int AUVTourMaxLength = -1;
	
	ArrayList<ArrayList<Integer>> theTour = new ArrayList<ArrayList<Integer>>();
	ArrayList<Double> DistanceTravelledForTour = new ArrayList<Double>();
	ArrayList<Double> ExecutionTimeOfTour = new ArrayList<Double>();
	
    private static double VoIAccumulatedByTour = -1;   //  Fitness not yet evaluated

	private static Random rand = new Random();  

	public Tour(String InitType, int AUVs, int Nodes, SimulationMap myMap, String DistanceType, int NumResurfaceStops) {
		NumOfAUVs = AUVs;
		NumOfNodes = Nodes;
		AUVTourMaxLength_1 = NumOfNodes/NumOfAUVs;
		AUVTourMaxLength_2 = (int)Math.ceil(NumOfNodes/NumOfAUVs);		
		AUVTourMaxLength = AUVTourMaxLength_2;

		for (int i = 0; i < NumOfAUVs; i++) {
			theTour.add(i, new ArrayList<Integer>());
			DistanceTravelledForTour.add(i, 0.0);
			ExecutionTimeOfTour.add(i, 0.0);
		}
		if (InitType == "Resurface Randomly 1") {
			NameOfPlanner = "RR1";
			LawnMower(myMap);
			InsertResurfaceStops(NameOfPlanner, NumResurfaceStops);
		}
		if (InitType == "Resurface Randomly 2") {
			NameOfPlanner = "RR2";
			LawnMower(myMap);
			InsertResurfaceStops(NameOfPlanner, NumResurfaceStops);
		}
		if (InitType == "Resurface At Every Node") {
			NameOfPlanner = "REN";
			LawnMower(myMap);
			InsertResurfaceStops(NameOfPlanner, NumResurfaceStops);
		}
		if (InitType == "Resurface At Last Node") {
			NameOfPlanner = "RLN";
			LawnMower(myMap);
			InsertResurfaceStops(NameOfPlanner, NumResurfaceStops);
		}
		if (InitType == "Resurface After K-Nodes") {
			NameOfPlanner = "RKN";
			LawnMower(myMap);
			InsertResurfaceStops(NameOfPlanner, NumResurfaceStops);
		}
	}	

	private void LawnMower(SimulationMap M) {
		int x = 0;
		int NodeToBeAssigned = -1;
		for(int y = 0; y < NumOfAUVs; y++) {
			for(int z = 0; z < AUVTourMaxLength; z++) {
				MapTraversalLoop:
				for(int m = 0; m < M.SizeMetric_X; m++) {
					for(int n = 0; n < M.SizeMetric_Y; n++) {
						if((m % 2) == 0) {
							NodeToBeAssigned = M.Nodes.get(m).get(n).NodeIdentifier;
						}
						else if ((m % 2) == 1) {
							NodeToBeAssigned = M.Nodes.get(m).get((M.SizeMetric_Y - 1) - n).NodeIdentifier;
						}
						if (((M.SizeMetric_X * m) + n) == x) {
							break MapTraversalLoop;
						}
					}
				}
				if(x < NumOfNodes) {
					theTour.get(y).add(z, NodeToBeAssigned);
					x++;
				}
				else {
					theTour.get(y).add(z, -1);
				}	
			}
		}
	}

	private void Random() {
		ArrayList<Integer> NodesPool = new ArrayList<Integer>();
		for (int i = 0; i < NumOfNodes; i++) {
			NodesPool.add(i, i);
		}
		int x = 0;
		for(int y = 0; y < NumOfAUVs; y++) {
			for(int z = 0; z < AUVTourMaxLength; z++) {
				if(x < NumOfNodes) {
					int Node = rand.nextInt(NodesPool.size());
					theTour.get(y).add(z, NodesPool.get(Node));
					NodesPool.remove(Node);
					x++;
				}
				else {
					theTour.get(y).add(z, -1);
				}	
			}
		}
	}

	private void InsertResurfaceStops(String TypeOfInsertion, int NodeVisitsBetweenStops) {
		if (TypeOfInsertion == "RR1" || TypeOfInsertion == "RR2") {
			for(int y = 0; y < NumOfAUVs; y++) {
				int InitialNodesInTour = theTour.get(y).size();
				for(int z = 0; z < theTour.get(y).size(); z++) {
					//Generate Random Number
					int num = rand.nextInt(InitialNodesInTour);
					if (num > InitialNodesInTour / 2) {
						theTour.get(y).add(z, -2); // Resurface Stop Indicator : -2
						z++;
					}
				}
				//Append resurface stop in the end if it is not appended by the above loop
				if (theTour.get(y).get(theTour.get(y).size()-1) != -2) {
					theTour.get(y).add(-2);
				}
			}		
		}
		if (TypeOfInsertion == "REN") {
			for(int y = 0; y < NumOfAUVs; y++) {
				int InsertIndex = 1;
				for(int z = InsertIndex - 1; z < theTour.get(y).size(); z += InsertIndex) {
					z++;
					theTour.get(y).add(z, -2); // Resurface Stop Indicator : -2
				}
				//Append resurface stop in the end if it is not appended by the above loop
				if (theTour.get(y).get(theTour.get(y).size()-1) != -2) {
					theTour.get(y).add(-2);
				}
			}		
		}
		if (TypeOfInsertion == "RLN") {
			for(int y = 0; y < NumOfAUVs; y++) {
				//Append resurface stop in the end if it is not appended by the above loop
				if (theTour.get(y).get(theTour.get(y).size()-1) != -2) {
					theTour.get(y).add(-2);
				}
			}
		}
		if (TypeOfInsertion == "RKN") {
			for(int y = 0; y < NumOfAUVs; y++) {
				int InsertIndex = NodeVisitsBetweenStops;
				for(int z = InsertIndex - 1; z < theTour.get(y).size(); z += InsertIndex) {
					z++;
					theTour.get(y).add(z, -2); // Resurface Stop Indicator : -2
				}
				//Append resurface stop in the end if it is not appended by the above loop
				if (theTour.get(y).get(theTour.get(y).size()-1) != -2) {
					theTour.get(y).add(-2);
				}
			}		
		}

	}
	
	public void PrintTour() {
		System.out.println("\n"+NameOfPlanner);
		for(int y = 0; y < this.theTour.size(); y++) {
			System.out.print("AUV "+ (y + 1) +"\t");
			for(int z = 0; z < this.theTour.get(y).size(); z++) { 
				if(this.theTour.get(y).get(z) == -2) {
					System.out.print("*R*\t");
				}
				else {
					System.out.print("Node "+ (this.theTour.get(y).get(z) + 1) +"\t");
				}
			}
			System.out.println();
		}	
	}

	public void PrintToursFitness(String FitnessBasis, SimulationMap MyMap, double LastPacketTS, double AUV_Speed, double Distance_Scale, String DistanceType) {
		VoIEvaluation F = new VoIEvaluation();
		System.out.println("\tTour Fitness\t\tTour Distance\t\tTour Time");
		for(int y = 0; y < this.NumOfAUVs; y++) {
			System.out.print("AUV "+ (y + 1) +"\t");
			System.out.print(F.VoISingleAUVTour(FitnessBasis, this, MyMap, LastPacketTS, y, AUV_Speed, Distance_Scale, DistanceType));
			System.out.print("\t"+this.DistanceTravelledForTour.get(y));
			System.out.println("\t"+this.ExecutionTimeOfTour.get(y));
		}	
		System.out.print("Total \t");
		System.out.println(F.VoIAllAUVTours(FitnessBasis, this, MyMap, LastPacketTS, AUV_Speed, Distance_Scale, DistanceType));
	}

	public int compareTo(Tour Comp) {
		if(this.getVoIAccumulatedByTour() < Comp.getVoIAccumulatedByTour()) {
            return -1;
        }
        if(this.getVoIAccumulatedByTour() > Comp.getVoIAccumulatedByTour()) {
            return 1;
        }
        return 0;
	}

	public static double getVoIAccumulatedByTour() {
		return VoIAccumulatedByTour;
	}

	public static void setVoIAccumulatedByTour(double voIAccumulatedByTour) {
		VoIAccumulatedByTour = voIAccumulatedByTour;
	}

}