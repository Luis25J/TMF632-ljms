package mx.att.digital.api.tmf632.application.port.out;

import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2Request;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2Response;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2User;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2UserDelete;

/**
 * The interface Custom scim 2 connector port.
 */
public interface CustomSCIM2ConnectorPort {

    /**
     * Retrieve user info by name scim 2 user.
     *
     * @param userName the user name
     * @return the scim 2 user
     */
    SCIM2User retrieveUserInfoByName(String userName);

    /**
     * Retrieve user info by id scim 2 user.
     *
     * @param id the id
     * @return the scim 2 user
     */
    SCIM2User retrieveUserInfoById(String id);

    /**
     * Delete user by id scim 2 user delete.
     *
     * @param id the id
     * @return the scim 2 user delete
     */
    SCIM2UserDelete deleteUserById(String id);

    /**
     * Update user by id scim 2 user.
     *
     * @param id   the id
     * @param user the user
     * @return the scim 2 user
     */
    SCIM2User updateUserById(String id, SCIM2User user);

    /**
     * Add user scim 2 response.
     *
     * @param request the request
     * @return the scim 2 response
     */
    SCIM2Response addUser(SCIM2Request request);

}

