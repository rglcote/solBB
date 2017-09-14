package com.rcsoft.solbb.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by RDCoteRi on 2017-09-13.
 */

public class NetUtils {

    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';

    public static String createQueryStringForParameters(Map<String, String> parameters) {
        StringBuilder parametersAsQueryString = new StringBuilder();
        if (parameters != null) {
            boolean firstParameter = true;

            for (String parameterName : parameters.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append(PARAMETER_DELIMITER);
                }

                try {
                    parametersAsQueryString.append(parameterName)
                            .append(PARAMETER_EQUALS_CHAR)
                            .append(URLEncoder.encode(
                                    parameters.get(parameterName), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    //eat exception, should always have UTF-8 encoding
                    e.printStackTrace();
                }

                firstParameter = false;
            }
        }
        return parametersAsQueryString.toString();
    }



}
