package UWSN_Simulator_1;

import java.util.ArrayList;

public class Node implements Comparable<Node> {
	
	public boolean WasVisited = false;
	
	public double coordinate_X;
	public double coordinate_Y;
	public double DistanceToSurface;
	
	public int NodeIdentifier = -1;
	
	public boolean IsTransmitNode = false;
	
	public Node [][] Neighbors = new Node [3][3];
	
	public ArrayList<Packet> Packets = new ArrayList<Packet>();
	
	public Node(boolean IsTN, double X_loc, double Y_loc, double DTS, int ID) {
		if (IsTN == false) {
			IsTransmitNode = IsTN;			
			coordinate_X = X_loc;
			coordinate_Y = Y_loc;
			DistanceToSurface = DTS; 
			NodeIdentifier = ID;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					Neighbors[i][j] = null;
				}
			}
		}
		else {
			//ID reserved for transmit node station
			NodeIdentifier = -2;
			IsTransmitNode = IsTN;			
		}
	}

	public void AcquirePacket(Packet NewPacket) {
		Packets.add(NewPacket);
	}

	public double VoIOfferAtNode(double TimeInstant) {
		double VoIVal = 0;
		for ( int i = 0; i < Packets.size(); i++) {
			VoIVal += Packets.get(i).currentVoIValue(TimeInstant);
		}
		return VoIVal;
	}
	
	public int compareTo(Node Comp) {
		double TS1 = this.Packets.get(this.Packets.size()-1).TimeStampAcquired;
		double TS2 = Comp.Packets.get(Comp.Packets.size()-1).TimeStampAcquired;
		double TimeInstant = ( TS1 > TS2 ) ? TS1 : TS2 ;
		if(this.VoIOfferAtNode(TimeInstant) < Comp.VoIOfferAtNode(TimeInstant)) {
            return 1;
        }
        if(this.VoIOfferAtNode(TimeInstant) > Comp.VoIOfferAtNode(TimeInstant)) {
            return -1;
        }
        //implement: else randomly choose between 0, 1 & -1
        return 0;
   }

}
