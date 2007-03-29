package org.genedb.web.mvc.controller.cgview;

import org.genedb.web.mvc.controller.BrowseCategory;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.ualberta.stothard.cgview.Cgview;
import ca.ualberta.stothard.cgview.LabelBounds;

/**
 * 
 *
 * @author Paul Stothard
 * @author Adrian Tivey
 */
public class CircularGenomeFormController extends SimpleFormController implements InitializingBean {
    
    private List<String> digestNames;
    
    private ExecutionService restrictService;
    
    private ExecutionService digestService;
    
    private CachedFileFactory cachedFileFactory;

    private EmbossDigestParser embossDigestParser;

    private Map<String, String> orgs = new HashMap<String, String>();
    
    public CircularGenomeFormController() {
        String ROOT = "/nfs/team81/art/circ_genome_data/";
        orgs.put("S. typhi", ROOT+"styphi/chr1/St.art");
        orgs.put("S. pyogenes", ROOT+"spyogenes/curated/SP.embl");
        orgs.put("C. jejuni", ROOT+"cjejuni/curated/chr1/Cj.embl");
    }


    /**
     * Custom handler for homepage
     * @param request current HTTP request
     * @param response current HTTP response
     * @return a ModelAndView to render the response
     */
    @SuppressWarnings("unchecked")
    @Override
    public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, 
            Object command, BindException errors) {

        CircularGenomeCommandBean cgcb = (CircularGenomeCommandBean) command;
        //List<String> answers = new ArrayList<String>();
        //    if (WebUtils.extractTaxonOrOrganism(request, false, true, answers)) {
        //        if (answers.size() > 0) {
        //            Taxon taxon = TaxonUtils.getTaxonFromList(answers, 0);

        CgviewFromGeneDBFactory factory = new CgviewFromGeneDBFactory();

        try {
            Map settings = new HashMap();
            
            String input = orgs.get(cgcb.getTaxon());

            // Run restrict over organism
            String embossDir = "/software/EMBOSS-4.0.0/";
            File output = File.createTempFile("circular_genome", ".txt");
            String[] args = {embossDir+"/bin/restrict", input, "-auto", "-limit", "y", "-enzymes", "'"+cgcb.getEnzymeName()+"'", "-out", output.getCanonicalPath() };
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            System.err.print("**");
            try {
                InputStream is = p.getInputStream();
                int inchar;
                while ((inchar = is.read())!=-1) {
                    char c = (char) inchar;
                    System.err.print(c);
                }
                System.err.println("**");
                p.waitFor();
                System.err.println("Process exited with '"+p.exitValue()+"'");
            } catch (InterruptedException exp) {
                // TODO Report error
                exp.printStackTrace();
                return null;
            }
            System.err.println("Written output to '"+output.getCanonicalPath()+"' but not using it!");
            
//            Cgview cgview = factory.createCgviewFromEmbossReport("/nfs/team81/art/circular-restrict.txt");            
            //Cgview cgview = factory.createCgviewFromEmbossReport(output.getCanonicalPath());
            Cgview cgview = factory.createCgviewFromEmbossReport("/nfs/team81/art/circ_genome_data/output1.txt");
//            System.err.println("Got a cgview");
//          cgview.setDesiredZoomCenter(centerBaseValue.intValue());
//          cgview.setDesiredZoom(zoomValue.doubleValue());
            CachedFile pngFile = cachedFileFactory.getCachedFile("organism-"+cgcb.getTaxon()+":"+"enzyme-"+cgcb.getEnzymeName());
            
            writeToPNGFile(cgview, pngFile.getFile(), false);
            
            String browserPath = pngFile.getBrowserPath(request);
            
            String imageMap = addImageMap(browserPath, cgview.getWidth(), 
                    cgview.getHeight(), cgview.getLabelBounds(), true);
            
            imageMap = addTempTable(imageMap, cgview.getLabelBounds());
            
            settings.put("map", imageMap);

            return new ModelAndView("graphics/circularGenome", "settings", settings);
        } catch (IOException exp) {
            exp.printStackTrace();
            // FIXME - Need msg before returning to form or homepage
        }

//      }
//      }
        return new ModelAndView("homepages/frontPage");
    }


    /**
     * Writes a Cgview object to a PNG file.
     *
     * @param cgview         the Cgview object.
     * @param filename       the file to create.
     * @param keepLastLabels whether or not to use labels generated by a previous call to one of the Cgview objects
     *                       draw() or drawZoomed() methods.
     * @throws IOException
     */
    public static void writeToPNGFile(Cgview cgview, File file, boolean keepLastLabels) throws IOException {

        BufferedImage buffImage = new BufferedImage(cgview.getWidth(), cgview.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = buffImage.createGraphics();
        try {
            if (cgview.getDesiredZoom() > 1.0d) {
                cgview.drawZoomed(graphics2D, cgview.getDesiredZoom(), cgview.getDesiredZoomCenter(), keepLastLabels);
            } else {
                cgview.draw(graphics2D, keepLastLabels);
            }
            System.err.println("Writing picture to " + file.getAbsolutePath());
            ImageIO.write(buffImage, "PNG", file);
        } finally {
            graphics2D.dispose();
        }
    }


    /**
     * Adds an image with an image map to this CgviewHTMLDocument, to implement mouseovers and hyperlinks associated
     * with Cgview Feature objects and FeatureRange objects. Image maps are used for PNG and JPG maps. SVG maps contain
     * the mouseover and hyperlink information internally.
     *
     * @param imageFile   the image URL that the image map refers to.
     * @param width       the width of the image.
     * @param height      the height of the image.
     * @param labelBounds an ArrayList of LabelBounds objects, obtained from a previously drawn Cgview object using the
     *                    {@link Cgview#getLabelBounds()} method.
     * @param useOverlib whether or not to use the overlib.js JavaScript library for PNG and JPG image maps.
     */
    public String addImageMap(String imageFile, int width, int height, List labelBounds, boolean useOverlib) {
        StringBuilder ret = new StringBuilder();

        ret.append("<img style=\"border:0\" src=\"" + StringEscapeUtils.escapeHtml(imageFile) + "\" width=\"" + Integer.toString(width) + "\" height=\"" + Integer.toString(height) + "\" usemap=\"#cgviewmap\" />" + '\n');
        ret.append("<map id=\"cgviewmap\" name=\"cgviewmap\">" + '\n');

        //add areas
        Iterator i;
        i = labelBounds.iterator();
        while (i.hasNext()) {

            LabelBounds currentLabelBounds = (LabelBounds) i.next();
            Rectangle2D bounds = currentLabelBounds.getBounds();
            if ((currentLabelBounds.getUse() == true) && ((currentLabelBounds.getMouseover() != null) || (currentLabelBounds.getHyperlink() != null))) {

                ret.append("<area shape=\"rect\" coords=\"" + Integer.toString((int) Math.floor(bounds.getX() + 0.5d)) + "," + Integer.toString((int) Math.floor(bounds.getY() + 0.5d)) + "," + Integer.toString((int) Math.floor(bounds.getX() + 0.5d) + (int) Math.floor(bounds.getWidth() + 0.5d)) + "," + Integer.toString((int) Math.floor(bounds.getY() + 0.5d) + (int) Math.floor(bounds.getHeight() + 0.5d)) + "\" ");

                if (currentLabelBounds.getHyperlink() != null) {
                    ret.append("href=\"" + currentLabelBounds.getHyperlink() + "\" ");
                }

//                if ((currentLabelBounds.getMouseover() != null) && (!(currentLabelBounds.getMouseover().matches("\\S*")))) {
                if (currentLabelBounds.getMouseover() != null) {
                    
//                    if (useOverlib) {
//                        ret.append("onmouseover=\"return showMenu(3,5);\" ");
////                        ret.append("onmouseover=\"return overlib('" + currentLabelBounds.getMouseover() + "');\" ");
//                        //ret.append("onmouseover=\"return overlib('" + StringEscapeUtils.escapeJavaScript(currentLabelBounds.getMouseover()) + "');\" ");
//                        ret.append("onmouseout=\"return nd();\" ");
//                    } else {
//                        //ret.append("onmouseover=\"self.status='" + StringEscapeUtils.escapeJavaScript(currentLabelBounds.getMouseover()) + "'; return true;\" ");
//                        //ret.append("onmouseout=\"self.status=' '; return true;\" ");
//                        ret.append("onmouseover=\""+currentLabelBounds.getMouseover() + "; return true;\" ");
//                    }
                }
                ret.append("/>" + '\n');
            }

        }
        ret.append("</map>" + '\n');
        return ret.toString();
    }

    @SuppressWarnings("unchecked")
	public String addTempTable(String in, List labelBounds) {
    	
    	Collections.sort(labelBounds, new Comparator<LabelBounds>() {
    		public int compare(LabelBounds lb1, LabelBounds lb2) {
                String label1 = lb1.getLabel();
                int colon = label1.indexOf(":");
                if (colon == -1) {
                	return -1;
                }
                String count1 = label1.substring(0, colon);
                
                String label2 = lb2.getLabel();
                if (label2 == null) {
                	return -1;
                }
                colon = label2.indexOf(":");
                if (colon == -1) {
                	return -1;
                }
                String count2 = label2.substring(0, colon);
                
                return count1.compareTo(count2);
    		}
    	});
    	
        StringBuilder ret = new StringBuilder(in);
        ret.append("<table border=\"1\">\n");
        ret.append("<tr><th>Fragment</th><th>Start</th><th>End</th><th>Length</th><th>EMBL</th><th>Artemis</th><th>Table</th></tr>");
                
        //add areas
        Iterator i;
        i = labelBounds.iterator();
        while (i.hasNext()) {
            LabelBounds clb = (LabelBounds) i.next();
            String label = clb.getLabel();

            System.err.println(label);
            int colon = label.indexOf(":");
            if (colon != -1) {
                int dash = label.indexOf("->");
                int lbr = label.indexOf("(");
                int bp = label.indexOf(" bp");
                String count = label.substring(0, colon);
                String start = label.substring(colon+1, dash);
                String end = label.substring(dash+2, lbr);
                String length = label.substring(lbr+1, bp);
                ret.append("<tr><td>");
                ret.append(count);
                ret.append("</td><td>");
                ret.append(start);
                ret.append("</td><td>");
                ret.append(end);
                ret.append("</td><td>");
                ret.append(length);
                ret.append("</td><td>");
                ret.append("<a href=\"FlatFileReport?outputFormat=EMBL&organism=wibble&min="+start+"&max="+end+"\">Link</a></td><td>");
                ret.append("<a href=\"FlatFileReport?outputFormat=Artemis&organism=wibble&min="+start+"&max="+end+"\">Link</a></td><td>");
                ret.append("<a href=\"FlatFileReport?outputFormat=Table&organism=wibble&min="+start+"&max="+end+"\">Link</a></td>");
                ret.append("</tr>\n");
            }
        }
        ret.append("</table>" + '\n');
        return ret.toString();
    }
    

    @Override
    protected Map referenceData(HttpServletRequest req) throws Exception {
        Map<String, Collection> ret = new HashMap<String, Collection>();
        ret.put("digestNames", digestNames);
        ret.put("organisms", orgs.keySet());
        return ret;
    }


    @Required
    public void setDigestService(ExecutionService digestService) {
        this.digestService = digestService;
    }

    @Required
    public void setRestrictService(ExecutionService restrictService) {
        this.restrictService = restrictService;
    }

    public void afterPropertiesSet() throws Exception {
        digestNames = embossDigestParser.getDigests();
    }


    public void setEmbossDigestParser(EmbossDigestParser embossDigestParser) {
        this.embossDigestParser = embossDigestParser;
    }


    public void setCachedFileFactory(CachedFileFactory cachedFileFactory) {
        this.cachedFileFactory = cachedFileFactory;
    }

    
    
    
}