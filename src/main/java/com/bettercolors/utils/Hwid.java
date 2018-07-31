package com.bettercolors.utils;

/**
 * Created by nero9 on 30/06/2017.
 */
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

public class Hwid {
    private static String hwid = null;
    private static String convertToHex(byte[] data)
    {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < data.length; i++)
        {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do
            {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            }
            while(two_halfs++ < 1);
        }
        return buf.toString();
    }

    private static String SHA512(String text)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-512");
        byte[] sha1hash = new byte[40];
        md.update(text.getBytes("UTF-8"), 0, text.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }
    public static String getHWID() {
        try {
            if(hwid != null)
                return hwid;
            hwid = "";
            hwid += System.getenv("PROCESSOR_IDENTIFIER");
            hwid += System.getenv("COMPUTERNAME");
            Enumeration<NetworkInterface> netenum = NetworkInterface.getNetworkInterfaces();
            if(netenum.hasMoreElements())
                hwid += netenum.nextElement().getHardwareAddress();
            hwid = SHA512(hwid);
            return hwid;
        } catch(NoSuchAlgorithmException nsae) {} catch (SocketException e) {} catch (UnsupportedEncodingException e) {}
        return null;
    }
}