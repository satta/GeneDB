/*
 * Copyright (c) 2006 Genome Research Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this program; see the file COPYING.LIB. If not, write to the Free
 * Software Foundation Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307
 * USA
 */

/**
 * 
 * 
 * @author <a href="mailto:art@sanger.ac.uk">Adrian Tivey</a>
 */
package org.genedb.db.loading;

import static org.genedb.db.loading.EmblFeatureKeys.FT_CDS;
import static org.genedb.db.loading.EmblFeatureKeys.FT_SOURCE;
import static org.genedb.db.loading.EmblQualifiers.*;

import org.genedb.db.hibernate3gen.Cv;
import org.genedb.db.hibernate3gen.CvTerm;
import org.genedb.db.hibernate3gen.FeatureLoc;
import org.genedb.db.hibernate3gen.FeatureRelationship;
import org.genedb.db.loading.featureProcessors.BaseFeatureProcessor;

import org.biojava.bio.Annotation;
import org.biojava.bio.BioException;
import org.biojava.bio.seq.Feature;
import org.biojava.bio.seq.FeatureFilter;
import org.biojava.bio.seq.FeatureHolder;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.StrandedFeature;
import org.biojava.bio.symbol.Location;
import org.biojava.utils.ChangeVetoException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class is the main entry point for GeneDB data miners. It's designed to
 * be called from the command-line, or a Makefile.
 * 
 * Usage: GenericRunner organism [-show_ids] [-show_contigs]
 * 
 * 
 * @author Adrian Tivey (art)
 */
public class StandardFeatureHandler extends BaseFeatureProcessor implements FeatureHandler {

    private NomenclatureHandler nomenclatureHandler;

    private List<FeatureListener> listeners = new ArrayList<FeatureListener>(0);
    
    
 //   public void afterPropertiesSet() {
//        CV_SO = this.daoFactory.getCvDao().findByName("sequence").get(0);
//        CV_MISC = daoFactory.getCvDao().findByName("autocreated").get(0);
//        CV_RELATION = this.daoFactory.getCvDao().findByName("relationship").get(0);
//        
//        REL_PART_OF = this.daoFactory.getCvTermDao().findByNameInCv("part_of", CV_RELATION).get(0);
//        REL_DERIVES_FROM = this.daoFactory.getCvTermDao().findByNameInCv(
//                "derives_from", CV_SO).get(0);
//        MISC_NOTE = daoFactory.getCvTermDao().findByNameInCv("note", CV_MISC)
//                .get(0);
 //   }
    
    public void processCDS(Sequence seq,
            org.genedb.db.jpa.Feature parent, int offset) {
        FeatureHolder fh = seq.filter(new FeatureFilter.ByType(FT_CDS));

        int count = 0;
        Iterator fit = fh.features();
        while (fit.hasNext()) {
            Feature f = (Feature) fit.next();
            count++;
            if (!(f instanceof StrandedFeature)) {
                System.err
                        .println("Found unstranded feature when looking for CDSs");
                continue;
            }
            StrandedFeature cds = (StrandedFeature) f;
            Annotation an = cds.getAnnotation();

            if (an.containsProperty(QUAL_PSEUDO)) {
                this.processPseudoGene();
            } else {
                this.processCodingGene(cds, an, parent, offset);
            }
        }
        
        // Now remove CDSs
        List<Feature> toRemove = new ArrayList<Feature>(count);
        fit = fh.features();
        while (fit.hasNext()) {
            toRemove.add((Feature) fit.next());
        }
        for (Feature feature : toRemove) {
            try {
                seq.removeFeature(feature);
            } catch (ChangeVetoException exp) {
                exp.printStackTrace();
            } catch (BioException exp) {
                exp.printStackTrace();
            }
        }

    }

