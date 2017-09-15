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

import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftTagTypes;
import net.htmlparser.jericho.PHPTagTypes;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by RDCoteRi on 2017-09-14.
 */

//todo cleancup
        /*
getExternalStorage() + "/Android/data/<package_name>/cache/"
replacing by your app packaged, e.g. com. Orabig.myFirstApp that special folder is automatically deleted from the system if the user uninstall the application, keeping the system free from temporary files.
edit:Please note that my manifest does not include theyou have to!
edit: also, if you creating temporary media files (PNG for example) is good practice to create an empty file named .nomedia on that folder. That way you avoid the Media Scanner scanning it and showing it on the gallery.
last edit:and before creating files you must create the folder by calling mkdirs() on the File object.
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

}
