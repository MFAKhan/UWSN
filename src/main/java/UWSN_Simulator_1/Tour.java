package UWSN_Simulator_1;

import java.util.ArrayList;
import java.util.Random;

import GA_UWSN.GeneticAlgorithms;

public class Tour implements Comparable<Tour> {

	public String NameOfPlanner;

	public int NumOfAUVs = -1;
	public int NumOfNodes = -1; // Not Initialized
	public int AUVTourMaxLength_1 = -1;
	public int AUVTourMaxLength_2 = -1;

	public int AUVTourMaxLength = -1;

	ArrayList<ArrayList<Integer>> theTour = new ArrayList<ArrayList<Integer>>();
	ArrayList<Double> DistanceTravelledForTour = new ArrayList<Double>();
	ArrayList<Double> ExecutionTimeOfTour = new ArrayList<Double>();

	// Fitness not yet evaluated
	private double VoIAccumulatedByTour = -1;
	private int NumTimesResurfaced = -1;

	private static Random rand = new Random();

	public Tour(final String InitType, final int AUVs, final int Nodes, final SimulationMap myMap,
			final String DistanceType, final int NumResurfaceStops, final double Speed, final double DistanceScale,
			final double TimeStamp, final ArrayList<Integer> aTour) {
		this.NumOfAUVs = AUVs;
		this.NumOfNodes = Nodes;
		this.AUVTourMaxLength_1 = this.NumOfNodes / this.NumOfAUVs;
		this.AUVTourMaxLength_2 = (int) Math.ceil(this.NumOfNodes / this.NumOfAUVs);
		this.AUVTourMaxLength = this.AUVTourMaxLength_2;

		for (int i = 0; i < this.NumOfAUVs; i++) {
			this.theTour.add(i, new ArrayList<Integer>());
			this.DistanceTravelledForTour.add(i, 0.0);
			this.ExecutionTimeOfTour.add(i, 0.0);
		}
		if (InitType == "Create From Input") {
			this.NameOfPlanner = "CFI";
			this.CreateTour(aTour);
		}
		if (InitType == "Resurface Randomly") {
			this.NameOfPlanner = "RR";
			this.LawnMower(myMap);
			this.InsertResurfaceStops(this.NameOfPlanner, NumResurfaceStops);
		}
		if (InitType == "Resurface At Every Node") {
			this.NameOfPlanner = "REN";
			this.LawnMower(myMap);
			this.InsertResurfaceStops(this.NameOfPlanner, NumResurfaceStops);
		}
		if (InitType == "Resurface At Last Node") {
			this.NameOfPlanner = "RLN";
			this.LawnMower(myMap);
			this.InsertResurfaceStops(this.NameOfPlanner, NumResurfaceStops);
		}
		if (InitType == "Resurface After K-Nodes") {
			this.NameOfPlanner = "RKN";
			this.LawnMower(myMap);
			this.InsertResurfaceStops(this.NameOfPlanner, NumResurfaceStops);
		}
		if (InitType == "Resurface Tour Using GA1") {
			this.NameOfPlanner = "G_PR ";
			this.LawnMower(myMap);
			this.RouteOptimizedByGA1(myMap, Nodes, AUVs, Speed, DistanceType, DistanceScale, TimeStamp, false);
		}
		if (InitType == "Reversal : Resurface Tour Using GA1") {
			this.NameOfPlanner = "G_PR-R ";
			this.LawnMower(myMap);
			this.RouteOptimizedByGA1(myMap, Nodes, AUVs, Speed, DistanceType, DistanceScale, TimeStamp, true);
		}
		if (InitType == "Resurface Tour Using GA2") {
			this.NameOfPlanner = "G_Opt ";
			this.LawnMower(myMap);
			this.RouteOptimizedByGA2(myMap, Nodes, AUVs, Speed, DistanceType, DistanceScale, TimeStamp,
					this.theTour.get(0));
		}
		if (InitType == "Resurface Tour Using GA3") {
			this.NameOfPlanner = "G_Opt-S ";
			this.LawnMower(myMap);
			this.RouteOptimizedByGA3(myMap, Nodes, AUVs, Speed, DistanceType, DistanceScale, TimeStamp,
					this.theTour.get(0));
		}
		if (InitType == "Reversal : Resurface After K-Nodes") {
			this.NameOfPlanner = "RKNR";
			this.LawnMower(myMap);
			this.InsertResurfaceStops(this.NameOfPlanner, NumResurfaceStops);
		}
		this.CountNumberOfTimesResurfaced();
	}

	private void CountNumberOfTimesResurfaced() {
		int count = 0;
		for (int y = 0; y < this.NumOfAUVs; y++) {
			for (int z = 0; z < this.theTour.get(y).size(); z++) {
				if (this.theTour.get(y).get(z) == -2) {
					count++;
				}
			}
		}
		this.NumTimesResurfaced = count;
	}

