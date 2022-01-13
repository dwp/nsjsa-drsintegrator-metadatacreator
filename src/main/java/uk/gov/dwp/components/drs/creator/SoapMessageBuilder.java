package uk.gov.dwp.components.drs.creator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.components.drs.creator.exceptions.MetadataCreatorPermanentException;
import uk.gov.govtalk.drs.documentupload.documentupload_request.Request;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayOutputStream;

/**
 * Builds a SOAP message to wrap the DRS request
 * <p>
 * This implementation simply marshals the Request via JAXB and then uses SAAJ to
 * wrap in a valid SOAP message body
 */
public class SoapMessageBuilder {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SoapMessageBuilder.class);

    /**
     * JAXB Context for marshalling Request objects back to XML
     */
    private JAXBContext jaxbContext = null;

    /**
     * Schema for validating generated XML
     */
    private Schema drsSchema = null;

    /**
     * Default, no-argument constructor. Prepares and caches a JAXB context for request marhsalling
     * along with a schema instance for request validation.
     * Logs errors when initialisation fails.
     */
    public SoapMessageBuilder() {
        try {
            jaxbContext = JAXBContext.newInstance(Request.class);
        } catch (JAXBException eJax) {
            LOGGER.error("Unable to create JAXB Context for request marshalling : {}", eJax.getMessage());
            LOGGER.debug("Unable to create JAXB Context for request marshalling", eJax);
        }
        try {
            SchemaFactory schemaFactory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            drsSchema = schemaFactory.newSchema(new Source[]{
                    new StreamSource(this.getClass().getResourceAsStream("/DocumentUpload/Schemas/common/metadata.xsd")),
                    new StreamSource(this.getClass().getResourceAsStream("/DocumentUpload/Schemas/common/request.xsd")),
                    new StreamSource(this.getClass().getResourceAsStream("/DocumentUpload/Schemas/transactions/DocumentUpload/DocumentUpload-Request.xsd")),
            });
        } catch (Exception e) {
            LOGGER.error("Unable to load DRS XML Schema for request validation {}", e.getMessage());
            LOGGER.debug("Unable to load DRS XML Schema for request validation", e);
        }
    }

    /**
     * Assemble a DRS SOAP request string from the DRS request model object
     *
     * @param request populated request details
     * @return SOAP request string for submission to DRS
     * @throws MetadataCreatorPermanentException if the request cannot be converted to SOAP
     */
    public String buildSoapMessage(Request request) throws MetadataCreatorPermanentException {
        try {
            // Create a template SOAP message
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPBody messageBody = soapMessage.getSOAPBody();

            // Marshal the request to XML and insert into the SOAP body
            Marshaller requestMarshaller = jaxbContext.createMarshaller();
            requestMarshaller.setSchema(drsSchema);
            requestMarshaller.marshal(request, messageBody);
            soapMessage.saveChanges();

            // Render and return as a string
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            soapMessage.writeTo(bs);
            return new String(bs.toByteArray(), "UTF-8");
        } catch (MarshalException eMarshall) {
            LOGGER.warn("Request properties are not schema compliant", eMarshall);
            throw new MetadataCreatorPermanentException("Request fails schema validation: " +
                    (eMarshall.getCause() != null ? eMarshall.getCause().getMessage() : "(no details available)"), eMarshall);
        } catch (Exception e) {
            LOGGER.warn("Unable to render SOAP request", e);
            throw new MetadataCreatorPermanentException("Unable to render SOAP request", e);
        }
    }
}