    @SuppressWarnings( { "unchecked" })
    private void processCodingGene(StrandedFeature cds, Annotation an,
            org.genedb.db.jpa.Feature parent, int offset) {
        String sysId = null;
        try {
            Location loc = cds.getLocation().translate(offset);
            Names names = this.nomenclatureHandler.findNames(an);
            sysId = names.getSystematicId();
            logger.debug("Looking at systematic id '" + sysId+"'");
            int transcriptNum = 1;

            short strand = (short) cds.getStrand().getValue();

            List<org.genedb.db.jpa.Feature> features = new ArrayList<org.genedb.db.jpa.Feature>();
            List<FeatureLoc> featureLocs = new ArrayList<FeatureLoc>();
            List<FeatureRelationship> featureRelationships = new ArrayList<FeatureRelationship>();

            // Cv CV_SO =
            // this.daoFactory.getCvDao().findByName("sequence").get(0);
            // Cvterm REL_DERIVES_FROM =
            // this.daoFactory.getCvTermDao().findByNameInCv("derives_from",
            // CV_SO).get(0);

            Cv CV_NAMING = this.daoFactory.getCvDao().findByName(
                    "genedb_synonym_type").get(0);
            CvTerm SYNONYM_RESERVED = this.daoFactory.getCvTermDao()
                    .findByNameInCv(QUAL_RESERVED, CV_NAMING).get(0);
            CvTerm SYNONYM_SYNONYM = this.daoFactory.getCvTermDao()
                    .findByNameInCv(QUAL_SYNONYM, CV_NAMING).get(0);
            CvTerm SYNONYM_PRIMARY = this.daoFactory.getCvTermDao()
                    .findByNameInCv(QUAL_PRIMARY, CV_NAMING).get(0);
            CvTerm SYNONYM_SYS_ID = this.daoFactory.getCvTermDao()
                    .findByNameInCv(QUAL_SYS_ID, CV_NAMING).get(0);
            CvTerm SYNONYM_TMP_SYS = this.daoFactory.getCvTermDao()
                    .findByNameInCv(QUAL_TEMP_SYS_ID, CV_NAMING).get(0);
            CvTerm SYNONYM_PROTEIN = this.daoFactory.getCvTermDao()
                    .findByNameInCv("protein_name", CV_NAMING).get(0);
            this.DUMMY_PUB = this.daoFactory.getPubDao().findByUniqueName(
                    "null").get(0);
            featureUtils.setDummyPub(this.DUMMY_PUB);

            Cv CV_PRODUCTS = this.daoFactory.getCvDao().findByName(
                    "genedb_products").get(0);

            // Is this a multiply spliced gene?
            org.genedb.db.jpa.Feature gene = null;
            boolean altSplicing = false;
            String sharedId = MiningUtils.getProperty("shared_id", an, null);
            if (sharedId != null) {
                gene = this.daoFactory.getFeatureDao().findByUniqueName(
                        sharedId);
                altSplicing = true;
            }

            if (gene == null) {
                if (altSplicing) {
                    gene = this.featureUtils.createFeature("gene", sharedId,
                            this.organism);
                    this.daoFactory.persist(gene);
                    featureUtils.createSynonym(SYNONYM_SYS_ID, sharedId, gene,
                            true);
                } else {
                    gene = this.featureUtils.createFeature("gene", this.gns
                            .getGene(sysId), this.organism);
                    this.daoFactory.persist(gene);
                    storeNames(names, SYNONYM_RESERVED, SYNONYM_SYNONYM,
                            SYNONYM_PRIMARY, SYNONYM_SYS_ID, SYNONYM_TMP_SYS,
                            gene);
                }
                // features.add(gene);
                // this.daoFactory.persist(gene);

                FeatureLoc geneFl = featureUtils.createLocation(parent, gene,
                        loc.getMin(), loc.getMax(), strand);
                featureLocs.add(geneFl);

            }

            String mRnaName = this.gns.getTranscript(sysId, transcriptNum);
            String baseName = sysId;
            if (altSplicing) {
                mRnaName = this.gns.getGene(sysId);
                baseName = mRnaName;
            }
            org.genedb.db.jpa.Feature mRNA = this.featureUtils
                    .createFeature("mRNA", mRnaName, this.organism);
            this.daoFactory.persist(mRNA);
            if (altSplicing) {
                storeNames(names, SYNONYM_RESERVED, SYNONYM_SYNONYM,
                        SYNONYM_PRIMARY, SYNONYM_SYS_ID, SYNONYM_TMP_SYS, mRNA);
            }
            FeatureLoc mRNAFl = featureUtils.createLocation(parent, mRNA, loc
                    .getMin(), loc.getMax(), strand);
            FeatureRelationship mRNAFr = featureUtils.createRelationship(mRNA,
                    gene, REL_PART_OF);
            // features.add(mRNA);
            featureLocs.add(mRNAFl);
            featureRelationships.add(mRNAFr);

            Iterator<Location> it = loc.blockIterator();
            int exonCount = 0;
            while (it.hasNext()) {
                exonCount++;
                Location l = it.next();
                org.genedb.db.jpa.Feature exon = this.featureUtils
                        .createFeature("exon", this.gns.getExon(baseName,
                                transcriptNum, exonCount), this.organism);
                FeatureRelationship exonFr = featureUtils.createRelationship(
                        exon, mRNA, REL_PART_OF);
                FeatureLoc exonFl = featureUtils.createLocation(parent, exon, l
                        .getMin(), l.getMax(), strand);
                features.add(exon);
                featureLocs.add(exonFl);
                featureRelationships.add(exonFr);
            }

            org.genedb.db.jpa.Feature polypeptide = this.featureUtils
                    .createFeature("polypeptide", this.gns.getPolypeptide(
                            baseName, transcriptNum), this.organism);
            // TODO Protein name - derived from gene name in some cases.
            // TODO where store protein name synonym - on polypeptide or
            // gene
            if (names.getProtein() != null) {
                polypeptide.setName(names.getProtein());
                featureUtils.createSynonym(SYNONYM_PROTEIN, names.getProtein(),
                        polypeptide, true);
            }
            FeatureRelationship pepFr = featureUtils.createRelationship(
                    polypeptide, mRNA, REL_DERIVES_FROM);
            //features.add(polypeptide);
            FeatureLoc pepFl = featureUtils.createLocation(parent, polypeptide,
                    loc.getMin(), loc.getMax(), strand);
            featureLocs.add(pepFl);
            featureRelationships.add(pepFr);
            // TODO store protein translation
            // TODO protein props - store here or just in query version
            daoFactory.persist(polypeptide);

            // Cv MISC = daoFactory.getCvDao().findByName("local").get(0);
            // Cv MISC =
            // daoFactory.getCvDao().findByName("autocreated").get(0);

            // featureProps.addAll(createProperties(polypeptide, an,
            // "product", "product", CV_PRODUCTS));

            // Store feature properties based on original annotation
            createFeatureProp(polypeptide, an, "colour", "colour", CV_MISC);
            // 
            // Cvterm cvTerm =
            // daoFactory.getCvTermDao().findByNameInCv("note",
            // MISC).get(0);
            createFeaturePropsFromNotes(polypeptide, an, QUAL_NOTE, MISC_NOTE);

            // String nucleic = parent.getResidues().substring(loc.getMin(),
            // loc.getMax());
            // String protein = translate(nucleic);
            // polypeptide.setResidues(protein);

            // Now persist gene heirachy
            for (org.genedb.db.jpa.Feature feature : features) {
                // System.err.print("Trying to persist
                // "+feature.getUniquename());
                this.daoFactory.persist(feature);
                // System.err.println(" ... done");
            }
            for (FeatureLoc location : featureLocs) {
                this.daoFactory.persist(location);
            }
            for (FeatureRelationship relationship : featureRelationships) {
                this.daoFactory.persist(relationship);
            }
            System.err.print(".");
        } catch (RuntimeException exp) {
            System.err.println("\n\nWas looking at '" + sysId + "'");
            throw exp;
        }
    }

