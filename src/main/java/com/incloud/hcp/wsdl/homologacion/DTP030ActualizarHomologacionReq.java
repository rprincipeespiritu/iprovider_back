
package com.incloud.hcp.wsdl.homologacion;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para DT_P030_ActualizarHomologacion_Req complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DT_P030_ActualizarHomologacion_Req">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LIFNR" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="NOTA" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="35"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FEEXP" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="HOMOL" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DT_P030_ActualizarHomologacion_Req", propOrder = {
    "lifnr",
    "nota",
    "feexp",
    "homol"
})
public class DTP030ActualizarHomologacionReq {

    @XmlElement(name = "LIFNR")
    protected String lifnr;
    @XmlElement(name = "NOTA")
    protected String nota;
    @XmlElement(name = "FEEXP")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar feexp;
    @XmlElement(name = "HOMOL")
    protected String homol;

    /**
     * Obtiene el valor de la propiedad lifnr.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLIFNR() {
        return lifnr;
    }

    /**
     * Define el valor de la propiedad lifnr.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLIFNR(String value) {
        this.lifnr = value;
    }

    /**
     * Obtiene el valor de la propiedad nota.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNOTA() {
        return nota;
    }

    /**
     * Define el valor de la propiedad nota.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNOTA(String value) {
        this.nota = value;
    }

    /**
     * Obtiene el valor de la propiedad feexp.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFEEXP() {
        return feexp;
    }

    /**
     * Define el valor de la propiedad feexp.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFEEXP(XMLGregorianCalendar value) {
        this.feexp = value;
    }

    /**
     * Obtiene el valor de la propiedad homol.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHOMOL() {
        return homol;
    }

    /**
     * Define el valor de la propiedad homol.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHOMOL(String value) {
        this.homol = value;
    }

}
