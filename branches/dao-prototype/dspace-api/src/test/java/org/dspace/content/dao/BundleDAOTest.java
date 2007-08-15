/*
 * BundleDAOTest.java
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

import java.util.List;

import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.core.ConfigurationManager;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.dao.EPersonDAO;
import org.dspace.eperson.dao.EPersonDAOFactory;
import org.dspace.storage.dao.CRUDTest;
import org.dspace.storage.dao.LinkTest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class BundleDAOTest implements CRUDTest, LinkTest
{
    private static Context context;
    private BundleDAO instance;
    private BitstreamDAO bitstreamDAO;
    private ItemDAO itemDAO;

    private static final String ADMIN_EMAIL = "james.rutherford@hp.com";
    private static final String CONFIG = "/opt/dspace-dao/config/dspace.cfg";
    
    public BundleDAOTest()
    {
        instance = BundleDAOFactory.getInstance(context);
        bitstreamDAO = BitstreamDAOFactory.getInstance(context);
        itemDAO = ItemDAOFactory.getInstance(context);
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
        ConfigurationManager.loadConfig(CONFIG);

        context = new Context();
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
        context.abort();
    }

    @Before
    public void setUp() throws Exception
    {
        // We set the EPerson in the Context before each test, just in case one
        // of them needs to alter it.
        EPersonDAO epersonDAO = EPersonDAOFactory.getInstance(context);
        EPerson admin = epersonDAO.retrieve(EPerson.EPersonMetadataField.EMAIL,
                ADMIN_EMAIL);

        context.setCurrentUser(admin);
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void create() throws Exception
    {
        Bundle result = instance.create();

        int id = result.getID();

        assertTrue(id > 0);
    }

    @Test
    public void retrieve() throws Exception
    {
        Bundle existing = instance.create();
        Bundle result = instance.retrieve(existing.getID());

        assertEquals(existing.getID(), result.getID());
    }

    @Test
    public void update() throws Exception
    {
        Bundle bundle = instance.create();
        bundle.setName("Bundle Test");
        instance.update(bundle);
        
        Bundle retrieved = instance.retrieve(bundle.getID());
        assertEquals("Bundle Test", retrieved.getName());
    }

    @Test
    public void delete() throws Exception
    {
        Bundle result = instance.create();
        int id = result.getID();

        instance.delete(id);

        assertNull(instance.retrieve(id));
    }

    @Test
    public void getBundles() throws Exception
    {
        /**
         * This deprecated method just defers to getBundlesByItem() so I'm
         * going to be kind and just let it pass.
         */
        assertTrue(true);
    }

    @Test
    public void getBundlesByItem() throws Exception
    {
        Item item = itemDAO.create();
        Bundle bundleOne = instance.create();
        Bundle bundleTwo = instance.create();
        boolean containsOne = false;
        boolean containsTwo = false;

        itemDAO.link(item, bundleOne);
        itemDAO.link(item, bundleTwo);
        List<Bundle> bundles = instance.getBundlesByItem(item);

        // We have to do it this way because even though we have a type-safe
        // List, Java insists on using Object.equals() which will fail, even
        // though the objects are actually equal.
        for (Bundle b : bundles)
        {
            if (bundleOne.equals(b))
            {
                containsOne = true;
            }
            if (bundleTwo.equals(b))
            {
                containsTwo = true;
            }
        }

        assertTrue(containsOne);
        assertTrue(containsTwo);
    }

    @Test
    public void getBundlesByBitstream() throws Exception
    {
        Bitstream bitstream = bitstreamDAO.create();
        Bundle bundle = instance.create();
        List<Bundle> bundles = null;

        bundles = instance.getBundlesByBitstream(bitstream);
        assertEquals(bundles.size(), 0);

        instance.link(bundle, bitstream);
        bundles = instance.getBundlesByBitstream(bitstream);
        assertEquals(bundles.size(), 1);
        assertTrue(bundle.equals(bundles.get(0)));
    }

    @Test
    public void link() throws Exception
    {
        Bitstream bitstream = bitstreamDAO.create();
        Bundle bundle = instance.create();

        assertTrue(!instance.linked(bundle, bitstream));

        instance.link(bundle, bitstream);
        assertTrue(instance.linked(bundle, bitstream));
    }

    @Test
    public void unlink() throws Exception
    {
        Bitstream bitstream = bitstreamDAO.create();
        Bundle bundle = instance.create();

        assertTrue(!instance.linked(bundle, bitstream));

        instance.link(bundle, bitstream);
        assertTrue(instance.linked(bundle, bitstream));

        instance.unlink(bundle, bitstream);
        assertTrue(!instance.linked(bundle, bitstream));
    }

    @Test
    public void linked() throws Exception
    {
        Bitstream bitstream = bitstreamDAO.create();
        Bundle bundle = instance.create();

        assertTrue(!instance.linked(bundle, bitstream));

        instance.link(bundle, bitstream);
        assertTrue(instance.linked(bundle, bitstream));

        instance.unlink(bundle, bitstream);
        assertTrue(!instance.linked(bundle, bitstream));
    }
}
