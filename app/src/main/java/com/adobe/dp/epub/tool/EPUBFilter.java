package com.adobe.dp.epub.tool;

import com.adobe.dp.epub.io.OCFContainerWriter;
import com.adobe.dp.epub.opf.Publication;

import java.io.File;
import java.io.FileOutputStream;

public class EPUBFilter {

	public static void main(String[] args) {
		try {
			File file = new File(args[0]);
			Publication epub = new Publication(file);
			epub.parseAll();
			epub.cascadeStyles();
			System.out.println("Loaded \"" + epub.getDCMetadata("title") + "\"");
			long start = System.currentTimeMillis();
			epub.refactorStyles();
			long end = System.currentTimeMillis();
			System.out.println( "Refactored styles in " + (end - start) + "ms");
			//raw.epub.addFonts();
			FileOutputStream out = new FileOutputStream(args[1]);
			epub.serialize(new OCFContainerWriter(out));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
