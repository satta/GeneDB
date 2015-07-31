package org.gmod.schema.feature;

import org.gmod.schema.cfg.FeatureType;
import org.gmod.schema.mapped.Organism;
import org.gmod.schema.feature.Chromosome;

import java.sql.Timestamp;

import javax.persistence.Entity;

@Entity
@FeatureType(cv="sequence", term="apicoplast_chromosome")
public class ApicoplastChromosome extends Chromosome {

    ApicoplastChromosome() {
        // empty
    }

    public ApicoplastChromosome(Organism organism, String uniqueName,
                                boolean analysis, boolean obsolete,
                                Timestamp dateAccessioned) {
        super(organism, uniqueName, analysis, obsolete, dateAccessioned);
    }
}