    private void storeNames(Names names, CvTerm SYNONYM_RESERVED,
            CvTerm SYNONYM_SYNONYM, CvTerm SYNONYM_PRIMARY,
            CvTerm SYNONYM_SYS_ID, CvTerm SYNONYM_TMP_SYS,
            org.genedb.db.jpa.Feature gene) {
        if (names.isIdTemporary()) {
            featureUtils.createSynonym(SYNONYM_TMP_SYS,
                    names.getSystematicId(), gene, true);
        } else {
            featureUtils.createSynonym(SYNONYM_SYS_ID, names.getSystematicId(),
                    gene, true);
        }
        if (names.getPrimary() != null) {
            gene.setName(names.getPrimary());
            featureUtils.createSynonym(SYNONYM_PRIMARY, names.getPrimary(),
                    gene, true);
        }
        if (names.getReserved() != null) {
            featureUtils.createSynonym(SYNONYM_RESERVED, names.getReserved(),
                    gene, true);
        }
        if (names.getSynonyms() != null) {
            featureUtils.createSynonyms(SYNONYM_SYNONYM, names.getSynonyms(),
                    gene, true);
        }
        if (names.getObsolete() != null) {

        }
        if (names.getPreviousSystematicIds() != null) {
            featureUtils.createSynonyms(SYNONYM_SYS_ID, names
                    .getPreviousSystematicIds(), gene, false);
        }
    }

