package org.gmod.schema.feature;

import org.gmod.schema.mapped.CvTerm;
import org.gmod.schema.mapped.Feature;
import org.gmod.schema.mapped.FeatureLoc;
import org.gmod.schema.mapped.FeatureRelationship;
import org.gmod.schema.mapped.FeatureSynonym;
import org.gmod.schema.mapped.Organism;
import org.gmod.schema.mapped.Synonym;

import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * A {@link Gene} or a {@link Pseudogene}.
 *
 * @author rh11
 *
 */
@Entity
public abstract class AbstractGene extends Region {
    private static final Logger logger = Logger.getLogger(AbstractGene.class);

    AbstractGene() {
        // empty
    }

    public AbstractGene(Organism organism, String uniqueName, boolean analysis, boolean obsolete,
            Timestamp dateAccessioned) {
        super(organism, uniqueName, analysis, obsolete, dateAccessioned);
    }

    @Transient
    public Collection<Transcript> getTranscripts() {
        Collection<Transcript> ret = new ArrayList<Transcript>();

        for (FeatureRelationship relationship : this.getFeatureRelationshipsForObjectId()) {
            Feature transcript = relationship.getSubjectFeature();
            if (transcript instanceof Transcript) {
                ret.add((Transcript) transcript);
            }
        }

        return ret;
    }

    @Transient
    public abstract String getProductsAsTabSeparatedString();

    @Transient
    public boolean isPseudo() {
        return (this instanceof Pseudogene);
    }

    /**
     * Rather than returning the residues stored in the database, this method
     * extracts the appropriate subsequence of the residues of the source
     * feature (chromosome, supercontig or contig).
     */
    @Transient
    @Override
    public String getResidues() {
        Feature parent = this.getRankZeroFeatureLoc().getSourceFeature();
        return parent.getResidues(this.getStart(), this.getStop());
    }

    public static <T extends AbstractGene> T make(Class<T> geneClass, Organism organism, String uniqueName, String name) {
        try {
            return geneClass.getDeclaredConstructor(Organism.class, String.class, String.class).newInstance(organism, uniqueName, name);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Internal error: failed to construct gene", e);
        } catch (SecurityException e) {
            throw new RuntimeException("Internal error: failed to construct gene", e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Internal error: failed to construct gene", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Internal error: failed to construct gene", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Internal error: failed to construct gene", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Internal error: failed to construct gene", e);
        }
    }

    /**
     * Create a transcript for this gene, which is assumed to be newly-created
     * and not to have a transcript yet.
     *
     * @param <T>
     * @param transcriptClass the class of the transcript to create
     * @param transcriptUniqueName the uniqueName of the transcript to create
     * @param fmin
     * @param fmax
     * @param phase the phase
     * @return
     */
    public <T extends Transcript> T makeTranscript(Class<T> transcriptClass, String transcriptUniqueName,
                int fmin, int fmax, int phase) {
            logger.trace(String.format("Creating transcript '%s' for gene '%s' at locations %d..%d (gene locations %d..%d)",
                transcriptUniqueName, getUniqueName(), fmin, fmax, getFmin(), getFmax()));

            if (fmax < fmin) {
                throw new IllegalArgumentException(String.format("fmax (%d) < fmin (%d)", fmax, fmin));
            }

            int relativeFmin = fmin - this.getFmin();
            if (relativeFmin < 0) {
                logger.trace(String.format("Start of transcript (%d) is before start of gene (%d). Resetting gene start",
                    fmin, this.getFmin()));
                this.lowerFminTo(fmin);
            }

            int relativeFmax = fmax - this.getFmin();
            if (fmax > this.getFmax()) {
                logger.trace(String.format("End of transcript (%d) is after end of gene (%d). Resetting gene end",
                    fmax, this.getFmax()));
                this.raiseFmaxTo(fmax);
            }

            T transcript = Transcript.construct(transcriptClass, getOrganism(), transcriptUniqueName, null);
            for (FeatureLoc featureLoc: getFeatureLocs()) {
                featureLoc.getSourceFeature().addLocatedChild(transcript, featureLoc.getFmin() + relativeFmin,
                    featureLoc.getFmin() + relativeFmax,
                    featureLoc.getStrand(), phase, featureLoc.getLocGroup(), featureLoc.getRank());
            }
            this.addFeatureRelationship(transcript, "relationship", "part_of");

            if (ProductiveTranscript.class.isAssignableFrom(transcriptClass)) {
                String polypeptideUniqueName;
                if (transcriptUniqueName.endsWith(":mRNA")) {
                    polypeptideUniqueName = String.format("%s:pep", transcriptUniqueName.substring(0, transcriptUniqueName.length() - 5));
                } else {
                    polypeptideUniqueName = String.format("%s:pep", transcriptUniqueName);
                }
                logger.trace(String.format("Creating polypeptide '%s' for transcript '%s'",
                    polypeptideUniqueName, getUniqueName()));
                Polypeptide polypeptide = new Polypeptide(getOrganism(), polypeptideUniqueName);
                for (FeatureLoc featureLoc: transcript.getFeatureLocs()) {
                    featureLoc.getSourceFeature().addLocatedChild(polypeptide, featureLoc.getFmin(), featureLoc.getFmax(),
                        featureLoc.getStrand(), phase, featureLoc.getLocGroup(), featureLoc.getRank());
                }
                ((ProductiveTranscript)transcript).setProtein(polypeptide);
            }

            return transcript;
    }

    /**
     * If this AbstractGene has a current synonym of the specified type,
     * the name and synonymSGML of that synonym are set to <code>synonymString</code>
     * and we return <code>true</code>.
     * If not, we do nothing and return <code>false</code>.
     * @param synonymType the type of synonym to look for
     * @param synonymString the string to which the synonym should be changed
     * @return whether or not a synonym was found
     */
    protected boolean setSynonymIfPresent(String synonymType, String synonymString) {
        logger.trace(String.format("Setting synonym (type %s) of gene '%s' to '%s' if present",
            synonymType, getUniqueName(), synonymString));
        for (FeatureSynonym featureSynonym: getFeatureSynonyms()) {
            if (!featureSynonym.isCurrent()) {
                continue;
            }

            Synonym thisSynonym = featureSynonym.getSynonym();
            CvTerm thisSynonymType = thisSynonym.getType();
            if (thisSynonymType.getCv().getName().equals("genedb_synonym_type") && synonymType.equals(thisSynonymType.getName())) {
                logger.trace(String.format("Synonym of type %s already present on gene; resetting existing synonym", synonymType));
                thisSynonym.setName(synonymString);
                thisSynonym.setSynonymSGML(synonymString);
                return true;
            }
        }
        logger.trace("Synonym of required type is not already present");
        return false;
    }

    public void setSystematicId(String systematicId) {
        logger.trace(String.format("Setting systematic_id of gene '%s' to '%s'", getUniqueName(), systematicId));

        if (!setSynonymIfPresent("systematic_id", systematicId)) {
            addSynonym("systematic_id", systematicId);
        }
    }

    @Transient
    public void setTemporarySystematicId(String temporarySystematicId) {
        logger.trace(String.format("Setting temporary_systematic_id of gene '%s' to '%s'", getUniqueName(), temporarySystematicId));
        if (!setSynonymIfPresent("temporary_systematic_id", temporarySystematicId)) {
            addSynonym("temporary_systematic_id", temporarySystematicId);
        }
    }
}
