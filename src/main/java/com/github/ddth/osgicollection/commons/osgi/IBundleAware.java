package com.github.ddth.osgicollection.commons.osgi;

import org.osgi.framework.Bundle;

/**
 * Service implements this interface will be called by
 * {@link AbstractActivator#registerService(Class, Object, java.util.Map)} to be
 * provided with a {@link Bundle} instance.
 * 
 * @author Thanh Ba Nguyen <bnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface IBundleAware {
    public void setBundle(Bundle bundle);
}
