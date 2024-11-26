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
    private static RTIPolicy instance;
    private Map<String,Map<String,Set<String>>> multiFederationPolicies;
    private String filePath;
    private String policyName;
    private String policyHash;

    private RTIPolicy( String filePath )
        throws Exception
    {
        this.multiFederationPolicies = new HashMap<>();
        this.filePath = filePath;
        loadPolicy( filePath );
    }

    public static synchronized RTIPolicy getInstance( String filePath ) throws Exception
    {
        if( instance == null )
        {
            instance = new RTIPolicy( filePath );
        }
        return instance;
    }

    public static synchronized RTIPolicy getInstance()
    {
        if( instance == null )
        {
            throw new IllegalStateException( "RTIPolicy has not been initialized yet." );
        }
        return instance;
    }

    private void loadPolicy( String filePath ) throws Exception
    {
        File policyFile = new File( filePath );
        if( !policyFile.exists() )
        {
            throw new IllegalArgumentException( "RTIPolicy file not found: " + filePath );
        }

        // Calcula o hash do arquivo
        this.policyHash = calculateSHA256( policyFile );

        // Parse o arquivo XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse( policyFile );
        doc.getDocumentElement().normalize();

        // Captura o nome da política RTI
        Element rootElement = doc.getDocumentElement();
        this.policyName = rootElement.getAttribute( "name" );

        // Ler as políticas de todas as federações
        NodeList federationNodes = doc.getElementsByTagName( "Federation" );
        for( int i = 0; i < federationNodes.getLength(); i++ )
        {
            Element federationElement = (Element)federationNodes.item( i );
            String federationName = federationElement.getAttribute( "name" );

            // Cria um mapa para os federados permitidos desta federação
            Map<String,Set<String>> federationAllowedFederates = new HashMap<>();

            // Processa federados permitidos
            Set<String> allowedFederates = new HashSet<>();
            NodeList allowedFederateNodes =
                federationElement.getElementsByTagName( "allowedFederate" );
            for( int j = 0; j < allowedFederateNodes.getLength(); j++ )
            {
                Element federateElement = (Element)allowedFederateNodes.item( j );
                String federateName = federateElement.getAttribute( "name" );
                allowedFederates.add( federateName );
            }

            federationAllowedFederates.put( "allowedFederates", allowedFederates );

            // Adiciona a política desta federação ao mapa de políticas
            multiFederationPolicies.put( federationName, federationAllowedFederates );
        }
    }

    private String calculateSHA256( File file ) throws Exception
    {
        // Método de cálculo de hash permanece igual ao original
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

    // Verifica se um federado específico é permitido em uma determinada federação
    public boolean isFederateAllowed( String federationName, String federateName )
    {
        Map<String,Set<String>> federationPolicies = multiFederationPolicies.get( federationName );
        if( federationPolicies != null )
        {
            Set<String> allowedFederates = federationPolicies.get( "allowedFederates" );
            return allowedFederates != null && allowedFederates.contains( federateName );
        }
        return false;
    }

    // Verifica se uma federação existe na política
    public boolean isFederationAllowed( String federationName )
    {
        return multiFederationPolicies.containsKey( federationName );
    }

    // Recupera os federados permitidos para uma federação específica
    public Set<String> getAllowedFederates( String federationName )
    {
        Map<String,Set<String>> federationPolicies = multiFederationPolicies.get( federationName );
        return federationPolicies != null ? federationPolicies.get( "allowedFederates" )
                                          : new HashSet<>();
    }

    // Getters permanecem inalterados
    public String getFilePath()
    {
        return this.filePath;
    }

    public String getPolicyName()
    {
        return this.policyName;
    }

    public String getPolicyFileHash()
    {
        return this.policyHash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\nRTI Policy Information\n\n");
        sb.append("Policy Name: ").append(policyName).append("\n");
        sb.append("Policy File Hash (SHA-256): ").append(policyHash).append("\n\n");
    
        sb.append("Federations and Authorized Federates:\n");
        for (String federationName : multiFederationPolicies.keySet()) {
            sb.append("  Federation: ").append(federationName).append("\n");
    
            Map<String, Set<String>> federationPolicies = multiFederationPolicies.get(federationName);
            Set<String> allowedFederates = federationPolicies.get("allowedFederates");
            if (allowedFederates != null) {
                sb.append("    Authorized Federates:\n");
                for (String federateName : allowedFederates) {
                    sb.append("      - Federate: ").append(federateName).append("\n");
                }
            }
    
            sb.append("    Profiles and Access Rights:\n");
            NodeList profileNodes = getProfilesForFederation(federationName); // Helper method to parse profiles
            if (profileNodes != null) {
                for (int i = 0; i < profileNodes.getLength(); i++) {
                    Element profileElement = (Element) profileNodes.item(i);
                    String profileName = profileElement.getAttribute("name");
    
                    sb.append("      - Profile: ").append(profileName).append("\n");
                    NodeList accessRights = profileElement.getElementsByTagName("accessRight");
                    for (int j = 0; j < accessRights.getLength(); j++) {
                        Element accessElement = (Element) accessRights.item(j);
                        String topic = accessElement.getAttribute("topic");
                        String operation = accessElement.getAttribute("op");
    
                        sb.append("          Object/Interaction Class: ").append(topic)
                          .append(" (Operation: ").append(operation).append(")\n");
                    }
                }
            }
        }
    
        return sb.toString();
    }

    // Helper method to parse profiles for a federation
    private NodeList getProfilesForFederation(String federationName) {
        try {
            // Parse the original XML policy file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            NodeList federationNodes = doc.getElementsByTagName("Federation");
            for (int i = 0; i < federationNodes.getLength(); i++) {
                Element federationElement = (Element) federationNodes.item(i);
                if (federationElement.getAttribute("name").equals(federationName)) {
                    return federationElement.getElementsByTagName("profileFederate");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
