package com.rcsoft.solbb.net;

import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import com.rcsoft.solbb.model.ChapterEntry;
import com.rcsoft.solbb.model.EbookData;
import com.rcsoft.solbb.utils.HTMLSanitiser;
import com.rcsoft.solbb.utils.HttpUtils;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rdcoteri
 * Date: 17.10.13
 * Time: 10:47
 */
public class SOLNetworkDAO {

    //net parameters
    public static final String BASE_URL = "https://www.storiesonline.net";
    public static final String STORY_URL = BASE_URL + "/s/";
    public static final String CHROME_USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36";

    //singleton
    private static SOLNetworkDAO instance = new SOLNetworkDAO();
    private CookieManager cookieManager;
    private TextView progressView;

    private SOLNetworkDAO() {
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    public static SOLNetworkDAO getInstance() {
        return instance;
    }

    public void setProgressView(TextView progressView) {
        this.progressView = progressView;
    }

    private void publishProgress(String message) {
        progressView.append(message + '\n');
    }

    public EbookData buildEbookFromStoryId(String storyId, Uri coverImage) {

        EbookData ebookData = new EbookData();
        ebookData.setCoverImage(coverImage);
        try {
            parseStoryTOC(storyId, ebookData);
            publishProgress("TOC parsed");
            downloadChapters(ebookData);
            return ebookData;
        } catch (IOException e) {
            throw new RuntimeException("Error downloading SOL data", e);
        }

    }

    public void parseStoryTOC(String storyId, EbookData ebookData) throws IOException {

        URL url = new URL(STORY_URL + storyId);
        Source source = downloadPageContent(url);

        //<title>Lazlo Zalezac: John Carter</title>
        Element titleElement = source.getFirstElement(HTMLElementName.TITLE);
        if (titleElement != null) {
            String titleStr = titleElement.getContent().getTextExtractor().toString();
            int ndx = titleStr.indexOf(":");
            ebookData.setAuthor(titleStr.substring(0, ndx).trim());
            ebookData.setTitle(titleStr.substring(ndx + 1).trim());
        }

        System.out.println("Loading TOC");
        Element div = source.getElementById("index-list");
//                    <div class="ind-e">Part 1: Foo Fighter</div> -- optional
//                    <span class="link"><a href="/s/43294:27409">Chapter 1</a></span>
//                    <span class="link"><a href="/s/43294:27410">Chapter 2</a></span>
//                    <span class="link"><a href="/s/43294:27411">Chapter 3</a></span>

        if (div != null) {
            List<Element> elements = div.getChildElements();
            String partString = null;
            for (Element el : elements) {

                if ("ind-e".equals(el.getAttributeValue("class"))) {
                    partString = el.getTextExtractor().toString();
                }
                if ("link".equals(el.getAttributeValue("class"))) {
                    String chapterName = el.getChildElements().get(0).getTextExtractor().toString();
                    if (partString != null) {
                        chapterName = partString + " - " + chapterName;
                    }
                    URL chapterLink = new URL(SOLNetworkDAO.BASE_URL + el.getChildElements().get(0).getAttributeValue("href"));
                    ebookData.getChapters().add(new ChapterEntry(chapterName, chapterLink));
                }
            }

        } else {
            Log.e("SOL", "No TOC element defined!");
        }
    }

    private void downloadChapters(EbookData ebookData) throws IOException {

        if (!ebookData.getChapters().isEmpty()) {
            for (ChapterEntry chapterEntry : ebookData.getChapters()) {

                publishProgress("Getting " + chapterEntry.getChapterName());

                Source source = downloadPageContent(chapterEntry.getChapterURL());

                Element title = source.getFirstElement("title");
                String titleStr = "";
                if (title != null) {
                    titleStr = title.getContent().getTextExtractor().toString();
                }

                String header = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                        "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en-US\" xml:lang=\"en-US\">" +
                        "\t<head>\n" +
                        "\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                        "\t\t<title>" + titleStr + "</title>\n" +
                        "\t</head>\n" +
                        "\t<body>";

                String footer = "\t</body>\n</html>";

                StringBuilder content = new StringBuilder(header);
                content.append(parseChapter(source, true));
                content.append(footer);

                chapterEntry.setContent(content.toString());

            }
        }
    }

    private String parseChapter(Source source, boolean isRecursive) throws IOException {

        //<div id="story">
        Element div = source.getElementById("story");
        OutputDocument outputDocument = new OutputDocument(div);

        //remove any intra-story links
        Element h3End = source.getFirstElement("class", "end", true);
        if (h3End != null) {
            outputDocument.remove(h3End);
        }

        //remove any contacts
        List<Element> conTags = source.getAllElements("class", "conTag", true);
        if (conTags != null) {
            outputDocument.remove(conTags);
        }

        //remove any commends
        Element endNote = source.getFirstElement("class", "end-note", true);
        if (endNote != null) {
            outputDocument.remove(endNote);
        }

        //remove voting form
        Element form = source.getElementById("vote-form");
        if (form != null) {
            outputDocument.remove(form);
        }

        //remove any end comments
        List<Element> cComments = source.getAllElements("class", "c", true);
        if (cComments != null) {
            outputDocument.remove(cComments);
        }

        //create SBs - one for main story, one for any posisble subchapters
        StringBuilder sbMain = new StringBuilder();
        StringBuilder sbSubchapters = new StringBuilder();

        //update div id if there are multiple subpages
        List<Element> pager = source.getAllElements("class", "pager", true);
        if (pager != null) {
            outputDocument.remove(pager);

            Attributes divAttributes = div.getAttributes();
            Map<String, String> attributesMap = new HashMap<String, String>();
            attributesMap.put("id", "story" + Math.random());
            outputDocument.replace(divAttributes, attributesMap);

            //loop through all pager links elements
            //if there are subchapters, this function will be called recursively
            //but only the main call should do the looping so the others will skip
            //this part
            if (isRecursive) {
                //there should only be 1 pager element
                List<Element> links = pager.get(0).getAllElements(HTMLElementName.A);
                for (Element link : links) {
                    //always go to the "next" link
                    if (link.getContent().getTextExtractor().toString().trim().equals("Next")) {

                        publishProgress("Getting sub chapter");
                        //download the next subchapter and append content
                        Source innerSource = new Source(downloadPageContent(new URL(SOLNetworkDAO.BASE_URL + link.getAttributeValue("href"))));
                        //don't recurse
                        sbSubchapters.append(parseChapter(innerSource, false));
                    }
                }
            }
        }

        //create final content
        sbMain.append(HTMLSanitiser.stripInvalidMarkup(outputDocument.toString()));
        sbMain.append(sbSubchapters);

        return sbMain.toString();

    }

    public Boolean login(HashMap<String, String> params, String loginUrl) {

        HttpURLConnection connection = null;
        Boolean success = false;

        try {

            URL url = new URL(loginUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", SOLNetworkDAO.CHROME_USER_AGENT);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);

            String postParameters = HttpUtils.createQueryStringForParameters(params);

            connection.setFixedLengthStreamingMode(
                    postParameters.getBytes().length);

            connection.connect();
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.print(postParameters);
            out.flush();
            out.close();

            int statusCode = connection.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP) /* 302 is success */ {
                success = true;
            }
            InputStream inputStream = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            inputStream.close();
            Log.d("SOL", "login output: " + sb.toString());

        } catch (IOException e) {
            Log.e("SOL", "Couldn't log in", e);
            success = false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return success;

    }

    private Source downloadPageContent(URL url) throws IOException {

        HttpURLConnection connection = null;
        Source source = null;
        try {

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", SOLNetworkDAO.CHROME_USER_AGENT);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK /*200*/) {

                source = new Source(connection.getInputStream());
                // Call fullSequentialParse manually as most of the source will be parsed.
                source.fullSequentialParse();

            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return source;
    }


}
