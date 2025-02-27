package de.uni_passau.fim.se2.sbse.neat.utils;

import java.util.Set;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.NetworkInnovation;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.NeuronGene;

public class Utils {

    /**
     * Create a new innovation if it does not exist
     * @param source source Neuron of the new innovation
     * @param target target Neuron of the new innovation
     * @param innovations A list of all previous created innovations
     * @return The innovation number of the created innovation or the innovation number of an already existing innovation
     */
    public static NetworkInnovation createOrGetInnovation(NeuronGene source, NeuronGene target, Set<Innovation> innovations) {
        for(Innovation el : innovations) {
            NetworkInnovation inno = (NetworkInnovation) el;
            if(inno.getSource().equals(source) && inno.getTraget().equals(target)) {
                inno.setExists(true);
                return inno;
            }
        }
        int innovationNumber = innovations.size();
        NetworkInnovation innovation = new NetworkInnovation(innovationNumber, source, target);
        innovations.add(innovation);
        return innovation;
    }
    
}
