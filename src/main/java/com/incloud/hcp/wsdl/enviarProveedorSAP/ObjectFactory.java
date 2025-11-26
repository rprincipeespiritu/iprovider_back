
package com.incloud.hcp.wsdl.enviarProveedorSAP;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.incloud.hcp.wsdl.enviarProveedorSAP package. 
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

    private final static QName _MTP028MaestroProveedoresReqOut_QNAME = new QName("urn:sanmartinperu.pe:portal028:MaestroProveedores", "MT_P028_MaestroProveedores_Req_Out");
    private final static QName _MTP028MaestroProveedoresResIn_QNAME = new QName("urn:sanmartinperu.pe:portal028:MaestroProveedores", "MT_P028_MaestroProveedores_Res_In");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.incloud.hcp.wsdl.enviarProveedorSAP
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTP028MaestroProveedoresReq }
     * 
     */
    public DTP028MaestroProveedoresReq createDTP028MaestroProveedoresReq() {
        return new DTP028MaestroProveedoresReq();
    }

    /**
     * Create an instance of {@link DTP028MaestroProveedoresReq.RETENCIONS }
     * 
     */
    public DTP028MaestroProveedoresReq.RETENCIONS createDTP028MaestroProveedoresReqRETENCIONS() {
        return new DTP028MaestroProveedoresReq.RETENCIONS();
    }

    /**
     * Create an instance of {@link DTP028MaestroProveedoresReq.EMAILS }
     * 
     */
    public DTP028MaestroProveedoresReq.EMAILS createDTP028MaestroProveedoresReqEMAILS() {
        return new DTP028MaestroProveedoresReq.EMAILS();
    }

    /**
     * Create an instance of {@link DTP028MaestroProveedoresReq.BANCO }
     * 
     */
    public DTP028MaestroProveedoresReq.BANCO createDTP028MaestroProveedoresReqBANCO() {
        return new DTP028MaestroProveedoresReq.BANCO();
    }

    /**
     * Create an instance of {@link DTP028MaestroProveedoresRes }
     * 
     */
    public DTP028MaestroProveedoresRes createDTP028MaestroProveedoresRes() {
        return new DTP028MaestroProveedoresRes();
    }

    /**
     * Create an instance of {@link DTP028MaestroProveedoresReq.MODO }
     * 
     */
    public DTP028MaestroProveedoresReq.MODO createDTP028MaestroProveedoresReqMODO() {
        return new DTP028MaestroProveedoresReq.MODO();
    }

    /**
     * Create an instance of {@link DTP028MaestroProveedoresReq.GENERALES }
     * 
     */
    public DTP028MaestroProveedoresReq.GENERALES createDTP028MaestroProveedoresReqGENERALES() {
        return new DTP028MaestroProveedoresReq.GENERALES();
    }

    /**
     * Create an instance of {@link DTP028MaestroProveedoresReq.SOCIEDAD }
     * 
     */
    public DTP028MaestroProveedoresReq.SOCIEDAD createDTP028MaestroProveedoresReqSOCIEDAD() {
        return new DTP028MaestroProveedoresReq.SOCIEDAD();
    }

    /**
     * Create an instance of {@link DTP028MaestroProveedoresReq.COMPRAS }
     * 
     */
    public DTP028MaestroProveedoresReq.COMPRAS createDTP028MaestroProveedoresReqCOMPRAS() {
        return new DTP028MaestroProveedoresReq.COMPRAS();
    }

    /**
     * Create an instance of {@link DTP028MaestroProveedoresReq.RETENCIONS.RETENCION }
     * 
     */
    public DTP028MaestroProveedoresReq.RETENCIONS.RETENCION createDTP028MaestroProveedoresReqRETENCIONSRETENCION() {
        return new DTP028MaestroProveedoresReq.RETENCIONS.RETENCION();
    }

    /**
     * Create an instance of {@link DTP028MaestroProveedoresReq.EMAILS.EMAIL }
     * 
     */
    public DTP028MaestroProveedoresReq.EMAILS.EMAIL createDTP028MaestroProveedoresReqEMAILSEMAIL() {
        return new DTP028MaestroProveedoresReq.EMAILS.EMAIL();
    }

    /**
     * Create an instance of {@link DTP028MaestroProveedoresReq.BANCO.BANCOS }
     * 
     */
    public DTP028MaestroProveedoresReq.BANCO.BANCOS createDTP028MaestroProveedoresReqBANCOBANCOS() {
        return new DTP028MaestroProveedoresReq.BANCO.BANCOS();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTP028MaestroProveedoresReq }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:sanmartinperu.pe:portal028:MaestroProveedores", name = "MT_P028_MaestroProveedores_Req_Out")
    public JAXBElement<DTP028MaestroProveedoresReq> createMTP028MaestroProveedoresReqOut(DTP028MaestroProveedoresReq value) {
        return new JAXBElement<DTP028MaestroProveedoresReq>(_MTP028MaestroProveedoresReqOut_QNAME, DTP028MaestroProveedoresReq.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTP028MaestroProveedoresRes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:sanmartinperu.pe:portal028:MaestroProveedores", name = "MT_P028_MaestroProveedores_Res_In")
    public JAXBElement<DTP028MaestroProveedoresRes> createMTP028MaestroProveedoresResIn(DTP028MaestroProveedoresRes value) {
        return new JAXBElement<DTP028MaestroProveedoresRes>(_MTP028MaestroProveedoresResIn_QNAME, DTP028MaestroProveedoresRes.class, null, value);
    }

}
