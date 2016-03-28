package UWSN_Simulator_1;

import java.util.ArrayList;
import java.util.Collections;

public class SimulationMap {

	public ArrayList<ArrayList<Node>> Nodes = new ArrayList<ArrayList<Node>>();
	public Node TransmitNode;

	public int SizeMetric_X = 0;
	public int SizeMetric_Y = 0;

	public double DistanceScale = -1;

	public SimulationMap(final int Size_X, final int Size_Y) {
		this.SizeMetric_X = Size_X;
		this.SizeMetric_Y = Size_Y;
	}

	public void InitializeMap(final String Topology, final double DeploymentDepth, final double DS) {
		this.DistanceScale = DS;
		if (Topology == "Mesh" || Topology == "StraightLine") {
			// Create Nodes
			// Create Transmit Node
			int ID = -2;
			this.TransmitNode = new Node(true, -1, -1, -1, ID);
			// Create Sensor Nodes
			ID = 0;
			for (int i = 0; i < this.SizeMetric_X; i++) {
				this.Nodes.add(i, new ArrayList<Node>());
				for (int j = 0; j < this.SizeMetric_Y; j++) {
					this.Nodes.get(i).add(new Node(false, i, j, DeploymentDepth, ID));
					ID++;
				}
			}
			// Set Neighbors
			for (int i = 0; i < this.SizeMetric_X; i++) {
				for (int j = 0; j < this.SizeMetric_Y; j++) {
					for (int x = 0; x < 3; x++) {
						for (int y = 0; y < 3; y++) {
							final int NeighborXindex = i + x - 1;
							final int NeighborYindex = j + y - 1;
							if ((NeighborXindex) > -1 && (NeighborYindex) > -1 && (NeighborXindex) < this.SizeMetric_X
									&& (NeighborYindex) < this.SizeMetric_Y) {
								this.Nodes.get(i).get(j).Neighbors[x][y] = this.Nodes.get(NeighborXindex)
										.get(NeighborYindex);
							} else {
								this.Nodes.get(i).get(j).Neighbors[x][y] = null;
							}
						}
					}
				}
			}
		}
	}

	public void PrintVoIMap(final double TS) {
		for (int Xloc = 0; Xloc < this.SizeMetric_X; Xloc++) {
			for (int Yloc = 0; Yloc < this.SizeMetric_Y; Yloc++) {
				System.out.printf("%10.3f", this.Nodes.get(Xloc).get(Yloc).VoIOfferAtNode(TS));
			}
			System.out.println();
		}
	}

	public Node GetNodeBasedOnId(final int ID) {
		Node NodeToFind = null;
		if (ID == -2) {
			NodeToFind = this.TransmitNode;
		} else {
			SearchLoop: for (int Xloc = 0; Xloc < this.SizeMetric_X; Xloc++) {
				for (int Yloc = 0; Yloc < this.SizeMetric_Y; Yloc++) {
					if (this.Nodes.get(Xloc).get(Yloc).NodeIdentifier == ID) {
						NodeToFind = this.Nodes.get(Xloc).get(Yloc);
						break SearchLoop;
					}
				}
			}
		}
		return NodeToFind;
	}

	public double InterNodeDistance(final Node Source, final Node Destination, final String DistanceType) {
		double Distance = -1;
		final double SX = Source.coordinate_X;
		final double SY = Source.coordinate_Y;
		final double DX = Destination.coordinate_X;
		final double DY = Destination.coordinate_Y;
		if (DistanceType == "Manhattan") {
			Distance = Math.abs(SX - DX) + Math.abs(SY - DY);
		} else if (DistanceType == "Euclidean") {
			Distance = Math.sqrt(Math.pow(Math.abs(SX - DX), 2) + Math.pow(Math.abs(SY - DY), 2));
		}
		return this.DistanceScale * Distance;
	}

	public double AverageNodeDistance(final String DistanceType) {
		double AverageDistance = 0;
		for (int XlocS = 0; XlocS < this.SizeMetric_X; XlocS++) {
			for (int YlocS = 0; YlocS < this.SizeMetric_Y; YlocS++) {
				for (int XlocD = 0; XlocD < this.SizeMetric_X; XlocD++) {
					for (int YlocD = 0; YlocD < this.SizeMetric_Y; YlocD++) {
						final Node Source = this.Nodes.get(XlocS).get(YlocS);
						final Node Destination = this.Nodes.get(XlocD).get(YlocD);
						AverageDistance += this.InterNodeDistance(Source, Destination, DistanceType);
					}
				}
			}
		}
		final int NumberofNodes = this.SizeMetric_X * this.SizeMetric_Y;
		final int NeighborsOfEachNode = (this.SizeMetric_X * this.SizeMetric_Y) - 1;
		return (AverageDistance) / (NumberofNodes * NeighborsOfEachNode);
	}

	public double AverageTourLength(final String DistanceType) {
		final int NumberofNodes = this.SizeMetric_X * this.SizeMetric_Y;
		return this.AverageNodeDistance(DistanceType) * NumberofNodes;
	}

