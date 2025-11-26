
package com.incloud.hcp.wsdl.materiales;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para DT_P027_MaestroMateriales_Req complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DT_P027_MaestroMateriales_Req">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FECHA" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DT_P027_MaestroMateriales_Req", propOrder = {
    "fecha"
})
public class DTP027MaestroMaterialesReq {

//    @XmlElement(name = "FECHA", required = true)
//    @XmlSchemaType(name = "date")
//    protected XMLGregorianCalendar fecha;
//
//    /**
//     * Obtiene el valor de la propiedad fecha.
//     *
//     * @return
//     *     possible object is
//     *     {@link XMLGregorianCalendar }
//     *
//     */
//    public XMLGregorianCalendar getFECHA() {
//        return fecha;
//    }
//
//    /**
//     * Define el valor de la propiedad fecha.
//     *
//     * @param value
//     *     allowed object is
//     *     {@link XMLGregorianCalendar }
//     *
//     */
//    public void setFECHA(XMLGregorianCalendar value) {
//        this.fecha = value;
//    }
//
//    @Override
//    public String toString() {
//        return "DTP027MaestroMaterialesReq{" +
//                "fecha=" + fecha +
//                '}';
//    }

    @XmlElement(name = "FECHA", required = true)
    protected String fecha;

    public String getFECHA() {
        return fecha;
    }

    public void setFECHA(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "DTP027MaestroMaterialesReqProd{" +
                "fecha='" + fecha + '\'' +
                '}';
    }
}
