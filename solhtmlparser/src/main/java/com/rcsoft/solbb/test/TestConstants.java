package com.rcsoft.solbb.test;

/**
 * Created by RDCoteRi on 2017-09-25.
 */

public class TestConstants {

    public static final String HTML_CONTENT = "\n" +
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"utf-8\"/>\n" +
            "    <title> aroslav: Art Critic: Chapter 5: Emerging</title>\n" +
            "    <base href=\"http://storiesonline.net/\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width\">\n" +
            "    <link rel=\"home\" href=\"/home.php\"/>\n" +
            "    <link rel=\"prev\" href=\"/s/16559:192193\"/>\n" +
            "    <style>\n" +
            "@import url('/css/pubcoments.css');\n" +
            "\t@import url('/css/stories.css');\n" +
            "\t@media screen {body#sol-story{background-color: #fff;}#story,#top-header,#bott-header{width:95%;max-width:95%;}}\n" +
            ".bottNav{list-style:none;padding-left:0;margin:auto}\n" +
            ".bottNav li{display:inline-block;font-size:.9em;font-family:Helvetica,sans-serif;padding:3px;margin:auto}\n" +
            ".hili{background-color:#ffa;color:#000}\n" +
            ".t1{margin:10px auto 30px auto;background-color:white;width:100%;max-width:35em;box-shadow:0px 0px .5em #aaa}\n" +
            ".fta{max-height:500px}\n" +
            "\n" +
            "    </style>\n" +
            "    <script src=\"/res/js/dc.min.3.js\"></script>\n" +
            "    <script src=\"/res/js/vote3.js\"></script>\n" +
            "\n" +
            "    <script src=\"/res/js/scrol.4.0.2.js\"></script>\n" +
            "    <script>\n" +
            "function el(id) {\n" +
            "var data;\n" +
            "if(ajx){\n" +
            "doCommand('moreData','shStDetails',[id]);}\n" +
            "else{window.location='/library/storyInfo.php?id='+id;}\n" +
            "}\n" +
            "\n" +
            "    </script>\n" +
            "</head>\n" +
            "<body id=\"sol-story\"\n" +
            "      onLoad=\"runScroller('/s/16559:192282', 'https://storiesonline.net/sol-secure');\">\n" +
            "<div>\n" +
            "    <div id=\"story\">\n" +
            "        <nav>\n" +
            "            <div id=\"top-header\">\n" +
            "                <div id=\"sd\"></div>\n" +
            "                <a id=\"page-top\"></a>\n" +
            "                <a href=\"/home.php\">Home</a>\n" +
            "                <span id=\"h_extra\">| <a href=\"/a/aroslav\" rel=\"author\">aroslav's Page</a> &laquo; <a\n" +
            "                        href=\"/s/16559/art-critic?ind=1\">Art Critic Index</a> &laquo; <a\n" +
            "                        href=\"/s/16559:192193\">Chapter 4</a>| &nbsp; <a\n" +
            "                        href=\"https://storiesonline.net/sol-secure/user/connect/\">Mail</a> | &nbsp; <a\n" +
            "                        href=\"/s/16559:192282#page-bottom\">Bottom</a></span></div>\n" +
            "        </nav>\n" +
            "        <article>\n" +
            "            <h2>Chapter 5: Emerging</h2>\n" +
            "            <div class=\"date\">Posted: September 23, 2017 - 09:53:01 pm</div>\n" +
            "\n" +
            "            <p>That wasn’t the end of our problems. It wasn’t the end of the blackness or depression\n" +
            "                or anxiety or panic. It didn’t heal the rift between Annette and Morgan. It didn’t\n" +
            "                bring us all back to the same bed.</p>\n" +
            "\n" +
            "            <p>It gave us a ray of hope to hang onto.</p>\n" +
            "\n" +
            "            <p>Annette continued to live with her parents and Morgan continued to sleep in the\n" +
            "                guestroom downstairs. Annette returned to our group at lunch and took me home each\n" +
            "                evening. On the weekend, she returned to the studio to do her reading and writing.\n" +
            "                Morgan returned her laptop to her desk in the studio and worked there.</p>\n" +
            "\n" +
            "            <div class=\"end-note\">\n" +
            "\n" +
            "                <p>Many thank yous to the people who help clean up my messes. Yes, that means\n" +
            "                    editors: Mr Spock, Old Rotorhead, and Pixel the Cat continue to offer good\n" +
            "                    advice, too often declined. All remaining errors are entirely my fault. Thank\n" +
            "                    you all!</p>\n" +
            "\n" +
            "            </div>\n" +
            "\n" +
            "            <div class=\"vform\" id=\"vote-form\">\n" +
            "\n" +
            "                <form action=\"/library/score.php\" method=\"post\" name=\"voteForm\" id=\"voteForm\"\n" +
            "                      onsubmit=\"return validateVote(this);\">\n" +
            "                    <div>\n" +
            "                        <input type=\"hidden\" name=\"id\" value=\"16559\">\n" +
            "                        <input type=\"hidden\" name=\"verCode\"\n" +
            "                               value=\"428ee223f60d17b4807c5d5323993534\">\n" +
            "                        Your opinion is <strong>important</strong>:<br>\n" +
            "                        Share with others what you think about<br>\n" +
            "                        \"<b>Art Critic</b>\"<br>\n" +
            "                        <select id=\"score\" name=\"score\">\n" +
            "                            <option value=\"0\">Select Score</option>\n" +
            "                            <option value=\"1\">You Call this a Story!?</option>\n" +
            "                            <option value=\"2\">Hated it</option>\n" +
            "                            <option value=\"3\">Pretty Bad</option>\n" +
            "                            <option value=\"4\">Not Good</option>\n" +
            "                            <option value=\"5\">Some Good, Some Bad</option>\n" +
            "                            <option value=\"6\">Not Bad</option>\n" +
            "                            <option value=\"7\">Good</option>\n" +
            "                            <option value=\"8\" selected>Very Good</option>\n" +
            "                            <option value=\"9\">Great</option>\n" +
            "                            <option value=\"10\">Most Amazing Story</option>\n" +
            "\n" +
            "                        </select>&nbsp; &nbsp; &nbsp;\n" +
            "                        <input type=\"submit\" name=\"Score\" value=\"Vote\"><br>\n" +
            "                    </div>\n" +
            "                </form>\n" +
            "                <div id=\"voteErrorDiv\"></div>\n" +
            "            </div>\n" +
            "\n" +
            "\n" +
            "            <h2 class=\"end\">To Be Continued...</h2>\n" +
            "\n" +
            "            <h4 class=\"c\"><a href=\"/series/1317/the-adventures-of-art-trange\">View the Strange Art\n" +
            "                Series</a>\n" +
            "                <br>Previous Part: <a href=\"/s/15872\">Art Project</a>\n" +
            "\n" +
            "            </h4>\n" +
            "\n" +
            "\n" +
            "        </article>\n" +
            "\n" +
            "        <ul class=\"bottNav\">|\n" +
            "            <li>Posted: 2017-09-11</li>\n" +
            "            |\n" +
            "            <li class=\"hili\"><a href=\"/ablog/aroslav\">aroslav's Blog</a></li>\n" +
            "            |\n" +
            "            <li><span>\n" +
            "<b>Share:</b>\n" +
            "<a href=\"https://twitter.com/intent/tweet?text=Reading%20aroslav%27s%20%22Art%20Critic%22&amp;url=http://storiesonline.net/s/16559/art-critic @storiesonline\"\n" +
            "   class=\"twitLink\" target=\"_blank\"><img width=\"18\" height=\"13\" alt=\"twitter\"\n" +
            "                                         src=\"https://res.wlpc.com/img/aa/bir.png\"> Tweet</a></span>\n" +
            "            </li>\n" +
            "            |\n" +
            "        </ul>\n" +
            "\n" +
            "        <footer>\n" +
            "            <div id=\"bott-header\">\n" +
            "                <div id=\"dhold\"></div>\n" +
            "                <a id=\"page-bottom\"></a>\n" +
            "                <a href=\"/home.php\">Home</a> |\n" +
            "                <a href=\"/s/16559:192282#page-top\">Top</a>\n" +
            "                | <a href=\"/a/aroslav\" rel=\"author\">aroslav's Page</a> |\n" +
            "                <script src=\"https://storiesonline.net/sol-secure/user/connect/conn.2.0.3.js\"></script>\n" +
            "                <script>\n" +
            "img1 = new Image(120,12);\n" +
            "img1.src = 'https://res.wlpc.com/img/aa/please-wait.gif';\n" +
            "\n" +
            "                </script>\n" +
            "\n" +
            "                &nbsp; <span class=\"error sm\"><a href=\"javascript:void(0)\"\n" +
            "                                                 onclick=\"solfeedback.gf('dhold','u:141719','a:6634','s:16559-192282');\">Feedback to author</a></span>\n" +
            "            </div>\n" +
            "        </footer>\n" +
            "        <div class=\"fo-div\">\n" +
            "            <h4 class=\"fo-thead\">Reader Comments</h4>\n" +
            "            <script src=\"/res/cmnt/cmnt.js\" async></script>\n" +
            "            <div class=\"formDiv c\"><a href=\"javascript:void(0)\"\n" +
            "                                      onclick=\"gf('ntdiv',16559);\">Comment</a></div>\n" +
            "            <div id=\"ntdiv\"></div>\n" +
            "            <div id=\"comcont\">\n" +
            "                <div class=\"post-wrap\" id=\"pd5493\"><a id=\"po5493\"></a>\n" +
            "                    <div class=\"fo-meta\"><b>Purplecat</b> <i class=\"timestamp fr\">\n" +
            "                        <script>\n" +
            "                            tstamp = new Date(1506273797000);document.write(tstamp.toLocaleString());\n" +
            "                        </script>\n" +
            "                        <noscript>2017-09-24 1:09:17pm</noscript>\n" +
            "                    </i></div>\n" +
            "                    <div class=\"post-text\"><p>aroslav, I stand by my earlier comment, very sad to\n" +
            "                        watch him go through this hard time in his life. Very glad to see that he is\n" +
            "                        starting the trip out of the depths, and maybe he will be able to 'see' so\n" +
            "                        much more one day.\n" +
            "                        <br>\n" +
            "                        <br>\n" +
            "                        Thank you for writing. Margie</p></div>\n" +
            "\n" +
            "                    <div class=\"replinks r\"><a href=\"javascript:void(0)\"\n" +
            "                                               onclick=\"gf('ntdiv5493','16559','5493');\"\n" +
            "                                               title=\"Reply to Post\" class=\"rep-arr\">&nbsp;</a>\n" +
            "                        &nbsp; <span id=\"edsp5493\"></span></div>\n" +
            "                    <div id=\"ntdiv5493\"></div>\n" +
            "                </div>\n" +
            "                <div class=\"post-wrap\" id=\"pd5492\"><a id=\"po5492\"></a>\n" +
            "                    <div class=\"fo-meta\"><b>emmthist</b> <i class=\"timestamp fr\">\n" +
            "                        <script>\n" +
            "                            tstamp = new Date(1506273787000);document.write(tstamp.toLocaleString());\n" +
            "                        </script>\n" +
            "                        <noscript>2017-09-24 1:09:07pm</noscript>\n" +
            "                    </i></div>\n" +
            "                    <div class=\"post-text\"><p>Erotic and good descriptive work keep it up</p></div>\n" +
            "\n" +
            "                    <div class=\"replinks r\"><a href=\"javascript:void(0)\"\n" +
            "                                               onclick=\"gf('ntdiv5492','16559','5492');\"\n" +
            "                                               title=\"Reply to Post\" class=\"rep-arr\">&nbsp;</a>\n" +
            "                        &nbsp; <span id=\"edsp5492\"></span></div>\n" +
            "                    <div id=\"ntdiv5492\"></div>\n" +
            "                </div>\n" +
            "                <div class=\"c\">\n" +
            "\n" +
            "                    <a href=\"javascript:void(0)\" onclick=\"mc(16559,1)\" class=\"nblo\">1</a>\n" +
            "                    <a href=\"javascript:void(0)\" onclick=\"mc(16559,2)\" class=\"nbl\">2</a>\n" +
            "\n" +
            "\n" +
            "                </div>\n" +
            "\n" +
            "            </div>\n" +
            "        </div>\n" +
            "\n" +
            "    </div>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>\n";

}
