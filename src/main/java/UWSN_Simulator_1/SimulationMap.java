package UWSN_Simulator_1;

import java.util.ArrayList;
import java.util.Collections;

import UWSN_ResurfacingGA.GAforResurfacing;

public class SimulationMap {

    public ArrayList<ArrayList<Node>> nodes = new ArrayList<ArrayList<Node>>();
    public Node transmitNode;

    public int sizeMetric_X;
    public int sizeMetric_Y;

    public double distaceScale = -1;

    public SimulationMap(final int _sizeMetric_X, final int _sizeMetric_Y) {
        this.sizeMetric_X = _sizeMetric_X;
        this.sizeMetric_Y = _sizeMetric_Y;
    }

    public void initializeMap(final String topology,
        final double deploymentDepth, final double _distanceScale) {
        GAforResurfacing.LOGGER.debug(String.format(
                "Initializing the map for topology %s with deployment depth %s and distance scale %s",
                topology, deploymentDepth, _distanceScale));
        this.distaceScale = _distanceScale;
        if (topology.equals("Mesh") || topology.equals("StraightLine")) {
            // Create Nodes
            // Create Transmit Node
            int id = -2;
            this.transmitNode = new Node(true, -1, -1, -1, id);
            // Create Sensor Nodes
            id = 0;
            for (int i = 0; i < this.sizeMetric_X; i++) {
                this.nodes.add(i, new ArrayList<Node>());
                for (int j = 0; j < this.sizeMetric_Y; j++) {
                    this.nodes.get(i)
                    .add(new Node(false, i, j, deploymentDepth, id));
                    id++;
                }
            }
            // Set Neighbors
            for (int i = 0; i < this.sizeMetric_X; i++) {
                for (int j = 0; j < this.sizeMetric_Y; j++) {
                    for (int x = 0; x < 3; x++) {
                        for (int y = 0; y < 3; y++) {
                            final int neighborXIndex = i + x - 1;
                            final int neighborYIndex = j + y - 1;
                            if ((neighborXIndex) > -1 && (neighborYIndex) > -1
                                    && (neighborXIndex) < this.sizeMetric_X
                                    && (neighborYIndex) < this.sizeMetric_Y) {
                                this.nodes.get(i).get(j).neighbors[x][y] =
                                        this.nodes.get(neighborXIndex)
                                        .get(neighborYIndex);
                            } else {
                                this.nodes.get(i).get(j).neighbors[x][y] = null;
                            }
                        }
                    }
                }
            }
        }
    }

    public void printVoiMap(final double TS) {
        for (int Xloc = 0; Xloc < this.sizeMetric_X; Xloc++) {
            for (int Yloc = 0; Yloc < this.sizeMetric_Y; Yloc++) {
                System.out.printf("%10.3f",
                        this.nodes.get(Xloc).get(Yloc).VoIOfferAtNode(TS));
            }
            System.out.println();
        }
    }

    public Node getNodeBasedOnID(final int ID) {
        Node nodeToFind = null;
        if (ID == -2) {
            nodeToFind = this.transmitNode;
        } else {
            SearchLoop: for (int Xloc = 0; Xloc < this.sizeMetric_X; Xloc++) {
                for (int Yloc = 0; Yloc < this.sizeMetric_Y; Yloc++) {
                    if (this.nodes.get(Xloc).get(Yloc).nodeIdentifier == ID) {
                        nodeToFind = this.nodes.get(Xloc).get(Yloc);
                        break SearchLoop;
                    }
                }
            }
        }
        return nodeToFind;
    }

    public double interNodeDistance(final Node source, final Node destination,
        final String DistanceType) {
        double distance = -1;
        final double SX = source.coordinate_X;
        final double SY = source.coordinate_Y;
        final double DX = destination.coordinate_X;
        final double DY = destination.coordinate_Y;
        if (DistanceType.equals("Manhattan")) {
            distance = Math.abs(SX - DX) + Math.abs(SY - DY);
        } else if (DistanceType.equals("Euclidean")) {
            distance = Math.sqrt(Math.pow(Math.abs(SX - DX), 2)
                    + Math.pow(Math.abs(SY - DY), 2));
        }
        return this.distaceScale * distance;
    }

    public double averageNodeDistance(final String DistanceType) {
        double averageDistance = 0;
        for (int XlocS = 0; XlocS < this.sizeMetric_X; XlocS++) {
            for (int YlocS = 0; YlocS < this.sizeMetric_Y; YlocS++) {
                for (int XlocD = 0; XlocD < this.sizeMetric_X; XlocD++) {
                    for (int YlocD = 0; YlocD < this.sizeMetric_Y; YlocD++) {
                        final Node source = this.nodes.get(XlocS).get(YlocS);
                        final Node destination =
                                this.nodes.get(XlocD).get(YlocD);
                        averageDistance += this.interNodeDistance(source,
                                destination, DistanceType);
                    }
                }
            }
        }
        final int numberOfNodes = this.sizeMetric_X * this.sizeMetric_Y;
        final int neighborsOfEachNode =
                (this.sizeMetric_X * this.sizeMetric_Y) - 1;
        return (averageDistance) / (numberOfNodes * neighborsOfEachNode);
    }

