/*
 * EditCollectionMetadataForm.java
 *
 * Version: $Revision: 1.0 $
 *
 * Date: $Date: 2006/07/13 23:20:54 $
 *
 * Copyright (c) 2002, Hewlett-Packard Company and Massachusetts
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
package org.dspace.app.xmlui.aspect.administrative.community;

import java.sql.SQLException;

import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Para;
import org.dspace.app.xmlui.wing.element.Text;
import org.dspace.app.xmlui.wing.element.TextArea;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Community;


/**
 * Presents the user with a form to enter the initial metadata for creation of a new community  
 * @author Alexey Maslov
 */
public class CreateCommunityForm extends AbstractDSpaceTransformer   
{
//	/** Language Strings */
//	private static final Message T_dspace_home =
//        message("xmlui.general.dspace_home");
//	
//	private static final Message T_title = message("xmlui.administrative.community.CreateCommunityForm.title");
//	private static final Message T_trail = message("xmlui.administrative.community.CreateCommunityForm.trail");
//
//	private static final Message T_main_head_sub = message("xmlui.administrative.community.CreateCommunityForm.main_head_sub");
//	private static final Message T_main_head_top = message("xmlui.administrative.community.CreateCommunityForm.main_head_top");
//
//	private static final Message T_label_name = message("xmlui.administrative.community.EditCommunityMetadataForm.label_name");
//	private static final Message T_label_short_description = message("xmlui.administrative.community.EditCommunityMetadataForm.label_short_description");
//	private static final Message T_label_introductory_text = message("xmlui.administrative.community.EditCommunityMetadataForm.label_introductory_text");
//	private static final Message T_label_copyright_text = message("xmlui.administrative.community.EditCommunityMetadataForm.label_copyright_text");
//	private static final Message T_label_side_bar_text = message("xmlui.administrative.community.EditCommunityMetadataForm.label_side_bar_text");
//	private static final Message T_label_logo = message("xmlui.administrative.community.EditCommunityMetadataForm.label_logo");
//
//	private static final Message T_submit_save = message("xmlui.administrative.community.CreateCommunityForm.submit_save");
//	private static final Message T_submit_cancel = message("xmlui.administrative.community.CreateCommunityForm.submit_cancel");
//	
//	
//	public void addPageMeta(PageMeta pageMeta) throws WingException
//    {
//        pageMeta.addMetadata("title").addContent(T_title);
//        pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
//        pageMeta.addTrail().addContent(T_trail);
//    }
//	
//	public void addBody(Body body) throws WingException, SQLException, AuthorizeException
//	{
//		int communityID = parameters.getParameterAsInteger("communityID", -1);
//		Community parentCommunity = Community.find(context, communityID);
//		
//		// DIVISION: main
//	    Division main = body.addInteractiveDivision("create-community",contextPath+"/admin/community",Division.METHOD_MULTIPART,"primary administrative community");
//	    /* Whether the parent community is null is what determines if 
//		  we are creating a top-level community or a sub-community */
//	    if (parentCommunity != null)
//	    	main.setHead(T_main_head_sub.parameterize(parentCommunity.getMetadata("name")));
//	    else
//	    	main.setHead(T_main_head_top);
//	        
//	    
//	    // The grand list of metadata options
//	    List metadataList = main.addList("metadataList", "form");
//	    
//	    // community name
//	    metadataList.addLabel(T_label_name);
//	    Text name = metadataList.addItem().addText("name");
//	    name.setSize(40);
//	    
//	    // short description
//	    metadataList.addLabel(T_label_short_description);
//	    Text short_description = metadataList.addItem().addText("short_description");
//	    short_description.setSize(40);
//	    
//	    // introductory text
//	    metadataList.addLabel(T_label_introductory_text);
//	    TextArea introductory_text = metadataList.addItem().addTextArea("introductory_text");
//	    introductory_text.setSize(6, 40);
//	    
//	    // copyright text
//	    metadataList.addLabel(T_label_copyright_text);
//	    TextArea copyright_text = metadataList.addItem().addTextArea("copyright_text");
//	    copyright_text.setSize(6, 40);
//	    
//	    // legacy sidebar text; may or may not be used for news 
//	    metadataList.addLabel(T_label_side_bar_text);
//	    TextArea side_bar_text = metadataList.addItem().addTextArea("side_bar_text");
//	    side_bar_text.setSize(6, 40);
//	    
//	    // the row to upload a new logo 
//	    metadataList.addLabel(T_label_logo);
//	    metadataList.addItem().addFile("logo");
//
//	    Para buttonList = main.addPara();
//	    buttonList.addButton("submit_save").setValue(T_submit_save);
//	    buttonList.addButton("submit_cancel").setValue(T_submit_cancel);
//	    
//    	main.addHidden("administrative-continue").setValue(knot.getId());
//    }
}
