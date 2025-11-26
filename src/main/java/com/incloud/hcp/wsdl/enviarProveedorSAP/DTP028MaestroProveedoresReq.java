
package com.incloud.hcp.wsdl.enviarProveedorSAP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para DT_P028_MaestroProveedores_Req complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DT_P028_MaestroProveedores_Req">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MODO" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="TPROC" minOccurs="0">
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
 *         &lt;element name="GENERALES" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="LIFNR" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="10"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="NAME1" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="40"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="NAME2" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="40"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="NAME3" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="40"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="SORTL" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="20"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="LAND1" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="2"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="REGIO" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="2"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="ORT01" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="35"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="ORT02" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="35"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="PSTLZ" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="10"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="STRAS" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="60"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="KTOKK" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="4"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="STCD1" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="16"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="STCDT" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="2"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="TELF1" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="16"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SOCIEDAD" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ZTERM" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="4"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="AKONT" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="2"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="BANCO" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="BANCOS" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="BANKS" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;maxLength value="3"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="BANKL" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;maxLength value="15"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="BANKN" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;maxLength value="18"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="BKONT" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;maxLength value="2"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="BKREF" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;maxLength value="20"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="COMPRAS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="WAERS" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="5"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EMAILS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="EMAIL" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="SMTP_ADDR" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;maxLength value="241"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="REMARK" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;maxLength value="50"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="RETENCIONS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="RETENCION" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="WITHT" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;maxLength value="2"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="WT_WITHCD" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;maxLength value="2"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
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
@XmlType(name = "DT_P028_MaestroProveedores_Req", propOrder = {
    "modo",
    "generales",
    "sociedad",
    "banco",
    "compras",
    "emails",
    "retencions"
})
public class DTP028MaestroProveedoresReq {

    @XmlElement(name = "MODO")
    protected MODO modo;
    @XmlElement(name = "GENERALES")
    protected GENERALES generales;
    @XmlElement(name = "SOCIEDAD")
    protected SOCIEDAD sociedad;
    @XmlElement(name = "BANCO")
    protected BANCO banco;
    @XmlElement(name = "COMPRAS")
    protected COMPRAS compras;
    @XmlElement(name = "EMAILS")
    protected EMAILS emails;
    @XmlElement(name = "RETENCIONS")
    protected RETENCIONS retencions;

    /**
     * Obtiene el valor de la propiedad modo.
     * 
     * @return
     *     possible object is
     *     {@link MODO }
     *     
     */
    public MODO getMODO() {
        return modo;
    }

    /**
     * Define el valor de la propiedad modo.
     * 
     * @param value
     *     allowed object is
     *     {@link MODO }
     *     
     */
    public void setMODO(MODO value) {
        this.modo = value;
    }

    /**
     * Obtiene el valor de la propiedad generales.
     * 
     * @return
     *     possible object is
     *     {@link GENERALES }
     *     
     */
    public GENERALES getGENERALES() {
        return generales;
    }

    /**
     * Define el valor de la propiedad generales.
     * 
     * @param value
     *     allowed object is
     *     {@link GENERALES }
     *     
     */
    public void setGENERALES(GENERALES value) {
        this.generales = value;
    }

    /**
     * Obtiene el valor de la propiedad sociedad.
     * 
     * @return
     *     possible object is
     *     {@link SOCIEDAD }
     *     
     */
    public SOCIEDAD getSOCIEDAD() {
        return sociedad;
    }

    /**
     * Define el valor de la propiedad sociedad.
     * 
     * @param value
     *     allowed object is
     *     {@link SOCIEDAD }
     *     
     */
    public void setSOCIEDAD(SOCIEDAD value) {
        this.sociedad = value;
    }

    /**
     * Obtiene el valor de la propiedad banco.
     * 
     * @return
     *     possible object is
     *     {@link BANCO }
     *     
     */
    public BANCO getBANCO() {
        return banco;
    }

    /**
     * Define el valor de la propiedad banco.
     * 
     * @param value
     *     allowed object is
     *     {@link BANCO }
     *     
     */
    public void setBANCO(BANCO value) {
        this.banco = value;
    }

    /**
     * Obtiene el valor de la propiedad compras.
     * 
     * @return
     *     possible object is
     *     {@link COMPRAS }
     *     
     */
    public COMPRAS getCOMPRAS() {
        return compras;
    }

