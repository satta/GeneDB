package org.gmod.schema.phylogeny;
// Generated Aug 31, 2006 4:02:18 PM by Hibernate Tools 3.2.0.beta7


import org.gmod.schema.cv.CvTerm;
import org.gmod.schema.sequence.Feature;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Phylonode generated by hbm2java
 */
@Entity
@Table(name="phylonode", uniqueConstraints = { @UniqueConstraint( columnNames = { "phylotree_id", "left_idx" } ), @UniqueConstraint( columnNames = { "phylotree_id", "right_idx" } ) })
public class Phylonode  implements java.io.Serializable {

    // Fields    

     private int phylonodeId;
     private Phylotree phylotree;
     private Phylonode phylonode;
     private CvTerm cvTerm;
     private Feature feature;
     private int leftIdx;
     private int rightIdx;
     private String label;
     private Double distance;
     private Set<PhylonodeRelationship> phylonodeRelationshipsForObjectId = new HashSet<PhylonodeRelationship>(0);
     private Set<PhylonodeOrganism> phylonodeOrganisms = new HashSet<PhylonodeOrganism>(0);
     private Set<PhylonodePub> phylonodePubs = new HashSet<PhylonodePub>(0);
     private Set<Phylonode> phylonodes = new HashSet<Phylonode>(0);
     private Set<PhylonodeRelationship> phylonodeRelationshipsForSubjectId = new HashSet<PhylonodeRelationship>(0);
     private Set<PhylonodeDbXRef> phylonodeDbXRefs = new HashSet<PhylonodeDbXRef>(0);
     private Set<PhylonodeProp> phylonodeProps = new HashSet<PhylonodeProp>(0);

     // Constructors

    /** default constructor */
    public Phylonode() {
    }

	/** minimal constructor */
    public Phylonode(int phylonodeId, Phylotree phylotree, int leftIdx, int rightIdx) {
        this.phylonodeId = phylonodeId;
        this.phylotree = phylotree;
        this.leftIdx = leftIdx;
        this.rightIdx = rightIdx;
    }
    /** full constructor */
    public Phylonode(int phylonodeId, Phylotree phylotree, Phylonode phylonode, CvTerm cvTerm, Feature feature, int leftIdx, int rightIdx, String label, Double distance, Set<PhylonodeRelationship> phylonodeRelationshipsForObjectId, Set<PhylonodeOrganism> phylonodeOrganisms, Set<PhylonodePub> phylonodePubs, Set<Phylonode> phylonodes, Set<PhylonodeRelationship> phylonodeRelationshipsForSubjectId, Set<PhylonodeDbXRef> phylonodeDbXRefs, Set<PhylonodeProp> phylonodeProps) {
       this.phylonodeId = phylonodeId;
       this.phylotree = phylotree;
       this.phylonode = phylonode;
       this.cvTerm = cvTerm;
       this.feature = feature;
       this.leftIdx = leftIdx;
       this.rightIdx = rightIdx;
       this.label = label;
       this.distance = distance;
       this.phylonodeRelationshipsForObjectId = phylonodeRelationshipsForObjectId;
       this.phylonodeOrganisms = phylonodeOrganisms;
       this.phylonodePubs = phylonodePubs;
       this.phylonodes = phylonodes;
       this.phylonodeRelationshipsForSubjectId = phylonodeRelationshipsForSubjectId;
       this.phylonodeDbXRefs = phylonodeDbXRefs;
       this.phylonodeProps = phylonodeProps;
    }
   
    // Property accessors
     @Id
    
    @Column(name="phylonode_id", unique=true, nullable=false, insertable=true, updatable=true)
    public int getPhylonodeId() {
        return this.phylonodeId;
    }
    
    public void setPhylonodeId(int phylonodeId) {
        this.phylonodeId = phylonodeId;
    }
    
@ManyToOne(cascade={}, fetch=FetchType.LAZY)
    @JoinColumn(name="phylotree_id", unique=false, nullable=false, insertable=true, updatable=true)
    public Phylotree getPhylotree() {
        return this.phylotree;
    }
    
    public void setPhylotree(Phylotree phylotree) {
        this.phylotree = phylotree;
    }
    
@ManyToOne(cascade={}, fetch=FetchType.LAZY)
    @JoinColumn(name="parent_phylonode_id", unique=false, nullable=true, insertable=true, updatable=true)
    public Phylonode getPhylonode() {
        return this.phylonode;
    }
    
