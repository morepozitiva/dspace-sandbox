/*
 * BundleDAO.java
 *
 * Version: $Revision: 1727 $
 *
 * Date: $Date: 2007-01-19 10:52:10 +0000 (Fri, 19 Jan 2007) $
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
package org.dspace.content.dao;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.uri.ObjectIdentifierService;
import org.dspace.uri.*;
import org.dspace.uri.dao.ObjectIdentifierStorageException;
import org.dspace.uri.dao.ExternalIdentifierStorageException;

import java.util.List;

/**
 * @author James Rutherford
 */
public class BundleDAOCore extends BundleDAO
{
    protected Logger log = Logger.getLogger(BundleDAO.class);

    protected BitstreamDAO bitstreamDAO;

    public BundleDAOCore(Context context)
    {
        super(context);

        bitstreamDAO = BitstreamDAOFactory.getInstance(context);
    }

    @Override
    public Bundle create() throws AuthorizeException
    {
        try
        {
            Bundle bundle =  childDAO.create();

            // now assign an object identifier
            ObjectIdentifier oid = ObjectIdentifierService.mint(context, bundle);

            // now assign any required external identifiers
            List<ExternalIdentifier> eids = ExternalIdentifierService.mintAll(context, bundle);
            bundle.setExternalIdentifiers(eids);

            log.info(LogManager.getHeader(context, "create_bundle", "bundle_id="
                    + bundle.getID()));

            return bundle;
        }
        catch (UnsupportedIdentifierException e)
        {
            throw new RuntimeException(e);
        }
        catch (IdentifierException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Bundle retrieve(int id)
    {
        Bundle bundle = (Bundle) context.fromCache(Bundle.class, id);

        if (bundle == null)
        {
            bundle = childDAO.retrieve(id);
        }

        return bundle;
    }

    @Override
    public void update(Bundle bundle) throws AuthorizeException
    {
        try
        {
            // FIXME: Check authorization? Or do we count on this only ever
            // happening via an Item?
            Bitstream[] bitstreams = bundle.getBitstreams();

            // Delete any Bitstreams that were removed from the in-memory list
            for (Bitstream dbBitstream : bitstreamDAO.getBitstreamsByBundle(bundle))
            {
                boolean deleted = true;

                for (Bitstream bitstream : bitstreams)
                {
                    if (bitstream.getID() == dbBitstream.getID())
                    {
                        // If the bitstream still exists in memory, don't delete
                        deleted = false;
                        break;
                    }
                }

                if (deleted)
                {
                    unlink(bundle, dbBitstream);
                }
            }

            // Now that we've cleared up the db, we make the Bundle <->
            // Bitstream link concrete.
            for (Bitstream bitstream : bitstreams)
            {
                link(bundle, bitstream);
            }

            // finally, deal with the item identifier/uuid
            ObjectIdentifier oid = bundle.getIdentifier();
            if (oid == null)
            {
                /*
                oid = new ObjectIdentifier(true);
                bundle.setIdentifier(oid);*/
                oid = ObjectIdentifierService.mint(context, bundle);
            }
            oidDAO.update(bundle.getIdentifier());

            // deal with the external identifiers
            List<ExternalIdentifier> eids = bundle.getExternalIdentifiers();
            for (ExternalIdentifier eid : eids)
            {
                identifierDAO.update(eid);
            }

            childDAO.update(bundle);
        }
        catch (ObjectIdentifierStorageException e)
        {
            log.error("caught exception: ", e);
            throw new RuntimeException(e);
        }
        catch (ExternalIdentifierStorageException e)
        {
            log.error("caught exception: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) throws AuthorizeException
    {
        try
        {
            Bundle bundle = retrieve(id);

            context.removeCached(bundle, id);

            log.info(LogManager.getHeader(context, "delete_bundle", "bundle_id="
                        + id));

            for (Bitstream bitstream : bundle.getBitstreams())
            {
                unlink(bundle, bitstream);
            }

            // remove our authorization policies
            AuthorizeManager.removeAllPolicies(context, bundle);

            // remove the object identifier
            oidDAO.delete(bundle.getIdentifier());

            childDAO.delete(id);
        }
        catch (ObjectIdentifierStorageException e)
        {
            log.error("caught exception: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void link(Bundle bundle, Bitstream bitstream) throws AuthorizeException
    {
        if (!linked(bundle, bitstream))
        {
            AuthorizeManager.authorizeAction(context, bundle, Constants.ADD);

            log.info(LogManager.getHeader(context, "add_bitstream",
                        "bundle_id=" + bundle.getID() +
                        ",bitstream_id=" + bitstream.getID()));

            bundle.addBitstream(bitstream);
        }

        childDAO.link(bundle, bitstream);
    }

    @Override
    public void unlink(Bundle bundle, Bitstream bitstream) throws AuthorizeException
    {
        AuthorizeManager.authorizeAction(context, bundle,
                Constants.REMOVE);

        log.info(LogManager.getHeader(context, "remove_bitstream",
                    "bundle_id=" + bundle.getID() +
                    ",bitstream_id=" + bitstream.getID()));

        bundle.removeBitstream(bitstream);

        childDAO.unlink(bundle, bitstream);

        // In the event that the bitstream to remove is actually
        // the primary bitstream, be sure to unset the primary
        // bitstream.
        if (bitstream.getID() == bundle.getPrimaryBitstreamID())
        {
            bundle.unsetPrimaryBitstreamID();
        }

        if (getBundles(bitstream).size() == 0)
        {
            // The bitstream is an orphan, delete it
            bitstreamDAO.delete(bitstream.getID());
        }
    }

    @Override
    public boolean linked(Bundle bundle, Bitstream bitstream)
    {
        // FIXME: Where do we do the check? In memory or in the storage layer?
        for (Bitstream b : bundle.getBitstreams())
        {
            if (b.equals(bitstream))
            {
                return true;
            }
        }

        return false;
    }
}