	public Node ClosestNeighborToDestination(final Node Source, final Node Destination, final String DistanceType) {
		Node MinimumDistanceNode = Source;
		double MinimumDistance = this.InterNodeDistance(Source, Destination, "Euclidean");
		double Distance = -1;
		double SNX = -1;
		double SNY = -1;
		final double DX = Destination.coordinate_X;
		final double DY = Destination.coordinate_Y;

		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (Source.Neighbors[x][y] != null) {
					SNX = Source.Neighbors[x][y].coordinate_X;
					SNY = Source.Neighbors[x][y].coordinate_Y;
					if (DistanceType == "Manhattan") {
						Distance = Math.abs(SNX - DX) + Math.abs(SNY - DY);
					} else if (DistanceType == "Euclidean") {
						Distance = Math.sqrt(Math.pow(Math.abs(SNX - DX), 2) + Math.pow(Math.abs(SNY - DY), 2));
					}
					if (MinimumDistance > Distance) {
						MinimumDistance = Distance;
						MinimumDistanceNode = Source.Neighbors[x][y];
					}
					// System.out.println("Node
					// "+Source.Neighbors[x][y].NodeIdentifier+"
					// "+Source.Neighbors[x][y].coordinate_X+","+Source.Neighbors[x][y].coordinate_Y+"
					// -- "+Distance);
				}
			}
		}
		// System.out.println("Node "+MinimumDistanceNode.NodeIdentifier+"
		// "+MinimumDistanceNode.coordinate_X+","+MinimumDistanceNode.coordinate_Y+"
		// -- "+MinimumDistance);
		return MinimumDistanceNode;
	}

	public ArrayList<Node> FindNodesInPath(final Node Source, final Node Destination, final String DistanceType) {
		final ArrayList<Node> NodesInPath = new ArrayList<Node>();
		Node ToAddToPath = Source;
		while (Destination.NodeIdentifier != ToAddToPath.NodeIdentifier) {
			ToAddToPath = this.ClosestNeighborToDestination(ToAddToPath, Destination, DistanceType);
			NodesInPath.add(ToAddToPath);
		}
		return NodesInPath;
	}

	public ArrayList<ArrayList<Integer>> AdjustTour(final ArrayList<ArrayList<Integer>> Tour,
			final String DistanceType) {
		final ArrayList<ArrayList<Integer>> AdjustedTour = new ArrayList<ArrayList<Integer>>();
		final ArrayList<ArrayList<Node>> AdjustedTourList = new ArrayList<ArrayList<Node>>();
		final ArrayList<Node> NodesInPath = new ArrayList<Node>();
		ArrayList<Node> Temp = new ArrayList<Node>();

		// Initializing
		for (int i = 0; i < Tour.size(); i++) {
			AdjustedTour.add(i, new ArrayList<Integer>());
		}

		// Adding Intermediate Nodes to a Node List
		for (int i = 0; i < Tour.size(); i++) {
			AdjustedTourList.add(i, new ArrayList<Node>());
			Node SourceNode = this.GetNodeBasedOnId(Tour.get(i).get(0));
			Node DestinationNode;
			NodesInPath.add(NodesInPath.size(), SourceNode);
			for (int j = 0; j < Tour.get(i).size(); j++) {
				if ((j + 1) < Tour.get(i).size()) {
					SourceNode = this.GetNodeBasedOnId(Tour.get(i).get(j));
					DestinationNode = this.GetNodeBasedOnId(Tour.get(i).get(j + 1));
					Temp = this.FindNodesInPath(SourceNode, DestinationNode, DistanceType);
					NodesInPath.addAll(NodesInPath.size(), Temp);
					AdjustedTourList.get(i).addAll(NodesInPath);
					NodesInPath.clear();
					Temp.clear();
				}
			}
		}

		// Copying Node-List to the Tour
		for (int i = 0; i < AdjustedTourList.size(); i++) {
			for (int j = 0; j < AdjustedTourList.get(i).size(); j++) {
				AdjustedTour.get(i).add(AdjustedTourList.get(i).get(j).NodeIdentifier);
			}
		}

		// Deleting Visited Nodes
		// Finding Length Of Max Size Tour
		int MaxLengthTour = 0;
		for (int i = 0; i < AdjustedTour.size(); i++) {
			if (AdjustedTour.get(i).size() > MaxLengthTour) {
				MaxLengthTour = AdjustedTour.get(i).size();
			}
		}
		// Delete
		for (int j = 0; j < MaxLengthTour; j++) {
			for (int i = 0; i < AdjustedTour.size(); i++) {
				if (j < AdjustedTour.get(i).size()) {
					this.SearchAndDeleteAllInstances(i, j, AdjustedTour);
				}
			}
		}
		// Load Balancing method here ();
		return AdjustedTour;
	}

	private void SearchAndDeleteAllInstances(final int i, final int j, final ArrayList<ArrayList<Integer>> ACList) {
		final int NodeIDToDelete = ACList.get(i).get(j);
		for (int m = 0; m < ACList.size(); m++) {
			for (int n = 0; n < ACList.get(m).size(); n++) {
				if (NodeIDToDelete == ACList.get(m).get(n)) {
					if ((m == i) && (n == j)) {
						// Do Nothing
					} else {
						// Mark for Deletion
						ACList.get(m).remove(n);
						n--;
					}
				}
			}
		}
	}

	public ArrayList<Node> MapToList() {
		final ArrayList<Node> NodeList = new ArrayList<Node>();
		for (int Xloc = 0; Xloc < this.SizeMetric_X; Xloc++) {
			for (int Yloc = 0; Yloc < this.SizeMetric_Y; Yloc++) {
				NodeList.add(this.Nodes.get(Xloc).get(Yloc));
			}
		}
		return NodeList;
	}

	public ArrayList<Node> SortedListBasedOnVoI() {
		final ArrayList<Node> NodeList = new ArrayList<Node>();
		for (int Xloc = 0; Xloc < this.SizeMetric_X; Xloc++) {
			for (int Yloc = 0; Yloc < this.SizeMetric_Y; Yloc++) {
				NodeList.add(this.Nodes.get(Xloc).get(Yloc));
			}
		}
		final ArrayList<Node> NodeListSorted = NodeList;
		Collections.sort(NodeListSorted);
		return NodeListSorted;
	}

}
