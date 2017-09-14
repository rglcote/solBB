package com.rcsoft.solbb.net;

import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import com.rcsoft.solbb.model.ChapterEntry;
import com.rcsoft.solbb.model.EbookData;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

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

    public EbookData buildEbookFromStoryId(String storyId, Uri coverImage) {

        EbookData ebookData = new EbookData();

        parseChapters(storyId, ebookData);
        publishProgress("TOC parsed");
        downloadChapters(ebookData);
        return ebookData;

        //todo cleancup
        /*
getExternalStorage() + "/Android/data/<package_name>/cache/"
replacing by your app packaged, e.g. com. Orabig.myFirstApp that special folder is automatically deleted from the system if the user uninstall the application, keeping the system free from temporary files.
edit:Please note that my manifest does not include theyou have to!
edit: also, if you creating temporary media files (PNG for example) is good practice to create an empty file named .nomedia on that folder. That way you avoid the Media Scanner scanning it and showing it on the gallery.
last edit:and before creating files you must create the folder by calling mkdirs() on the File object.
         */

    }


    public void parseChapters(String storyId, EbookData ebookData) {

        HttpURLConnection connection = null;

        try {

            URL url = new URL(STORY_URL + storyId);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", SOLNetworkDAO.CHROME_USER_AGENT);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK /*200*/) {

                Source source = new Source(connection.getInputStream());
                // Call fullSequentialParse manually as most of the source will be parsed.
                source.fullSequentialParse();

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

        } catch (IOException e) {
            Log.e("SOL", "Error parsing TOC", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    private void downloadChapters(EbookData ebookData) {

        if (!ebookData.getChapters().isEmpty()) {
            for (ChapterEntry chapter : ebookData.getChapters()) {
                downloadChapter(chapter);
            }
        }
    }

    private void downloadChapter(ChapterEntry chapterEntry) {

        HttpURLConnection connection = null;

        try {

            connection = (HttpURLConnection) chapterEntry.getChapterURL().openConnection();
            connection.setRequestProperty("User-Agent", SOLNetworkDAO.CHROME_USER_AGENT);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK /*200*/) {

                //reset index in case of subchapters
                int ndx = 1;

                File chapterFile = File.createTempFile("sol", ".html", cacheDir);
                chapterEntry.setChapterFile(chapterFile);
                chapterFile.deleteOnExit();

                publishProgress("Getting " + chapterEntry.getChapterName());
                PrintWriter out = new PrintWriter(new FileWriter(chapterFile));

                Source source = new Source(connection.getInputStream());
                // Call fullSequentialParse manually as most of the source will be parsed.
                source.fullSequentialParse();

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

                out.println(header);

                //<div id="story">
                Element div = source.getElementById("story");

                //update index
                ndx++;

                publishProgress(" p" + ndx);
                writeSegment(out, source, div, ndx);

                out.println("\t</body>\n</html>");

                out.close();

            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    private void publishProgress(String message) {
        progressView.append(message + '\n');
    }

    private void writeSubSegment(Element pager, PrintWriter out, int ndx) throws IOException {

        AndroidHttpClient httpclient = AndroidHttpClient.newInstance(SOLNetworkDAO.CHROME_USER_AGENT);
        try {

            List<Element> links = pager.getAllElements(HTMLElementName.A);
            for (Element link : links) {
                if (link.getContent().getTextExtractor().toString().trim().equals("Next")) {


                    HttpGet httpGet = new HttpGet(SOLNetworkDAO.BASE_URL + link.getAttributeValue("href"));
                    HttpResponse response = httpclient.execute(httpGet, context);
                    HttpEntity entity = response.getEntity();

                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK /*200*/) {
                        if (entity != null) {

                            publishProgress("Getting sub chapter " + ndx);

                            Source source = new Source(entity.getContent());
                            // Call fullSequentialParse manually as most of the source will be parsed.
                            source.fullSequentialParse();

                            //<div id="story">
                            Element div = source.getElementById("story");
                            writeSegment(out, source, div, ndx++);

                        } else {
                            exception = new RuntimeException("Could not load sub page: " + (ndx - 1));
                            Log.e("SOL", "Error downloading chapter");
                        }
                    }

                }
            }

        } catch (ClientProtocolException e) {
            exception = e;
            Log.e("SOL", "Error downloading chapter", e);
        } catch (IOException e) {
            exception = e;
            Log.e("SOL", "Error downloading chapter", e);
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }

    }

    public String downloadStorySubChapters(String url, int ndx) {
        publishProgress("Getting sub chapter " + ndx);

        return null;
    }


    public Boolean login(HashMap<String, String> params, String loginUrl) {

        return true;

//        HttpURLConnection connection = null;
//        Boolean success = false;
//
//        try {
//
//            URL url = new URL(loginUrl);
//            connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestProperty("User-Agent", SOLNetworkDAO.CHROME_USER_AGENT);
//            connection.setRequestMethod("POST");
//            connection.setDoInput(true);
//
//            String postParameters = NetUtils.createQueryStringForParameters(params);
//
//            connection.setFixedLengthStreamingMode(
//                    postParameters.getBytes().length);
//
//            connection.connect();
//            PrintWriter out = new PrintWriter(connection.getOutputStream());
//            out.print(postParameters);
//            out.flush();
//            out.close();
//
//            int statusCode = connection.getResponseCode();
//
//            if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP) /* 302 is success */{
//                success = true;
//            }
//            InputStream inputStream = connection.getInputStream();
//            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
//            StringBuilder sb = new StringBuilder();
//            String line = "";
//            while ((line = rd.readLine()) != null) {
//                sb.append(line);
//            }
//            inputStream.close();
//            Log.d("SOL", "login output: " + sb.toString());
//
//
//        } catch (IOException e) {
//            Log.e("SOL", "Couldn't log in", e);
//            success = false;
//        } finally {
//            if (connection != null) {
//                connection.disconnect();
//            }
//        }
//
//        return success;

    }

    public void setProgressView(TextView progressView) {
        this.progressView = progressView;
    }
}
