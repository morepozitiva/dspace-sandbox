/*
 * JSPInitialQuestionsStep.java
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
package org.dspace.app.webui.submit.step;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.dspace.app.util.DCInputsReader;
import org.dspace.app.util.SubmissionInfo;
import org.dspace.app.webui.submit.JSPStep;
import org.dspace.app.webui.submit.JSPStepManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.core.Context;
import org.dspace.submit.step.InitialQuestionsStep;

/**
 * Initial Submission servlet for DSpace. Handles the initial questions which
 * are asked to users to gather information regarding what metadata needs to be
 * gathered.
 * <P>
 * This JSPStepManager class works with the SubmissionController servlet
 * for the JSP-UI
 * 
 * The following methods are called in this order:
 * <ul>
 * <li>Call doPreProcessing() method</li>
 * <li>If showJSP() was specified from doPreProcessing(), then the JSP
 * specified will be displayed</li>
 * <li>If showJSP() was not specified from doPreProcessing(), then the
 * doProcessing() method is called an the step completes immediately</li>
 * <li>Call doProcessing() method after the user returns from the JSP, in order
 * to process the user input</li>
 * <li>Call doPostProcessing() method to determine if more user interaction is
 * required, and if further JSPs need to be called.</li>
 * <li>If there are more "pages" in this step then, the process begins again
 * (for the new page).</li>
 * <li>Once all pages are complete, control is forwarded back to the
 * SubmissionController, and the next step is called.</li>
 * </ul>
 * 
 * @see org.dspace.app.webui.servlet.SubmissionController
 * @see org.dspace.app.webui.submit.JSPStep
 * @see org.dspace.submit.step.InitialQuestionsStep
 * 
 * @author Tim Donohue
 * @version $Revision$
 */
public class JSPInitialQuestionsStep extends InitialQuestionsStep implements JSPStep
{
    /** JSP which displays initial questions * */
    private static final String INITIAL_QUESTIONS_JSP = "/submit/initial-questions.jsp";

    /** JSP which verifies users wants to change answers * */
    private static final String VERIFY_PRUNE_JSP = "/submit/verify-prune.jsp";

    /** JSP which tells the user that theses are not allowed * */
    private static final String NO_THESES_JSP = "/submit/no-theses.jsp";

    /** log4j logger */
    private static Logger log = Logger.getLogger(JSPInitialQuestionsStep.class);

    /**
     * Do any pre-processing to determine which JSP (if any) is used to generate
     * the UI for this step. This method should include the gathering and
     * validating of all data required by the JSP. In addition, if the JSP
     * requires any variable to passed to it on the Request, this method should
     * set those variables.
     * <P>
     * If this step requires user interaction, then this method must call the
     * JSP to display, using the "showJSP()" method of the JSPStepManager class.
     * <P>
     * If this step doesn't require user interaction OR you are solely using
     * Manakin for your user interface, then this method may be left EMPTY,
     * since all step processing should occur in the doProcessing() method.
     * 
     * @param context
     *            current DSpace context
     * @param request
     *            current servlet request object
     * @param response
     *            current servlet response object
     * @param subInfo
     *            submission info object
     */
    public void doPreProcessing(Context context, HttpServletRequest request,
            HttpServletResponse response, SubmissionInfo subInfo)
            throws ServletException, IOException, SQLException,
            AuthorizeException
    {
        // prepare & show the initial questions page, by default
        showInitialQuestions(context, request, response, subInfo);
    }