    public void setPhylonode(Phylonode phylonode) {
        this.phylonode = phylonode;
    }
@ManyToOne(cascade={},
        fetch=FetchType.LAZY)
    
    @JoinColumn(name="type_id", unique=false, nullable=true, insertable=true, updatable=true)
    public CvTerm getCvTerm() {
        return this.cvTerm;
    }
    
    public void setCvTerm(CvTerm cvTerm) {
        this.cvTerm = cvTerm;
    }
@ManyToOne(cascade={},
        fetch=FetchType.LAZY)
    
    @JoinColumn(name="feature_id", unique=false, nullable=true, insertable=true, updatable=true)
    public Feature getFeature() {
        return this.feature;
    }
    
    public void setFeature(Feature feature) {
        this.feature = feature;
    }
    
    @Column(name="left_idx", unique=false, nullable=false, insertable=true, updatable=true)
    public int getLeftIdx() {
        return this.leftIdx;
    }
    
    public void setLeftIdx(int leftIdx) {
        this.leftIdx = leftIdx;
    }
    
    @Column(name="right_idx", unique=false, nullable=false, insertable=true, updatable=true)
    public int getRightIdx() {
        return this.rightIdx;
    }
    
    public void setRightIdx(int rightIdx) {
        this.rightIdx = rightIdx;
    }
    
    @Column(name="label", unique=false, nullable=true, insertable=true, updatable=true)
    public String getLabel() {
        return this.label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    @Column(name="distance", unique=false, nullable=true, insertable=true, updatable=true, precision=17, scale=17)
    public Double getDistance() {
        return this.distance;
    }
    
    public void setDistance(Double distance) {
        this.distance = distance;
    }
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy="phylonodeByObjectId")
    public Collection<PhylonodeRelationship> getPhylonodeRelationshipsForObjectId() {
        return this.phylonodeRelationshipsForObjectId;
    }
    
    public void setPhylonodeRelationshipsForObjectId(Set<PhylonodeRelationship> phylonodeRelationshipsForObjectId) {
        this.phylonodeRelationshipsForObjectId = phylonodeRelationshipsForObjectId;
    }
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy="phylonode")
    public Collection<PhylonodeOrganism> getPhylonodeOrganisms() {
        return this.phylonodeOrganisms;
    }
    
    public void setPhylonodeOrganisms(Set<PhylonodeOrganism> phylonodeOrganisms) {
        this.phylonodeOrganisms = phylonodeOrganisms;
    }
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy="phylonode")
    public Collection<PhylonodePub> getPhylonodePubs() {
        return this.phylonodePubs;
    }
    
    public void setPhylonodePubs(Set<PhylonodePub> phylonodePubs) {
        this.phylonodePubs = phylonodePubs;
    }
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy="phylonode")
    public Collection<Phylonode> getPhylonodes() {
        return this.phylonodes;
    }
    
    public void setPhylonodes(Set<Phylonode> phylonodes) {
        this.phylonodes = phylonodes;
    }
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy="phylonodeBySubjectId")
    public Collection<PhylonodeRelationship> getPhylonodeRelationshipsForSubjectId() {
        return this.phylonodeRelationshipsForSubjectId;
    }
    
    public void setPhylonodeRelationshipsForSubjectId(Set<PhylonodeRelationship> phylonodeRelationshipsForSubjectId) {
        this.phylonodeRelationshipsForSubjectId = phylonodeRelationshipsForSubjectId;
    }
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy="phylonode")
    public Collection<PhylonodeDbXRef> getPhylonodeDbXRefs() {
        return this.phylonodeDbXRefs;
    }
    
    public void setPhylonodeDbXRefs(Set<PhylonodeDbXRef> phylonodeDbXRefs) {
        this.phylonodeDbXRefs = phylonodeDbXRefs;
    }
    @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy="phylonode")
    public Collection<PhylonodeProp> getPhylonodeProps() {
        return this.phylonodeProps;
    }
    
    public void setPhylonodeProps(Set<PhylonodeProp> phylonodeProps) {
        this.phylonodeProps = phylonodeProps;
    }




}


