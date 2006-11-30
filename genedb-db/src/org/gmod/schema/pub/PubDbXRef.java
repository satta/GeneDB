package org.gmod.schema.pub;

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
@Table(name="pub_dbxref")
public class PubDbXRef implements Serializable {

    // Fields    
     @Id
    
    @Column(name="pub_dbxref_id", unique=false, nullable=false, insertable=true, updatable=true)
     private int pubDbXRefId;
     
     @ManyToOne(cascade={},fetch=FetchType.LAZY)
         
         @JoinColumn(name="dbxref_id", unique=false, nullable=false, insertable=true, updatable=true)
     private DbXRef dbXRef;
     
     @ManyToOne(cascade={},fetch=FetchType.LAZY)
         
         @JoinColumn(name="pub_id", unique=false, nullable=false, insertable=true, updatable=true)
     private Pub pub;
     
     @Column(name="is_current", unique=false, nullable=false, insertable=true, updatable=true)
     private boolean current;

     // Constructors

    /** default constructor */
    public PubDbXRef() {
    }

    /** full constructor */
    public PubDbXRef(Pub pub, DbXRef dbXRef, boolean current) {
       this.dbXRef = dbXRef;
       this.pub = pub;
       this.current = current;
    }
    
   
    // Property accessors

    /* (non-Javadoc)
     * @see org.genedb.db.jpa.PubDbXRefI#getPubDbXRefId()
     */
    private int getPubDbXRefId() {
        return this.pubDbXRefId;
    }
    
    /* (non-Javadoc)
     * @see org.genedb.db.jpa.PubDbXRefI#setPubDbXRefId(int)
     */
    private void setPubDbXRefId(int pubDbXRefId) {
        this.pubDbXRefId = pubDbXRefId;
    }

    /* (non-Javadoc)
     * @see org.genedb.db.jpa.PubDbXRefI#getDbXRef()
     */
    public DbXRef getDbXRef() {
        return this.dbXRef;
    }
    
    /* (non-Javadoc)
     * @see org.genedb.db.jpa.PubDbXRefI#setDbXRef(org.gmod.schema.general.DbXRefI)
     */
    public void setDbXRef(DbXRef dbXRef) {
        this.dbXRef = dbXRef;
    }

    /* (non-Javadoc)
     * @see org.genedb.db.jpa.PubDbXRefI#getPub()
     */
    public Pub getPub() {
        return this.pub;
    }
    
    /* (non-Javadoc)
     * @see org.genedb.db.jpa.PubDbXRefI#setPub(org.gmod.schema.pub.PubI)
     */
    public void setPub(Pub pub) {
        this.pub = pub;
    }
    

    /* (non-Javadoc)
     * @see org.genedb.db.jpa.PubDbXRefI#isCurrent()
     */
    public boolean isCurrent() {
        return this.current;
    }
    
    /* (non-Javadoc)
     * @see org.genedb.db.jpa.PubDbXRefI#setCurrent(boolean)
     */
    public void setCurrent(boolean current) {
        this.current = current;
    }




}


