package UWSN_Simulator_1;

import java.util.ArrayList;
import java.util.Collections;

public class SimulationMap {

	public ArrayList<ArrayList<Node>> Nodes = new ArrayList<ArrayList<Node>>();
	public Node TransmitNode;
	
	public int SizeMetric_X = 0;
	public int SizeMetric_Y = 0;
	
	public double DistanceScale = -1;

	public SimulationMap(int Size_X, int Size_Y) {
		SizeMetric_X = Size_X;
		SizeMetric_Y = Size_Y;
	}

	public void InitializeMap (String Topology, double DeploymentDepth, double DS) {
		DistanceScale = DS;
		if (Topology == "Mesh" || Topology == "StraightLine") {
			// Create Nodes
			// Create Transmit Node
			int ID = -2;
			TransmitNode = new Node(true, -1, -1, -1, ID);
			// Create Sensor Nodes
			ID = 0;
			for (int i = 0; i < SizeMetric_X; i++) {
				Nodes.add(i, new ArrayList<Node>());
				for (int j = 0; j < SizeMetric_Y; j++) {
					Nodes.get(i).add(new Node(false, i, j, DeploymentDepth, ID));
					ID++;
				}
			}
			// Set Neighbors
			for (int i = 0; i < SizeMetric_X; i++) {
				for (int j = 0; j < SizeMetric_Y; j++) {
					for (int x = 0; x < 3; x++) {
						for (int y = 0; y < 3; y++) {
							int NeighborXindex = i + x - 1;
							int NeighborYindex = j + y - 1;
							if ((NeighborXindex) > -1 && (NeighborYindex) > -1 && (NeighborXindex) < SizeMetric_X && (NeighborYindex) < SizeMetric_Y) {
								Nodes.get(i).get(j).Neighbors[x][y] = Nodes.get(NeighborXindex).get(NeighborYindex); 
							}
							else {
								Nodes.get(i).get(j).Neighbors[x][y] = null; 
							}
						}
					}
				}
			}
		}
	}
	
	public void PrintVoIMap (double TS) {
		for (int Xloc = 0; Xloc < SizeMetric_X; Xloc++) {
			for (int Yloc = 0; Yloc < SizeMetric_Y; Yloc++) {
				System.out.printf("%10.3f", Nodes.get(Xloc).get(Yloc).VoIOfferAtNode(TS));
			}
			System.out.println();
		}
	}

	public Node GetNodeBasedOnId(int ID) {
		Node NodeToFind = null;
		if (ID == -2) {
			NodeToFind = TransmitNode;
		}
		else {
			SearchLoop:
			for (int Xloc = 0; Xloc < SizeMetric_X; Xloc++) {
				for (int Yloc = 0; Yloc < SizeMetric_Y; Yloc++) {		
					if (Nodes.get(Xloc).get(Yloc).NodeIdentifier == ID) {
						NodeToFind = Nodes.get(Xloc).get(Yloc);
						break SearchLoop;
					}
				}
			}
		}
		return NodeToFind;
	}
	
	public double InterNodeDistance(Node Source, Node Destination, String DistanceType) {
		double Distance = -1;
		double SX = Source.coordinate_X;
		double SY = Source.coordinate_Y;
		double DX = Destination.coordinate_X;
		double DY = Destination.coordinate_Y;
		if (DistanceType == "Manhattan") {
			Distance = Math.abs(SX - DX) + Math.abs(SY - DY);
		}
		else if (DistanceType == "Euclidean") {
			Distance = Math.sqrt(Math.pow(Math.abs(SX - DX), 2) + Math.pow(Math.abs(SY - DY), 2));
		}
		return DistanceScale * Distance;
	}
	
	public double AverageNodeDistance(String DistanceType) {
		double AverageDistance = 0;
		for (int XlocS = 0; XlocS < SizeMetric_X; XlocS++) {
			for (int YlocS = 0; YlocS < SizeMetric_Y; YlocS++) {
				for (int XlocD = 0; XlocD < SizeMetric_X; XlocD++) {
					for (int YlocD = 0; YlocD < SizeMetric_Y; YlocD++) {
						Node Source = Nodes.get(XlocS).get(YlocS);
						Node Destination = Nodes.get(XlocD).get(YlocD);
						AverageDistance += InterNodeDistance(Source, Destination, DistanceType); 
					}
				}
			}
		}	
		int NumberofNodes = SizeMetric_X * SizeMetric_Y;
		int NeighborsOfEachNode = (SizeMetric_X * SizeMetric_Y) - 1;
		return (AverageDistance) / (NumberofNodes * NeighborsOfEachNode);	
	}
	
	public double AverageTourLength(String DistanceType) {
		int NumberofNodes = SizeMetric_X * SizeMetric_Y;
		return AverageNodeDistance(DistanceType) * NumberofNodes;
	}
	
