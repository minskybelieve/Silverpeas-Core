package com.silverpeas.domains.silverpeasdriver;

import org.silverpeas.persistence.model.identifier.UniqueIntegerIdentifier;
import org.silverpeas.persistence.repository.jpa.JpaBasicEntityManager;

import java.util.List;

/**
 * @author: ebonnet
 */
public class SPGroupJpaManager extends JpaBasicEntityManager<SPGroup, UniqueIntegerIdentifier>
    implements SPGroupManager {

  @Override
  public List<SPGroup> findByName(final String name) {
    return listFromNamedQuery("SPUser.findByName", newNamedParameters().add("name", name));
  }

  @Override
  public List<SPGroup> findByDescription(final String description) {
    return listFromNamedQuery("SPUser.findByDescription",
        newNamedParameters().add("description", description));
  }

  @Override
  public List<SPGroup> listAllRootGroups() {
    return listFromNamedQuery("SPUser.listAllRootGroups", newNamedParameters());
  }
}