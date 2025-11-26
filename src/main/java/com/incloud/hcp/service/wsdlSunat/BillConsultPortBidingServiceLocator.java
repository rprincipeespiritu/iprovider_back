package com.incloud.hcp.service.wsdlSunat;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Service;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.util.HashSet;
import java.util.Iterator;

public class BillConsultPortBidingServiceLocator extends Service implements BillConsultPortBidingService {

    public BillConsultPortBidingServiceLocator() {
    }

    private String BillConsultServicePort_address = "https://ww1.sunat.gob.pe:443/ol-it-wsconscpegem/billConsultService";
    //
    public String getBillConsultServicePortAddress() {
        return BillConsultServicePort_address;
    }

    private String BillConsultServicePortWSDDServiceName = "BillConsultServicePort";

    public String getBillConsultServicePortWSDDServiceName() {
        return BillConsultServicePortWSDDServiceName;
    }

    public void setBillConsultServicePortEndpointAddress(String address) {
        BillConsultServicePort_address = address;
    }

    @Override
    public BillConsultService getBillConsultServicePort() throws ServiceException {
        URL endpoint;
        try {
            endpoint = new URL(BillConsultServicePort_address);
        }
        catch (MalformedURLException e) {
            throw new ServiceException(e);
        }
        return getBillConsultServicePort(endpoint);
    }

    @Override
    public BillConsultService getBillConsultServicePort(URL portAddress) throws ServiceException {
        try {
            BillConsultPortBidingServiceStub _stub = new BillConsultPortBidingServiceStub(portAddress, this);
            _stub.setPortName(getBillConsultServicePortWSDDServiceName());
            return _stub;
        }
        catch (AxisFault e) {
            return null;
        }
    }

    @Override
    public Remote getPort(Class serviceEndpointInterface) throws ServiceException {
        try {
            if (BillConsultService.class.isAssignableFrom(serviceEndpointInterface)) {
                BillConsultPortBidingServiceStub _stub = new BillConsultPortBidingServiceStub(new java.net.URL(BillConsultServicePort_address), this);
                _stub.setPortName(getBillConsultServicePortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));

    }

        @Override
    public Remote getPort(QName portName, Class serviceEndpointInterface) throws ServiceException {
            if (portName == null) {
                return getPort(serviceEndpointInterface);
            }
            java.lang.String inputPortName = portName.getLocalPart();
            if ("BillConsultServicePort".equals(inputPortName)) {
                return getBillConsultServicePort();
            }
            else  {
                java.rmi.Remote _stub = getPort(serviceEndpointInterface);
                ((org.apache.axis.client.Stub) _stub).setPortName(portName);
                return _stub;
            }
    }

    public QName getServiceName() {
        return new QName("http://service.ws.consulta.comppago.electronico.registro.servicio2.sunat.gob.pe/", "billConsultService");
    }


    private HashSet ports = null;

    @Override
    public Iterator getPorts() {
        if (ports == null) {
            ports = new HashSet();
            ports.add(new QName("http://service.ws.consulta.comppago.electronico.registro.servicio2.sunat.gob.pe/", "BillConsultServicePort"));
        }
        return ports.iterator();
    }
}
