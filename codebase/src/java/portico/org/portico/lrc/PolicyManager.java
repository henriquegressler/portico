package org.portico.lrc;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.*;

public class PolicyManager {
    private Map<String, Profile> profiles = new HashMap<>();
    private Map<String, String> federateAssignments = new HashMap<>();

    public PolicyManager(String policyFilePath) throws Exception {
        parsePolicyFile(policyFilePath);
    }

    private void parsePolicyFile(String policyFilePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(policyFilePath));

        // Parse profiles
        NodeList profileNodes = document.getElementsByTagName("profileFederate");
        for (int i = 0; i < profileNodes.getLength(); i++) {
            Element profileElement = (Element) profileNodes.item(i);
            String profileName = profileElement.getAttribute("name");

            Profile profile = new Profile(profileName);
            NodeList accessRights = profileElement.getElementsByTagName("accessRight");
            for (int j = 0; j < accessRights.getLength(); j++) {
                Element accessElement = (Element) accessRights.item(j);
                String topic = accessElement.getAttribute("topic");
                String operations = accessElement.getAttribute("op");
                profile.addAccess(topic, operations.split(","));
            }
            profiles.put(profileName, profile);
        }

        // Parse assignments
        NodeList assignNodes = document.getElementsByTagName("profileAssign");
        for (int i = 0; i < assignNodes.getLength(); i++) {
            Element assignElement = (Element) assignNodes.item(i);
            String federate = assignElement.getAttribute("federate");
            String profile = assignElement.getAttribute("profile");
            federateAssignments.put(federate, profile);
        }
    }

    public Profile getProfileForFederate(String federateName) {
        String profileName = federateAssignments.get(federateName);
        return profiles.get(profileName);
    }
}
