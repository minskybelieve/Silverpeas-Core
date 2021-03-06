package org.silverpeas.core.chat.servers;

import org.silverpeas.core.admin.user.model.User;
import org.silverpeas.core.chat.ChatServerException;
import org.silverpeas.core.util.ResourceLocator;
import org.silverpeas.core.util.SettingBundle;
import org.silverpeas.core.util.StringUtil;

/**
 * This interface represents a Chat server. An implementation of this interface has to implement
 * the access to a concrete chat server.
 *
 * Configuration available in <code>Silverpeas-Core/core-configuration/src/main/config/
 * properties/org/silverpeas/chat/settings/chat.properties</code>
 * @author remipassmoilesel
 */
public interface ChatServer {

  /**
   * Gets the settings on the chat service. These settings provide the endpoint definitions as well
   * all the required parameters to communicate correctly with the remote chat server.
   * @return a bundle of settings on the chat service.
   */
  static SettingBundle getChatSettings() {
    return ResourceLocator.getSettingBundle("org.silverpeas.chat.settings.chat");
  }

  /**
   * Is a chat server enabled? A chat server is enabled if there is a chat server defined
   * for Silverpeas and the chat service is explicitly enabled. The definition of a chat server and
   * the activation of the chat service are both done through the properties file
   * {@code org/silverpeas/chat/settings/chat.properties}.
   * @return true if both the chat service is enabled and a chat server is defined in the
   * Silverpeas configuration. False otherwise.
   */
  static boolean isEnabled() {
    return getChatSettings().getBoolean("chat.enable", false) &&
        StringUtil.isDefined(getChatSettings().getString("chat.xmpp.httpBindUrl", null)) &&
        StringUtil.isDefined(getChatSettings().getString("chat.xmpp.restUrl", null));
  }

  /**
   * Creates an account in the chat server for the specified user. The user login in lower case
   * (without any domain part if any) is used as the chat login and the API token is used as
   * password.
   *
   * Be caution with email addresses used as login because they contain a domain part and domain
   * parts are not supported in login by chat servers. Before creating the account, all domain part
   * or so such interpreted, are first removed from the user login.
   * @param user a Silverpeas user.
   * @throws ChatServerException if an error occurs while creating the user in the chat server.
   */
  void createUser(User user);

  /**
   * Deletes in the chat server the account of the specified user.
   * @param user a Silverpeas user.
   * @throws ChatServerException if an error occurs while deleting the user in the chat server.
   */
  void deleteUser(User user);

  /**
   * Creates a relationship between the two specified user in the chat server. If the relationship
   * already exists, does nothing.
   * @param user1 a Silverpeas user.
   * @param user2 another Silverpeas user.
   * @throws ChatServerException if an error occurs while creating a relationship between the two
   * users in the chat server.
   */
  void createRelationShip(User user1, User user2);

  /**
   * Deletes the relationship existing between the two specified user in the chat server.
   * @param user1 a Silverpeas user.
   * @param user2 another Silverpeas user.
   * @throws ChatServerException if an error occurs while deleting a relationship between the two
   * users in the chat server.
   */
  void deleteRelationShip(User user1, User user2);

  /**
   * Is the specified user has already an account in the chat server.
   * @param user a Silverpeas user.
   * @return true if the user has an account in the chat server, false otherwise.
   * @throws ChatServerException if an error occurs while communicating with the chat server.
   */
  boolean isUserExisting(User user);

}
