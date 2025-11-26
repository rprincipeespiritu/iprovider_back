
package com.incloud.hcp.wsdl.solped;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.solped package. 
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

    private final static QName _MTP026ConsultaSOLPEDReqOut_QNAME = new QName("urn:sanmartinperu.pe:portal025:ConsultaSOLPED", "MT_P026_ConsultaSOLPED_Req_Out");
    private final static QName _MTP026ConsultaSOLPEDResIn_QNAME = new QName("urn:sanmartinperu.pe:portal025:ConsultaSOLPED", "MT_P026_ConsultaSOLPED_Res_In");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.solped
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTP026ConsultaSOLPEDReq }
     * 
     */
    public DTP026ConsultaSOLPEDReq createDTP026ConsultaSOLPEDReq() {
        return new DTP026ConsultaSOLPEDReq();
    }

    /**
     * Create an instance of {@link DTP026ConsultaSOLPEDRes }
     * 
     */
    public DTP026ConsultaSOLPEDRes createDTP026ConsultaSOLPEDRes() {
        return new DTP026ConsultaSOLPEDRes();
    }

    /**
     * Create an instance of {@link DTP026ConsultaSOLPEDReq.Request }
     * 
     */
    public DTP026ConsultaSOLPEDReq.Request createDTP026ConsultaSOLPEDReqRequest() {
        return new DTP026ConsultaSOLPEDReq.Request();
    }

    /**
     * Create an instance of {@link DTP026ConsultaSOLPEDRes.Detalle }
     * 
     */
    public DTP026ConsultaSOLPEDRes.Detalle createDTP026ConsultaSOLPEDResDetalle() {
        return new DTP026ConsultaSOLPEDRes.Detalle();
    }

    /**
     * Create an instance of {@link DTP026ConsultaSOLPEDRes.Log }
     * 
     */
    public DTP026ConsultaSOLPEDRes.Log createDTP026ConsultaSOLPEDResLog() {
        return new DTP026ConsultaSOLPEDRes.Log();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTP026ConsultaSOLPEDReq }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:sanmartinperu.pe:portal025:ConsultaSOLPED", name = "MT_P026_ConsultaSOLPED_Req_Out")
    public JAXBElement<DTP026ConsultaSOLPEDReq> createMTP026ConsultaSOLPEDReqOut(DTP026ConsultaSOLPEDReq value) {
        return new JAXBElement<DTP026ConsultaSOLPEDReq>(_MTP026ConsultaSOLPEDReqOut_QNAME, DTP026ConsultaSOLPEDReq.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTP026ConsultaSOLPEDRes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:sanmartinperu.pe:portal025:ConsultaSOLPED", name = "MT_P026_ConsultaSOLPED_Res_In")
    public JAXBElement<DTP026ConsultaSOLPEDRes> createMTP026ConsultaSOLPEDResIn(DTP026ConsultaSOLPEDRes value) {
        return new JAXBElement<DTP026ConsultaSOLPEDRes>(_MTP026ConsultaSOLPEDResIn_QNAME, DTP026ConsultaSOLPEDRes.class, null, value);
    }

}
