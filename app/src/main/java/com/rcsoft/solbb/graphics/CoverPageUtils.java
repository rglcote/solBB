package com.rcsoft.solbb.graphics;

/**
 * Created with IntelliJ IDEA.
 * User: rdcoteri
 * Date: 17.10.13
 * Time: 10:04
 */
public class CoverPageUtils {

/*
Bitmap image = ..... (whatever)

...

if(image != null) {

image.recycle();

image = null;

}
     */

/*
    watermark

public static Bitmap mark(Bitmap src, String watermark, Point location, Color color, int alpha, int size, boolean underline) {
    int w = src.getWidth();
    int h = src.getHeight();
    Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

    Canvas canvas = new Canvas(result);
    canvas.drawBitmap(src, 0, 0, null);

    Paint paint = new Paint();
    paint.setColor(color);
    paint.setAlpha(alpha);
    paint.setTextSize(size);
    paint.setAntiAlias(true);
    paint.setUnderlineText(underline);
    canvas.drawText(watermark, location.x, location.y, paint);

    return result;
}
     */



//
//    private static final int DEFAULT_FONT_SIZE = 18;
//    private static final int DEFAULT_COVER_IMAGE_TEXT_BOX_WIDTH = 395;
//    private static final int DEFAULT_COVER_IMAGE_TEXT_BOX_HEIGHT = 50;
//    private static final Color DEFAULT_TEXT_COLOR = new Color(153, 0, 204);
//
//    private int getY(Graphics2D g2, int boxCorner) {
//
//        int msgHeight = g2.getFontMetrics().getHeight();
//        int retval = boxCorner + (DEFAULT_COVER_IMAGE_TEXT_BOX_HEIGHT - msgHeight) / 2 + msgHeight / 2;
//        return retval;
//    }
//
//    private Font resizeFont(String message, Graphics2D g2) {
//
//        //try default font first
//        int fontSize = DEFAULT_FONT_SIZE;
//        boolean foundFont = false;
//
//        Font retval = new Font("TimesRoman", Font.BOLD, fontSize);
//
//        while (!foundFont) {
//
//            retval = new Font("TimesRoman", Font.BOLD, fontSize);
//            g2.setFont(retval);
//            FontMetrics fm = g2.getFontMetrics();
//            //the message is still too wide for the panel to properly display
//            if (fm.stringWidth(message) > DEFAULT_COVER_IMAGE_TEXT_BOX_WIDTH) {
//                fontSize--;
//            } else {
//                foundFont = true;
//            }
//
//        }
//        return retval;
//    }
//
//    public static BitmapDrawable generateCoverImage(String author, String title){
//
//        URL coverImageURL = getClass().getClassLoader().getResource("cover.png");
//        BufferedImage img = null;
//        if (coverImageURL != null) {
//
//            img = ImageIO.read(new File(coverImageURL.toURI()));
//
//            //update image with author and title
//            Graphics2D g2d = img.createGraphics();
//
//            // draw graphics
//            g2d.drawImage(img, 0, 0, null);
//            g2d.setColor(DEFAULT_TEXT_COLOR);
//            //draw title
//            g2d.setFont(resizeFont(title, g2d));
//            g2d.drawString(title, 15, getY(g2d, 10));
//            //draw author
//            g2d.setFont(resizeFont(author, g2d));
//            g2d.drawString(author, 15, getY(g2d, 65));
//
//            g2d.dispose();
//
//            //write new cover image
//            File coverFile = File.createTempFile("cover", ".png", outdir);
//            coverFile.deleteOnExit();
//            ImageIO.write(img, "png", coverFile);
//
//        }
//
//        return img;
//
//    }
//
}
