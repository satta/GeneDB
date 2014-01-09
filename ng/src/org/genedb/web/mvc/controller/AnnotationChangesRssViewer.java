package org.genedb.web.mvc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;
import org.genedb.web.mvc.model.AnnotationChangesRssContent;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.rss.Item;

public class AnnotationChangesRssViewer extends AbstractRssFeedView {

	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel feed,
		HttpServletRequest request) {
    String orgName = (String) model.get("orgName");
    String dateSince = (String) model.get("dateSince");

		feed.setTitle("GeneDB annotation activity for " + orgName);
		feed.setDescription("Annotation activity for " + orgName + " since " + dateSince + ".");
		feed.setLink("http://www.genedb.org/Homepage/" + orgName);

		super.buildFeedMetadata(model, feed, request);
	}

	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model,
		HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		@SuppressWarnings("unchecked")
		List<AnnotationChangesRssContent> listContent = (List<AnnotationChangesRssContent>) model.get("feedContent");
		List<Item> items = new ArrayList<Item>(listContent.size());

		for(AnnotationChangesRssContent tempContent : listContent ){

			Item item = new Item();

			Content content = new Content();
			content.setValue(tempContent.getSummary());
			item.setContent(content);

			item.setTitle(tempContent.getGene());
			item.setLink(tempContent.getUrl());
			item.setPubDate(tempContent.getDate());

			items.add(item);
		}

		return items;
	}

}