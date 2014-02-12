package com.github.ddth.osgicollection.commons.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ddth.osgicollection.commons.osgi.Constants;

/**
 * OSGi related utilities.
 * 
 * @author Thanh Ba Nguyen <bnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class OsgiUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(OsgiUtils.class);

    /**
     * Gets the string represented the bundle state.
     * 
     * @param bundle
     * @return
     */
    public static String getBundleStateAsString(Bundle bundle) {
        int state = bundle.getState();
        if ((state & Bundle.UNINSTALLED) != 0) {
            return "UNINSTALLED";
        }
        if ((state & Bundle.INSTALLED) != 0) {
            return "INSTALLED";
        }
        if ((state & Bundle.RESOLVED) != 0) {
            return "RESOLVED";
        }
        if ((state & Bundle.STARTING) != 0) {
            return "STARTING";
        }
        if ((state & Bundle.STOPPING) != 0) {
            return "STOPPING";
        }
        if ((state & Bundle.ACTIVE) != 0) {
            return "ACTIVE";
        }
        return "UNKNOWN";
    }

    /**
     * Looks up an OSGi service.
     * 
     * <p>
     * This method unregisters the {@link ServiceReference} so caller does not
     * need to do it.
     * </p>
     * 
     * @param clazz
     * @return
     */
    public static <T> T lookupService(BundleContext bundleContext, Class<T> clazz) {
        ServiceReference<T> sref = lookupServiceReference(bundleContext, clazz);
        if (sref != null) {
            try {
                return getService(bundleContext, sref, clazz);
            } finally {
                ungetServiceReference(bundleContext, sref);
            }
        }
        return null;
    }

    /**
     * Looks up an OSGi service.
     * 
     * <p>
     * This method unregisters the {@link ServiceReference} so caller does not
     * need to do it.
     * </p>
     * 
     * @param clazz
     * @param filter
     * @return
     */
    public static <T> T lookupService(BundleContext bundleContext, Class<T> clazz,
            Map<String, String> filter) {
        ServiceReference<T> sref = lookupServiceReference(bundleContext, clazz, filter);
        if (sref != null) {
            try {
                return getService(bundleContext, sref, clazz);
            } finally {
                ungetServiceReference(bundleContext, sref);
            }
        }
        return null;
    }

    /**
     * Looks up an OSGi service.
     * 
     * <p>
     * This method unregisters the {@link ServiceReference} so caller does not
     * need to do it.
     * </p>
     * 
     * @param clazz
     * @param filter
     * @return
     */
    public static <T> T lookupService(BundleContext bundleContext, Class<T> clazz, Properties filter) {
        ServiceReference<T> sref = lookupServiceReference(bundleContext, clazz, filter);
        if (sref != null) {
            try {
                return getService(bundleContext, sref, clazz);
            } finally {
                ungetServiceReference(bundleContext, sref);
            }
        }
        return null;
    }

    /**
     * Gets the service instance from a service reference.
     * 
     * @param serviceRef
     * @return
     */
    public static Object getService(BundleContext bundleContext, ServiceReference<?> serviceRef) {
        if (serviceRef != null) {
            return bundleContext.getService(serviceRef);
        }
        return null;
    }

    /**
     * Gets the service instance from a service reference.
     * 
     * @param serviceRef
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getService(BundleContext bundleContext, ServiceReference<?> serviceRef,
            Class<T> clazz) {
        Object service = getService(bundleContext, serviceRef);
        if (service != null && clazz.isAssignableFrom(service.getClass())) {
            return (T) service;
        }
        return null;
    }

    /**
     * "Unget" a service reference.
     * 
     * @param serviceRef
     */
    public static void ungetServiceReference(BundleContext bundleContext,
            ServiceReference<?> serviceRef) {
        if (serviceRef != null) {
            bundleContext.ungetService(serviceRef);
        }
    }

    /**
     * Looks up an OSGi service reference.
     * 
     * @param clazz
     * @return
     */
    public static <T> ServiceReference<T> lookupServiceReference(BundleContext bundleContext,
            Class<T> clazz) {
        return lookupServiceReference(bundleContext, clazz, (String) null);
    }

    /**
     * Looks up an OSGi service reference.
     * 
     * @param clazz
     * @param filter
     * @return
     */
    public static <T> ServiceReference<T> lookupServiceReference(BundleContext bundleContext,
            Class<T> clazz, Properties filter) {
        if (filter == null || filter.size() == 0) {
            return lookupServiceReference(bundleContext, clazz, (String) null);
        }
        StringBuilder query = new StringBuilder("(&");
        for (Entry<Object, Object> entry : filter.entrySet()) {
            query.append("(");
            query.append(entry.getKey().toString());
            query.append("=");
            query.append(entry.getValue().toString());
            query.append(")");
        }
        query.append(")");
        return lookupServiceReference(bundleContext, clazz, query.toString());
    }

    /**
     * Looks up an OSGi service reference.
     * 
     * @param clazz
     * @param filter
     * @return
     */
    public static <T> ServiceReference<T> lookupServiceReference(BundleContext bundleContext,
            Class<T> clazz, Map<?, ?> filter) {
        if (filter == null || filter.size() == 0) {
            return lookupServiceReference(bundleContext, clazz, (String) null);
        }
        StringBuilder query = new StringBuilder("(&");
        for (Entry<?, ?> entry : filter.entrySet()) {
            query.append("(");
            query.append(entry.getKey().toString());
            query.append("=");
            query.append(entry.getValue().toString());
            query.append(")");
        }
        query.append(")");
        return lookupServiceReference(bundleContext, clazz, query.toString());
    }

    /**
     * Looks up an OSGi service reference.
     * 
     * @param clazz
     * @param filter
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> ServiceReference<T> lookupServiceReference(BundleContext bundleContext,
            Class<T> clazz, String filter) {
        final ServiceReference<T>[] EMPTY_ARRAY = new ServiceReference[0];
        Collection<ServiceReference<T>> _refs = lookupServiceReferences(bundleContext, clazz,
                filter);
        ServiceReference<T>[] refs = _refs != null ? _refs.toArray(EMPTY_ARRAY) : EMPTY_ARRAY;
        ServiceReference<T> ref = (refs != null && refs.length > 0) ? refs[0] : null;
        for (int i = 1, n = refs != null ? refs.length : 0; i < n; i++) {
            ServiceReference<T> temp = refs[i];
            Object v1 = ref.getProperty(Constants.LOOKUP_PROP_VERSION);
            Object v2 = temp.getProperty(Constants.LOOKUP_PROP_VERSION);
            if (VersionUtils.compareVersions(v1 != null ? v1.toString() : null,
                    v2 != null ? v2.toString() : null) < 0) {
                // unget unmatched service reference
                ungetServiceReference(bundleContext, ref);
                ref = temp;
            } else {
                // unget unmatched service reference
                ungetServiceReference(bundleContext, temp);
            }
        }
        return ref;
    }

    /**
     * Looks up OSGi service references.
     * 
     * @param clazz
     * @param filter
     * @return
     */
    public static <T> Collection<ServiceReference<T>> lookupServiceReferences(
            BundleContext bundleContext, Class<T> clazz, Properties filter) {
        if (filter == null || filter.size() == 0) {
            return lookupServiceReferences(bundleContext, clazz, (String) null);
        }
        StringBuilder query = new StringBuilder("(&");
        for (Entry<Object, Object> entry : filter.entrySet()) {
            query.append("(");
            query.append(entry.getKey().toString());
            query.append("=");
            query.append(entry.getValue().toString());
            query.append(")");
        }
        query.append(")");
        return lookupServiceReferences(bundleContext, clazz, query.toString());
    }

    /**
     * Looks up OSGi service references.
     * 
     * @param clazz
     * @param filter
     * @return
     */
    public static <T> Collection<ServiceReference<T>> lookupServiceReferences(
            BundleContext bundleContext, Class<T> clazz, Map<?, ?> filter) {
        if (filter == null || filter.size() == 0) {
            return lookupServiceReferences(bundleContext, clazz, (String) null);
        }
        StringBuilder query = new StringBuilder("(&");
        for (Entry<?, ?> entry : filter.entrySet()) {
            query.append("(");
            query.append(entry.getKey().toString());
            query.append("=");
            query.append(entry.getValue().toString());
            query.append(")");
        }
        query.append(")");
        return lookupServiceReferences(bundleContext, clazz, query.toString());
    }

    /**
     * Looks up OSGi service references.
     * 
     * @param clazz
     * @param filter
     * @return
     */
    public static <T> Collection<ServiceReference<T>> lookupServiceReferences(
            BundleContext bundleContext, Class<T> clazz, String filter) {
        Collection<ServiceReference<T>> result = new ArrayList<ServiceReference<T>>();
        if (filter == null) {
            ServiceReference<T> serviceRef = bundleContext.getServiceReference(clazz);
            if (serviceRef != null) {
                result.add(serviceRef);
            }
        } else {
            try {
                result = bundleContext.getServiceReferences(clazz, filter);
            } catch (InvalidSyntaxException e) {
                LOGGER.error(
                        "Can not get service reference [" + clazz + "/" + filter + "]: "
                                + e.getMessage(), e);
            }
        }
        return result != null && result.size() > 0 ? result : null;
    }

    /**
     * Finds all resources from a location.
     * 
     * @param bundleContext
     * @param resourceLocaotion
     * @return
     * @throws IOException
     */
    public static String[] enumResources(BundleContext bundleContext, String resourceLocation)
            throws IOException {
        return enumResources(bundleContext.getBundle(), resourceLocation);
    }

    /**
     * Finds all resources from a location.
     * 
     * @param bundle
     * @param resourceLocation
     * @return
     * @throws IOException
     */
    public static String[] enumResources(Bundle bundle, String resourceLocation) throws IOException {
        List<String> result = new ArrayList<String>();
        Enumeration<?> e = bundle.getEntryPaths(resourceLocation);
        if (e != null) {
            while (e.hasMoreElements()) {
                Object obj = e.nextElement();
                result.add(obj.toString());
            }
        }
        return result.toArray(new String[0]);
    }

    /**
     * Loads a resource located within the bundle.
     * 
     * @param bundleContext
     * @param resourceLocation
     * @return
     * @throws IOException
     */
    public static InputStream loadBundleResource(BundleContext bundleContext,
            String resourceLocation) throws IOException {
        return loadBundleResource(bundleContext.getBundle(), resourceLocation);
    }

    /**
     * Loads a resource located within the bundle.
     * 
     * @param bundle
     * @param resourceLocation
     * @return
     * @throws IOException
     */
    public static InputStream loadBundleResource(Bundle bundle, String resourceLocation)
            throws IOException {
        URL url = bundle.getEntry(resourceLocation);
        return url != null ? url.openStream() : null;
    }
}
