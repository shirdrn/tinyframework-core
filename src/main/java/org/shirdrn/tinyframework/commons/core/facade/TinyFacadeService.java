package org.shirdrn.tinyframework.commons.core.facade;

/**
 * Establish the relationship between {@link TinyFacade} and {@link TinyFacadeService},
 * and then we can register a service definition which can expose specified operations
 * by invoking {@link TinyFacadeServiceFactory#registerService(TinyFacade, Class)}.</br></br>
 * 
 * The user defined service interface must be extends {@link TinyFacadeService}, and later 
 * retrieve the {@link TinyFacade} object related to the service interface.
 * 
 * @author Yanjun
 */
public interface TinyFacadeService {

}
