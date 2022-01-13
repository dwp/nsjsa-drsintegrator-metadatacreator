//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.01.11 at 08:29:06 AM GMT 
//


package uk.gov.govtalk.drs.common.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * AAnnnnnnA or AAnnnnnn
 * <p>
 * <p>Java class for NINO complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="NINO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NINoBody">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[A-CEHJ-MOPRSW-Y]{1}[A-CEGHJ-NPR-TW-Z]{1}[0-9]{6}"/>
 *               &lt;pattern value="[\S\s\d]{0,0}"/>
 *               &lt;pattern value="[G]{1}[ACEGHJ-NPR-TW-Z]{1}[0-9]{6}"/>
 *               &lt;pattern value="[N]{1}[A-CEGHJL-NPR-TW-Z]{1}[0-9]{6}"/>
 *               &lt;pattern value="[T]{1}[A-CEGHJ-MPR-TW-Z]{1}[0-9]{6}"/>
 *               &lt;pattern value="[Z]{1}[A-CEGHJ-NPR-TW-Y]{1}[0-9]{6}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="NINoSuffix" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[A-D ]{1}"/>
 *               &lt;pattern value="[\S\s\d]{0,0}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NINO", propOrder = {
        "niNoBody",
        "niNoSuffix"
})
public class NINO {

    @XmlElement(name = "NINoBody", required = true)
    protected String niNoBody;
    @XmlElement(name = "NINoSuffix")
    protected String niNoSuffix;

    /**
     * Gets the value of the niNoBody property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getNINoBody() {
        return niNoBody;
    }

    /**
     * Sets the value of the niNoBody property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setNINoBody(String value) {
        this.niNoBody = value;
    }

    /**
     * Gets the value of the niNoSuffix property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getNINoSuffix() {
        return niNoSuffix;
    }

    /**
     * Sets the value of the niNoSuffix property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setNINoSuffix(String value) {
        this.niNoSuffix = value;
    }

}