	public Node ClosestNeighborToDestination(Node Source, Node Destination, String DistanceType) {
		Node MinimumDistanceNode = Source;
		double MinimumDistance = InterNodeDistance(Source, Destination, "Euclidean");
		double Distance = -1;
		double SNX = -1;
		double SNY = -1;
		double DX = Destination.coordinate_X;
		double DY = Destination.coordinate_Y;
		
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (Source.Neighbors[x][y] != null) {
					SNX = Source.Neighbors[x][y].coordinate_X;
					SNY = Source.Neighbors[x][y].coordinate_Y;
					if (DistanceType == "Manhattan") {
						Distance = Math.abs(SNX - DX) + Math.abs(SNY - DY);
					}
					else if (DistanceType == "Euclidean") {
						Distance = Math.sqrt(Math.pow(Math.abs(SNX - DX), 2) + Math.pow(Math.abs(SNY - DY), 2));
					}
					if (MinimumDistance > Distance) {
						MinimumDistance = Distance;
						MinimumDistanceNode = Source.Neighbors[x][y];
					}
					//System.out.println("Node "+Source.Neighbors[x][y].NodeIdentifier+"  "+Source.Neighbors[x][y].coordinate_X+","+Source.Neighbors[x][y].coordinate_Y+" -- "+Distance);
				}
			}
		}
		//System.out.println("Node "+MinimumDistanceNode.NodeIdentifier+"  "+MinimumDistanceNode.coordinate_X+","+MinimumDistanceNode.coordinate_Y+" -- "+MinimumDistance);
		return MinimumDistanceNode;
	}
	
	public ArrayList<Node> FindNodesInPath (Node Source, Node Destination, String DistanceType) {
		ArrayList<Node> NodesInPath = new ArrayList<Node>();
		Node ToAddToPath = Source;
		while (Destination.NodeIdentifier != ToAddToPath.NodeIdentifier) {
			ToAddToPath = ClosestNeighborToDestination(ToAddToPath, Destination, DistanceType);
			NodesInPath.add(ToAddToPath);
		}
		return NodesInPath;
	}
	
	public ArrayList<ArrayList<Integer>> AdjustTour(ArrayList<ArrayList<Integer>> Tour, String DistanceType) {
		ArrayList<ArrayList<Integer>> AdjustedTour = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Node>> AdjustedTourList = new ArrayList<ArrayList<Node>>();
		ArrayList<Node> NodesInPath = new ArrayList<Node>();
		ArrayList<Node> Temp = new ArrayList<Node>();
		
		//Initializing
		for (int i = 0; i < Tour.size(); i++) {
			AdjustedTour.add(i, new ArrayList<Integer>());
		}
		
		//Adding Intermediate Nodes to a Node List
		for (int i = 0; i < Tour.size(); i++) {
			AdjustedTourList.add(i, new ArrayList<Node>());
			Node SourceNode = GetNodeBasedOnId(Tour.get(i).get(0));
			Node DestinationNode;
			NodesInPath.add(NodesInPath.size(), SourceNode);
			for (int j = 0; j < Tour.get(i).size(); j++) {
				if ((j + 1) < Tour.get(i).size()) {
					SourceNode = GetNodeBasedOnId(Tour.get(i).get(j));
					DestinationNode = GetNodeBasedOnId(Tour.get(i).get(j+1));
					Temp = FindNodesInPath(SourceNode, DestinationNode, DistanceType);
					NodesInPath.addAll(NodesInPath.size(), Temp);					
					AdjustedTourList.get(i).addAll(NodesInPath);
					NodesInPath.clear();
					Temp.clear();
				}
			}
		}

		//Copying Node-List to the Tour
		for (int i = 0; i < AdjustedTourList.size(); i++) {
			for (int j = 0; j < AdjustedTourList.get(i).size(); j++) {
				AdjustedTour.get(i).add(AdjustedTourList.get(i).get(j).NodeIdentifier);
			}
		}

		//Deleting Visited Nodes
		//Finding Length Of Max Size Tour
		int MaxLengthTour = 0;
		for (int i = 0; i < AdjustedTour.size(); i++) {
			if (AdjustedTour.get(i).size() > MaxLengthTour) {
				MaxLengthTour = AdjustedTour.get(i).size();
			}
		}
		//Delete
		for (int j = 0; j < MaxLengthTour; j++) {
			for (int i = 0; i < AdjustedTour.size(); i++) {
				if (j < AdjustedTour.get(i).size()) {
					SearchAndDeleteAllInstances(i, j, AdjustedTour);
				}
			}
		}
		// Load Balancing method here ();
		return AdjustedTour;
	}
	
	private void SearchAndDeleteAllInstances(int i, int j, ArrayList<ArrayList<Integer>> ACList) {
		int NodeIDToDelete = ACList.get(i).get(j);
		for (int m = 0; m < ACList.size(); m++) {		
			for (int n = 0; n < ACList.get(m).size(); n++) {
				if (NodeIDToDelete == ACList.get(m).get(n)) {
					if ((m == i) && (n == j)) {
						//Do Nothing
					}
					else {
						//Mark for Deletion
						ACList.get(m).remove(n);
						n--;
					}
				}
			}		
		}
	}

	public ArrayList<Node> MapToList() {
		ArrayList<Node> NodeList = new ArrayList<Node>();
		for (int Xloc = 0; Xloc < SizeMetric_X; Xloc++) {
			for (int Yloc = 0; Yloc < SizeMetric_Y; Yloc++) {
				NodeList.add(Nodes.get(Xloc).get(Yloc));
			}
		}
		return NodeList;
	}
	
	public ArrayList<Node> SortedListBasedOnVoI() {
		ArrayList<Node> NodeList = new ArrayList<Node>();
		for (int Xloc = 0; Xloc < SizeMetric_X; Xloc++) {
			for (int Yloc = 0; Yloc < SizeMetric_Y; Yloc++) {
				NodeList.add(Nodes.get(Xloc).get(Yloc));
			}
		}
		ArrayList<Node> NodeListSorted = NodeList;
		Collections.sort(NodeListSorted);
		return NodeListSorted;
	}

}
