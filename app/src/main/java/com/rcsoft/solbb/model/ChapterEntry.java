package com.rcsoft.solbb.model;

import java.io.File;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: rdcoteri
 * Date: 17.10.13
 * Time: 09:58
 */
public class ChapterEntry {

        private String chapterName;
        private URL chapterURL;
        private File chapterFile;

        public ChapterEntry(String chapterName, URL chapterURL) {
            this.chapterName = chapterName;
            this.chapterURL = chapterURL;
        }

        public String getChapterName() {
            return chapterName;
        }

        public void setChapterName(String chapterName) {
            this.chapterName = chapterName;
        }

        public URL getChapterURL() {
            return chapterURL;
        }

        public void setChapterURL(URL chapterURL) {
            this.chapterURL = chapterURL;
        }

        public File getChapterFile() {
            return chapterFile;
        }

        public void setChapterFile(File chapterFile) {
            this.chapterFile = chapterFile;
        }

}
