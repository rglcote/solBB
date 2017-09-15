package com.rcsoft.solbb.utils;

import android.os.AsyncTask;

/**
 * Created by RDCoteRi on 2017-09-15.
 */

public abstract class PublishingAsyncTask<T, T1, T2> extends AsyncTask<T, T1, T2>{

    public void doProgress(T1... values){
        publishProgress(values);
    }

}


