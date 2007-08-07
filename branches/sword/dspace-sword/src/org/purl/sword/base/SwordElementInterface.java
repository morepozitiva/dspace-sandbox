package org.purl.sword.base;

/**
 *   Author   : $Author$
 *   Date     : $Date$
 *   Revision : $Revision$
 *   Name     : $Name$
 */

import nu.xom.Element;

/**
 * Common methods that should be supported by all classes that
 * represent data in the SWORD api. 
 * 
 * @author Neil Taylor
 */
public interface SwordElementInterface
{
   /**
    * Marshall the data in the object to the XOM Element. 
    * 
    * @return The Element. 
    */
   public Element marshall( ); 
  
   /**
    * Unmarshall the data in the specified element and store it
    * in the object. 
    * 
    * @param element The data to unmarshall. 
    * @throws UnmarshallException If the element is not of the 
    * correct type, or if there is an error unmarshalling the data. 
    */
   public void unmarshall( Element element )
   throws UnmarshallException;
}
