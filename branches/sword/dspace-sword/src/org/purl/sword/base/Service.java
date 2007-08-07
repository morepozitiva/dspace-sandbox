package org.purl.sword.base;

/**
 *   Author   : $Author$
 *   Date     : $Date$
 *   Revision : $Revision$
 *   Name     : $Name$
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.purl.sword.Namespaces;

import nu.xom.Element; 
import nu.xom.Elements;

/**
 * Represents an Atom Publishing Protocol Service element, with 
 * SWORD extensions. 
 * 
 * @author Neil Taylor
 */
public class Service extends XmlElement implements SwordElementInterface
{
   /**
    * The local name for the element. 
    */
   private static final String ELEMENT_NAME = "service";
   
   /**
    * The service compliance level. 
    */
   private ServiceLevel complianceLevel; 
   
   /**
    * The noOp value. 
    */
   private boolean noOp; 
   
   /**
    * Flag to record if the noOp value has been set by a user of the class. 
    */
   private boolean isNoOp; 
   
   /**
    * The verbose value. 
    */
   private boolean verbose;
   
   /**
    * Flag to record if the verbose value has been set by a user of the class. 
    */
   private boolean isVerbose; 
   
   /**
    * List of Workspaces. 
    */
   private List<Workspace> workspaces; 
   
   /**
    * Create a new instance. 
    */
   public Service()
   {
      super("app");
      
      isVerbose = false;
      isNoOp = false; 
      workspaces = new ArrayList<Workspace>();
      complianceLevel = ServiceLevel.UNDEFINED;
   }
   
   /**
    * Create a new instance. 
    * 
    * @param complianceLevel The service compliance level. 
    */
   public Service( ServiceLevel complianceLevel)
   {
      this();
      this.complianceLevel = complianceLevel;
   }
   
   /**
    * Create a new instance with the specified compliance level, noOp and 
    * verbose values. 
    * 
    * @param complianceLevel  The service compliance level. 
    * @param noOp             The noOp.
    * @param verbose          The verbose element. 
    */
   public Service( ServiceLevel complianceLevel, boolean noOp, boolean verbose ) 
   {
      this();
      this.complianceLevel = complianceLevel; 
      setNoOp(noOp);
      setVerbose(verbose);
   }

   /**
    * Get the service compliance level. 
    * 
    * @return The compliance level. 
    */
   public ServiceLevel getComplianceLevel()
   {
      return complianceLevel;
   }

   /**
    * Set the service compliance level. 
    * 
    * @param The compliance level. 
    */
   public void setComplianceLevel(ServiceLevel complianceLevel)
   {
      this.complianceLevel = complianceLevel;
   }

   /**
    * Get the NoOp value. 
    * 
    * @return The value. 
    */
   public boolean isNoOp()
   {
      return noOp;
   }

   /**
    * Set the NoOp value. 
    * 
    * @param noOp The value. 
    */
   public void setNoOp(boolean noOp)
   {
      this.noOp = noOp;
      isNoOp = true;
   }
   
   /**
    * Determine if the NoOp value has been set. This should be called to 
    * check if an item has been programatically set and does not have a
    * default value. 
    * 
    * @return True if it has been set programmatically. Otherwise, false. 
    */
   public boolean isNoOpSet()
   {
      return isNoOp; 
   }

   /**
    * Get the Verbose setting. 
    * 
    * @return The value. 
    */
   public boolean isVerbose()
   {
      return verbose;
   }

   /**
    * Set the Verbose value. 
    * 
    * @param verbose The value. 
    */
   public void setVerbose(boolean verbose)
   {
      this.verbose = verbose;
      isVerbose = true;  
   }

   /**
    * Determine if the Verbose value has been set. This should be called to 
    * check if an item has been programatically set and does not have a
    * default value. 
    * 
    * @return True if it has been set programmatically. Otherwise, false. 
    */
   public boolean isVerboseSet()
   {
      return isVerbose; 
   }
   
   /**
    * Get a list of workspaces. 
    * 
    * @return The workspace. 
    */
   public Iterator<Workspace> getWorkspaces()
   {
      return workspaces.iterator();
   }

   /**
    * Add a workspace. 
    * 
    * @param workspace The workspace. 
    */
   public void addWorkspace(Workspace workspace)
   {
      this.workspaces.add(workspace);
   }
   
