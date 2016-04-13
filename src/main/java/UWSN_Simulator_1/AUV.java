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

	public AUV(final double velocity, final double scale, final int ID) {
		this.AUVvelocity = velocity;
		this.DistanceScale = scale;
		this.AUVIdentifier = ID;
	}

	public double TimeToNode(final Node Current, final Node Next, final String DistanceType) {
		final double AUVTravelDistance = this.InterNodeDistance(Current, Next, DistanceType);
		final double TravelTime = AUVTravelDistance / this.AUVvelocity;
		return TravelTime;
	}

	public double ArrivalTimeAtNode(final Node Current, final Node Next, final double CurrentTS,
			final String DistanceType) {
		final double TravelTime = this.TimeToNode(Current, Next, DistanceType);
		final double ArrivalTime = TravelTime + CurrentTS;
		return ArrivalTime;
	}

	public double InterNodeDistance(final Node Source, final Node Destination, final String DistanceType) {
		double Distance = -1;
		final double SX = Source.coordinate_X;
		final double SY = Source.coordinate_Y;
		final double DX = Destination.coordinate_X;
		final double DY = Destination.coordinate_Y;
		if (Source.nodeIdentifier == -2) {
			Distance = Destination.DistanceToSurface;
		} else if (Destination.nodeIdentifier == -2) {
			Distance = Source.DistanceToSurface;
		} else {
			if (DistanceType == "Manhattan") {
				Distance = this.DistanceScale * (Math.abs(SX - DX) + Math.abs(SY - DY));
			} else if (DistanceType == "Euclidean") {
				Distance = this.DistanceScale
						* (Math.sqrt(Math.pow(Math.abs(SX - DX), 2) + Math.pow(Math.abs(SY - DY), 2)));
			}
		}
		return Distance;
	}

	public void RetrievePacketsFromNode(final Node BeingVisited, final double CurrentTimeAtVisit) {
		for (int i = 0; i < BeingVisited.packets.size(); i++) {
			BeingVisited.packets.get(i).SetTimeStampRetrieved(CurrentTimeAtVisit);
			final Packet Retrieved = BeingVisited.packets.get(i).CreateCopy();
			this.RetirevedPackets.add(Retrieved);
		}
		BeingVisited.WasVisited = true;
	}

	public double VoIOfferAtAUVBasedOnRetrieve() {
		double VoIVal = 0;
		for (int i = 0; i < this.RetirevedPackets.size(); i++) {
			final double TimeInstant = this.RetirevedPackets.get(i).GetTimeStampRetrieved();
			VoIVal += this.RetirevedPackets.get(i).currentVoIValue(TimeInstant);
		}
		return VoIVal;
	}

	public double VoIOfferAtAUVBasedOnTransmit() {
		double VoIVal = 0;
		int BatchNumber = -1;
		for (int i = 0; i < this.RetirevedPackets.size(); i++) {
			BatchNumber = this.RetirevedPackets.get(i).ResurfaceTransmitBatchNumber;
			this.RetirevedPackets.get(i).TimeStampTransmitted = this.TimeTransmitNodeVisitedAt.get(BatchNumber);
			final double TimeInstant = this.RetirevedPackets.get(i).GetTimeStampTransmitted();
			VoIVal += this.RetirevedPackets.get(i).currentVoIValue(TimeInstant);
		}
		return VoIVal;
	}

	public void CreateTour(final SimulationMap M, final ArrayList<ArrayList<Integer>> T) {
		for (int i = 0; i < T.get(this.AUVIdentifier).size(); i++) {
			final Node NodeToAddToTour = M.getNodeBasedOnID(T.get(this.AUVIdentifier).get(i));
			this.CurrentTour.add(NodeToAddToTour);
			// System.out.print(this.CurrentTour.get(i).IsTransmitNode + " - ");
		}
	}

	public double ExecuteTourForMapTraversalWithoutReSurfacing(final double StartTime, final String DistanceType,
			final Tour T) {
		double CurrentTS = StartTime;
		for (int i = 0; i < this.CurrentTour.size(); i++) {
			for (int j = 0; j < this.CurrentTour.get(i).packets.size(); j++) {
				final Packet PacketRetrieved = this.CurrentTour.get(i).packets.get(j).CreateCopy();
				PacketRetrieved.SetTimeStampRetrieved(CurrentTS);
				this.RetirevedPackets.add(PacketRetrieved);
			}
			if (i < (this.CurrentTour.size() - 1)) {
				CurrentTS = this.ArrivalTimeAtNode(this.CurrentTour.get(i), this.CurrentTour.get(i + 1), CurrentTS,
						DistanceType);
			}
		}
		return CurrentTS;
	}

	public double ExecuteTourForMapTraversalWithReSurfacing(final double StartTime, final String DistanceType,
			final Tour T) {
		double CurrentTS = StartTime;
		int BatchNumber = 0;
		for (int i = 0; i < this.CurrentTour.size(); i++) {
			this.TimeNodeVisitedAt.add(CurrentTS);
			// if node is not a transmit point i.e. resurfacing point
			if (this.CurrentTour.get(i).nodeIdentifier != -2) {
				for (int j = 0; j < this.CurrentTour.get(i).packets.size(); j++) {
					final Packet PacketRetrieved = this.CurrentTour.get(i).packets.get(j).CreateCopy();
					PacketRetrieved.SetTimeStampRetrieved(CurrentTS);
					PacketRetrieved.StoredAtNodeID = this.CurrentTour.get(i).nodeIdentifier;
					PacketRetrieved.ResurfaceTransmitBatchNumber = BatchNumber;
					this.RetirevedPackets.add(PacketRetrieved);
				}
			} else {
				this.TimeTransmitNodeVisitedAt.add(BatchNumber, CurrentTS);
				BatchNumber++;
			}
			if (i < (this.CurrentTour.size() - 1)) {
				CurrentTS = this.ArrivalTimeAtNode(this.CurrentTour.get(i), this.CurrentTour.get(i + 1), CurrentTS,
						DistanceType);
			}
		}
		return CurrentTS;
	}

	public void PrintPackets() {
		for (int i = 0; i < this.RetirevedPackets.size(); i++) {
			System.out.print(this.RetirevedPackets.get(i).StoredAtNodeID + " ");
			// System.out.print(this.RetirevedPackets.get(i).VoIDecay + " ");
			// System.out.print(this.RetirevedPackets.get(i).VoIInitialMagnitude
			// + " ");
			System.out.print(this.RetirevedPackets.get(i).TimeStampAcquired + " ");
			System.out.print(this.RetirevedPackets.get(i).TimeStampRetrieved + " ");
			System.out.print(this.RetirevedPackets.get(i).TimeStampTransmitted + " ");
			System.out.print(this.RetirevedPackets.get(i).ResurfaceTransmitBatchNumber + " ");
			System.out.println(
					this.RetirevedPackets.get(i).currentVoIValue(this.RetirevedPackets.get(i).TimeStampTransmitted)
							+ " ");
		}
	}

}
