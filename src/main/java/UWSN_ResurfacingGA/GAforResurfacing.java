package UWSN_ResurfacingGA;

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

    private GAforResurfacing() {
        this.rand = new Random();
    }

    public void run() {
        for (int i = 0; i < constUWSN.SAMPLES_PER_TOUR; i++) {
            GAforResurfacing.LOGGER.info(
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
        myMap.InitializeMap("StraightLine", constUWSN.DEPLOYMENT_DEPTH,
                constUWSN.DISTANCE_SCALE);

        // Initialize an AUV for packet initialization measurements
        final AUV explorer =
                new AUV(constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, 0);

        // Initialize Packets for Nodes on the Map
        final double lastPacketTS = this.IntializeNodePackets(constUWSN.X_DIM,
                constUWSN.Y_DIM, constUWSN.PACKET_INITIALIZATION_TYPE, myMap,
                explorer, constUWSN.DISTANCE_TYPE,
                constUWSN.PACKET_INITIALIZATION_MAGNITUDE,
                constUWSN.PACKET_INITIALIZATION_DESIRED_VOI_FACTOR);
        /*
         * System.out.println("VoI Map"); MyMap.PrintVoIMap(LastPacketTS);
         * System.out.println();
         */
        this.createAndEvaluateTours(myMap, lastPacketTS);

    }

    public void createAndEvaluateTours(final SimulationMap MyMap,
        final double LastPacketTS) {
        // add GA tour here
        Tour tour;
        VoIEvaluation voiEvaluation;
        for (int numResurfaceStops =
                0; numResurfaceStops < constUWSN.NUM_OF_NODES; numResurfaceStops++) {
            tour = new Tour("Resurface After K-Nodes", constUWSN.NUM_OF_AUVS,
                    constUWSN.NUM_OF_NODES, MyMap, constUWSN.DISTANCE_TYPE,
                    numResurfaceStops, constUWSN.AUV_SPEED,
                    constUWSN.DISTANCE_SCALE, LastPacketTS, null);
            voiEvaluation = new VoIEvaluation();
            tour.setVoIAccumulatedByTour(
                    voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis,
                            tour, MyMap, LastPacketTS, constUWSN.AUV_SPEED,
                            constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
            constUWSN.AllTours.add(tour);
        }
        tour = new Tour("Resurface Tour Using GA1", constUWSN.NUM_OF_AUVS,
                constUWSN.NUM_OF_NODES, MyMap, constUWSN.DISTANCE_TYPE, -1,
                constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, LastPacketTS,
                null);
        voiEvaluation = new VoIEvaluation();
        tour.setVoIAccumulatedByTour(
                voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis,
                        tour, MyMap, LastPacketTS, constUWSN.AUV_SPEED,
                        constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
        constUWSN.AllTours.add(tour);
        for (int NumResurfaceStops =
                0; NumResurfaceStops < constUWSN.NUM_OF_NODES; NumResurfaceStops++) {
            tour = new Tour("Reversal : Resurface After K-Nodes",
                    constUWSN.NUM_OF_AUVS, constUWSN.NUM_OF_NODES, MyMap,
                    constUWSN.DISTANCE_TYPE, NumResurfaceStops,
                    constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, LastPacketTS,
                    null);
            voiEvaluation = new VoIEvaluation();
            tour.setVoIAccumulatedByTour(
                    voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis,
                            tour, MyMap, LastPacketTS, constUWSN.AUV_SPEED,
                            constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
            constUWSN.AllTours.add(tour);
        }
        tour = new Tour("Reversal : Resurface Tour Using GA1",
                constUWSN.NUM_OF_AUVS, constUWSN.NUM_OF_NODES, MyMap,
                constUWSN.DISTANCE_TYPE, -1, constUWSN.AUV_SPEED,
                constUWSN.DISTANCE_SCALE, LastPacketTS, null);
        voiEvaluation = new VoIEvaluation();
        tour.setVoIAccumulatedByTour(
                voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis,
                        tour, MyMap, LastPacketTS, constUWSN.AUV_SPEED,
                        constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
        constUWSN.AllTours.add(tour);
        tour = new Tour("Resurface Tour Using GA2", constUWSN.NUM_OF_AUVS,
                constUWSN.NUM_OF_NODES, MyMap, constUWSN.DISTANCE_TYPE, -1,
                constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, LastPacketTS,
                null);
        voiEvaluation = new VoIEvaluation();
        tour.setVoIAccumulatedByTour(
                voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis,
                        tour, MyMap, LastPacketTS, constUWSN.AUV_SPEED,
                        constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
        constUWSN.AllTours.add(tour);
        tour = new Tour("Resurface Tour Using GA3", constUWSN.NUM_OF_AUVS,
                constUWSN.NUM_OF_NODES, MyMap, constUWSN.DISTANCE_TYPE, -1,
                constUWSN.AUV_SPEED, constUWSN.DISTANCE_SCALE, LastPacketTS,
                null);
        voiEvaluation = new VoIEvaluation();
        tour.setVoIAccumulatedByTour(
                voiEvaluation.VoIAllAUVTours(constUWSN.VoICalculationBasis,
                        tour, MyMap, LastPacketTS, constUWSN.AUV_SPEED,
                        constUWSN.DISTANCE_SCALE, constUWSN.DISTANCE_TYPE));
        constUWSN.AllTours.add(tour);
    }

    public double IntializeNodePackets(final int x, final int y,
        final String Method, final SimulationMap M, final AUV A,
        final String DistanceType, final double Magnitude,
        final double DesiredVoIFactor) {
        double decay = -1; // to be set yet
        double LatestTS = 0;
        // Equal Initialization
        if (Method == "Equal") {
            for (int Xloc = 0; Xloc < x; Xloc++) {
                for (int Yloc = 0; Yloc < y; Yloc++) {
                    LatestTS = 0;
                    for (int i =
                            0; i < constUWSN.EXPECTED_MAX_PACKETS_AT_NODE; i++) {
                        LatestTS += 1;
                        final Packet NewPacket = new Packet("Normal", Magnitude,
                                decay, LatestTS);
                        M.Nodes.get(Xloc).get(Yloc).AcquirePacket(NewPacket);
                    }
                }
            }
        }
        // Symmetrical Initialization
        else if (Method == "Symmetrical") {
            for (int Xloc = 0; Xloc < x; Xloc++) {
                for (int Yloc = 0; Yloc < y; Yloc++) {
                    LatestTS = 5 * Math.pow(Xloc, 2) * Math.pow(Yloc, 2);
                    for (int i =
                            0; i < constUWSN.EXPECTED_MAX_PACKETS_AT_NODE; i++) {
                        LatestTS += 1;
                        final Packet NewPacket = new Packet("Normal", Magnitude,
                                decay, LatestTS);
                        M.Nodes.get(Xloc).get(Yloc).AcquirePacket(NewPacket);
                    }
                }
            }
        }
        // Random Initialization
        else if (Method == "Random") {
            int TS = 0;
            LatestTS = 0;
            for (int Xloc = 0; Xloc < x; Xloc++) {
                for (int Yloc = 0; Yloc < y; Yloc++) {
                    TS = 0;
                    // Each node should have at least 1 packet
                    final int RandNumOfPackets = this.getRand().nextInt(
                            constUWSN.EXPECTED_MAX_PACKETS_AT_NODE) + 1;
                    for (int i = 0; i < RandNumOfPackets; i++) {
                        TS += 10;
                        final Packet NewPacket = new Packet("Normal", Magnitude,
                                decay, LatestTS);
                        M.Nodes.get(Xloc).get(Yloc).AcquirePacket(NewPacket);
                        if (LatestTS < TS) {
                            LatestTS = TS;
                        }
                    }
                }
            }
        }
        // Setting up decays
        decay = determineDecayConst(M, A, Magnitude, DesiredVoIFactor,
                DistanceType, LatestTS);
        for (int Xloc = 0; Xloc < x; Xloc++) {
            for (int Yloc = 0; Yloc < y; Yloc++) {
                for (int i = 0; i < M.Nodes.get(Xloc).get(Yloc).Packets
                        .size(); i++) {
                    final String PacketClass =
                            M.Nodes.get(Xloc).get(Yloc).Packets
                            .get(i).PacketDataClass;
                    if (PacketClass == "Normal") {
                        M.Nodes.get(Xloc).get(Yloc).Packets.get(i).VoIDecay =
                                decay;
                    }
                }
            }
        }
        return LatestTS;
    }

    private double determineDecayConst(final SimulationMap M, final AUV A,
        final double Magnitude, final double DesiredVoIFactor,
        final String DistanceType, final Double LatestTS) {
        // t = S / V
        // VoI = A * e^-(Bt) => B = ln(VoI/A) * -1/t
        double Decay = 0;
        // double OffsetTime = LatestTS;
        final double OffsetTime = 0;

        final double TimeForAverageTourLength =
                M.AverageTourLength(DistanceType) / A.AUVvelocity;
        Decay = Math.log((DesiredVoIFactor * Magnitude) / Magnitude)
                * -(1 / (TimeForAverageTourLength + OffsetTime));

        return Decay;
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
                    constUWSN.AllTours.get(i).getVoIAccumulatedByTour();
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
            System.out.print(constUWSN.AllTours.get(i).NameOfPlanner + "\t");
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
