<%@ include file="/WEB-INF/jsp/topinclude.jspf"%>
<%@ taglib prefix="db" uri="db"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<format:header title="GeneDB Frequently Asked Questions" />
<format:page>
	<br/>
	<div id="col-1-1">
	<h2>Frequently Asked Questions</h2>
	<h3>I've found my favourite protein, how do I now download the fasta?</h3>
	<div class="main-light-grey-top"></div>
	<div class="main-light-grey">
	<p>
	<ol style="padding-left:20px">
	<li>From the gene page of your protein, click "ADD TO BASKET".</li>
	<li>Choose the "History" option from the "Tools" menu.</li>
	<li>Click the "Download" link for your search.</li>
	<li>Set "Output Format" to "FASTA Sequences", choose the fields required for the fasta header and select "Protein sequence" as Sequence type.</li>
	<li>Choose "Normal Page" as output destination and click "Submit Query".</li>
	</ol>
	</p>
	</div>
	<div class="main-light-grey-bot"></div>
	<h3>Can I download all proteins from my favourite species?</h3>
	<div class="main-light-grey-top"></div>
	<div class="main-light-grey">
	<p>
	<ol style="padding-left:20px">
	<li>From the species front page, select the "Gene Type" search.</li>
	<li>Select "protein-coding" as Gene Type and click "Submit".</li>
	<li>Choose the "History" option from the "Tools" menu.</li>
	<li>Click the "Download" link for your search.</li>
	<li>Set "Output Format" to "FASTA Sequences", choose the fields required for the fasta header and select "Protein sequence" as Sequence type.</li>
	<li>Choose either "Normal Page" or "Email to" as output destination and click "Submit Query". It is recommended that larger downloads should be emailed rather than output to screen.</li>
	</ol>
	</p>
	</div>
	<div class="main-light-grey-bot"></div>
	<h3>How do I retrieve selected hits from a blast-search as a fasta file?</h3>
	<div class="light-grey-top"></div>
	<div class="light-grey"></div>
	<div class="light-grey-bot"></div>
	<h3>I've searched for my favourite protein, and yesterday it was there, but now I can't find it. Where is it?</h3>
	<div class="light-grey-top"></div>
	<div class="light-grey"></div>
	<p style="padding-left:20px">There are two probable reasons for this:</p>
	<p>
	<ol style="padding-left:20px">
	<li>GeneDB is updated every 24 hours and sometimes gene models may be changed by a curator. Two gene models may be merged, a gene model may be split into two genes or it may be decided that a gene model is not real.</li>
	<li>There is a known bug in GeneDB in which gene models are not indexed for GeneDB's searches.</li>
	</ol>
	</p>
	<p style="padding-left:20px">In both cases, we'd recommend searching for gene models with similar IDs or looking at the contig the gene model was previously found to be on, using the gene page maps, to check whether the gene is genuinely absent. If not, check again in 24 hours then email <a href="mailto:genedb-help@sanger.ac.uk">the GeneDB helpdesk</a> so the status of the gene can be investigated further.
	</p>
	<div class="light-grey-bot"></div>
	<h3>Can I download the whole genome?</h3>
	<div class="light-grey-top"></div>
	<div class="light-grey"></div>
	<div class="light-grey-bot"></div>
	<h3>How can I see/access RNAseq evidence for my favourite gene?</h3>
	<div class="baby-blue-top"></div>
	<div class="baby-blue"></div>
	<div class="baby-blue-bot"></div>
	</div>
</format:page>