	private void CreateTour(final ArrayList<Integer> aTour) {
		this.theTour.clear();
		this.theTour.add(new ArrayList<Integer>());
		for (int z = 0; z < aTour.size(); z++) {
			this.theTour.get(0).add(aTour.get(z));
		}
	}

	private void LawnMower(final SimulationMap M) {
		int x = 0;
		int NodeToBeAssigned = -1;
		for (int y = 0; y < this.NumOfAUVs; y++) {
			for (int z = 0; z < this.AUVTourMaxLength; z++) {
				MapTraversalLoop: for (int m = 0; m < M.sizeMetric_X; m++) {
					for (int n = 0; n < M.sizeMetric_Y; n++) {
						if ((m % 2) == 0) {
							NodeToBeAssigned = M.nodes.get(m).get(n).nodeIdentifier;
						} else if ((m % 2) == 1) {
							NodeToBeAssigned = M.nodes.get(m).get((M.sizeMetric_Y - 1) - n).nodeIdentifier;
						}
						if (((M.sizeMetric_X * m) + n) == x) {
							break MapTraversalLoop;
						}
					}
				}
				if (x < this.NumOfNodes) {
					this.theTour.get(y).add(z, NodeToBeAssigned);
					x++;
				} else {
					this.theTour.get(y).add(z, -1);
				}
			}
		}
	}

	private void RouteOptimizedByGA1(final SimulationMap Map, final int NumNodes, final int NumAUVs, final double Speed,
			final String DistanceType, final double DistanceScale, final double TimeStamp, final boolean Reversal) {
		final GeneticAlgorithms G = new GeneticAlgorithms();
		final int optimizedResurfacingStops = G.OptimizeRurfacingUsingGA1(Map, NumNodes, NumAUVs, Speed, DistanceType,
				DistanceScale, TimeStamp, Reversal);
		this.InsertResurfaceStops("RKN", optimizedResurfacingStops);
	}

	private void RouteOptimizedByGA2(final SimulationMap Map, final int NumNodes, final int NumAUVs, final double Speed,
			final String DistanceType, final double DistanceScale, final double TimeStamp, final ArrayList<Integer> T) {
		final GeneticAlgorithms G = new GeneticAlgorithms();
		final ArrayList<Integer> Tour = G.OptimizeRurfacingUsingGA2(Map, NumNodes, NumAUVs, Speed, DistanceType,
				DistanceScale, TimeStamp, T);
		for (int i = 0; i < Tour.size(); i++) {
			if (Tour.get(i) == -3) {
				Tour.remove(i);
			}
		}
		this.CreateTour(Tour);
	}

	private void RouteOptimizedByGA3(final SimulationMap Map, final int NumNodes, final int NumAUVs, final double Speed,
			final String DistanceType, final double DistanceScale, final double TimeStamp, final ArrayList<Integer> T) {
		final GeneticAlgorithms G = new GeneticAlgorithms();
		final ArrayList<Integer> Tour = G.OptimizeRurfacingUsingGA3(Map, NumNodes, NumAUVs, Speed, DistanceType,
				DistanceScale, TimeStamp, T);
		for (int i = 0; i < Tour.size(); i++) {
			if (Tour.get(i) == -3) {
				Tour.remove(i);
			}
		}
		this.CreateTour(Tour);
	}

