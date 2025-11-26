
package com.incloud.hcp.wsdl.solped;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para DT_P026_ConsultaSOLPED_Res complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DT_P026_ConsultaSOLPED_Res">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Detalle" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="BANFN" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="10"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="BNFPO" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="5"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="BSART" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="4"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="LOEKZ" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="1"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="WERKS" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="4"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="LGORT" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="4"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="MATNR" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="18"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="MENGE" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
 *                         &lt;fractionDigits value="3"/>
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
 *                   &lt;element name="LFDAT" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *                   &lt;element name="MATKL" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="9"/>
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
 *                   &lt;element name="PACKNO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="INTROW" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="10"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="SRVPOS" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="18"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="SMENGE" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                   &lt;element name="SMEINS" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="0"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
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
@XmlType(name = "DT_P026_ConsultaSOLPED_Res", propOrder = {
    "detalle",
    "log"
})
public class DTP026ConsultaSOLPEDRes {

    @XmlElement(name = "Detalle")
    protected List<Detalle> detalle;
    @XmlElement(name = "Log")
    protected Log log;

    /**
     * Gets the value of the detalle property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the detalle property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDetalle().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Detalle }
     * 
     * 
     */
    public List<Detalle> getDetalle() {
        if (detalle == null) {
            detalle = new ArrayList<Detalle>();
        }
        return this.detalle;
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
     *         &lt;element name="BANFN" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="10"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="BNFPO" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="5"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="BSART" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="4"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="LOEKZ" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="1"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="WERKS" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="4"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="LGORT" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="4"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="MATNR" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="18"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="MENGE" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
     *               &lt;fractionDigits value="3"/>
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
     *         &lt;element name="LFDAT" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
     *         &lt;element name="MATKL" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="9"/>
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
     *         &lt;element name="PACKNO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="INTROW" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="10"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="SRVPOS" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="18"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="SMENGE" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
     *         &lt;element name="SMEINS" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="0"/>
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
        "banfn",
        "bnfpo",
        "bsart",
        "loekz",
        "werks",
        "lgort",
        "matnr",
        "menge",
        "meins",
        "lfdat",
        "matkl",
        "mfrpn",
        "packno",
        "introw",
        "srvpos",
        "smenge",
        "smeins"
    })
    public static class Detalle {

        @XmlElement(name = "BANFN")
        protected String banfn;
        @XmlElement(name = "BNFPO")
        protected String bnfpo;
        @XmlElement(name = "BSART")
        protected String bsart;
        @XmlElement(name = "LOEKZ")
        protected String loekz;
        @XmlElement(name = "WERKS")
        protected String werks;
        @XmlElement(name = "LGORT")
        protected String lgort;
        @XmlElement(name = "MATNR")
        protected String matnr;
        @XmlElement(name = "MENGE")
        protected BigDecimal menge;
        @XmlElement(name = "MEINS")
        protected String meins;
        @XmlElement(name = "LFDAT")
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar lfdat;
        @XmlElement(name = "MATKL")
        protected String matkl;
        @XmlElement(name = "MFRPN")
        protected String mfrpn;
        @XmlElement(name = "PACKNO")
        protected String packno;
        @XmlElement(name = "INTROW")
        protected String introw;
        @XmlElement(name = "SRVPOS")
        protected String srvpos;
        @XmlElement(name = "SMENGE")
        protected Integer smenge;
        @XmlElement(name = "SMEINS")
        protected String smeins;

        /**
         * Obtiene el valor de la propiedad banfn.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBANFN() {
            return banfn;
        }

        /**
         * Define el valor de la propiedad banfn.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBANFN(String value) {
            this.banfn = value;
        }

        /**
         * Obtiene el valor de la propiedad bnfpo.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBNFPO() {
            return bnfpo;
        }

        /**
         * Define el valor de la propiedad bnfpo.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBNFPO(String value) {
            this.bnfpo = value;
        }

        /**
         * Obtiene el valor de la propiedad bsart.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBSART() {
            return bsart;
        }

        /**
         * Define el valor de la propiedad bsart.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBSART(String value) {
            this.bsart = value;
        }

        /**
         * Obtiene el valor de la propiedad loekz.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLOEKZ() {
            return loekz;
        }

        /**
         * Define el valor de la propiedad loekz.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLOEKZ(String value) {
            this.loekz = value;
        }

        /**
         * Obtiene el valor de la propiedad werks.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getWERKS() {
            return werks;
        }

        /**
         * Define el valor de la propiedad werks.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setWERKS(String value) {
            this.werks = value;
        }

        /**
         * Obtiene el valor de la propiedad lgort.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLGORT() {
            return lgort;
        }

        /**
         * Define el valor de la propiedad lgort.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLGORT(String value) {
            this.lgort = value;
        }

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
         * Obtiene el valor de la propiedad menge.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getMENGE() {
            return menge;
        }

        /**
         * Define el valor de la propiedad menge.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setMENGE(BigDecimal value) {
            this.menge = value;
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
         * Obtiene el valor de la propiedad lfdat.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getLFDAT() {
            return lfdat;
        }

        /**
         * Define el valor de la propiedad lfdat.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setLFDAT(XMLGregorianCalendar value) {
            this.lfdat = value;
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
         * Obtiene el valor de la propiedad packno.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPACKNO() {
            return packno;
        }

        /**
         * Define el valor de la propiedad packno.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPACKNO(String value) {
            this.packno = value;
        }

        /**
         * Obtiene el valor de la propiedad introw.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getINTROW() {
            return introw;
        }

        /**
         * Define el valor de la propiedad introw.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setINTROW(String value) {
            this.introw = value;
        }

        /**
         * Obtiene el valor de la propiedad srvpos.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSRVPOS() {
            return srvpos;
        }

        /**
         * Define el valor de la propiedad srvpos.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSRVPOS(String value) {
            this.srvpos = value;
        }

        /**
         * Obtiene el valor de la propiedad smenge.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getSMENGE() {
            return smenge;
        }

        /**
         * Define el valor de la propiedad smenge.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setSMENGE(Integer value) {
            this.smenge = value;
        }

        /**
         * Obtiene el valor de la propiedad smeins.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSMEINS() {
            return smeins;
        }

        /**
         * Define el valor de la propiedad smeins.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSMEINS(String value) {
            this.smeins = value;
        }

        @Override
        public String toString() {
            return "Detalle{" +
                    "banfn='" + banfn + '\'' +
                    ", bnfpo='" + bnfpo + '\'' +
                    ", bsart='" + bsart + '\'' +
                    ", loekz='" + loekz + '\'' +
                    ", werks='" + werks + '\'' +
                    ", lgort='" + lgort + '\'' +
                    ", matnr='" + matnr + '\'' +
                    ", menge=" + menge +
                    ", meins='" + meins + '\'' +
                    ", lfdat=" + lfdat +
                    ", matkl='" + matkl + '\'' +
                    ", mfrpn='" + mfrpn + '\'' +
                    ", packno='" + packno + '\'' +
                    ", introw='" + introw + '\'' +
                    ", srvpos='" + srvpos + '\'' +
                    ", smenge=" + smenge +
                    ", smeins='" + smeins + '\'' +
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

    }

}
