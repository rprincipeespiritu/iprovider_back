
package com.incloud.hcp.wsdl.materiales;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.incloud.hcp.wsdl.materiales package.
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

    private final static QName _MTP027MaestroMaterialesReqOut_QNAME = new QName("urn:sanmartinperu.pe:portal027:MaestroMateriales", "MT_P027_MaestroMateriales_Req_Out");
    private final static QName _MTP027MaestroMaterialesResIn_QNAME = new QName("urn:sanmartinperu.pe:portal027:MaestroMateriales", "MT_P027_MaestroMateriales_Res_In");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.incloud.hcp.wsdl.materiales
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTP027MaestroMaterialesRes }
     *
     */
    public DTP027MaestroMaterialesRes createDTP027MaestroMaterialesRes() {
        return new DTP027MaestroMaterialesRes();
    }

    /**
     * Create an instance of {@link DTP027MaestroMaterialesReq }
     *
     */
    public DTP027MaestroMaterialesReq createDTP027MaestroMaterialesReq() {
        return new DTP027MaestroMaterialesReq();
    }

    /**
     * Create an instance of {@link DTP027MaestroMaterialesRes.Response }
     *
     */
    public DTP027MaestroMaterialesRes.Response createDTP027MaestroMaterialesResResponse() {
        return new DTP027MaestroMaterialesRes.Response();
    }

    /**
     * Create an instance of {@link DTP027MaestroMaterialesRes.LOG }
     *
     */
    public DTP027MaestroMaterialesRes.LOG createDTP027MaestroMaterialesResLOG() {
        return new DTP027MaestroMaterialesRes.LOG();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTP027MaestroMaterialesReq }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:sanmartinperu.pe:portal027:MaestroMateriales", name = "MT_P027_MaestroMateriales_Req_Out")
    public JAXBElement<DTP027MaestroMaterialesReq> createMTP027MaestroMaterialesReqOut(DTP027MaestroMaterialesReq value) {
        return new JAXBElement<DTP027MaestroMaterialesReq>(_MTP027MaestroMaterialesReqOut_QNAME, DTP027MaestroMaterialesReq.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTP027MaestroMaterialesRes }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:sanmartinperu.pe:portal027:MaestroMateriales", name = "MT_P027_MaestroMateriales_Res_In")
    public JAXBElement<DTP027MaestroMaterialesRes> createMTP027MaestroMaterialesResIn(DTP027MaestroMaterialesRes value) {
        return new JAXBElement<DTP027MaestroMaterialesRes>(_MTP027MaestroMaterialesResIn_QNAME, DTP027MaestroMaterialesRes.class, null, value);
    }

}