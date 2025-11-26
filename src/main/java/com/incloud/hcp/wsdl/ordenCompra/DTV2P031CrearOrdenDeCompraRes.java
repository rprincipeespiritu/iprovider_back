
package com.incloud.hcp.wsdl.ordenCompra;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ....
 * 
 * <p>Clase Java para DT_v2_P031_CrearOrdenDeCompra_Res complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DT_v2_P031_CrearOrdenDeCompra_Res">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Response" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="EBELN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Log" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="MESAJ" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DT_v2_P031_CrearOrdenDeCompra_Res", propOrder = {
    "response",
    "log"
})
public class DTV2P031CrearOrdenDeCompraRes {

    @XmlElement(name = "Response")
    protected Response response;
    @XmlElement(name = "Log")
    protected Log log;

    /**
     * Obtiene el valor de la propiedad response.
     * 
     * @return
     *     possible object is
     *     {@link Response }
     *     
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Define el valor de la propiedad response.
     * 
     * @param value
     *     allowed object is
     *     {@link Response }
     *     
     */
    public void setResponse(Response value) {
        this.response = value;
    }

    /**
     * Obtiene el valor de la propiedad log.
     * 
     * @return
     *     possible object is
     *     {@link Log }
     *     
     */
    public Log getLog() {
        return log;
    }

    /**
     * Define el valor de la propiedad log.
     * 
     * @param value
     *     allowed object is
     *     {@link Log }
     *     
     */
    public void setLog(Log value) {
        this.log = value;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="MESAJ" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "code",
        "mesaj"
    })
    public static class Log {

        @XmlElement(name = "CODE")
        protected String code;
        @XmlElement(name = "MESAJ")
        protected String mesaj;

        /**
         * Obtiene el valor de la propiedad code.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCODE() {
            return code;
        }

        /**
         * Define el valor de la propiedad code.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCODE(String value) {
            this.code = value;
        }

        /**
         * Obtiene el valor de la propiedad mesaj.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMESAJ() {
            return mesaj;
        }

        /**
         * Define el valor de la propiedad mesaj.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMESAJ(String value) {
            this.mesaj = value;
        }

        @Override
        public String toString() {
            return "Log{" +
                    "code='" + code + '\'' +
                    ", mesaj='" + mesaj + '\'' +
                    '}';
        }
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="EBELN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "ebeln"
    })
    public static class Response {

        @XmlElement(name = "EBELN")
        protected String ebeln;

        /**
         * Obtiene el valor de la propiedad ebeln.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEBELN() {
            return ebeln;
        }

        /**
         * Define el valor de la propiedad ebeln.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEBELN(String value) {
            this.ebeln = value;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "ebeln='" + ebeln + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DTV2P031CrearOrdenDeCompraRes{" +
                "response=" + response +
                ", log=" + log +
                '}';
    }
}
