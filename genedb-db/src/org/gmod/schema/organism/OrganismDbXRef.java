package org.gmod.schema.organism;

import org.gmod.schema.general.DbXRef;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="organism_dbxref")
public class OrganismDbXRef implements Serializable {

    // Fields    
     @Id
    
    @Column(name="organism_dbxref_id", unique=false, nullable=false, insertable=true, updatable=true)
     private int organismDbXRefId;
     
     @ManyToOne(cascade={},fetch=FetchType.LAZY)
         
         @JoinColumn(name="organism_id", unique=false, nullable=false, insertable=true, updatable=true)
     private Organism organism;
     
     @ManyToOne(cascade={},fetch=FetchType.LAZY)
         
         @JoinColumn(name="dbxref_id", unique=false, nullable=false, insertable=true, updatable=true)
     private DbXRef dbXRef;

     // Constructors

    /** default constructor */
    public OrganismDbXRef() {
    }

    /** full constructor */
    private OrganismDbXRef(Organism organism, DbXRef dbXRef) {
       this.organism = organism;
       this.dbXRef = dbXRef;
    }
    
   
    // Property accessors

    /* (non-Javadoc)
     * @see org.genedb.db.jpa.OrganismDbXRefI#getOrganismDbXRefId()
     */
    private int getOrganismDbXRefId() {
        return this.organismDbXRefId;
    }
    
    /* (non-Javadoc)
     * @see org.genedb.db.jpa.OrganismDbXRefI#setOrganismDbXRefId(int)
     */
    private void setOrganismDbXRefId(int organismDbXRefId) {
        this.organismDbXRefId = organismDbXRefId;
    }

    /* (non-Javadoc)
     * @see org.genedb.db.jpa.OrganismDbXRefI#getOrganism()
     */
    private Organism getOrganism() {
        return this.organism;
    }
    
    /* (non-Javadoc)
     * @see org.genedb.db.jpa.OrganismDbXRefI#setOrganism(org.gmod.schema.organism.OrganismI)
     */
    private void setOrganism(Organism organism) {
        this.organism = organism;
    }

    /* (non-Javadoc)
     * @see org.genedb.db.jpa.OrganismDbXRefI#getDbXRef()
     */
    private DbXRef getDbXRef() {
        return this.dbXRef;
    }
    
    /* (non-Javadoc)
     * @see org.genedb.db.jpa.OrganismDbXRefI#setDbXRef(org.gmod.schema.general.DbXRefI)
     */
    private void setDbXRef(DbXRef dbXRef) {
        this.dbXRef = dbXRef;
    }




}


