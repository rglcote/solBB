package com.rcsoft.solbb.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.EdgeEffect;

import com.adobe.dp.css.CSSLength;
import com.adobe.dp.css.CSSNumber;
import com.adobe.dp.css.SelectorRule;
import com.adobe.dp.epub.io.FileDataSource;
import com.adobe.dp.epub.io.InputStreamDataSource;
import com.adobe.dp.epub.io.OCFContainerWriter;
import com.adobe.dp.epub.io.StringDataSource;
import com.adobe.dp.epub.ncx.TOCEntry;
import com.adobe.dp.epub.opf.NCXResource;
import com.adobe.dp.epub.opf.OPSResource;
import com.adobe.dp.epub.opf.Publication;
import com.adobe.dp.epub.opf.StyleResource;
import com.adobe.dp.epub.ops.OPSDocument;
import com.adobe.dp.epub.ops.SVGElement;
import com.adobe.dp.epub.ops.SVGImageElement;
import com.rcsoft.solbb.model.ChapterEntry;
import com.rcsoft.solbb.model.EbookData;

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

public class EpubBuilder {

    private EbookData data;
    private Context context;

    public EpubBuilder(EbookData data, Context context) {

        this.data = data;
        this.context = context;

        //boilerplate from jericho parser
        MicrosoftTagTypes.register();
        PHPTagTypes.register();
        MasonTagTypes.register();

    }

    private File getDownloadStorageDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    /* Checks if external storage is available for read and write */
    private File getCacheDir() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.rcsoft.solbb/cache/");
        } else {
            return context.getFilesDir();
        }
    }

    public String writeEpub() {

        String fileName = "book.raw.epub";
        try {
            // create new EPUB document
            Publication epub = new Publication();

            // set up title and author
            epub.addDCMetadata("title", data.getTitle());
            epub.addDCMetadata("publisher", System.getProperty("user.name"));
            epub.addDCMetadata("creator", data.getAuthor());
            epub.addDCMetadata("language", "en");

            // create a stylesheet
            StyleResource style = epub.createStyleResource("OPS/styles.css");

            style.load(new InputStreamDataSource(context.getAssets().open("epub/OEBPS/stylesheet.css")));

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

            for (ChapterEntry entry : data.getChapters()) {
                createChapter(epub, toc, style, entry);
            }

            // save EPUB to an OCF container
            if (data.getTitle() != null && data.getAuthor() != null) {
                fileName = data.getAuthor() + "-" + data.getTitle() + ".raw.epub";
            }

            File epubFile = new File(getDownloadStorageDir(), fileName);
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
        OPSResource opsResource = epub.createOPSResource("OPS/" + chapterEntry.getChapterName());
        epub.addToSpine(opsResource);

        opsResource.load(new StringDataSource(chapterEntry.getContent()));

        // get chapter document
        OPSDocument opsDocument = opsResource.getDocument();

        // link our stylesheet
        opsDocument.addStyleResource(style);

        // add chapter to the table of contents
        TOCEntry tocEntry = toc.createTOCEntry(chapterEntry.getChapterName(), opsDocument.getRootXRef());
        toc.getRootTOCEntry().add(tocEntry);

    }

}
