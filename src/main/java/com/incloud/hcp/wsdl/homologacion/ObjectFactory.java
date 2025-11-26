
package com.incloud.hcp.wsdl.homologacion;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.homologacion package. 
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

    private final static QName _MTP030ActualizarHomologacionReqOut_QNAME = new QName("urn:sanmartinperu.pe:portal030:ActualizarHomologacion", "MT_P030_ActualizarHomologacion_Req_Out");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.homologacion
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTP030ActualizarHomologacionReq }
     * 
     */
    public DTP030ActualizarHomologacionReq createDTP030ActualizarHomologacionReq() {
        return new DTP030ActualizarHomologacionReq();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTP030ActualizarHomologacionReq }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:sanmartinperu.pe:portal030:ActualizarHomologacion", name = "MT_P030_ActualizarHomologacion_Req_Out")
    public JAXBElement<DTP030ActualizarHomologacionReq> createMTP030ActualizarHomologacionReqOut(DTP030ActualizarHomologacionReq value) {
        return new JAXBElement<DTP030ActualizarHomologacionReq>(_MTP030ActualizarHomologacionReqOut_QNAME, DTP030ActualizarHomologacionReq.class, null, value);
    }

}
