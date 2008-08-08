package org.genedb.querying.core;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.springframework.util.StringUtils;

public class LuceneIndex {

    public String getIndexName() {
		return indexName;
	}

	private static final Logger logger = Logger.getLogger(LuceneIndex.class);

    private IndexReader indexReader;

    private String indexDirectoryName;
    private String indexName;

    // Effectively disable the clause limit.
    // (Wildcard and range queries are expanded into many clauses.)
    public LuceneIndex() {
    	BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
    }

    /**
     * Open the named Lucene index from the default location.
     *
     * @param indexName The name of the index
     * @return
     * @throws IOException
     */
    private void openIndex() {
    	if ( ! (StringUtils.hasText(indexDirectoryName) && StringUtils.hasText(indexName)) ) {
    		return;
    	}
        String indexFilename = indexDirectoryName + File.separatorChar + indexName;
        logger.info(String.format("Opening Lucene index at '%s'", indexFilename));
        try {
			indexReader = IndexReader.open(indexFilename);
		} catch (IOException exp) {
			throw new RuntimeException(String.format("Failed to open lucene index '%s'", indexFilename), exp);
		}
    }

    /**
     * Perform a Lucene search
     *
     * @param ir A Lucene index, as returned by {@link #openIndex(String)}
     * @param analyzer An analyzer, used to parse the query
     * @param defaultField The name of the field to use as the default for the
     *                query
     * @param queryString The text of the query
     * @return The result of the search
     */
    public Hits search(Analyzer analyzer, String defaultField, String queryString) {
        Query query = null;
        QueryParser qp = new QueryParser(defaultField, analyzer);

        try {
            query = qp.parse(queryString);
            logger.debug("query is -> " + query.toString());

            return search(query);
        } catch (ParseException e) {
            throw new RuntimeException(String.format("Lucene failed to parse query '%s'",
                queryString), e);
        }
    }

    /**
     * Perform a Lucene search using a prebuilt Query object.
     *
     * @param ir A Lucene index, as returned by {@link #openIndex(String)}
     * @param query The query
     * @return The result of the search
     */
    public Hits search(Query query) {
        IndexSearcher searcher = new IndexSearcher(indexReader);
        logger.debug("searcher is -> " + searcher.toString());
        try {
            return searcher.search(query);
        } catch (IOException e) {
            throw new RuntimeException(String.format("I/O error during Lucene query '%s'",
                query), e);
        }
    }

	public void setIndexDirectoryName(String indexDirectoryName) {
		this.indexDirectoryName = indexDirectoryName;
		openIndex();
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
		openIndex();
	}

}