    /**
     * Do any pre-processing to determine which JSP (if any) is used to generate
     * the UI for this step. This method should include the gathering and
     * validating of all data required by the JSP. In addition, if the JSP
     * requires any variable to passed to it on the Request, this method should
     * set those variables.
     * <P>
     * If this step requires user interaction, then this method must call the
     * JSP to display, using the "showJSP()" method of the JSPStepManager class.
     * <P>
     * If this step doesn't require user interaction OR you are solely using
     * Manakin for your user interface, then this method may be left EMPTY,
     * since all step processing should occur in the doProcessing() method.
     * 
     * @param context
     *            current DSpace context
     * @param request
     *            current servlet request object
     * @param response
     *            current servlet response object
     * @param subInfo
     *            submission info object
     * @param status
     *            any status/errors reported by doProcessing() method
     */
    public void doPostProcessing(Context context, HttpServletRequest request,
            HttpServletResponse response, SubmissionInfo subInfo, int status)
            throws ServletException, IOException, SQLException,
            AuthorizeException
    {
        // Get the values from the initial questions form
        boolean multipleTitles = UIUtil.getBoolParameter(request,
                "multiple_titles");
        boolean publishedBefore = UIUtil.getBoolParameter(request,
                "published_before");
        boolean multipleFiles = UIUtil.getBoolParameter(request,
                "multiple_files");

        // If we actually came from the "verify prune" page,
        // then prune any excess data.
        if (JSPStepManager.getLastJSPDisplayed(request).equals(VERIFY_PRUNE_JSP))
        {
            if (status == STATUS_CANCEL_PRUNE)
            {
                // User cancelled pruning (show initial questions again)
                showInitialQuestions(context, request, response, subInfo);
                return;
            }
        }
        else
        // Otherwise, if just coming from "initial questions" page
        {
            if (status == STATUS_THESIS_REJECTED)
            {
                // Display no-theses JSP to user
                JSPStepManager.showJSP(request, response, subInfo, NO_THESES_JSP);

                return;
            }

            // If anything is going to be removed from the item as a result
            // of changing the answer to one of the questions, we need
            // to inform the user and make sure that's OK
            if (status == STATUS_VERIFY_PRUNE)
            {
                showVerifyPrune(context, request, response, subInfo,
                        multipleTitles, publishedBefore, multipleFiles);
                return;
            }
        }

    }

    /**
     * Show the page which displays all the Initial Questions to the user
     * 
     * @param context
     *            current DSpace context
     * @param request
     *            the request object
     * @param response
     *            the response object
     * @param subInfo
     *            the SubmissionInfo object
     * 
     */
    private void showInitialQuestions(Context context,
            HttpServletRequest request, HttpServletResponse response,
            SubmissionInfo subInfo) throws SQLException, ServletException,
            IOException
    {
        // determine collection
        Collection c = subInfo.getSubmissionItem().getCollection();

        // read configurable submissions forms data
        DCInputsReader inputsReader = new DCInputsReader();

        // load the proper submission inputs to be used by the JSP
        request.setAttribute("submission.inputs", inputsReader.getInputs(c
                .getIdentifier().getCanonicalForm()));

        // forward to initial questions JSP
        JSPStepManager.showJSP(request, response, subInfo, INITIAL_QUESTIONS_JSP);
    }

    /**
     * Show the page which warns the user that by changing the answer to one of
     * these questions, some previous data may be deleted.
     * 
     * @param context
     *            current DSpace context
     * @param request
     *            the request object
     * @param response
     *            the response object
     * @param subInfo
     *            the SubmissionInfo object
     * @param multipleTitles
     *            if there is multiple titles
     * @param publishedBefore
     *            if published before
     * @param multipleFiles
     *            if there will be multiple files
     * @param willRemoveTitles
     *            if a title needs to be removed
     * @param willRemoveDate
     *            if a date needs to be removed
     * @param willRemoveFiles
     *            if one or more files need to be removed
     */
    private void showVerifyPrune(Context context, HttpServletRequest request,
            HttpServletResponse response, SubmissionInfo subInfo,
            boolean multipleTitles, boolean publishedBefore,
            boolean multipleFiles) throws SQLException, ServletException,
            IOException
    {
        // Verify pruning of extra bits
        request.setAttribute("multiple.titles", new Boolean(multipleTitles));
        request.setAttribute("published.before", new Boolean(publishedBefore));
        request.setAttribute("multiple.files", new Boolean(multipleFiles));
        request.setAttribute("will.remove.titles",
                new Boolean(willRemoveTitles));
        request.setAttribute("will.remove.date", new Boolean(willRemoveDate));
        request.setAttribute("will.remove.files", new Boolean(willRemoveFiles));
        request.setAttribute("button.pressed", UIUtil.getSubmitButton(request,
                NEXT_BUTTON));

        // forward to verify prune JSP
        // forward to initial questions JSP
        JSPStepManager.showJSP(request, response, subInfo, VERIFY_PRUNE_JSP);
    }
}

