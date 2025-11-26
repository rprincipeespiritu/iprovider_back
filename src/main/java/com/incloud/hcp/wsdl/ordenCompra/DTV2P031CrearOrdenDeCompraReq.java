
package com.incloud.hcp.wsdl.ordenCompra;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * ...
 * 
 * <p>Clase Java para DT_v2_P031_CrearOrdenDeCompra_Req complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DT_v2_P031_CrearOrdenDeCompra_Req">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cabecera" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="BSART" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="LIFNR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Detalle" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="BANFN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="POSNR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="MENGE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="KBETR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "DT_v2_P031_CrearOrdenDeCompra_Req", propOrder = {
    "cabecera",
    "detalle"
})
public class DTV2P031CrearOrdenDeCompraReq {

    @XmlElement(name = "Cabecera")
    protected Cabecera cabecera;
    @XmlElement(name = "Detalle")
    protected List<Detalle> detalle;

    /**
     * Obtiene el valor de la propiedad cabecera.
     * 
     * @return
     *     possible object is
     *     {@link Cabecera }
     *     
     */
    public Cabecera getCabecera() {
        return cabecera;
    }

    /**
     * Define el valor de la propiedad cabecera.
     * 
     * @param value
     *     allowed object is
     *     {@link Cabecera }
     *     
     */
    public void setCabecera(Cabecera value) {
        this.cabecera = value;
    }

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



    public void setDetalle(List<DTV2P031CrearOrdenDeCompraReq.Detalle> detalle) {
        this.detalle = detalle;
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
     *         &lt;element name="BSART" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="LIFNR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
        "bsart",
        "lifnr"
    })
    public static class Cabecera {

        @XmlElement(name = "BSART")
        protected String bsart;
        @XmlElement(name = "LIFNR")
        protected String lifnr;

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

        @Override
        public String toString() {
            return "Cabecera{" +
                    "bsart='" + bsart + '\'' +
                    ", lifnr='" + lifnr + '\'' +
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
     *         &lt;element name="BANFN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="POSNR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="MENGE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="KBETR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
        "posnr",
        "menge",
        "kbetr"
    })
    public static class Detalle {

        @XmlElement(name = "BANFN")
        protected String banfn;
        @XmlElement(name = "POSNR")
        protected String posnr;
        @XmlElement(name = "MENGE")
        protected String menge;
        @XmlElement(name = "KBETR")
        protected String kbetr;

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
         * Obtiene el valor de la propiedad posnr.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPOSNR() {
            return posnr;
        }

        /**
         * Define el valor de la propiedad posnr.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPOSNR(String value) {
            this.posnr = value;
        }

        /**
         * Obtiene el valor de la propiedad menge.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMENGE() {
            return menge;
        }

        /**
         * Define el valor de la propiedad menge.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMENGE(String value) {
            this.menge = value;
        }

        /**
         * Obtiene el valor de la propiedad kbetr.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getKBETR() {
            return kbetr;
        }

        /**
         * Define el valor de la propiedad kbetr.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setKBETR(String value) {
            this.kbetr = value;
        }

        @Override
        public String toString() {
            return "Detalle{" +
                    "banfn='" + banfn + '\'' +
                    ", posnr='" + posnr + '\'' +
                    ", menge='" + menge + '\'' +
                    ", kbetr='" + kbetr + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DTV2P031CrearOrdenDeCompraReq{" +
                "cabecera=" + cabecera +
                ", detalle=" + detalle +
                '}';
    }
}