	private void InsertResurfaceStops(final String TypeOfInsertion, final int NodeVisitsBetweenStops) {
		if (TypeOfInsertion == "RR") {
			for (int y = 0; y < this.NumOfAUVs; y++) {
				final int InitialNodesInTour = this.theTour.get(y).size();
				for (int z = 0; z < this.theTour.get(y).size(); z++) {
					// Generate Random Number
					final int num = rand.nextInt(InitialNodesInTour);
					if ((num > InitialNodesInTour / 2) && this.theTour.get(y).get(z) != -2) {
						this.theTour.get(y).add(z, -2); // Resurface Stop
														// Indicator : -2
						z++;
					}
				}
				// Append resurface stop in the end if it is not appended by the
				// above loop
				if (this.theTour.get(y).get(this.theTour.get(y).size() - 1) != -2) {
					this.theTour.get(y).add(-2);
				}
			}
		}
		if (TypeOfInsertion == "REN") {
			for (int y = 0; y < this.NumOfAUVs; y++) {
				final int InsertIndex = 1;
				for (int z = InsertIndex; z < this.theTour.get(y).size(); z += InsertIndex) {
					z++;
					this.theTour.get(y).add(z, -2); // Resurface Stop Indicator
													// : -2
				}
				// Append resurface stop in the end if it is not appended by the
				// above loop
				if (this.theTour.get(y).get(this.theTour.get(y).size() - 1) != -2) {
					this.theTour.get(y).add(-2);
				}
			}
		}
		if ((TypeOfInsertion == "RLN")
				|| (((TypeOfInsertion == "RKN") || (TypeOfInsertion == "RKNR")) && (NodeVisitsBetweenStops == 0))) {
			for (int y = 0; y < this.NumOfAUVs; y++) {
				// Append resurface stop in the end if it is not appended by the
				// above loop
				if (this.theTour.get(y).get(this.theTour.get(y).size() - 1) != -2) {
					this.theTour.get(y).add(-2);
				}
				this.NameOfPlanner = this.NameOfPlanner + " -\t" + Integer.toString(NodeVisitsBetweenStops);
			}
		}
		if ((TypeOfInsertion == "RKN") && (NodeVisitsBetweenStops != 0)) {
			for (int y = 0; y < this.NumOfAUVs; y++) {
				final int InsertIndex = NodeVisitsBetweenStops;
				for (int z = InsertIndex - 1; z < this.theTour.get(y).size(); z += InsertIndex) {
					z++;
					this.theTour.get(y).add(z, -2);
				}
				// Append resurface stop in the end if it is not appended by the
				// above loop
				if (this.theTour.get(y).get(this.theTour.get(y).size() - 1) != -2) {
					this.theTour.get(y).add(-2);
				}
				this.NameOfPlanner = this.NameOfPlanner + " -\t" + Integer.toString(NodeVisitsBetweenStops);
			}
		}
		if ((TypeOfInsertion == "RKNR") && (NodeVisitsBetweenStops != 0)) {
			for (int y = 0; y < this.NumOfAUVs; y++) {
				final int LastNode = this.theTour.get(y).size();
				final int InsertIndex = NodeVisitsBetweenStops;
				// System.out.println("Stops " + NodeVisitsBetweenStops);
				for (int z = 0; (LastNode - z) >= 0; z += InsertIndex) {
					// this.PrintTour();
					// System.out.println(z);
					this.theTour.get(y).add((LastNode - z), -2);
				}
				// Append resurface stop in the end if it is not appended by the
				// above loop
				if (this.theTour.get(y).get(0) == -2) {
					this.theTour.get(y).remove(0);
				}
				this.NameOfPlanner = this.NameOfPlanner + " -\t" + Integer.toString(NodeVisitsBetweenStops);
			}
		}

	}

	public void PrintTour() {
		System.out.println("\n" + this.NameOfPlanner);
		for (int y = 0; y < this.theTour.size(); y++) {
			System.out.print("AUV " + (y + 1) + "\t");
			for (int z = 0; z < this.theTour.get(y).size(); z++) {
				if (this.theTour.get(y).get(z) == -2) {
					System.out.print("*R*\t");
				} else {
					System.out.print("Node " + (this.theTour.get(y).get(z) + 1) + "\t");
				}
			}
			System.out.println();
		}
	}

	public void PrintToursFitness(final String FitnessBasis, final SimulationMap MyMap, final double LastPacketTS,
			final double AUV_Speed, final double Distance_Scale, final String DistanceType) {
		final VoIEvaluation F = new VoIEvaluation();
		System.out.println("\tTour Fitness\t\tTour Distance\t\tTour Time");
		for (int y = 0; y < this.NumOfAUVs; y++) {
			System.out.print("AUV " + (y + 1) + "\t");
			System.out.print(F.VoISingleAUVTour(FitnessBasis, this, MyMap, LastPacketTS, y, AUV_Speed, Distance_Scale,
					DistanceType));
			System.out.print("\t" + this.DistanceTravelledForTour.get(y));
			System.out.println("\t" + this.ExecutionTimeOfTour.get(y));
		}
		System.out.print("Total \t");
		System.out.println(
				F.VoIAllAUVTours(FitnessBasis, this, MyMap, LastPacketTS, AUV_Speed, Distance_Scale, DistanceType));
	}

	@Override
	public int compareTo(final Tour Comp) {
		if (this.getVoIAccumulatedByTour() < Comp.getVoIAccumulatedByTour()) {
			return -1;
		}
		if (this.getVoIAccumulatedByTour() > Comp.getVoIAccumulatedByTour()) {
			return 1;
		}
		return 0;
	}

	public double getVoIAccumulatedByTour() {
		return this.VoIAccumulatedByTour;
	}

	public void setVoIAccumulatedByTour(final double voIAccumulatedByTour) {
		this.VoIAccumulatedByTour = voIAccumulatedByTour;
	}

	public double getNumTimesResurfaced() {
		return this.NumTimesResurfaced;
	}

}