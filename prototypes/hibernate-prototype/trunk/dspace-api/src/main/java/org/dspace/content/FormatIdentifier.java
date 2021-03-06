/*
 * FormatIdentifier.java
 *
 * Version: $Revision: 2417 $
 *
 * Date: $Date: 2007-12-10 18:00:07 +0100 (lun, 10 dic 2007) $
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
package org.dspace.content;

import java.util.List;

import org.dspace.core.Context;
import org.dspace.content.dao.BitstreamFormatDAO;
import org.dspace.content.dao.BitstreamFormatDAOFactory;

/**
 * This class handles the recognition of bitstream formats, using the format
 * registry in the database. For the moment, the format identifier simply uses
 * file extensions stored in the "BitstreamFormatIdentifier" table. This
 * probably isn't a particularly satisfactory long-term solution.
 * 
 * @author Robert Tansley
 * @version $Revision: 2417 $
 */
public class FormatIdentifier
{
    /**
     * Attempt to identify the format of a particular bitstream. If the format
     * is unknown, null is returned.
     * 
     * @param bitstream
     *            the bitstream to identify the format of
     * 
     * @return a format from the bitstream format registry, or null
     */
    public static BitstreamFormat guessFormat(Context context,
            Bitstream bitstream)
    {
        // FIXME: Just setting format to first guess
        // For now just get the file name
        String filename = bitstream.getName().toLowerCase();

        // Gracefully handle the null case
        if (filename == null)
        {
            return null;
        }

        // This isn't rocket science. We just get the name of the
        // bitstream, get the extension, and see if we know the type.
        String extension = filename;
        int lastDot = filename.lastIndexOf('.');

        if (lastDot != -1)
        {
            extension = filename.substring(lastDot + 1);
        }

        // If the last character was a dot, then extension will now be
        // an empty string. If this is the case, we don't know what
        // file type it is.
        if (extension.equals(""))
        {
            return null;
        }

        // See if the extension is associated with any formats
        BitstreamFormatDAO dao = BitstreamFormatDAOFactory.getInstance(context);
        List<BitstreamFormat> formats = dao.getBitstreamFormats(extension, context);

        BitstreamFormat retFormat = null;

        if (!formats.isEmpty())
        {
            // We can do no better than guess the first if there are multiple
            // results. 
            retFormat = formats.get(0);
        }

        return retFormat;
    }
}