    private void processPseudoGene() {
        // TODO Pseudogenes
        return;
    }

//    private List<Featureprop> createProperties(
//            org.genedb.db.hibernate.Feature f, Annotation an,
//            String annotationKey, String dbKey, Cv cv) {
//        List<Featureprop> ret = new ArrayList<Featureprop>(3);
//
//        List<Cvterm> cvTerms = daoFactory.getCvTermDao().findByNameInCv(
//                annotationKey, cv);
//
//        Cvterm cvTerm;
//        if (cvTerms == null || cvTerms.size() == 0) {
//            cvTerm = new Cvterm();
//            cvTerm.setCv(cv);
//            cvTerm.setName(annotationKey);
//            cvTerm.setDefinition(annotationKey);
//        } else {
//            cvTerm = cvTerms.get(0);
//        }
//
//        List<String> values = MiningUtils.getProperties(annotationKey, an);
//
//        int i = 0;
//        for (String value : values) {
//            Featureprop fp = new Featureprop();
//            fp.setRank(i);
//            fp.setCvterm(cvTerm);
//            fp.setFeature(f);
//            // fp.setFeaturepropPubs(arg0);
//            fp.setValue(value);
//
//            f.getFeatureprops().add(fp);
//            ret.add(fp);
//        }
//        return ret;
//    }

//    private ParsedString parseDbXref(String in, String prefix) {
//        ParsedString ret;
//        String lookFor = "(" + prefix;
//        int index = in.indexOf(lookFor);
//        if (index != -1) {
//            int rbracket = in.indexOf(")", index);
//            String part = in.substring(index + 1, rbracket);
//            in = in.substring(0, index) + in.substring(rbracket + 1);
//            ret = new ParsedString(in, part);
//        } else {
//            ret = new ParsedString(in, null);
//        }
//        return ret;
//    }

    // private String translate(String nucleic) {
    // if (translation != null && translation.length() > 0 ) {
    // this.setSequence(SequenceType.SEQ_PROTEIN, translation);
    // return;
    // }

    // if ( table != null) {
    // try {
    // int num = Integer.parseInt(table);
    // if (GeneticCodes.isValidTransTable(num)) {
    // setTranslationTableNum(num);
    // } else {
    // System.err.println("WARN: Attempted to set unrecognized translation
    // table ("+table+") in "+getId());
    // }
    // }
    // catch (NumberFormatException exp) {
    // System.err.println("WARN: Attempted to set unrecognized translation
    // table ("+table+") in "+getId());
    // }
    // }

