
package com.incloud.hcp.wsdl.materiales;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para DT_P027_MaestroMateriales_Res complex type.
 *
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 *
 * <pre>
 * &lt;complexType name="DT_P027_MaestroMateriales_Res">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Response" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="MATNR" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="18"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="MTART" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="4"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="MATKL" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="9"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="MEINS" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="3"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="MFRPN" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="40"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="MAKTX" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="40"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="TAXM1" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="1"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="LOG" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="CODE" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="1"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="MESAJ" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="200"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
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
@XmlType(name = "DT_P027_MaestroMateriales_Res", propOrder = {
        "response",
        "log"
})
public class DTP027MaestroMaterialesRes {

    @XmlElement(name = "Response")
    protected List<DTP027MaestroMaterialesRes.Response> response;
    @XmlElement(name = "LOG")
    protected DTP027MaestroMaterialesRes.LOG log;

    @Override
    public String toString() {
        return "DTP027MaestroMaterialesRes{" +
                "response=" + response +
                ", log=" + log +
                '}';
    }

    /**
     * Gets the value of the response property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the response property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResponse().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DTP027MaestroMaterialesRes.Response }
     *
     *
     */
    public List<DTP027MaestroMaterialesRes.Response> getResponse() {
        if (response == null) {
            response = new ArrayList<DTP027MaestroMaterialesRes.Response>();
        }
        return this.response;
    }

    public void setResponse(List<DTP027MaestroMaterialesRes.Response> response) {
        this.response = response;
    }

    /**
     * Obtiene el valor de la propiedad log.
     *
     * @return
     *     possible object is
     *     {@link DTP027MaestroMaterialesRes.LOG }
     *
     */
    public DTP027MaestroMaterialesRes.LOG getLOG() {
        return log;
    }

    /**
     * Define el valor de la propiedad log.
     *
     * @param value
     *     allowed object is
     *     {@link DTP027MaestroMaterialesRes.LOG }
     *
     */
    public void setLOG(DTP027MaestroMaterialesRes.LOG value) {
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
     *         &lt;element name="CODE" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="1"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="MESAJ" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="200"/>
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
    @XmlType(name = "", propOrder = {
            "code",
            "mesaj"
    })
    public static class LOG {

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
     *         &lt;element name="MATNR" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="18"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="MTART" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="4"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="MATKL" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="9"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="MEINS" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="3"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="MFRPN" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="40"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="MAKTX" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="40"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="TAXM1" minOccurs="0">
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
    @XmlType(name = "", propOrder = {
            "matnr",
            "mtart",
            "matkl",
            "meins",
            "mfrpn",
            "maktx",
            "taxm1"
    })
    public static class Response {

        @XmlElement(name = "MATNR")
        protected String matnr;
        @XmlElement(name = "MTART")
        protected String mtart;
        @XmlElement(name = "MATKL")
        protected String matkl;
        @XmlElement(name = "MEINS")
        protected String meins;
        @XmlElement(name = "MFRPN")
        protected String mfrpn;
        @XmlElement(name = "MAKTX")
        protected String maktx;
        @XmlElement(name = "TAXM1")
        protected String taxm1;

        /**
         * Obtiene el valor de la propiedad matnr.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getMATNR() {
            return matnr;
        }

        /**
         * Define el valor de la propiedad matnr.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setMATNR(String value) {
            this.matnr = value;
        }

        /**
         * Obtiene el valor de la propiedad mtart.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getMTART() {
            return mtart;
        }

        /**
         * Define el valor de la propiedad mtart.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setMTART(String value) {
            this.mtart = value;
        }

        /**
         * Obtiene el valor de la propiedad matkl.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getMATKL() {
            return matkl;
        }

        /**
         * Define el valor de la propiedad matkl.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setMATKL(String value) {
            this.matkl = value;
        }

        /**
         * Obtiene el valor de la propiedad meins.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getMEINS() {
            return meins;
        }

        /**
         * Define el valor de la propiedad meins.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setMEINS(String value) {
            this.meins = value;
        }

        /**
         * Obtiene el valor de la propiedad mfrpn.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getMFRPN() {
            return mfrpn;
        }

        /**
         * Define el valor de la propiedad mfrpn.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setMFRPN(String value) {
            this.mfrpn = value;
        }

        /**
         * Obtiene el valor de la propiedad maktx.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getMAKTX() {
            return maktx;
        }

        /**
         * Define el valor de la propiedad maktx.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setMAKTX(String value) {
            this.maktx = value;
        }

        /**
         * Obtiene el valor de la propiedad taxm1.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getTAXM1() {
            return taxm1;
        }

        /**
         * Define el valor de la propiedad taxm1.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setTAXM1(String value) {
            this.taxm1 = value;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "matnr='" + matnr + '\'' +
                    ", mtart='" + mtart + '\'' +
                    ", matkl='" + matkl + '\'' +
                    ", meins='" + meins + '\'' +
                    ", mfrpn='" + mfrpn + '\'' +
                    ", maktx='" + maktx + '\'' +
                    ", taxm1='" + taxm1 + '\'' +
                    '}';
        }
    }

}