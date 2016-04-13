package UWSN_Simulator_1;

public class VoIEvaluation {

	public double VoIAllAUVTours(final String CalculationBasis, final Tour T, final SimulationMap MyMap,
			final double TourStartTime, final double AUVSpeed, final double DS, final String DistanceType) {
		double VoI = 0;
		for (int i = 0; i < T.theTour.size(); i++) {
			VoI += this.VoISingleAUVTour(CalculationBasis, T, MyMap, TourStartTime, i, AUVSpeed, DS, DistanceType);
		}
		return VoI;
	}

	public double VoISingleAUVTour(final String CalculationBasis, final Tour T, final SimulationMap MyMap,
			final double TourStartTime, final int AUV, final double AUVSpeed, final double DS,
			final String DistanceType) {
		double VoI = 0;
		final int i = AUV;
		final AUV Explorer = new AUV(AUVSpeed, DS, i);
		Explorer.CreateTour(MyMap, T.theTour);
		if (CalculationBasis == "Retrieve") {
			final double TourEndTS = Explorer.ExecuteTourForMapTraversalWithoutReSurfacing(TourStartTime, DistanceType,
					T);
			final double TourTime = TourEndTS - TourStartTime;
			final double DistanceTravelled = TourTime * AUVSpeed;
			T.ExecutionTimeOfTour.add(AUV, TourTime);
			T.DistanceTravelledForTour.add(AUV, DistanceTravelled);
			VoI = Explorer.VoIOfferAtAUVBasedOnRetrieve();
		} else if (CalculationBasis == "Transmit") {
			final double TourEndTS = Explorer.ExecuteTourForMapTraversalWithReSurfacing(TourStartTime, DistanceType, T);
			final double TourTime = TourEndTS - TourStartTime;
			final double DistanceTravelled = TourTime * AUVSpeed;
			T.ExecutionTimeOfTour.add(AUV, TourTime);
			T.DistanceTravelledForTour.add(AUV, DistanceTravelled);
			VoI = Explorer.VoIOfferAtAUVBasedOnTransmit();
			// Explorer.PrintPackets();
		}
		return VoI;
	}

}