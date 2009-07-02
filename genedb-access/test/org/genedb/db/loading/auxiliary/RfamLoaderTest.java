package org.genedb.db.loading.auxiliary;

import static org.junit.Assert.*;


import org.gmod.schema.feature.Chromosome;
import org.gmod.schema.feature.Gene;
import org.gmod.schema.feature.Transcript;
import org.gmod.schema.mapped.Analysis;
import org.gmod.schema.mapped.AnalysisFeature;
import org.gmod.schema.mapped.DbXRef;
import org.gmod.schema.mapped.FeatureLoc;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/****************************************************************************************************************************************
 * Tests the rfamloader class. At the moment, we have created a fictititous set of rfam results for pfalciparum so 
 * that the tests will work against the hsql pfalciparum database. Once rfam is run against pfalciparum for real, this
 * test file should be replaced with actual results and the expected number of transcripts, type of RNA in the first row and
 * chromosome name (constants below) changed accordingly.
 * TODO: Investigate the use of FeatureTester to check properties of genes and transcripts instead of writing them out here
 * 
 * @author nds
 ****************************************************************************************************************************************/

public class RfamLoaderTest {
    //Constants
    private static final Logger logger = Logger.getLogger(RfamLoaderTest.class);
    private static final int EXPECTED_NUMBER_OF_GENES_AND_TRANSCRIPTS = 4;
    private static String CHROMOSOME_NAME = "Pf3D7_01";
    private static String TYPE_OF_FIRST_TRANSCRIPT = "ncRNA";
    //Configurable
    private static RfamLoader loader;
    private Gene gene;
    private Transcript transcript;
    private static Chromosome chromosome; 
    private Analysis analysis;
    private static SessionFactory sessionFactory; 
    private static Session session;
    private static String analysisProgramVersion = "9.1"; //When running the rfamloader, the version will get picked up from command line. Here we insert it manually

    @BeforeClass
    public static void setup() throws IOException, HibernateException, SQLException, ClassNotFoundException {
        
        /* Here we set the application context and extract the rfamloader bean. Then we delete any existing rfam features from the test database and
         * load the test results.  */
        
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {"Load.xml", "AuxTest.xml"});
        loader = ctx.getBean("rfamloader", RfamLoader.class);
        assertTrue(loader.processOptionIfValid("rfam-version", analysisProgramVersion)); 
        sessionFactory = loader.getSessionFactory();
        session = SessionFactoryUtils.getSession(sessionFactory, true);
        loader.clear("Pfalciparum", null); 
        new Load(loader).load("test/data/Pfalciparum-rfam.fa.o"); 

