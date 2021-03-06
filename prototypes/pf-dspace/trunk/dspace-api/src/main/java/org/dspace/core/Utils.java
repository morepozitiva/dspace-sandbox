/*
 * Utils.java
 *
 * Version: $Revision$
 *
 * Date: $Date$
 *
 * Copyright (c) 2002-2005, Hewlett-Packard Company and Massachusetts
 * Institute of Technology.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the Hewlett-Packard Company nor the name of the
 * Massachusetts Institute of Technology nor the names of their
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package org.dspace.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.rmi.dgc.VMID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.apache.log4j.Logger;

/**
 * Utility functions for DSpace.
 * 
 * @author Peter Breton
 * @version $Revision$
 */
public class Utils
{
    /** log4j logger */
    private static Logger log = Logger.getLogger(Utils.class);

    private static final Pattern DURATION_PATTERN = Pattern
            .compile("(\\d+)([smhdwy])");

    private static final long MS_IN_SECOND = 1000L;

    private static final long MS_IN_MINUTE = 60000L;

    private static final long MS_IN_HOUR = 3600000L;

    private static final long MS_IN_DAY = 86400000L;

    private static final long MS_IN_WEEK = 604800000L;

    private static final long MS_IN_YEAR = 31536000000L;

    private static int counter = 0;

    private static Random random = new Random();

    private static VMID vmid = new VMID();

    // for parseISO8601Date
    private static SimpleDateFormat parseFmt[]  =
    {
        // first try at parsing, has milliseconds (note General time zone)
        new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss.SSSz"),

        // second try at parsing, no milliseconds (note General time zone)
        new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ssz"),


        // finally, try without any timezone (defaults to current TZ)
        new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss.SSS"),

        new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss")
    };

    // for formatISO8601Date
    // output canonical format (note RFC22 time zone, easier to hack)
    private static SimpleDateFormat outFmtSecond = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ssZ");

    // output format with millsecond precision
    private static SimpleDateFormat outFmtMillisec = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss.SSSZ");

    private static Calendar outCal = GregorianCalendar.getInstance();

    /** Private Constructor */
    private Utils()
    {
    }

    /**
     * Return an MD5 checksum for data in hex format.
     * 
     * @param data
     *            The data to checksum.
     * @return MD5 checksum for the data in hex format.
     */
    public static String getMD5(String data)
    {
        return getMD5(data.getBytes());
    }

    /**
     * Return an MD5 checksum for data in hex format.
     * 
     * @param data
     *            The data to checksum.
     * @return MD5 checksum for the data in hex format.
     */
    public static String getMD5(byte[] data)
    {
        return toHex(getMD5Bytes(data));
    }

