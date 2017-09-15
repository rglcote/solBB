package com.adobe.dp.epub.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by richard on 9/15/17.
 */

public class InputStreamDataSource extends DataSource {

    private InputStream is;

    public InputStreamDataSource(InputStream is) {
        this.is = is;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return is;
    }
}
