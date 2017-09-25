package com.rcsoft.solbb;

import com.rcsoft.solbb.test.TestConstants;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolHTMLParser {

    public String parse(Source source) {

        //<article>
        Element story = source.getFirstElement("article");
        if (story == null) {
            throw new RuntimeException("Could not find main story element");
        }
        OutputDocument outputDocument = new OutputDocument(story);

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
        }

        //create final content
        sbMain.append(HTMLSanitiser.stripInvalidMarkup(outputDocument.toString()));
        sbMain.append(sbSubchapters);
        return sbMain.toString();

    }

    public static void main(String[] args) {

        try {
            SolHTMLParser sp = new SolHTMLParser();
            Source ss = new Source(TestConstants.HTML_CONTENT);
            System.out.println(sp.parse(ss));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
