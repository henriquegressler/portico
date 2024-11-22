package org.portico.lrc.model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RTIPolicy
{
    private Map<String,Set<String>> federationPolicies; // Map<federationName, allowedFederates>
    private String filePath; // Caminho do arquivo RTIPolicy.xml
    private String federationName; // Nome da federação
    private String policyName; // Nome da política RTI
    private String policyHash; // Hash SHA-256 do arquivo RTIPolicy.xml

    public RTIPolicy( String filePath )
        throws Exception
    {
        this.federationPolicies = new HashMap<>();
        this.filePath = filePath; // Salva o caminho do arquivo
        loadPolicy( filePath );
    }

    private void loadPolicy( String filePath ) throws Exception
    {
        File policyFile = new File( filePath );
        if( !policyFile.exists() )
        {
            throw new IllegalArgumentException( "RTIPolicy file not found: " + filePath );
        }

        // Calcular o hash SHA-256 do arquivo
        this.policyHash = calculateSHA256( policyFile );

        // Parse o arquivo XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse( policyFile );
        doc.getDocumentElement().normalize();

        // Captura o nome da política RTI
        Element rootElement = doc.getDocumentElement();
        this.policyName = rootElement.getAttribute( "name" );

        // Ler as políticas de federação
        NodeList federationNodes = doc.getElementsByTagName( "Federation" );
        for( int i = 0; i < federationNodes.getLength(); i++ )
        {
            Element federationElement = (Element)federationNodes.item( i );
            String federationName = federationElement.getAttribute( "name" );

            // Apenas define a federação na primeira leitura
            if( this.federationName == null )
            {
                this.federationName = federationName;
            }

            Set<String> allowedFederates = new HashSet<>();
            NodeList federateNodes = federationElement.getElementsByTagName( "allowedFederate" );
            for( int j = 0; j < federateNodes.getLength(); j++ )
            {
                Element federateElement = (Element)federateNodes.item( j );
                String federateName = federateElement.getAttribute( "name" );
                allowedFederates.add( federateName );
            }
            federationPolicies.put( federationName, allowedFederates );
        }
    }

    private String calculateSHA256( File file ) throws Exception
    {
        MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
        FileInputStream fis = new FileInputStream( file );
        byte[] byteArray = new byte[1024];
        int bytesRead;

        while( (bytesRead = fis.read( byteArray )) != -1 )
        {
            digest.update( byteArray, 0, bytesRead );
        }
        fis.close();

        byte[] hashBytes = digest.digest();

        // Converter bytes para uma representação hexadecimal
        StringBuilder hexString = new StringBuilder();
        for( byte b : hashBytes )
        {
            String hex = Integer.toHexString( 0xff & b );
            if( hex.length() == 1 )
            {
                hexString.append( '0' );
            }
            hexString.append( hex );
        }

        return hexString.toString();
    }

    public boolean isFederateAllowed( String federationName, String federateName )
    {
        Set<String> allowedFederates = federationPolicies.get( federationName );
        return allowedFederates != null && allowedFederates.contains( federateName );
    }

    public boolean isFederationAllowed( String federationName )
    {
        return this.federationName.equals( federationName );
    }

    // Getter para o caminho do arquivo RTIPolicy.xml
    public String getFilePath()
    {
        return this.filePath;
    }

    // Getter para o nome da federação
    public String getFederationName()
    {
        return this.federationName;
    }

    // Getter para o nome da política RTI
    public String getPolicyName()
    {
        return this.policyName;
    }

    // Getter para o hash do arquivo de política
    public String getPolicyFileHash()
    {
        return this.policyHash;
    }
}
