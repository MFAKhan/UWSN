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
    public static Logger LOGGER =
            LoggerFactory.getLogger("UWSN_ResurfacingGA.GAforResurfacing");


    private static final GAforResurfacing instance = new GAforResurfacing();
    private final Random rand;
    public ArrayList<Tour> allTours = new ArrayList<Tour>();

    private GAforResurfacing() {
        this.rand = new Random();
        this.allTours = new ArrayList<Tour>();
    }

    public void run() {
        for (int i = 0; i < constUWSN.SAMPLES_PER_TOUR; i++) {
            GAforResurfacing.LOGGER.debug(
                    String.format("Currently the run number %s is executing",
                            constUWSN.SAMPLES_PER_TOUR));
            this.singleSimulationRun();
        }
        this.analyzeExperiment();
        this.printAverages();
    }

    public void singleSimulationRun() {

        // Initialize Map
        final SimulationMap myMap =
                new SimulationMap(constUWSN.X_DIM, constUWSN.Y_DIM);
        GAforResurfacing.LOGGER.debug("Initializing the map");
        myMap.initializeMap("StraightLine", constUWSN.DEPLOYMENT_DEPTH,
                constUWSN.DISTANCE_SCALE);

        // Initialize an AUV for packet initialization measurements
        final AUV explorer =
                new AUV(constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, 0);

        // Initialize Packets for Nodes on the Map
        final double lastPacketTimeStamp = this.initializeNodePackets(constUWSN.X_DIM,
                constUWSN.Y_DIM, constUWSN.PACKET_INITIALIZATION_TYPE, myMap,
                explorer, constUWSN.DISTANCE_TYPE,
                constUWSN.PACKET_INITIALIZATION_MAGNITUDE,
                constUWSN.PACKET_INITIALIZATION_DESIRED_VOI_FACTOR);
        /*
         * System.out.println("VoI Map"); MyMap.PrintVoIMap(LastPacketTS);
         * System.out.println();
         */
        GAforResurfacing.LOGGER.debug("Creating and evaluating the tour");
        this.createAndEvaluateTours(myMap, lastPacketTimeStamp);

    }

    public void createAndEvaluateTours(final SimulationMap MyMap,
        final double lastPacketTimeStamp) {
        // add GA tour here
        Tour tour;
        VoIEvaluation voiEvaluation;
        for (int numResurfaceStops =
                0; numResurfaceStops < constUWSN.NUM_OF_NODES; numResurfaceStops++) {
            tour = new Tour("Resurface After K-Nodes", constUWSN.NUM_OF_AUVS,
                    constUWSN.NUM_OF_NODES, MyMap, constUWSN.DISTANCE_TYPE,
                    numResurfaceStops, constUWSN.AUV_SPEED,
                    constUWSN.DISTANCE_SCALE, lastPacketTimeStamp, null);
            voiEvaluation = new VoIEvaluation();
            tour.setVoIAccumulatedByTour(
                    voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis,
                            tour, MyMap, lastPacketTimeStamp, constUWSN.AUV_SPEED,
                            constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
            allTours.add(tour);
        }
        tour = new Tour("Resurface Tour Using GA1", constUWSN.NUM_OF_AUVS,
                constUWSN.NUM_OF_NODES, MyMap, constUWSN.DISTANCE_TYPE, -1,
                constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, lastPacketTimeStamp,
                null);
        voiEvaluation = new VoIEvaluation();
        tour.setVoIAccumulatedByTour(
                voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis,
                        tour, MyMap, lastPacketTimeStamp, constUWSN.AUV_SPEED,
                        constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
        allTours.add(tour);
        for (int numOfResurfaceStops =
                0; numOfResurfaceStops < constUWSN.NUM_OF_NODES; numOfResurfaceStops++) {
            tour = new Tour("Reversal : Resurface After K-Nodes",
                    constUWSN.NUM_OF_AUVS, constUWSN.NUM_OF_NODES, MyMap,
                    constUWSN.DISTANCE_TYPE, numOfResurfaceStops,
                    constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, lastPacketTimeStamp,
                    null);
            voiEvaluation = new VoIEvaluation();
            tour.setVoIAccumulatedByTour(
                    voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis,
                            tour, MyMap, lastPacketTimeStamp, constUWSN.AUV_SPEED,
                            constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
            allTours.add(tour);
        }
        tour = new Tour("Reversal : Resurface Tour Using GA1",
                constUWSN.NUM_OF_AUVS, constUWSN.NUM_OF_NODES, MyMap,
                constUWSN.DISTANCE_TYPE, -1, constUWSN.AUV_SPEED,
                constUWSN.DISTANCE_SCALE, lastPacketTimeStamp, null);
        voiEvaluation = new VoIEvaluation();
        tour.setVoIAccumulatedByTour(
                voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis,
                        tour, MyMap, lastPacketTimeStamp, constUWSN.AUV_SPEED,
                        constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
        allTours.add(tour);
        tour = new Tour("Resurface Tour Using GA2", constUWSN.NUM_OF_AUVS,
                constUWSN.NUM_OF_NODES, MyMap, constUWSN.DISTANCE_TYPE, -1,
                constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, lastPacketTimeStamp,
                null);
        voiEvaluation = new VoIEvaluation();
        tour.setVoIAccumulatedByTour(
                voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis,
                        tour, MyMap, lastPacketTimeStamp, constUWSN.AUV_SPEED,
                        constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
        allTours.add(tour);
        tour = new Tour("Resurface Tour Using GA3", constUWSN.NUM_OF_AUVS,
                constUWSN.NUM_OF_NODES, MyMap, constUWSN.DISTANCE_TYPE, -1,
                constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, lastPacketTimeStamp,
                null);
        voiEvaluation = new VoIEvaluation();
        tour.setVoIAccumulatedByTour(
                voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis,
                        tour, MyMap, lastPacketTimeStamp, constUWSN.AUV_SPEED,
                        constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
        allTours.add(tour);
    }

    public double initializeNodePackets(final int x, final int y,
        final String method, final SimulationMap simulationMap, final AUV auv,
        final String distanceType, final double magnitude,
        final double desiredVoiFactor) {
        double decay = -1; // to be set yet
        double latestTS = 0;
        // Equal Initialization
        if (method.equals("Equal")) {
            for (int Xloc = 0; Xloc < x; Xloc++) {
                for (int Yloc = 0; Yloc < y; Yloc++) {
                    latestTS = 0;
                    for (int i =
                            0; i < constUWSN.EXPECTED_MAX_PACKETS_AT_NODE; i++) {
                        latestTS += 1;
                        final Packet newPacket = new Packet("Normal", magnitude,
                                decay, latestTS);
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
                    for (int i =
                            0; i < constUWSN.EXPECTED_MAX_PACKETS_AT_NODE; i++) {
                        latestTS += 1;
                        final Packet NewPacket = new Packet("Normal", magnitude,
                                decay, latestTS);
                        simulationMap.nodes.get(Xloc).get(Yloc).acquirePacket(NewPacket);
                    }
                }
            }
        }
        // Random Initialization
        else if (method.equals("Random")) {
            int TS = 0;
            latestTS = 0;
            for (int Xloc = 0; Xloc < x; Xloc++) {
                for (int Yloc = 0; Yloc < y; Yloc++) {
                    TS = 0;
                    // Each node should have at least 1 packet
                    final int RandNumOfPackets = this.getRand().nextInt(
                            constUWSN.EXPECTED_MAX_PACKETS_AT_NODE) + 1;
                    for (int i = 0; i < RandNumOfPackets; i++) {
                        TS += 10;
                        final Packet NewPacket = new Packet("Normal", magnitude,
                                decay, latestTS);
                        simulationMap.nodes.get(Xloc).get(Yloc).acquirePacket(NewPacket);
                        if (latestTS < TS) {
                            latestTS = TS;
                        }
                    }
                }
            }
        }
        // Setting up decays
        decay = determineDecayConst(simulationMap, auv, magnitude, desiredVoiFactor,
                distanceType, latestTS);
        for (int Xloc = 0; Xloc < x; Xloc++) {
            for (int Yloc = 0; Yloc < y; Yloc++) {
                for (int i = 0; i < simulationMap.nodes.get(Xloc).get(Yloc).packets
                        .size(); i++) {
                    final String PacketClass =
                            simulationMap.nodes.get(Xloc).get(Yloc).packets
                            .get(i).PacketDataClass;
                    if (PacketClass == "Normal") {
                        simulationMap.nodes.get(Xloc).get(Yloc).packets.get(i).VoIDecay =
                                decay;
                    }
                }
            }
        }
        return latestTS;
    }

    private double determineDecayConst(final SimulationMap map, final AUV A,
        final double magnitude, final double desiredVoiFactor,
        final String distanceType, final Double latestTS) {
        // t = S / V
        // VoI = A * e^-(Bt) => B = ln(VoI/A) * -1/t
        double decay = 0;
        // double OffsetTime = LatestTS;
        final double offsetTime = 0;

        final double timeForAvgTourLength =
                map.averageTourLength(distanceType) / A.AUVvelocity;
        decay = Math.log((desiredVoiFactor * magnitude) / magnitude)
                * -(1 / (timeForAvgTourLength + offsetTime));

        return decay;
    }

    void analyzeExperiment() {
        // Calculating Averages
        // Initialization
        for (int i = 0; i < constUWSN.TOUR_TYPES; i++) {
            constUWSN.results[i][0] = 0;
        }
        // Summation
        for (int i = 0; i < constUWSN.TOTAL_SAMPLES; i++) {
            constUWSN.results[i % constUWSN.TOUR_TYPES][0] +=
                    allTours.get(i).getVoIAccumulatedByTour();
        }
        // Division
        for (int i = 0; i < constUWSN.TOUR_TYPES; i++) {
            constUWSN.results[i][0] =
                    constUWSN.results[i][0] / constUWSN.SAMPLES_PER_TOUR;
        }
    }

    void printAverages() {
        System.out.println("Tour Type\tAvg. VoI\t");
        for (int i = 0; i < constUWSN.TOUR_TYPES; i++) {
            System.out.print(allTours.get(i).NameOfPlanner + "\t");
            for (int j = 0; j < 1; j++) {
                System.out
                .print(String.format("%8.2f", constUWSN.results[i][j]));
            }
            System.out.println();
        }
    }

    public Random getRand() {
        return rand;
    }

    public static GAforResurfacing getInstance() {
        return GAforResurfacing.instance;
    }
}