    /**
     * Define el valor de la propiedad compras.
     * 
     * @param value
     *     allowed object is
     *     {@link COMPRAS }
     *     
     */
    public void setCOMPRAS(COMPRAS value) {
        this.compras = value;
    }

    /**
     * Obtiene el valor de la propiedad emails.
     * 
     * @return
     *     possible object is
     *     {@link EMAILS }
     *     
     */
    public EMAILS getEMAILS() {
        return emails;
    }

    /**
     * Define el valor de la propiedad emails.
     * 
     * @param value
     *     allowed object is
     *     {@link EMAILS }
     *     
     */
    public void setEMAILS(EMAILS value) {
        this.emails = value;
    }


    /**
     * Obtiene el valor de la propiedad retencions.
     * 
     * @return
     *     possible object is
     *     {@link RETENCIONS }
     *     
     */

    public RETENCIONS getRETENCIONS() {
        return retencions;
    }

    /**
     * Define el valor de la propiedad retencions.
     * 
     * @param value
     *     allowed object is
     *     {@link RETENCIONS }
     *     
     */
    public void setRETENCIONS(RETENCIONS value) {
        this.retencions = value;
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
     *         &lt;element name="BANCOS" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="BANKS" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;maxLength value="3"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="BANKL" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;maxLength value="15"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="BANKN" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;maxLength value="18"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="BKONT" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;maxLength value="2"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="BKREF" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;maxLength value="20"/>
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
    @XmlType(name = "", propOrder = {
        "bancos"
    })
    public static class BANCO {

        @XmlElement(name = "BANCOS")
        protected List<BANCOS> bancos;

        public void setBancos(List<BANCOS> bancos) {
            this.bancos = bancos;
        }

        /**
         * Gets the value of the bancos property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the bancos property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getBANCOS().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BANCOS }
         * 
         * 
         */
        public List<BANCOS> getBANCOS() {
            if (bancos == null) {
                bancos = new ArrayList<BANCOS>();
            }
            return this.bancos;
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
         *         &lt;element name="BANKS" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;maxLength value="3"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="BANKL" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;maxLength value="15"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="BANKN" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;maxLength value="18"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="BKONT" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;maxLength value="2"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="BKREF" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;maxLength value="20"/>
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
            "banks",
            "bankl",
            "bankn",
            "bkont",
            "bkref"
        })
        public static class BANCOS {

            @XmlElement(name = "BANKS")
            protected String banks;
            @XmlElement(name = "BANKL")
            protected String bankl;
            @XmlElement(name = "BANKN")
            protected String bankn;
            @XmlElement(name = "BKONT")
            protected String bkont;
            @XmlElement(name = "BKREF")
            protected String bkref;

            /**
             * Obtiene el valor de la propiedad banks.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getBANKS() {
                return banks;
            }

            /**
             * Define el valor de la propiedad banks.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setBANKS(String value) {
                this.banks = value;
            }

            /**
             * Obtiene el valor de la propiedad bankl.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getBANKL() {
                return bankl;
            }

            /**
             * Define el valor de la propiedad bankl.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setBANKL(String value) {
                this.bankl = value;
            }

            /**
             * Obtiene el valor de la propiedad bankn.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getBANKN() {
                return bankn;
            }

            /**
             * Define el valor de la propiedad bankn.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setBANKN(String value) {
                this.bankn = value;
            }

            /**
             * Obtiene el valor de la propiedad bkont.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getBKONT() {
                return bkont;
            }

            /**
             * Define el valor de la propiedad bkont.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setBKONT(String value) {
                this.bkont = value;
            }

            /**
             * Obtiene el valor de la propiedad bkref.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getBKREF() {
                return bkref;
            }

            /**
             * Define el valor de la propiedad bkref.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setBKREF(String value) {
                this.bkref = value;
            }

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
     *         &lt;element name="WAERS" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="5"/>
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
        "waers"
    })
    public static class COMPRAS {

        @XmlElement(name = "WAERS")
        protected String waers;

        /**
         * Obtiene el valor de la propiedad waers.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getWAERS() {
            return waers;
        }

        /**
         * Define el valor de la propiedad waers.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setWAERS(String value) {
            this.waers = value;
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
     *         &lt;element name="EMAIL" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="SMTP_ADDR" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;maxLength value="241"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="REMARK" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;maxLength value="50"/>
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
    @XmlType(name = "", propOrder = {
        "email"
    })
    public static class EMAILS {

        @XmlElement(name = "EMAIL")
        protected List<EMAIL> email;

        /**
         * Gets the value of the email property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the email property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEMAIL().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link EMAIL }
         * 
         * 
         */
        public List<EMAIL> getEMAIL() {
            if (email == null) {
                email = new ArrayList<EMAIL>();
            }
            return this.email;
        }

        public void setEmail(List<EMAIL> email) {
            this.email = email;
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
         *         &lt;element name="SMTP_ADDR" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;maxLength value="241"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="REMARK" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;maxLength value="50"/>
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
            "smtpaddr",
            "remark"
        })
        public static class EMAIL {

            @XmlElement(name = "SMTP_ADDR")
            protected String smtpaddr;
            @XmlElement(name = "REMARK")
            protected String remark;

            /**
             * Obtiene el valor de la propiedad smtpaddr.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSMTPADDR() {
                return smtpaddr;
            }

            /**
             * Define el valor de la propiedad smtpaddr.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSMTPADDR(String value) {
                this.smtpaddr = value;
            }

            /**
             * Obtiene el valor de la propiedad remark.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getREMARK() {
                return remark;
            }

            /**
             * Define el valor de la propiedad remark.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setREMARK(String value) {
                this.remark = value;
            }

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
     *         &lt;element name="LIFNR" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="10"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="NAME1" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="40"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="NAME2" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="40"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="NAME3" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="40"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="SORTL" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="20"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="LAND1" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="2"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="REGIO" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="2"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="ORT01" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="35"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="ORT02" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="35"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="PSTLZ" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="10"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="STRAS" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="60"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="KTOKK" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="4"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="STCD1" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="16"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="STCDT" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="2"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="TELF1" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="16"/>
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
        "lifnr",
        "name1",
        "name2",
        "name3",
        "sortl",
        "land1",
        "regio",
        "ort01",
        "ort02",
        "pstlz",
        "stras",
        "ktokk",
        "stcd1",
        "stcdt",
        "telf1"
    })
    public static class GENERALES {

        @XmlElement(name = "LIFNR")
        protected String lifnr;
        @XmlElement(name = "NAME1")
        protected String name1;
        @XmlElement(name = "NAME2")
        protected String name2;
        @XmlElement(name = "NAME3")
        protected String name3;
        @XmlElement(name = "SORTL")
        protected String sortl;
        @XmlElement(name = "LAND1")
        protected String land1;
        @XmlElement(name = "REGIO")
        protected String regio;
        @XmlElement(name = "ORT01")
        protected String ort01;
        @XmlElement(name = "ORT02")
        protected String ort02;
        @XmlElement(name = "PSTLZ")
        protected String pstlz;
        @XmlElement(name = "STRAS")
        protected String stras;
        @XmlElement(name = "KTOKK")
        protected String ktokk;
        @XmlElement(name = "STCD1")
        protected String stcd1;
        @XmlElement(name = "STCDT")
        protected String stcdt;
        @XmlElement(name = "TELF1")
        protected String telf1;

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
         * Obtiene el valor de la propiedad name1.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNAME1() {
            return name1;
        }

        /**
         * Define el valor de la propiedad name1.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNAME1(String value) {
            this.name1 = value;
        }

        /**
         * Obtiene el valor de la propiedad name2.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNAME2() {
            return name2;
        }

        /**
         * Define el valor de la propiedad name2.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNAME2(String value) {
            this.name2 = value;
        }

        /**
         * Obtiene el valor de la propiedad name3.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNAME3() {
            return name3;
        }

        /**
         * Define el valor de la propiedad name3.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNAME3(String value) {
            this.name3 = value;
        }

        /**
         * Obtiene el valor de la propiedad sortl.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSORTL() {
            return sortl;
        }

        /**
         * Define el valor de la propiedad sortl.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSORTL(String value) {
            this.sortl = value;
        }

        /**
         * Obtiene el valor de la propiedad land1.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLAND1() {
            return land1;
        }

        /**
         * Define el valor de la propiedad land1.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLAND1(String value) {
            this.land1 = value;
        }

        /**
         * Obtiene el valor de la propiedad regio.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getREGIO() {
            return regio;
        }

        /**
         * Define el valor de la propiedad regio.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setREGIO(String value) {
            this.regio = value;
        }

        /**
         * Obtiene el valor de la propiedad ort01.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getORT01() {
            return ort01;
        }

        /**
         * Define el valor de la propiedad ort01.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setORT01(String value) {
            this.ort01 = value;
        }

        /**
         * Obtiene el valor de la propiedad ort02.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getORT02() {
            return ort02;
        }

        /**
         * Define el valor de la propiedad ort02.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setORT02(String value) {
            this.ort02 = value;
        }

        /**
         * Obtiene el valor de la propiedad pstlz.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPSTLZ() {
            return pstlz;
        }

        /**
         * Define el valor de la propiedad pstlz.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPSTLZ(String value) {
            this.pstlz = value;
        }

        /**
         * Obtiene el valor de la propiedad stras.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSTRAS() {
            return stras;
        }

        /**
         * Define el valor de la propiedad stras.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSTRAS(String value) {
            this.stras = value;
        }

        /**
         * Obtiene el valor de la propiedad ktokk.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getKTOKK() {
            return ktokk;
        }

        /**
         * Define el valor de la propiedad ktokk.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setKTOKK(String value) {
            this.ktokk = value;
        }

        /**
         * Obtiene el valor de la propiedad stcd1.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSTCD1() {
            return stcd1;
        }

        /**
         * Define el valor de la propiedad stcd1.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSTCD1(String value) {
            this.stcd1 = value;
        }

        /**
         * Obtiene el valor de la propiedad stcdt.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSTCDT() {
            return stcdt;
        }

        /**
         * Define el valor de la propiedad stcdt.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSTCDT(String value) {
            this.stcdt = value;
        }

        /**
         * Obtiene el valor de la propiedad telf1.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTELF1() {
            return telf1;
        }

        /**
         * Define el valor de la propiedad telf1.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTELF1(String value) {
            this.telf1 = value;
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
     *         &lt;element name="TPROC" minOccurs="0">
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
        "tproc"
    })
    public static class MODO {

        @XmlElement(name = "TPROC")
        protected String tproc;

        /**
         * Obtiene el valor de la propiedad tproc.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTPROC() {
            return tproc;
        }

        /**
         * Define el valor de la propiedad tproc.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTPROC(String value) {
            this.tproc = value;
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
     *         &lt;element name="RETENCION" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="WITHT" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;maxLength value="2"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="WT_WITHCD" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;maxLength value="2"/>
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
    @XmlType(name = "", propOrder = {
        "retencion"
    })
    public static class RETENCIONS {

        @XmlElement(name = "RETENCION")
        protected List<RETENCION> retencion;

        public void setRetencion(List<RETENCION> retencion) {
            this.retencion = retencion;
        }

        /**
         * Gets the value of the retencion property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the retencion property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRETENCION().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link RETENCION }
         * 
         * 
         */
        public List<RETENCION> getRETENCION() {
            if (retencion == null) {
                retencion = new ArrayList<RETENCION>();
            }
            return this.retencion;
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
         *         &lt;element name="WITHT" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;maxLength value="2"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="WT_WITHCD" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;maxLength value="2"/>
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
            "witht",
            "wtwithcd"
        })
        public static class RETENCION {

            @XmlElement(name = "WITHT")
            protected String witht;
            @XmlElement(name = "WT_WITHCD")
            protected String wtwithcd;

            /**
             * Obtiene el valor de la propiedad witht.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getWITHT() {
                return witht;
            }

            /**
             * Define el valor de la propiedad witht.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setWITHT(String value) {
                this.witht = value;
            }

            /**
             * Obtiene el valor de la propiedad wtwithcd.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getWTWITHCD() {
                return wtwithcd;
            }

            /**
             * Define el valor de la propiedad wtwithcd.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setWTWITHCD(String value) {
                this.wtwithcd = value;
            }

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
     *         &lt;element name="ZTERM" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="4"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="AKONT" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="2"/>
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
        "zterm",
        "akont"
    })
    public static class SOCIEDAD {

        @XmlElement(name = "ZTERM")
        protected String zterm;
        @XmlElement(name = "AKONT")
        protected String akont;

        /**
         * Obtiene el valor de la propiedad zterm.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZTERM() {
            return zterm;
        }

        /**
         * Define el valor de la propiedad zterm.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZTERM(String value) {
            this.zterm = value;
        }

        /**
         * Obtiene el valor de la propiedad akont.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAKONT() {
            return akont;
        }

        /**
         * Define el valor de la propiedad akont.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAKONT(String value) {
            this.akont = value;
        }

    }

}
