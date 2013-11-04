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
	<div class="main-light-grey-top"></div>
	<div class="main-light-grey">
	
	</div>
	<div class="main-light-grey-bot"></div>
	<h3>I've searched for my favourite protein, and yesterday it was there, but now I can't find it. Where is it?</h3>
	<div class="main-light-grey-top"></div>
	<div class="main-light-grey">
	<p style="padding-left:20px">There are two probable reasons for this:</p><br/>
	<p>
	<ol style="padding-left:20px">
	<li>GeneDB is updated every 24 hours and sometimes gene models may be changed by a curator. Two gene models may be merged, a gene model may be split into two genes or it may be decided that a gene model is not real.</li>
	<li>There is a known bug in GeneDB in which gene models are not indexed for GeneDB's searches.</li>
	</ol>
	</p><br/>
	<p style="padding-left:20px">In both cases, we'd recommend first checking the Annotation Statistics link on the organism home page. This lists all annotation changes in the last 120 days. If this fails, we'd recommend searching for gene models with similar IDs or looking at the contig the gene model was previously found to be on, using the gene page maps, to check whether the gene is genuinely absent. If not, check again in 24 hours and then please email <a href="mailto:genedb-help@sanger.ac.uk">the GeneDB helpdesk</a> so the status of the gene can be investigated further.
	</p>
	</div>
	<div class="main-light-grey-bot"></div>
	<h3>Can I download the whole genome?</h3>
	<div class="main-light-grey-top"></div>
	<div class="main-light-grey">
        <p>
	Whole genome download is not available directly from GeneDB. However, the front page for most organisms contains an ftp link from which the full genome can usually be downloaded.
	</p>
	</div>
	<div class="main-light-grey-bot"></div>
	<h3>How can I see/access RNAseq evidence for my favourite gene?</h3>
	<div class="main-light-grey-top"></div>
	<div class="main-light-grey">
	<p>GeneDB currently only has RNAseq data available for <i>Schistosoma mansoni</i>. However, all RNAseq data sequenced at the Wellcome Trust Sanger Institute should be available via the <a href="http://www.ebi.ac.uk/ena/">European Nucelotide Archive</a>. Please contact <a href="mailto:genedb-help@sanger.ac.uk">the GeneDB helpdesk</a> for further details.</p>
	<p>Where RNAseq evidence is present for an organism, to access it from the gene page, click the link to view your region of interest in a separate Web Artemis window. From the View pulldown menu, the BAM option should list all available BAM files of RNAseq data.</p>
	</div>
	<div class="main-light-grey-bot"></div>
	</div>
</format:page>