    /**
     * Return an MD5 checksum for data as a byte array.
     * 
     * @param data
     *            The data to checksum.
     * @return MD5 checksum for the data as a byte array.
     */
    public static byte[] getMD5Bytes(byte[] data)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            return digest.digest(data);
        }
        catch (NoSuchAlgorithmException nsae)
        {
        }

        // Should never happen
        return null;
    }

    /**
     * Return a hex representation of the byte array
     * 
     * @param data
     *            The data to transform.
     * @return A hex representation of the data.
     */
    public static String toHex(byte[] data)
    {
        if ((data == null) || (data.length == 0))
        {
            return null;
        }

        StringBuffer result = new StringBuffer();

        // This is far from the most efficient way to do things...
        for (int i = 0; i < data.length; i++)
        {
            int low = (int) (data[i] & 0x0F);
            int high = (int) (data[i] & 0xF0);

            result.append(Integer.toHexString(high).substring(0, 1));
            result.append(Integer.toHexString(low));
        }

        return result.toString();
    }

    /**
     * Generate a unique key. The key is a long (length 38 to 40) sequence of
     * digits.
     * 
     * @return A unique key as a long sequence of base-10 digits.
     */
    public static String generateKey()
    {
        return new BigInteger(generateBytesKey()).abs().toString();
    }

    /**
     * Generate a unique key. The key is a 32-character long sequence of hex
     * digits.
     * 
     * @return A unique key as a long sequence of hex digits.
     */
    public static String generateHexKey()
    {
        return toHex(generateBytesKey());
    }

    /**
     * Generate a unique key as a byte array.
     * 
     * @return A unique key as a byte array.
     */
    public static synchronized byte[] generateBytesKey()
    {
        byte[] junk = new byte[16];

        random.nextBytes(junk);

        String input = new StringBuffer().append(vmid).append(
                new java.util.Date()).append(junk).append(counter++).toString();

        return getMD5Bytes(input.getBytes());
    }

    // The following two methods are taken from the Jakarta IOUtil class.

    /**
     * Copy stream-data from source to destination. This method does not buffer,
     * flush or close the streams, as to do so would require making non-portable
     * assumptions about the streams' origin and further use. If you wish to
     * perform a buffered copy, use {@link #bufferedCopy}.
     * 
     * @param input
     *            The InputStream to obtain data from.
     * @param output
     *            The OutputStream to copy data to.
     */
    public static void copy(final InputStream input, final OutputStream output)
            throws IOException
    {
        final int BUFFER_SIZE = 1024 * 4;
        final byte[] buffer = new byte[BUFFER_SIZE];

        while (true)
        {
            final int count = input.read(buffer, 0, BUFFER_SIZE);

            if (-1 == count)
            {
                break;
            }

            // write out those same bytes
            output.write(buffer, 0, count);
        }

        // needed to flush cache
        // output.flush();
    }

    /**
     * Copy stream-data from source to destination, with buffering. This is
     * equivalent to passing {@link #copy}a
     * <code>java.io.BufferedInputStream</code> and
     * <code>java.io.BufferedOuputStream</code> to {@link #copy}, and
     * flushing the output stream afterwards. The streams are not closed after
     * the copy.
     * 
     * @param source
     *            The InputStream to obtain data from.
     * @param destination
     *            The OutputStream to copy data to.
     */
    public static void bufferedCopy(final InputStream source,
            final OutputStream destination) throws IOException
    {
        final BufferedInputStream input = new BufferedInputStream(source);
        final BufferedOutputStream output = new BufferedOutputStream(
                destination);
        copy(input, output);
        output.flush();
    }

    /**
     * Replace characters that could be interpreted as HTML codes with symbolic
     * references (entities). This function should be called before displaying
     * any metadata fields that could contain the characters " <", ">", "&",
     * "'", and double quotation marks. This will effectively disable HTML links
     * in metadata.
     * 
     * @param value
     *            the metadata value to be scrubbed for display
     * 
     * @return the passed-in string, with html special characters replaced with
     *         entities.
     */
    public static String addEntities(String value)
    {
        if (value==null || value.length() == 0)
            return value;
        
        value = value.replaceAll("&", "&amp;");
        value = value.replaceAll("\"", "&quot;");

        // actually, &apos; is an XML entity, not in HTML.
        // that's why it's commented out.
        // value = value.replaceAll("'", "&apos;");
        value = value.replaceAll("<", "&lt;");
        value = value.replaceAll(">", "&gt;");

        return value;
    }

    /**
     * Utility method to parse durations defined as \d+[smhdwy] (seconds,
     * minutes, hours, days, weeks, years)
     * 
     * @param duration
     *            specified duration
     * 
     * @return number of milliseconds equivalent to duration.
     * 
     * @throws ParseException
     *             if the duration is of incorrect format
     */
    public static long parseDuration(String duration) throws ParseException
    {
        Matcher m = DURATION_PATTERN.matcher(duration.trim());
        if (!m.matches())
        {
            throw new ParseException("'" + duration
                    + "' is not a valid duration definition", 0);
        }

        String units = m.group(2);
        long multiplier = MS_IN_SECOND;

        if ("s".equals(units))
        {
            multiplier = MS_IN_SECOND;
        }
        else if ("m".equals(units))
        {
            multiplier = MS_IN_MINUTE;
        }
        else if ("h".equals(units))
        {
            multiplier = MS_IN_HOUR;
        }
        else if ("d".equals(units))
        {
            multiplier = MS_IN_DAY;
        }
        else if ("w".equals(units))
        {
            multiplier = MS_IN_WEEK;
        }
        else if ("y".equals(units))
        {
            multiplier = MS_IN_YEAR;
        }
        else
        {
            throw new ParseException(units
                    + " is not a valid time unit (must be 'y', "
                    + "'w', 'd', 'h', 'm' or 's')", duration.indexOf(units));
        }

        long qint = Long.parseLong(m.group(1));

        return qint * multiplier;
    }

    /**
     * Translates timestamp from an ISO 8601-standard format, which
     * is commonly used in XML and RDF documents.
     * This method is synchronized because it depends on a non-reentrant
     * static DateFormat (more efficient than creating a new one each call).
     *
     * @param s the input string
     * @return Date object, or null if there is a problem translating.
     */
    public static synchronized Date parseISO8601Date(String s)
    {
        // attempt to normalize the timezone to something we can parse;
        // SimpleDateFormat can't handle "Z"
        char tzSign = s.charAt(s.length()-6);
        if (s.endsWith("Z"))
            s = s.substring(0, s.length()-1) + "GMT+00:00";

        // check for trailing timezone
        else if (tzSign == '-' || tzSign == '+')
            s = s.substring(0, s.length()-6) + "GMT" + s.substring(s.length()-6);

        // try to parse without millseconds
        ParseException lastError = null;
        for (int i = 0; i < parseFmt.length; ++i)
        {
            try
            {
                return parseFmt[i].parse(s);
            }
            catch (ParseException e)
            {
                lastError = e;
            }
        }
        if (lastError != null)
            log.error("Error parsing date:", lastError);
        return null;
    }

    /**
     * Convert a Date to String in the ISO 8601 standard format.
     * The RFC822 timezone is almost right, still need to insert ":".
     * This method is synchronized because it depends on a non-reentrant
     * static DateFormat (more efficient than creating a new one each call).
     *
     * @param d the input Date
     * @return String containing formatted date.
     */
    public static synchronized String formatISO8601Date(Date d)
    {
        String result;
        outCal.setTime(d);
        if (outCal.get(Calendar.MILLISECOND) == 0)
            result = outFmtSecond.format(d);
        else
            result = outFmtMillisec.format(d);
        int rl = result.length();
        return result.substring(0, rl-2) + ":" + result.substring(rl-2);
    }
}
