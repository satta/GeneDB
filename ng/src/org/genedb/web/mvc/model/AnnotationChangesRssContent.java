package org.genedb.web.mvc.model;

import java.lang.Comparable;
import java.util.Date;

public class AnnotationChangesRssContent implements Comparable<AnnotationChangesRssContent> {

	String gene;
	String url;
	String summary;
	Date date;

  public String getGene() {
    return this.gene;
  }

  public void setGene(String gene) {
    this.gene = gene;
  }

  public String getSummary() {
    return this.summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public Date getDate() {
    return this.date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public int compareTo(AnnotationChangesRssContent c) {
    return -1 * getDate().compareTo(c.getDate());
  }

}