   /**
    * Clear the list of workspaces. 
    */
   public void clearWorkspaces()
   {
	   this.workspaces.clear();
   }
   
   /**
    * Marshall the data in this object to an Element object. 
    * 
    * @return A XOM Element that holds the data for this Content element. 
    */
   public Element marshall( )
   {
      Element service = new Element(ELEMENT_NAME, Namespaces.NS_APP);
      service.addNamespaceDeclaration("atom", Namespaces.NS_ATOM);
      service.addNamespaceDeclaration("dcterms", Namespaces.NS_DC_TERMS);
      service.addNamespaceDeclaration("sword", Namespaces.NS_SWORD);
      
      
      if( complianceLevel != ServiceLevel.UNDEFINED )
      {
    	  System.out.println("The compliance level is: " + complianceLevel );
         Element compliance = new Element("sword:level", Namespaces.NS_SWORD);
         compliance.appendChild(Integer.toString(complianceLevel.number()));
         service.appendChild(compliance);
      }
      
      if( isVerboseSet() )
      {
         Element verboseElement = new Element("sword:verbose", Namespaces.NS_SWORD); 
         verboseElement.appendChild(Boolean.toString(verbose));
         service.appendChild(verboseElement);
      }
      
      if( isNoOpSet() ) 
      {
         Element noOpElement = new Element("sword:noOp", Namespaces.NS_SWORD);
         noOpElement.appendChild(Boolean.toString(noOp)); 
         service.appendChild(noOpElement);
      }
      
      for( Workspace item : workspaces )
      {
         service.appendChild(item.marshall());
      }
      
      return service;    
   }
   
   
   /**
    * Get a service level that corresponds to the specified value. Used 
    * during the unmarshall process. 
    * 
    * @param level The integer version of a service level. 
    * 
    * @return If the parameter matches one of the defined levels, 
    *    a ServiceLevel of ONE or ZERO is returned. Otherwise, 
    *    ServiceLevel.UNDEFINED is returned. 
    */
   private ServiceLevel getServiceLevel( int level ) 
   {
      ServiceLevel theLevel = ServiceLevel.UNDEFINED; 
      
      switch( level )
      {
         case 0: 
            theLevel = ServiceLevel.ZERO;
            break;
               
         case 1: 
            theLevel = ServiceLevel.ONE;
            break;
               
         default: 
            // FIXME - should this default to ZERO instead? 
            theLevel = ServiceLevel.UNDEFINED;
            InfoLogger.getLogger().writeError("Invalid value for sword:level");
            break;
      }
      
      return theLevel; 
      
   }
     
   /**
    * Unmarshall the content element into the data in this object. 
    * 
    * @throws UnmarshallException If the element does not contain a
    *                             content element or if there are problems
    *                             accessing the data. 
    */
   public void unmarshall( Element service )
   throws UnmarshallException
   {
      if( ! isInstanceOf(service, "service", Namespaces.NS_APP))
      {
         throw new UnmarshallException( "Not an app:service element" );
      }
      
      try
      {
         workspaces.clear(); 
         
         // retrieve all of the sub-elements
         Elements elements = service.getChildElements();
         Element element = null; 
         int length = elements.size();
         
         for(int i = 0; i < length; i++ )
         {
            element = elements.get(i);

            if( isInstanceOf(element, "level", Namespaces.NS_SWORD ) )
            {
               int level = unmarshallInteger(element); 
               complianceLevel = getServiceLevel(level);
            }
            else if( isInstanceOf(element, "verbose", Namespaces.NS_SWORD))
            {
               setVerbose(unmarshallBoolean(element)); 
            }
            else if( isInstanceOf(element, "noOp", Namespaces.NS_SWORD))
            {
               setNoOp(unmarshallBoolean(element));
            }
            else if( isInstanceOf(element, "workspace", Namespaces.NS_APP ))
            {
               Workspace workspace = new Workspace( );
               workspace.unmarshall(element);
               workspaces.add(workspace);
            }
         }
      }
      catch( Exception ex )
      {
         InfoLogger.getLogger().writeError("Unable to parse an element in Service: " + ex.getMessage());
         throw new UnmarshallException("Unable to parse element in Service", ex);
      }
   }
}