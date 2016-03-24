package UWSN_Simulator_1;

//import java.io.*;
//import java.util.*;
//import java.text.*;

public class VoIEvaluation{

	public double VoIAllAUVTours(String CalculationBasis, Tour T, SimulationMap MyMap, double TourStartTime, double AUVSpeed, double DS, String DistanceType){
		double VoI = 0;
		for (int i = 0; i < T.theTour.size(); i++) {
			VoI += VoISingleAUVTour(CalculationBasis, T, MyMap, TourStartTime, i, AUVSpeed, DS, DistanceType);
		}
		return VoI;
	}

	public double VoISingleAUVTour(String CalculationBasis, Tour T, SimulationMap MyMap, double TourStartTime, int AUV, double AUVSpeed, double DS, String DistanceType){
		double VoI = 0;
		int i = AUV; 		
		AUV Explorer = new AUV (AUVSpeed, DS, i);
		Explorer.CreateTour(MyMap, T.theTour);
		if (CalculationBasis == "Retrieve") {
			double TourEndTS = Explorer.ExecuteTourForMapTraversalWithoutReSurfacing(TourStartTime, DistanceType, T);
			double TourTime = TourEndTS - TourStartTime;
			double DistanceTravelled = TourTime * AUVSpeed;
			T.ExecutionTimeOfTour.add(AUV, TourTime);
			T.DistanceTravelledForTour.add(AUV, DistanceTravelled);
			VoI = Explorer.VoIOfferAtAUVBasedOnRetrieve();	
		}
		else if (CalculationBasis == "Transmit") {
			double TourEndTS = Explorer.ExecuteTourForMapTraversalWithReSurfacing(TourStartTime, DistanceType, T);
			double TourTime = TourEndTS - TourStartTime;
			double DistanceTravelled = TourTime * AUVSpeed;
			T.ExecutionTimeOfTour.add(AUV, TourTime);
			T.DistanceTravelledForTour.add(AUV, DistanceTravelled);
			VoI = Explorer.VoIOfferAtAUVBasedOnTransmit();	
		}
		return VoI;
	}

}