/**
 * ServiceRUC.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.incloud.hcp.wsdl.sunat;

import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import java.net.URL;

public interface ServiceRUC extends Service {
    String getServiceRUCPortAddress();

    ServiceRUCPortType getServiceRUCPort() throws ServiceException;

    ServiceRUCPortType getServiceRUCPort(URL portAddress) throws ServiceException;
}
