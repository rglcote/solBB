package com.rcsoft.solbb.model;

import android.util.Log;

import com.adobe.dp.css.CSSLength;
import com.adobe.dp.css.CSSNumber;
import com.adobe.dp.css.SelectorRule;
import com.adobe.dp.epub.io.FileDataSource;
import com.adobe.dp.epub.io.OCFContainerWriter;
import com.adobe.dp.epub.ncx.TOCEntry;
import com.adobe.dp.epub.opf.NCXResource;
import com.adobe.dp.epub.opf.OPSResource;
import com.adobe.dp.epub.opf.Publication;
import com.adobe.dp.epub.opf.StyleResource;
import com.adobe.dp.epub.ops.OPSDocument;
import com.adobe.dp.epub.ops.SVGElement;
import com.adobe.dp.epub.ops.SVGImageElement;
import com.rcsoft.solbb.html.HTMLSanitiser;
import com.rcsoft.solbb.net.SOLNetworkDAO;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftTagTypes;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.PHPTagTypes;
import net.htmlparser.jericho.Source;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by RDCoteRi on 2017-09-14.
 */

public class EpubBuilder {

    private String author;
    private String title;
    private File cacheDir;
    private String coverImageFilePath;
    private ArrayList<ChapterEntry> chapters;

    public EpubBuilder(File cacheDir, String coverImageFilePath, ArrayList<ChapterEntry> chapters) {

        this.coverImageFilePath = coverImageFilePath;
        this.cacheDir = cacheDir;
        this.chapters = chapters;

        //boilerplate from jericho parser
        MicrosoftTagTypes.register();
        PHPTagTypes.register();
        MasonTagTypes.register();

    }

    private String writeEpub(ArrayList<ChapterEntry> chapters) {

        String fileName = "book.raw.epub";
        try {
            // create new EPUB document
            Publication epub = new Publication();

            // set up title and author
            epub.addDCMetadata("title", title);
            epub.addDCMetadata("publisher", System.getProperty("user.name"));
            epub.addDCMetadata("creator", author);
            epub.addDCMetadata("language", "en");

            // create a stylesheet
            StyleResource style = epub.createStyleResource("OPS/styles.css");
            URL stylesheetURL = getClass().getClassLoader().getResource("raw/epub/OEBPS/stylesheet.css");
            if (stylesheetURL != null) {
                style.load(new FileDataSource(new File(stylesheetURL.toURI())));
            }

            //todo
            //BitmapDrawable img = CoverPageUtils.generateCoverImage(author, title);

            //create cover page
            OPSResource cover = epub.createOPSResource("OPS/cover.xhtml");
            epub.addToSpine(cover);
            //set cover image in document
            OPSDocument coverDoc = cover.getDocument();
            com.adobe.dp.epub.ops.Element body = coverDoc.getBody();
            body.setClassName("cover");
            //update styles
            SelectorRule coverBodyRule = style.getStylesheet().getRuleForSelector(style.getStylesheet().getSimpleSelector("body",
                    "cover"), true);
            coverBodyRule.set("oeb-column-number", new CSSNumber(1));
            coverBodyRule.set("margin", new CSSLength(0, "px"));
            coverBodyRule.set("padding", new CSSLength(0, "px"));
            SVGElement svg = coverDoc.createSVGElement("svg");
            //todo
            //svg.setAttribute("viewBox", "0 0 " + img.getIntrinsicWidth() + " " + img.getIntrinsicHeight());
            svg.setClassName("cover-svg");
            body.add(svg);
            SelectorRule svgRule = style.getStylesheet().getRuleForSelector(style.getStylesheet().getSimpleSelector("svg",
                    "cover-svg"), true);
            svgRule.set("width", new CSSLength(100, "%"));
            svgRule.set("height", new CSSLength(100, "%"));
            SVGImageElement image = coverDoc.createSVGImageElement("image");
            //todo
            //image.setAttribute("width", Integer.toString(img.getIntrinsicWidth()));
            //image.setAttribute("height", Integer.toString(img.getIntrinsicHeight()));
            //create resource
            //BitmapImageResource resource = raw.epub.createBitmapImageResource("cover.jpg", "image/jpg", new FileDataSource(coverFile));
            //image.setImageResource(resource);
            svg.add(image);


            // prepare table of contents
            NCXResource toc = epub.getTOC();

            for (ChapterEntry entry : chapters) {
                createChapter(epub, toc, style, entry);
            }

            // save EPUB to an OCF container
            if (title != null && author != null) {
                fileName = author + "-" + title + ".raw.epub";
            }

            File epubFile = new File(cacheDir, fileName);
            OCFContainerWriter writer = new OCFContainerWriter(new FileOutputStream(epubFile));
            epub.serialize(writer);
            fileName = epubFile.getAbsolutePath();

        } catch (Exception e) {
            fileName = null;
            Log.e("SOL", "Error writing raw.epub file", e);
        }

        return fileName;

    }

