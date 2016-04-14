package UWSN_ResurfacingGA;

import java.util.ArrayList;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import UWSN_Simulator_1.AUV;
import UWSN_Simulator_1.Packet;
import UWSN_Simulator_1.SimulationMap;
import UWSN_Simulator_1.Tour;
import UWSN_Simulator_1.VoIEvaluation;

/**
 * The simlulation setup for the experiments of the underwater resurfacing
 * scheduling using the genetic algorithms
 *
 */
public class GAforResurfacing implements constUWSN {
	public static Logger LOGGER = LoggerFactory.getLogger("UWSN_ResurfacingGA.GAforResurfacing");

	private static final GAforResurfacing instance = new GAforResurfacing();
	private final Random rand;
	public ArrayList<Tour> allTours = new ArrayList<Tour>();

	private GAforResurfacing() {
		this.rand = new Random();
		this.allTours = new ArrayList<Tour>();
	}

	public void run() {
		for (int i = 0; i < constUWSN.SAMPLES_PER_TOUR; i++) {
			GAforResurfacing.LOGGER
					.debug(String.format("Currently the run number %s is executing", constUWSN.SAMPLES_PER_TOUR));
			this.singleSimulationRun();
			this.saveExperiment(i);
		}
		this.analyzeExperiment();
		this.printAverages();
		this.printAllExperiments();
	}

	public void singleSimulationRun() {

		// Initialize Map
		final SimulationMap myMap = new SimulationMap(constUWSN.X_DIM, constUWSN.Y_DIM);
		GAforResurfacing.LOGGER.debug("Initializing the map");
		myMap.initializeMap("StraightLine", constUWSN.DEPLOYMENT_DEPTH, constUWSN.DISTANCE_SCALE);

		// Initialize an AUV for packet initialization measurements
		final AUV explorer = new AUV(constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, 0);

		// Initialize Packets for Nodes on the Map
		final double lastPacketTimeStamp = this.initializeNodePackets(constUWSN.X_DIM, constUWSN.Y_DIM,
				constUWSN.PACKET_INITIALIZATION_TYPE, myMap, explorer, constUWSN.DISTANCE_TYPE,
				constUWSN.PACKET_INITIALIZATION_MAGNITUDE, constUWSN.PACKET_INITIALIZATION_DESIRED_VOI_FACTOR);
		/*
		 * System.out.println("VoI Map"); MyMap.PrintVoIMap(LastPacketTS);
		 * System.out.println();
		 */
		GAforResurfacing.LOGGER.debug("Creating and evaluating the tour");
		this.createAndEvaluateTours(myMap, lastPacketTimeStamp);

	}

