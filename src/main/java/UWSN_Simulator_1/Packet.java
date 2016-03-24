package UWSN_Simulator_1;

public class Packet {
	
	public String PacketDataClass;	//Normal, HotSpot
	public double VoIInitialMagnitude;
	public double VoIDecay;
	public double TimeStampAcquired = -1;
	public double TimeStampRetrieved = -1;
	public double TimeStampTransmitted = -1;
	public int StoredAtNodeID = -1;
	public int ResurfaceTransmitBatchNumber = -1;
	
	public Packet(String ClassOfData, double Mag, double Dec, double TsA) {
		PacketDataClass = ClassOfData;
		VoIInitialMagnitude = Mag;
		VoIDecay = Dec;
		TimeStampAcquired = TsA;
	}
	
	public double currentVoIValue(double CurrentTime) {
		return (VoIInitialMagnitude * Math.exp(-(VoIDecay * (CurrentTime - TimeStampAcquired)))); 
	}
	
	public double MaxVoIValue() {
		return (VoIInitialMagnitude * Math.exp(-(VoIDecay * (TimeStampAcquired - TimeStampAcquired)))); 
	}
	
	public Packet CreateCopy() {
		Packet Copy = new Packet (this.PacketDataClass, this.VoIInitialMagnitude, this.VoIDecay, this.TimeStampAcquired);
		Copy.SetTimeStampRetrieved(this.TimeStampRetrieved);
		Copy.SetTimeStampTransmitted(this.TimeStampTransmitted);
		return Copy;
	}
	
	public void SetVoIInitialMagnitude(double Mag) {
		VoIInitialMagnitude = Mag;
	}

	public void SetVoIDecay(double Dec) {
		VoIDecay = Dec;
	}

	public void SetTimeStampAcquired(double TsA) {
		TimeStampAcquired = TsA;
	}

	public void SetTimeStampRetrieved(double TsR) {
		TimeStampRetrieved = TsR;
	}

	public void SetTimeStampTransmitted(double TsT) {
		TimeStampTransmitted = TsT;
	}

	public double GetTimeStampAcquired() {
		return TimeStampAcquired;
	}

	public double GetTimeStampRetrieved() {
		return TimeStampRetrieved;
	}

	public double GetTimeStampTransmitted() {
		return TimeStampTransmitted;
	}

}
