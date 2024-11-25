package com.example.labstr.models;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.File;

public class XmlUtil {

    public static void saveToXml(BonusCardXml bonusCard, String filePath) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(BonusCardXml.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        File file = new File(filePath);
        marshaller.marshal(bonusCard, file);
    }
}