    // int cdStartNum = 1;
    // if (cdStart != null && cdStart.length() != 0) {
    // cdStartNum = Integer.parseInt(cdStart);
    // }
    // if (cdStartNum < 1 || cdStartNum > 3) {
    // LogUtils.bprintln("WARN: Ignoring unexpected value of codon_start ("
    // + cdStart + ") in " + getId());
    // cdStartNum = 1;
    // }
    // if (cdStartNum != 1 && !isPartial()) {
    // LogUtils.bprintln("WARN: Got non '1' value for codon_start ("
    // + cdStart + ") but no /partial in " + getId());
    // setPartial(true);
    // }

    // if (cdStartNum != 1) {
    // setCodonStart(cdStartNum);
    // }

    // SeqTrans.SeqTransResult result =
    // SeqTrans.getInstance().translate(this, getTranslationTableNumber(),
    // getCodonStart().intValue(), codon, except);
    // setProteinWarning(result.getWarning());
    // setSequence(SequenceType.SEQ_PROTEIN, result.getSeq());

    // }

    /*
     * (non-Javadoc)
     * 
     * @see org.genedb.db.loading.FeatureHandler#processSources(org.biojava.bio.seq.Sequence)
     */
    @SuppressWarnings("unchecked")
    public org.genedb.db.jpa.Feature processSources(Sequence seq)
            throws ChangeVetoException, BioException {
        FeatureHolder fh = seq.filter(new FeatureFilter.ByType(FT_SOURCE));

        List<Feature> sources = new ArrayList<Feature>();

        Iterator fit = fh.features();
        while (fit.hasNext()) {
            sources.add((Feature) fit.next());
        }
        Collections.sort(sources, Feature.byLocationOrder);

        Feature fullLengthSource = null;
        for (Feature source : sources) {
            Location loc = source.getLocation();
            if (loc.getMin() > 1) {
                break;
            }
            if (loc.getMin() == 1 && loc.getMax() == seq.length()) {
                // Got a full-length source feature
                if (fullLengthSource != null) {
                    // error - there can be only one
                }
                fullLengthSource = source;
            }
        }
        if (fullLengthSource == null) {
            // log
            throw new RuntimeException("Can't find full length source");
        }

        // FeatureUtils.dumpFeature(fullLengthSource, "Full length");

        MiningUtils.sanityCheckAnnotation(fullLengthSource, new String[] {
                QUAL_SYS_ID, QUAL_SO_TYPE }, new String[] {},
                new String[] { QUAL_CHROMOSOME },
                new String[] { QUAL_PRIVATE }, new String[]{}, false, true);

        Annotation an = fullLengthSource.getAnnotation();
        String foundType = MiningUtils.getProperty(QUAL_SO_TYPE, an, null);
        String uniqueName = MiningUtils.getProperty(QUAL_SYS_ID, an, null);
        // System.err.println("Would like to create a '"+foundType+"' with name
        // '"+uniqueName+"'");

        org.genedb.db.jpa.Feature topLevel = this.featureUtils
                .createFeature(foundType, uniqueName, this.organism);
        // System.err.println("Got a feature to persist");

        topLevel.setResidues(seq.seqString());
        topLevel.setMd5Checksum(FeatureUtils.calcMD5(seq.seqString())); // FIXME
        // -
        // should
        // be
        // set
        // by
        // setResidues

        this.daoFactory.persist(topLevel);
        // System.err.println("Have persisted feature");

        sources.remove(fullLengthSource);
        seq.removeFeature(fullLengthSource);

        for (Feature feature : sources) {
            FeatureUtils.dumpFeature(feature, null);
            seq.removeFeature(feature);
        }
        return topLevel;
    }


    public void setNomenclatureHandler(NomenclatureHandler nomenclatureHandler) {
        this.nomenclatureHandler = nomenclatureHandler;
    }

    public void addFeatureListener(FeatureListener fl) {
        listeners.add(fl);
    }

    public void removeFeatureListener(FeatureListener fl) {
        listeners.remove(fl);
    }

    private void fireEvent(FeatureEvent fe) {
        for (FeatureListener fl : listeners) {
            // TODO
        }
    }

    @Override
    public void processStrandedFeature(org.genedb.db.jpa.Feature parent, StrandedFeature feat) {
        // Dummy method
    }

}
