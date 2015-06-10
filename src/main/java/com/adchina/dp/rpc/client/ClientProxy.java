package com.adchina.dp.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.adchina.dp.rpc.common.model.Request;
import com.adchina.dp.rpc.common.model.Respose;
import com.adchina.dp.rpc.registy.ServiceDiscovery;

public class ClientProxy {
    private String serviceAddress;
    private ServiceDiscovery serviceDiscovery;

    public ClientProxy(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public ClientProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaces, final String version) {
        return (T) Proxy.newProxyInstance(interfaces.getClassLoader(), new Class<?>[] { interfaces }, new InvocationHandler() {

            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Request request = new Request();
                request.setRequestId(UUID.randomUUID().toString());

                String interfaceName = interfaces.getName();
                request.setVersion(version);
                request.setInterfaceName(interfaceName);
                request.setMethodName(method.getName());
                request.setParamTypes(method.getParameterTypes());
                request.setParams(args);

                if (serviceDiscovery != null) {
                    String serviceName = interfaces.getName();
                    if (StringUtils.isNotEmpty(version)) {
                        serviceName = serviceName + "-" + version;
                    }

                    serviceAddress = serviceDiscovery.discover(serviceName);
                }

                if (StringUtils.isEmpty(serviceAddress)) {
                    throw new RuntimeException("serviceAddress is null");
                }

                String[] array = StringUtils.split(serviceAddress, ":");
                String host = array[0];
                int port = Integer.parseInt(array[1]);

                Client client = new Client(host, port);
                Respose respose = client.send(request);
                if (respose.getExcption() != null) {
                    throw respose.getExcption();
                } else {
                    return respose;
                }

            }
        });
    }
}