	public void createAndEvaluateTours(final SimulationMap MyMap, final double lastPacketTimeStamp) {
		// add GA tour here
		Tour tour;
		VoIEvaluation voiEvaluation;
		for (int numResurfaceStops = 0; numResurfaceStops < constUWSN.NUM_OF_NODES; numResurfaceStops++) {
			tour = new Tour("Resurface After K-Nodes", constUWSN.NUM_OF_AUVS, constUWSN.NUM_OF_NODES, MyMap,
					constUWSN.DISTANCE_TYPE, numResurfaceStops, constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE,
					lastPacketTimeStamp, null);
			voiEvaluation = new VoIEvaluation();
			tour.setVoIAccumulatedByTour(voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis, tour, MyMap,
					lastPacketTimeStamp, constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
			this.allTours.add(tour);
		}
		/**/
		tour = new Tour("Resurface Tour Using GA1", constUWSN.NUM_OF_AUVS, constUWSN.NUM_OF_NODES, MyMap,
				constUWSN.DISTANCE_TYPE, -1, constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, lastPacketTimeStamp, null);
		voiEvaluation = new VoIEvaluation();
		tour.setVoIAccumulatedByTour(voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis, tour, MyMap,
				lastPacketTimeStamp, constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
		this.allTours.add(tour);
		for (int numOfResurfaceStops = 0; numOfResurfaceStops < constUWSN.NUM_OF_NODES; numOfResurfaceStops++) {
			tour = new Tour("Reversal : Resurface After K-Nodes", constUWSN.NUM_OF_AUVS, constUWSN.NUM_OF_NODES, MyMap,
					constUWSN.DISTANCE_TYPE, numOfResurfaceStops, constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE,
					lastPacketTimeStamp, null);
			voiEvaluation = new VoIEvaluation();
			tour.setVoIAccumulatedByTour(voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis, tour, MyMap,
					lastPacketTimeStamp, constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
			this.allTours.add(tour);
		}
		tour = new Tour("Reversal : Resurface Tour Using GA1", constUWSN.NUM_OF_AUVS, constUWSN.NUM_OF_NODES, MyMap,
				constUWSN.DISTANCE_TYPE, -1, constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, lastPacketTimeStamp, null);
		voiEvaluation = new VoIEvaluation();
		tour.setVoIAccumulatedByTour(voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis, tour, MyMap,
				lastPacketTimeStamp, constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
		this.allTours.add(tour);
		tour = new Tour("Resurface Tour Using GA2", constUWSN.NUM_OF_AUVS, constUWSN.NUM_OF_NODES, MyMap,
				constUWSN.DISTANCE_TYPE, -1, constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, lastPacketTimeStamp, null);
		voiEvaluation = new VoIEvaluation();
		tour.setVoIAccumulatedByTour(voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis, tour, MyMap,
				lastPacketTimeStamp, constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
		this.allTours.add(tour);
		tour = new Tour("Resurface Tour Using GA3", constUWSN.NUM_OF_AUVS, constUWSN.NUM_OF_NODES, MyMap,
				constUWSN.DISTANCE_TYPE, -1, constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, lastPacketTimeStamp, null);
		voiEvaluation = new VoIEvaluation();
		tour.setVoIAccumulatedByTour(voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis, tour, MyMap,
				lastPacketTimeStamp, constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
		this.allTours.add(tour);
		/**/
	}

	public double initializeNodePackets(final int x, final int y, final String method,
			final SimulationMap simulationMap, final AUV auv, final String distanceType, final double magnitude,
			final double desiredVoiFactor) {
		double decay = -1; // to be set yet
		double latestTS = 0;
		// Equal Initialization
		if (method.equals("Equal")) {
			for (int Xloc = 0; Xloc < x; Xloc++) {
				for (int Yloc = 0; Yloc < y; Yloc++) {
					latestTS = 0;
					for (int i = 0; i < constUWSN.EXPECTED_MAX_PACKETS_AT_NODE; i++) {
						latestTS += 1;
						final Packet newPacket = new Packet("Normal", magnitude, decay, latestTS);
						simulationMap.nodes.get(Xloc).get(Yloc).acquirePacket(newPacket);
					}
				}
			}
		}
		// Symmetrical Initialization
		else if (method.equals("Symmetrical")) {
			for (int Xloc = 0; Xloc < x; Xloc++) {
				for (int Yloc = 0; Yloc < y; Yloc++) {
					latestTS = 5 * Math.pow(Xloc, 2) * Math.pow(Yloc, 2);
					for (int i = 0; i < constUWSN.EXPECTED_MAX_PACKETS_AT_NODE; i++) {
						latestTS += 1;
						final Packet NewPacket = new Packet("Normal", magnitude, decay, latestTS);
						simulationMap.nodes.get(Xloc).get(Yloc).acquirePacket(NewPacket);
					}
				}
			}
		}
		// Random Initialization 1
		else if (method.equals("Random 1")) {
			int TS = 0;
			latestTS = 0;
			for (int Xloc = 0; Xloc < x; Xloc++) {
				for (int Yloc = 0; Yloc < y; Yloc++) {
					TS = 0;
					// Each node should have at least 1 packet
					final int RandNumOfPackets = this.getRand().nextInt(constUWSN.EXPECTED_MAX_PACKETS_AT_NODE) + 1;
					for (int i = 0; i < RandNumOfPackets; i++) {
						TS += 10;
						final Packet NewPacket = new Packet("Normal", magnitude, decay, TS);
						simulationMap.nodes.get(Xloc).get(Yloc).acquirePacket(NewPacket);
						if (latestTS < TS) {
							latestTS = TS;
						}
					}
				}
			}
		}
		// Random Initialization 2 - Initial Magnitudes vary from 1 to 100
		else if (method.equals("Random Number of Packets - Variable VoI Magnitude")) {
			latestTS = constUWSN.NUM_OF_NODES * (constUWSN.DISTANCE_SCALE / constUWSN.AUV_SPEED);
			for (int Xloc = 0; Xloc < x; Xloc++) {
				for (int Yloc = 0; Yloc < y; Yloc++) {
					// Each node should have at least 1 packet
					final int RandNumOfPackets = this.getRand().nextInt(constUWSN.EXPECTED_MAX_PACKETS_AT_NODE) + 1;
					for (int i = 0; i < RandNumOfPackets; i++) {
						final Packet NewPacket = new Packet("Normal", this.getRand().nextInt(99) + 1, decay,
								this.getRand().nextInt((int) latestTS));
						/*
						 * Packet NewPacket; if (Yloc == 3) { NewPacket = new
						 * Packet("Normal", 1000 * magnitude, 0.2 * decay,
						 * this.getRand().nextInt((int) latestTS)); } else {
						 * NewPacket = new Packet("Normal", magnitude, decay,
						 * this.getRand().nextInt((int) latestTS)); }
						 */
						simulationMap.nodes.get(Xloc).get(Yloc).acquirePacket(NewPacket);
					}
				}
			}
		}
		// Random Initialization 3 - Initial Magnitudes are from constants file
		else if (method.equals("Random Number of Packets - Fixed VoI Magnitude")) {
			latestTS = constUWSN.NUM_OF_NODES * (constUWSN.DISTANCE_SCALE / constUWSN.AUV_SPEED);
			for (int Xloc = 0; Xloc < x; Xloc++) {
				for (int Yloc = 0; Yloc < y; Yloc++) {
					// Each node should have at least 1 packet
					final int RandNumOfPackets = this.getRand().nextInt(constUWSN.EXPECTED_MAX_PACKETS_AT_NODE) + 1;
					for (int i = 0; i < RandNumOfPackets; i++) {
						final Packet NewPacket = new Packet("Normal", magnitude, decay,
								this.getRand().nextInt((int) latestTS));
						/*
						 * Packet NewPacket; if (Yloc == 3) { NewPacket = new
						 * Packet("Normal", 1000 * magnitude, 0.2 * decay,
						 * this.getRand().nextInt((int) latestTS)); } else {
						 * NewPacket = new Packet("Normal", magnitude, decay,
						 * this.getRand().nextInt((int) latestTS)); }
						 */
						simulationMap.nodes.get(Xloc).get(Yloc).acquirePacket(NewPacket);
					}
				}
			}
		}
		// Setting up decays
		decay = this.determineDecayConst(simulationMap, auv, magnitude, desiredVoiFactor, distanceType, latestTS);
		for (int Xloc = 0; Xloc < x; Xloc++) {
			for (int Yloc = 0; Yloc < y; Yloc++) {
				for (int i = 0; i < simulationMap.nodes.get(Xloc).get(Yloc).packets.size(); i++) {
					final String PacketClass = simulationMap.nodes.get(Xloc).get(Yloc).packets.get(i).PacketDataClass;
					if (PacketClass == "Normal") {
						simulationMap.nodes.get(Xloc).get(Yloc).packets.get(i).VoIDecay = decay;
					}
				}
			}
		}
		return latestTS;
	}

	private double determineDecayConst(final SimulationMap map, final AUV A, final double magnitude,
			final double desiredVoiFactor, final String distanceType, final Double latestTS) {
		// t = S / V
		// VoI = A * e^-(Bt) => B = ln(VoI/A) * -1/t
		double decay = 0;
		// double OffsetTime = LatestTS;
		final double offsetTime = 0;

		final double timeForAvgTourLength = map.averageTourLength(distanceType) / A.AUVvelocity;
		decay = Math.log((desiredVoiFactor * magnitude) / magnitude) * -(1 / (timeForAvgTourLength + offsetTime));

		return decay;
	}

	void analyzeExperiment() {
		// Calculating Averages
		// Initialization
		for (int i = 0; i < constUWSN.TOUR_TYPES; i++) {
			constUWSN.results[i][0] = 0;
			constUWSN.results[i][1] = 0;
		}
		// Summation
		for (int i = 0; i < constUWSN.TOTAL_SAMPLES; i++) {
			constUWSN.results[i % constUWSN.TOUR_TYPES][0] += this.allTours.get(i).getVoIAccumulatedByTour();
			constUWSN.results[i % constUWSN.TOUR_TYPES][1] += this.allTours.get(i).getNumTimesResurfaced();
		}
		// Division
		for (int i = 0; i < constUWSN.TOUR_TYPES; i++) {
			constUWSN.results[i][0] = constUWSN.results[i][0] / constUWSN.SAMPLES_PER_TOUR;
			constUWSN.results[i][1] = constUWSN.results[i][1] / constUWSN.SAMPLES_PER_TOUR;
		}
	}

	void printAverages() {
		System.out.println("\n");
		System.out.println("Simulation Paramaters : ");
		System.out.println("Number of Runs\t\t" + constUWSN.SAMPLES_PER_TOUR);
		System.out.println("Number of Nodes\t\t" + constUWSN.NUM_OF_NODES);
		System.out.println("Deployment Depth - DD\t" + constUWSN.DEPLOYMENT_DEPTH);
		System.out.println("Internode Distance - ID\t" + constUWSN.DISTANCE_SCALE);
		System.out.println("Ratio of DD to ID\t" + constUWSN.RATIO_DD_TO_DS);
		System.out.println("Packets at node\t\t1 - " + constUWSN.EXPECTED_MAX_PACKETS_AT_NODE);
		System.out.println("Packet Initilization\t" + constUWSN.PACKET_INITIALIZATION_TYPE);
		System.out.println("\n");
		System.out.println("Results : ");
		System.out.println("Tour Type\tAvg. VoI\tResurfacing Count");
		for (int i = 0; i < constUWSN.TOUR_TYPES; i++) {
			if (this.allTours.get(i).NameOfPlanner.contains("G_")) {
				final String Name = this.allTours.get(i).NameOfPlanner;
				System.out.print(Name.substring(0, Name.indexOf(" ")) + "\t\t");
				for (int j = 0; j < 2; j++) {
					System.out.print(String.format("%8.2f", constUWSN.results[i][j]) + "\t");
				}
				System.out.println();
			}
		}
	}

	void printAllExperiments() {
		System.out.println("\nAll Experiments\n");
		for (int i = 0; i < constUWSN.SAMPLES_PER_TOUR; i++) {
			for (int j = 0; j < 4; j++) {
				System.out.print(String.format("%8.4f", constUWSN.allExperiments[i][j]) + "\t");
			}
			System.out.println();
		}
	}

	void saveExperiment(final int ExperimentNumber) {
		int index = 0;
		for (int i = 0; i < constUWSN.TOUR_TYPES; i++) {
			if (this.allTours.get(i).NameOfPlanner.contains("G_")) {
				constUWSN.allExperiments[ExperimentNumber][index] = this.allTours
						.get(ExperimentNumber * constUWSN.TOUR_TYPES + i).getVoIAccumulatedByTour();
				index++;
			}
		}
	}

	public Random getRand() {
		return this.rand;
	}

	public static GAforResurfacing getInstance() {
		return GAforResurfacing.instance;
	}
}