    private void createChapter(Publication epub, NCXResource toc, StyleResource style, ChapterEntry chapterEntry) throws Exception {

        // create first chapter resource
        OPSResource opsResource = epub.createOPSResource("OPS/" + chapterEntry.getChapterFile().getName());
        epub.addToSpine(opsResource);

        opsResource.load(new FileDataSource(chapterEntry.getChapterFile()));

        // get chapter document
        OPSDocument opsDocument = opsResource.getDocument();

        // link our stylesheet
        opsDocument.addStyleResource(style);

        // add chapter to the table of contents
        TOCEntry tocEntry = toc.createTOCEntry(chapterEntry.getChapterName(), opsDocument.getRootXRef());
        toc.getRootTOCEntry().add(tocEntry);

    }


    private void writeSegment(PrintWriter out, Source source, Element element, int ndx) throws IOException {

        OutputDocument outputDocument = new OutputDocument(element);

        //remove any intra-story links
        Element h3End = source.getFirstElement("class", "end", true);
        if (h3End != null) {
            outputDocument.remove(h3End);
        }

        List<Element> conTags = source.getAllElements("class", "conTag", true);
        if (conTags != null) {
            outputDocument.remove(conTags);
        }

        List<Element> pager = source.getAllElements("class", "pager", true);
        if (pager != null) {
            outputDocument.remove(pager);
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

        //update div id if there are multiple subpages
        if (pager != null && !pager.isEmpty()) {
            Attributes divAttributes = element.getAttributes();
            Map<String, String> attributesMap = new HashMap<String, String>();
            attributesMap.put("id", "story" + ndx);
            outputDocument.replace(divAttributes, attributesMap);
        }

        out.println(HTMLSanitiser.stripInvalidMarkup(outputDocument.toString()));

        if (pager != null && !pager.isEmpty()) {
            writeSubSegment(pager.iterator().next(), out, ndx);
        }

    }

    private void writeSubSegment(Element pager, PrintWriter out, int ndx) throws IOException {

        try {

            List<Element> links = pager.getAllElements(HTMLElementName.A);
            for (Element link : links) {
                if (link.getContent().getTextExtractor().toString().trim().equals("Next")) {

                    String content = SOLNetworkDAO.getInstance().downloadStorySubChapters(SOLNetworkDAO.BASE_URL + link.getAttributeValue("href"), ndx);

                    Source source = new Source(content);
                    // Call fullSequentialParse manually as most of the source will be parsed.
                    source.fullSequentialParse();

                    //<div id="story">
                    Element div = source.getElementById("story");
                    writeSegment(out, source, div, ndx++);

                } else {
                    Log.e("SOL", "Error downloading chapter");
                }
            }

        } catch (IOException e) {
            Log.e("SOL", "Error downloading chapter", e);
        }

    }


}