    public double averageTourLength(final String distanceType) {
        final int numberOfNodes = this.sizeMetric_X * this.sizeMetric_Y;
        return this.averageNodeDistance(distanceType) * numberOfNodes;
    }

    public Node closestNeighborToDestination(final Node source,
        final Node destination, final String distanceType) {
        Node MinimumDistanceNode = source;
        double minimumDistance =
                this.interNodeDistance(source, destination, "Euclidean");
        double distance = -1;
        double SNX = -1;
        double SNY = -1;
        final double DX = destination.coordinate_X;
        final double DY = destination.coordinate_Y;

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (source.neighbors[x][y] != null) {
                    SNX = source.neighbors[x][y].coordinate_X;
                    SNY = source.neighbors[x][y].coordinate_Y;
                    if (distanceType.equals("Manhattan")) {
                        distance = Math.abs(SNX - DX) + Math.abs(SNY - DY);
                    } else if (distanceType == "Euclidean") {
                        distance = Math.sqrt(Math.pow(Math.abs(SNX - DX), 2)
                                + Math.pow(Math.abs(SNY - DY), 2));
                    }
                    if (minimumDistance > distance) {
                        minimumDistance = distance;
                        MinimumDistanceNode = source.neighbors[x][y];
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

    public ArrayList<Node> findNodesInPath(final Node source,
        final Node destination, final String distanceType) {
        final ArrayList<Node> nodesInPath = new ArrayList<Node>();
        Node toAddToPath = source;
        while (destination.nodeIdentifier != toAddToPath.nodeIdentifier) {
            toAddToPath = this.closestNeighborToDestination(toAddToPath,
                    destination, distanceType);
            nodesInPath.add(toAddToPath);
        }
        return nodesInPath;
    }

    public ArrayList<ArrayList<Integer>> adjustTour(
        final ArrayList<ArrayList<Integer>> tour, final String distanceType) {
        final ArrayList<ArrayList<Integer>> adjustedTour =
                new ArrayList<ArrayList<Integer>>();
        final ArrayList<ArrayList<Node>> adjustedTourList =
                new ArrayList<ArrayList<Node>>();
        final ArrayList<Node> nodesInPath = new ArrayList<Node>();
        ArrayList<Node> temp = new ArrayList<Node>();

        // Initializing
        for (int i = 0; i < tour.size(); i++) {
            adjustedTour.add(i, new ArrayList<Integer>());
        }

        // Adding Intermediate Nodes to a Node List
        for (int i = 0; i < tour.size(); i++) {
            adjustedTourList.add(i, new ArrayList<Node>());
            Node sourceNode = this.getNodeBasedOnID(tour.get(i).get(0));
            Node destinationNde;
            nodesInPath.add(nodesInPath.size(), sourceNode);
            for (int j = 0; j < tour.get(i).size(); j++) {
                if ((j + 1) < tour.get(i).size()) {
                    sourceNode = this.getNodeBasedOnID(tour.get(i).get(j));
                    destinationNde =
                            this.getNodeBasedOnID(tour.get(i).get(j + 1));
                    temp = this.findNodesInPath(sourceNode, destinationNde,
                            distanceType);
                    nodesInPath.addAll(nodesInPath.size(), temp);
                    adjustedTourList.get(i).addAll(nodesInPath);
                    nodesInPath.clear();
                    temp.clear();
                }
            }
        }

        // Copying Node-List to the Tour
        for (int i = 0; i < adjustedTourList.size(); i++) {
            for (int j = 0; j < adjustedTourList.get(i).size(); j++) {
                adjustedTour.get(i)
                .add(adjustedTourList.get(i).get(j).nodeIdentifier);
            }
        }

        // Deleting Visited Nodes
        // Finding Length Of Max Size Tour
        int maxLengthTour = 0;
        for (int i = 0; i < adjustedTour.size(); i++) {
            if (adjustedTour.get(i).size() > maxLengthTour) {
                maxLengthTour = adjustedTour.get(i).size();
            }
        }
        // Delete
        for (int j = 0; j < maxLengthTour; j++) {
            for (int i = 0; i < adjustedTour.size(); i++) {
                if (j < adjustedTour.get(i).size()) {
                    this.SearchAndDeleteAllInstances(i, j, adjustedTour);
                }
            }
        }
        // Load Balancing method here ();
        return adjustedTour;
    }

    private void SearchAndDeleteAllInstances(final int i, final int j,
        final ArrayList<ArrayList<Integer>> ACList) {
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
        for (int Xloc = 0; Xloc < this.sizeMetric_X; Xloc++) {
            for (int Yloc = 0; Yloc < this.sizeMetric_Y; Yloc++) {
                NodeList.add(this.nodes.get(Xloc).get(Yloc));
            }
        }
        return NodeList;
    }

    public ArrayList<Node> SortedListBasedOnVoI() {
        final ArrayList<Node> NodeList = new ArrayList<Node>();
        for (int Xloc = 0; Xloc < this.sizeMetric_X; Xloc++) {
            for (int Yloc = 0; Yloc < this.sizeMetric_Y; Yloc++) {
                NodeList.add(this.nodes.get(Xloc).get(Yloc));
            }
        }
        final ArrayList<Node> NodeListSorted = NodeList;
        Collections.sort(NodeListSorted);
        return NodeListSorted;
    }

}
