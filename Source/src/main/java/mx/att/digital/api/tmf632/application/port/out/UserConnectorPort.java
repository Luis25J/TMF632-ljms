package mx.att.digital.api.tmf632.application.port.out;

import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorRequest;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorResponse;

/**
 * The interface User connector port.
 */
public interface UserConnectorPort {

    /**
     * Retrieve user by id user connector.
     *
     * @param id the id
     * @return the user connector
     */
    UserConnector retrieveUserById(String id);


    /**
     * Update user user connector response.
     *
     * @param id      the id
     * @param request the request
     * @return the user connector response
     */
    UserConnectorResponse updateUser(String id, UserConnectorRequest request);

    /**
     * Update resource user connector response.
     *
     * @param id           the id
     * @param idResource   the id resource
     * @param typeResource the type resource
     * @param request      the request
     * @return the user connector response
     */
    UserConnectorResponse updateResource(String id, String idResource, String typeResource,
                                                        UserConnectorRequest request);

    /**
     * Create user user connector response.
     *
     * @param request the request
     * @return the user connector response
     */
    UserConnectorResponse createUser(UserConnectorRequest request);

    /**
     * Delete user user connector response.
     *
     * @param id the id
     * @return the user connector response
     */
    UserConnectorResponse deleteUser(String id);

    /**
     * Delete resource user connector response.
     *
     * @param id           the id
     * @param idResource   the id resource
     * @param typeResource the type resource
     * @return the user connector response
     */
    UserConnectorResponse deleteResource(String id, String idResource, String typeResource);

    /**
     * Add contacts user user connector response.
     *
     * @param request the request
     * @return the user connector response
     */
    UserConnectorResponse addContactsUser(UserConnectorRequest request);

    /**
     * Add consents user user connector response.
     *
     * @param request the request
     * @return the user connector response
     */
    UserConnectorResponse addConsentsUser(UserConnectorRequest request);

    /**
     * Add address user user connector response.
     *
     * @param request the request
     * @return the user connector response
     */
    UserConnectorResponse addAddressUser(UserConnectorRequest request);

}
