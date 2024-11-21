package org.portico.lrc.model;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
public class RTIPolicy
{
    private Map<String,Set<String>> federationPolicies; // Map<federationName, allowedFederates>
    public RTIPolicy( String filePath )
        throws Exception
    {
        this.federationPolicies = new HashMap<>();
        loadPolicy( filePath );
    }
    private void loadPolicy( String filePath ) throws Exception
    {
        File policyFile = new File( filePath );
        if( !policyFile.exists() )
        {
            throw new IllegalArgumentException( "RTIPolicy file not found: " + filePath );
        }
        // Parse the XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse( policyFile );
        doc.getDocumentElement().normalize();
        // Read federation policies
        NodeList federationNodes = doc.getElementsByTagName( "Federation" );
        for( int i = 0; i < federationNodes.getLength(); i++ )
        {
            Element federationElement = (Element)federationNodes.item( i );
            String federationName = federationElement.getAttribute( "name" );
            Set<String> allowedFederates = new HashSet<>();
            NodeList federateNodes = federationElement.getElementsByTagName( "Federate" );
            for( int j = 0; j < federateNodes.getLength(); j++ )
            {
                Element federateElement = (Element)federateNodes.item( j );
                String federateName = federateElement.getAttribute( "name" );
                allowedFederates.add( federateName );
            }
            federationPolicies.put( federationName, allowedFederates );
        }
    }
    public boolean isFederateAllowed( String federationName, String federateName )
    {
        Set<String> allowedFederates = federationPolicies.get( federationName );
        return allowedFederates != null && allowedFederates.contains( federateName );
    }
}