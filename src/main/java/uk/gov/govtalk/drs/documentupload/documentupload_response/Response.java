//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.01.11 at 08:29:06 AM GMT 
//


package uk.gov.govtalk.drs.documentupload.documentupload_response;

import uk.gov.govtalk.drs.common.response.DRSResponseHeader;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="header" type="{http://www.govtalk.gov.uk/drs/common/response}DRS_Response_Header"/>
 *         &lt;element name="body" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="DocumentID" type="{http://www.govtalk.gov.uk/drs/common/metadata}GUID"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "header",
        "body"
})
@XmlRootElement(name = "response")
public class Response {

    @XmlElement(required = true)
    protected DRSResponseHeader header;
    protected Response.Body body;

    /**
     * Gets the value of the header property.
     *
     * @return possible object is
     * {@link DRSResponseHeader }
     */
    public DRSResponseHeader getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     *
     * @param value allowed object is
     *              {@link DRSResponseHeader }
     */
    public void setHeader(DRSResponseHeader value) {
        this.header = value;
    }

    /**
     * Gets the value of the body property.
     *
     * @return possible object is
     * {@link Response.Body }
     */
    public Response.Body getBody() {
        return body;
    }

    /**
     * Sets the value of the body property.
     *
     * @param value allowed object is
     *              {@link Response.Body }
     */
    public void setBody(Response.Body value) {
        this.body = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * <p>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="DocumentID" type="{http://www.govtalk.gov.uk/drs/common/metadata}GUID"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "documentID"
    })
    public static class Body {

        @XmlElement(name = "DocumentID", required = true)
        protected String documentID;

        /**
         * Gets the value of the documentID property.
         *
         * @return possible object is
         * {@link String }
         */
        public String getDocumentID() {
            return documentID;
        }

        /**
         * Sets the value of the documentID property.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setDocumentID(String value) {
            this.documentID = value;
        }

    }

}
