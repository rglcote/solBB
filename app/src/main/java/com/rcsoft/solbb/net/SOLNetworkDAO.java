package com.rcsoft.solbb.net;

import android.net.Uri;
import android.util.Log;

import com.rcsoft.solbb.BookBuildActivity;
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
    public static final String HTTP_BASE_URL = "http://storiesonline.net";
    public static final String HTTPS_BASE_URL = "https://storiesonline.net";
    public static final String STORY_URL = HTTP_BASE_URL + "/s/";
    public static final String LOGIN_URL = HTTPS_BASE_URL + "/sol-secure/login.php";
    public static final String CHROME_USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36";

    //singleton
    private static SOLNetworkDAO instance = new SOLNetworkDAO();
    private CookieManager cookieManager;
    private BookBuildActivity.RetrieveSOLContentTask caller;

    private SOLNetworkDAO() {
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    public static SOLNetworkDAO getInstance() {
        return instance;
    }

    public void setCaller(BookBuildActivity.RetrieveSOLContentTask caller) {
        this.caller = caller;
    }

    private void publishProgress(String message) {
        Log.d("SOL", message);
        caller.doProgress(message + '\n');
    }

    public EbookData buildEbookFromStoryId(String storyId, Uri coverImage) {

        EbookData ebookData = new EbookData();
        ebookData.setCoverImage(coverImage);
        try {
            parseStoryTOC(storyId, ebookData);
            publishProgress("TOC parsed");
            downloadChapters(ebookData);
            return ebookData;
        } catch (Exception e) {
            Log.e("SOL", "Error downloading SOL data", e);
            throw new RuntimeException("Error downloading SOL data", e);
        }

    }

    private void parseStoryTOC(String storyId, EbookData ebookData) throws IOException {

        URL url = new URL(STORY_URL + storyId);
        Source source = downloadPageContent(url);
        if (source == null) {
            throw new RuntimeException("Invalid URL: " + url.toString());
        }

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
                    URL chapterLink = new URL(HTTP_BASE_URL + el.getChildElements().get(0).getAttributeValue("href"));
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
                if (source == null) {
                    throw new RuntimeException("Invalid URL: " + chapterEntry.getChapterURL().toString());
                }

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

        //<article>
        Element story = source.getFirstElement("article");
        if (story == null) {
            throw new RuntimeException("Could not find main story element");
        }
        OutputDocument outputDocument = new OutputDocument(story);

        //if we're dealing with calls because of a pager - isRecursive == false
        //need to strip the title in a <h2> block
        if (!isRecursive) {

            //<h2>Chapter 5: Emerging</h2>
            //<div class="date">Posted: September 23, 2017 - 09:53:01 pm</div>
            Element h2Title = source.getFirstElement("h2");
            if (h2Title != null) {
                outputDocument.remove(h2Title);
            }
            Element dateDiv = source.getFirstElement("class", "date", true);
            if (dateDiv != null) {
                outputDocument.remove(dateDiv);
            }
        }

        //remove any continues/continued spans
        List<Element> contTags = source.getAllElements("class", "conTag", true);
        if (contTags != null) {
            outputDocument.remove(contTags);
        }

        //remove any notes
        List<Element> endNotes = source.getAllElements("class", "end-note", true);
        if (endNotes != null) {
            outputDocument.remove(endNotes);
        }

        //remove voting form
        Element form = source.getElementById("vote-form");
        if (form != null) {
            outputDocument.remove(form);
        }

        //remove any intra-story links
        List<Element> hEnds = source.getAllElements("class", "end", true);
        if (hEnds != null) {
            outputDocument.remove(hEnds);
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

            Attributes divAttributes = story.getAttributes();
            Map<String, String> attributesMap = new HashMap<>();
            attributesMap.put("id", "story" + Math.random());
            outputDocument.replace(divAttributes, attributesMap);

            //loop through all pager links elements
            //if there are subchapters, this function will be called recursively
            //but only the main call should do the looping so the others will skip
            //this part
            if (isRecursive && !pager.isEmpty()) {
                //there should only be 1 pager element
                List<Element> links = pager.get(0).getAllElements(HTMLElementName.A);
                for (Element link : links) {
                    //always go to the "next" link
                    if (link.getContent().getTextExtractor().toString().trim().equals("Next")) {

                        publishProgress("Getting sub chapter");
                        //download the next subchapter and append content
                        URL innerURL = new URL(HTTP_BASE_URL + link.getAttributeValue("href"));
                        Source innerSource = downloadPageContent(innerURL);
                        if (innerSource == null) {
                            throw new RuntimeException("Invalid URL: " + innerURL.toString());
                        }
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

    public Boolean login(HashMap<String, String> params) {

        HttpURLConnection connection = null;
        Boolean success = false;

        try {

            URL url = new URL(LOGIN_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", CHROME_USER_AGENT);
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
            String line;
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
            connection.setRequestProperty("User-Agent", CHROME_USER_AGENT);

            int statusCode = connection.getResponseCode();
            Log.d("SOL", "Status code: " + statusCode);

            if (statusCode == HttpURLConnection.HTTP_OK /*200*/) {

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
