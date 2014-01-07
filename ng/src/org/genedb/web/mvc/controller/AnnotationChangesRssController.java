package org.genedb.web.mvc.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Iterator;
import java.lang.StringBuffer;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import javax.servlet.http.HttpServletRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.genedb.web.mvc.model.AnnotationChangesRssContent;

@Controller
@RequestMapping(value="/rss")
public class AnnotationChangesRssController {

  private JsonParser parser = new JsonParser();
  private ClientHttpRequestFactory fac = new SimpleClientHttpRequestFactory();
  private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

  private String getJSONforOrganism(String url) throws IOException, URISyntaxException {
    BufferedReader bufferedReader = null;
    StringBuilder builder = new StringBuilder();
    ClientHttpRequest req = fac.createRequest(new URI(url), HttpMethod.GET);
    ClientHttpResponse res = req.execute();
    InputStream inputStream = res.getBody();
    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    for (String line = null; (line = bufferedReader.readLine()) != null;) {
        builder.append(line).append("\n");
    }
    return builder.toString();
  }

  private String getDateSinceFormatted(int value, int unit) {
    StringBuffer returnedStringBuf = new StringBuffer(20);
    Date now = new Date();
    Calendar c = Calendar.getInstance();
    c.setTime(now);
    c.add(unit, -1 * value);
    returnedStringBuf = dateFormatter.format(c.getTime(), returnedStringBuf, new FieldPosition(0));
    return returnedStringBuf.toString();
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(method = RequestMethod.GET , value="/{orgName}")
  public ModelAndView getRssForOrganism(
          @PathVariable(value="orgName") String orgName) throws IOException, URISyntaxException {
    // default: 120 days
    return getRssForOrganismAndTime(orgName, 120, Calendar.DAY_OF_MONTH);
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(method = RequestMethod.GET , value="/{orgName}/since/{nDays}/days")
  public ModelAndView getRssForOrganismAndDays(
          @PathVariable(value="orgName") String orgName,
          @PathVariable(value="nDays") int nDays) throws IOException, URISyntaxException {
    return getRssForOrganismAndTime(orgName, nDays, Calendar.DAY_OF_MONTH);
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(method = RequestMethod.GET , value="/{orgName}/since/{nMonths}/months")
  public ModelAndView getRssForOrganismAndMonths(
          @PathVariable(value="orgName") String orgName,
          @PathVariable(value="nMonths") int nMonths) throws IOException, URISyntaxException {
    return getRssForOrganismAndTime(orgName, nMonths, Calendar.MONTH);
  }

  private ModelAndView getRssForOrganismAndTime(String orgName, int since, int unit) throws IOException, URISyntaxException {
    ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
    HttpServletRequest req = sra.getRequest();
    List<AnnotationChangesRssContent> items = new ArrayList<AnnotationChangesRssContent>();
    String baseUrl = req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort();
    String dateSince = getDateSinceFormatted(since, unit);

    JsonElement rootElement = parser.parse(getJSONforOrganism(baseUrl
                                   + "/services/features/annotation_changes.json?organism=com%3A"
                                   + orgName + "&date="+dateSince));

    if (rootElement.isJsonArray()) {
      JsonArray jsonArray = rootElement.getAsJsonArray();
      Iterator<JsonElement> msg = jsonArray.iterator();
      while (msg.hasNext()) {
        JsonObject jo = msg.next().getAsJsonObject();
        Date lastDate;

        // get unique name
        String uniqueName = jo.get("uniqueName").getAsString();

        // get changes
        JsonArray changes = jo.get("changes").getAsJsonArray();
        Iterator<JsonElement> changeIt = changes.iterator();
        StringBuilder changeStrB = new StringBuilder();
        String dateStr = "";
        while (changeIt.hasNext()) {
          JsonObject changeO = changeIt.next().getAsJsonObject();
          try {
            String user = changeO.get("user").getAsString();
            String detail = changeO.get("detail").getAsString();
            String type = changeO.get("type").getAsString();
            changeStrB.append(user + " (" + type + ")" + ": " + detail ).append("\n");
            dateStr = changeO.get("date").getAsString();
          } catch (Exception e) {
            // skip invalid entries (of which there should be none)
            continue;
          }
        }

        // get date
        try {
          lastDate = dateFormatter.parse(dateStr);
        } catch (ParseException e) {
          lastDate = new Date();
        }

     		AnnotationChangesRssContent content  = new AnnotationChangesRssContent();
     		content.setGene(uniqueName);
        content.setUrl(baseUrl+"/gene/"+uniqueName);
        content.setSummary(changeStrB.toString());
     		content.setDate(lastDate);
     		items.add(content);
      }
    }

    Collections.sort(items);

    ModelAndView mav = new ModelAndView();
		mav.setViewName("rssViewer");
		mav.addObject("feedContent", items);
    mav.addObject("orgName", orgName);
    mav.addObject("dateSince", dateSince);

		return mav;
  }
}