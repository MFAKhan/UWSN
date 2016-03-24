package UWSN_Simulator_1;

import java.util.ArrayList;

public class AUV {

	public double AUVvelocity = -1;
	public double DistanceScale = -1;

	public int AUVIdentifier = -1;
	
	public ArrayList<Node> CurrentTour = new ArrayList<Node>();
	public ArrayList<Double> TimeNodeVisitedAt = new ArrayList<Double>();
	public ArrayList<Double> TimeTransmitNodeVisitedAt = new ArrayList<Double>();
	public ArrayList<Packet> RetirevedPackets = new ArrayList<Packet>();
	
	public AUV(double velocity, double scale, int ID) {
		AUVvelocity = velocity;
		DistanceScale = scale;
		AUVIdentifier = ID;
	}
	
	public double TimeToNode(Node Current, Node Next, String DistanceType) {
		double AUVTravelDistance = InterNodeDistance(Current, Next, DistanceType);
		double TravelTime = AUVTravelDistance / AUVvelocity;
		return TravelTime;
	}

	public double ArrivalTimeAtNode(Node Current, Node Next, double CurrentTS, String DistanceType) {
		double TravelTime = TimeToNode(Current, Next, DistanceType) ;
		double ArrivalTime = TravelTime + CurrentTS;
		return ArrivalTime;
	}

	public double InterNodeDistance(Node Source, Node Destination, String DistanceType) {
		double Distance = -1;
		double SX = Source.coordinate_X;
		double SY = Source.coordinate_Y;
		double DX = Destination.coordinate_X;
		double DY = Destination.coordinate_Y;
		if (Source.NodeIdentifier == -2 ) {
			Distance = Destination.DistanceToSurface;
		}
		else if (Destination.NodeIdentifier == -2) {
			Distance = Source.DistanceToSurface;
		}
		else {
			if (DistanceType == "Manhattan") {
				Distance = DistanceScale * (Math.abs(SX - DX) + Math.abs(SY - DY));
			}
			else if (DistanceType == "Euclidean") {
				Distance = DistanceScale * (Math.sqrt(Math.pow(Math.abs(SX - DX), 2) + Math.pow(Math.abs(SY - DY), 2)));
			}
		}
		return Distance;
	}

	public void RetrievePacketsFromNode(Node BeingVisited, double CurrentTimeAtVisit) {
		for (int i = 0; i < BeingVisited.Packets.size(); i++) {
			BeingVisited.Packets.get(i).SetTimeStampRetrieved(CurrentTimeAtVisit);
			Packet Retrieved = BeingVisited.Packets.get(i).CreateCopy();
			RetirevedPackets.add(Retrieved);
		}
		BeingVisited.WasVisited = true;
	}
		
	public double VoIOfferAtAUVBasedOnRetrieve() {
		double VoIVal = 0;
		for (int i = 0; i < RetirevedPackets.size(); i++) {
			double TimeInstant = RetirevedPackets.get(i).GetTimeStampRetrieved();
			VoIVal += RetirevedPackets.get(i).currentVoIValue(TimeInstant);
		}
		return VoIVal;
	}
	
	public double VoIOfferAtAUVBasedOnTransmit() {
		double VoIVal = 0;
		int BatchNumber = -1;
		for (int i = 0; i < RetirevedPackets.size(); i++) {
			BatchNumber = RetirevedPackets.get(i).ResurfaceTransmitBatchNumber;
			double TimeInstant = TimeTransmitNodeVisitedAt.get(BatchNumber);
			VoIVal += RetirevedPackets.get(i).currentVoIValue(TimeInstant);
		}
		return VoIVal;
	}
	
	public void CreateTour(SimulationMap M, ArrayList<ArrayList<Integer>> T) {
		for (int i = 0; i < T.get(this.AUVIdentifier).size(); i++) {
			Node NodeToAddToTour = M.GetNodeBasedOnId(T.get(this.AUVIdentifier).get(i));
			CurrentTour.add(NodeToAddToTour);
		}
	}
	
	public double ExecuteTourForMapTraversalWithoutReSurfacing(double StartTime, String DistanceType, Tour T) {
		double CurrentTS = StartTime;
		for (int i = 0; i < CurrentTour.size(); i++) {
			for (int j = 0; j < CurrentTour.get(i).Packets.size(); j++) {
				Packet PacketRetrieved = CurrentTour.get(i).Packets.get(j).CreateCopy();
				PacketRetrieved.SetTimeStampRetrieved(CurrentTS);
				RetirevedPackets.add(PacketRetrieved);
			}
			if (i < (CurrentTour.size() - 1)) {
				CurrentTS = ArrivalTimeAtNode(CurrentTour.get(i), CurrentTour.get(i + 1), CurrentTS, DistanceType);
			}
		}
		return CurrentTS;
	}

	public double ExecuteTourForMapTraversalWithReSurfacing(double StartTime, String DistanceType, Tour T) {
		double CurrentTS = StartTime;
		int BatchNumber = 0;
		for (int i = 0; i < CurrentTour.size(); i++) {
			TimeNodeVisitedAt.add(CurrentTS);
			//if node is not a transmit point i.e. resurfacing point
			if (CurrentTour.get(i).NodeIdentifier != -2) {
				for (int j = 0; j < CurrentTour.get(i).Packets.size(); j++) {
					Packet PacketRetrieved = CurrentTour.get(i).Packets.get(j).CreateCopy();
					PacketRetrieved.SetTimeStampRetrieved(CurrentTS);
					PacketRetrieved.StoredAtNodeID = CurrentTour.get(i).NodeIdentifier;
					PacketRetrieved.ResurfaceTransmitBatchNumber = BatchNumber;
					RetirevedPackets.add(PacketRetrieved);
				}
			}
			else {
				TimeTransmitNodeVisitedAt.add(BatchNumber, CurrentTS);
				BatchNumber++;
			}
			if (i < (CurrentTour.size() - 1)) {
				CurrentTS = ArrivalTimeAtNode(CurrentTour.get(i), CurrentTour.get(i + 1), CurrentTS, DistanceType);
			}
		}
		return CurrentTS;
	}
	
}