        /*Get the chromosome object */
        chromosome = (Chromosome)session.createQuery("from Feature where uniquename='" + CHROMOSOME_NAME + "'").uniqueResult();
        assertNotNull(chromosome);
    }
    
    /**
     * Tests if the rfam analysis has been added and, if so, the following properties of the analysis are checked:
     * Program
     * ProgramVersion 
     * Time
     */
    
    @SuppressWarnings("unchecked") //Remove after sorting out type safety issue below
    @Test
    public void testRfamAnalysis(){
        List<Analysis> analysisList = session.createQuery("from Analysis where program='rfam'").list();
        assertEquals(analysisList.size(),1);
        analysis = analysisList.get(0);
        assertEquals(analysis.getProgram(), "rfam");
        assertEquals(analysis.getProgramVersion(), analysisProgramVersion);
        //Check time also - how?
    }
    
    /**
     * The loading process is expected to result in the creation of a number of genes. Here, we test the following properties of one
     * such gene:
     * 1. Uniquename
     * 2. Organism ID
     * 3. Dbxref ID 
     * 4. Type ID
     * 5. Associated FeatureLoc - fmin, fmax, strand, rank, sourcefeature
     */
  
    @SuppressWarnings("unchecked") //TODO: remove after type safety below is sorted out
    @Test
    public void testRfamGenes() {
    
        logger.info("Doing tests on rfam genes");
        
        /* Check that the right number of genes have been added */
        List<Gene> allRfamGenes = session.createQuery("from Feature where uniquename LIKE '" + CHROMOSOME_NAME + "_rfam_____'").list();
        assertEquals(EXPECTED_NUMBER_OF_GENES_AND_TRANSCRIPTS, allRfamGenes.size()); 
        
        /*Do other tests on the first gene created i.e with uniquename CHROMOSOME_NAME_rfam_0001*/
        String expectedGeneName = CHROMOSOME_NAME.concat("_rfam_0001");
        gene  = (Gene)session.createQuery("from Feature where uniquename='" + expectedGeneName + "'").uniqueResult(); 
        assertNotNull(gene);
        assertEquals(gene.getUniqueName(), expectedGeneName); //repeat test but do it anyway
        assertEquals(gene.getOrganism().getOrganismId(), 27);
        assertNull(gene.getDbXRef());
        assertEquals(gene.getType().getCvTermId(), 792); //Perhaps this value should not be hard-coded?
        
        /*Tests on featureloc */
        List<FeatureLoc> featureLocList = gene.getFeatureLocs();
        assertEquals(featureLocList.size(), 1); //There should only be one associated feature Loc
        FeatureLoc featureLoc = featureLocList.get(0);
        System.out.println("Featureloc details for feature " + featureLoc.getFeature().getFeatureId() + ":" + featureLoc.getFmin() + " - " + featureLoc.getFmax());
        assertEquals(featureLoc.getFmin().intValue(), 1882443);
        assertEquals(featureLoc.getFmax().intValue(), 1882574);
        assertEquals(featureLoc.getStrand().intValue(), -1);
        assertEquals(featureLoc.getRank(), 0);
        assertEquals(featureLoc.getSourceFeature().getFeatureId(), chromosome.getFeatureId());
    }
    
    
    
    /**
     * The RfamLoader should also add the right number of transcripts. This method checks that it has and for one of them checks the following:
     * Uniquename
     * OrganismId
     * Type Id
     * Associated Dbxref - Dbxref ID, DB ID, Accession, Description
     * Associated FeatureLoc - fmin, fmax, strand, sourcefeature
     * Associated analysisFeature - analysis ID, score
     */
    
    
    @SuppressWarnings("unchecked") //Remove after sorting out type safety issues below
    @Test
    public void testRfamTranscripts() {
      
        logger.info("Doing tests on rfam transcripts");
      
        /* Check that the right number of transcripts have been added */
        List<Transcript> allRfamTranscripts = session.createQuery("from Feature where uniquename LIKE '" + CHROMOSOME_NAME + "_rfam_____:%'").list();
        assertEquals(EXPECTED_NUMBER_OF_GENES_AND_TRANSCRIPTS, allRfamTranscripts.size()); 
        
        /*Do other tests on first transcript i.e. with uniquename CHROMOSOME_NAME_rfam_0001:ncRNA*/
        String expectedTranscriptName = CHROMOSOME_NAME.concat("_rfam_0001:").concat(TYPE_OF_FIRST_TRANSCRIPT);
        transcript  = (Transcript)session.createQuery("from Feature where uniquename='" + expectedTranscriptName + "'").uniqueResult(); 
        assertNotNull(transcript);
        assertEquals(transcript.getUniqueName(), expectedTranscriptName); //Repeat test but do anyway
        assertEquals(transcript.getOrganism().getOrganismId(), 27);
        assertEquals(transcript.getType().getCvTermId(), 743); //Perhaps this value should not be hard-coded?
        
        /* Tests on dbxref */
        DbXRef dbxref = transcript.getDbXRef();
        assertNotNull(dbxref);
        assertEquals(dbxref.getDb().getDbId(), 199); //Hard-coded value - consider changing
        assertEquals(dbxref.getAccession(), "RF00015");
        assertEquals(dbxref.getDescription(), "U4");
        
        /*Tests on featureloc */
        List<FeatureLoc> featureLocList = transcript.getFeatureLocs();
        assertEquals(featureLocList.size(), 1); //There should only be one associated feature Loc
        FeatureLoc featureLoc = featureLocList.get(0);
        System.out.println("Featureloc details for feature " + featureLoc.getFeature().getFeatureId() + ":" + featureLoc.getFmin() + " - " + featureLoc.getFmax());
        assertEquals(featureLoc.getFmin().intValue(), 2004527);
        assertEquals(featureLoc.getFmax().intValue(), 2004566);
        assertEquals(featureLoc.getStrand().intValue(), -1);
        assertEquals(featureLoc.getRank(), 0);
        assertEquals(featureLoc.getSourceFeature().getFeatureId(), chromosome.getFeatureId());
        
        /*Tests on analysisfeature */
        List<AnalysisFeature> analysisFeatureList = (List)transcript.getAnalysisFeatures();
        assertEquals(analysisFeatureList.size(), EXPECTED_NUMBER_OF_GENES_AND_TRANSCRIPTS); 
        AnalysisFeature analysisFeature = analysisFeatureList.get(0);
        assertEquals(analysisFeature.getAnalysis().getAnalysisId(), analysis.getAnalysisId());
        assertEquals(analysisFeature.getRawScore().toString(), new Double(77.01).toString());      
    }
    
  
    @AfterClass 
    @SuppressWarnings("deprecation")
    public static void shutdownDatabase() throws HibernateException, SQLException {

        // When session.connection() is deprecated, change to use doWork(Work)
        // and remove the @SuppressWarnings("deprecation").
        session.connection().createStatement().execute("shutdown");
        SessionFactoryUtils.releaseSession(session, sessionFactory);
    }
}
