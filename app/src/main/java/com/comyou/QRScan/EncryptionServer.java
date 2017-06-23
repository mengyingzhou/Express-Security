package com.comyou.QRScan;

import java.io.UnsupportedEncodingException;

/**
 * Created by aerber on 16-9-23.
 */
public class EncryptionServer {

    StringBuffer unicode = new StringBuffer();
    String result;

    public String Encryption(String content) {
        int num = 0;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == '&') unicode.append("&");
            else {
                int c_code = (int) c;
                ++c_code;
                unicode.append(c_code + "|");
            }
        }
        result = unicode.toString();
        unicode.delete(0, result.length());


        for (int i = 0; i < result.length(); ++i) {
            char c = result.charAt(i);

            if (c == '|') {
                char temp = (char) num;
                unicode.append(temp);
                num = 0;
            } else if (c == '&') unicode.append("&");
            else num = c - '0' + num * 10;
        }
        result = unicode.toString();
        try {
            result = new String(result.getBytes("UTF-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
    }
}
