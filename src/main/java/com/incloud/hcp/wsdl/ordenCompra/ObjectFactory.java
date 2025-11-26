
package com.incloud.hcp.wsdl.ordenCompra;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.ordenCompra package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _MTV2P031CrearOrdenDeCompraRes_QNAME = new QName("urn:sanmartinperu.pe:portal031:CrearOrdendeCompra", "MT_v2_P031_CrearOrdenDeCompra_Res");
    private final static QName _MTV2P031CrearOrdenDeCompraReq_QNAME = new QName("urn:sanmartinperu.pe:portal031:CrearOrdendeCompra", "MT_v2_P031_CrearOrdenDeCompra_Req");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.ordenCompra
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTV2P031CrearOrdenDeCompraReq }
     * 
     */
    public DTV2P031CrearOrdenDeCompraReq createDTV2P031CrearOrdenDeCompraReq() {
        return new DTV2P031CrearOrdenDeCompraReq();
    }

    /**
     * Create an instance of {@link DTV2P031CrearOrdenDeCompraRes }
     * 
     */
    public DTV2P031CrearOrdenDeCompraRes createDTV2P031CrearOrdenDeCompraRes() {
        return new DTV2P031CrearOrdenDeCompraRes();
    }

    /**
     * Create an instance of {@link DTV2P031CrearOrdenDeCompraReq.Cabecera }
     * 
     */
    public DTV2P031CrearOrdenDeCompraReq.Cabecera createDTV2P031CrearOrdenDeCompraReqCabecera() {
        return new DTV2P031CrearOrdenDeCompraReq.Cabecera();
    }

    /**
     * Create an instance of {@link DTV2P031CrearOrdenDeCompraReq.Detalle }
     * 
     */
    public DTV2P031CrearOrdenDeCompraReq.Detalle createDTV2P031CrearOrdenDeCompraReqDetalle() {
        return new DTV2P031CrearOrdenDeCompraReq.Detalle();
    }

    /**
     * Create an instance of {@link DTV2P031CrearOrdenDeCompraRes.Response }
     * 
     */
    public DTV2P031CrearOrdenDeCompraRes.Response createDTV2P031CrearOrdenDeCompraResResponse() {
        return new DTV2P031CrearOrdenDeCompraRes.Response();
    }

    /**
     * Create an instance of {@link DTV2P031CrearOrdenDeCompraRes.Log }
     * 
     */
    public DTV2P031CrearOrdenDeCompraRes.Log createDTV2P031CrearOrdenDeCompraResLog() {
        return new DTV2P031CrearOrdenDeCompraRes.Log();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTV2P031CrearOrdenDeCompraRes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:sanmartinperu.pe:portal031:CrearOrdendeCompra", name = "MT_v2_P031_CrearOrdenDeCompra_Res")
    public JAXBElement<DTV2P031CrearOrdenDeCompraRes> createMTV2P031CrearOrdenDeCompraRes(DTV2P031CrearOrdenDeCompraRes value) {
        return new JAXBElement<DTV2P031CrearOrdenDeCompraRes>(_MTV2P031CrearOrdenDeCompraRes_QNAME, DTV2P031CrearOrdenDeCompraRes.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTV2P031CrearOrdenDeCompraReq }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:sanmartinperu.pe:portal031:CrearOrdendeCompra", name = "MT_v2_P031_CrearOrdenDeCompra_Req")
    public JAXBElement<DTV2P031CrearOrdenDeCompraReq> createMTV2P031CrearOrdenDeCompraReq(DTV2P031CrearOrdenDeCompraReq value) {
        return new JAXBElement<DTV2P031CrearOrdenDeCompraReq>(_MTV2P031CrearOrdenDeCompraReq_QNAME, DTV2P031CrearOrdenDeCompraReq.class, null, value);
    }

}
