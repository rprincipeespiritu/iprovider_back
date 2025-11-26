/**
 * ServiceRUCLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.incloud.hcp.wsdl.sunat;

public class ServiceRUCLocator extends org.apache.axis.client.Service implements ServiceRUC {

    public ServiceRUCLocator() {
    }


    public ServiceRUCLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ServiceRUCLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ServiceRUCPort
    private String ServiceRUCPort_address = "http://ws.insite.pe/sunat/ruc.php";

    public String getServiceRUCPortAddress() {
        return ServiceRUCPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String ServiceRUCPortWSDDServiceName = "ServiceRUCPort";

    public String getServiceRUCPortWSDDServiceName() {
        return ServiceRUCPortWSDDServiceName;
    }

    public void setServiceRUCPortWSDDServiceName(String name) {
        ServiceRUCPortWSDDServiceName = name;
    }

    public com.incloud.hcp.wsdl.sunat.ServiceRUCPortType getServiceRUCPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ServiceRUCPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getServiceRUCPort(endpoint);
    }

    public com.incloud.hcp.wsdl.sunat.ServiceRUCPortType getServiceRUCPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            ServiceRUCBindingStub _stub = new ServiceRUCBindingStub(portAddress, this);
            _stub.setPortName(getServiceRUCPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setServiceRUCPortEndpointAddress(String address) {
        ServiceRUCPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.incloud.hcp.wsdl.sunat.ServiceRUCPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                ServiceRUCBindingStub _stub = new ServiceRUCBindingStub(new java.net.URL(ServiceRUCPort_address), this);
                _stub.setPortName(getServiceRUCPortWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("ServiceRUCPort".equals(inputPortName)) {
            return getServiceRUCPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws.insite.pe/sunat/ruc.php?wsdl", "ServiceRUC");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws.insite.pe/sunat/ruc.php?wsdl", "ServiceRUCPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("ServiceRUCPort".equals(portName)) {
            setServiceRUCPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
