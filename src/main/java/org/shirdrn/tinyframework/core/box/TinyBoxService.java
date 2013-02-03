package org.shirdrn.tinyframework.core.box;

/**
 * Establish the relationship between {@link TinyBox} and {@link TinyBoxService},
 * and then we can register a service definition which can expose specified operations
 * by invoking {@link TinyBoxServiceFactory#registerService(TinyBox, Class)}.</br></br>
 * 
 * The user defined service interface must be extends {@link TinyBoxService}, and later 
 * retrieve the {@link TinyBox} object related to the service interface.
 * 
 * @author Yanjun
 */
public interface TinyBoxService {

}
