package org.silverpeas.core.socialnetwork.service;

import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.silverpeas.core.persistence.Transaction;
import org.silverpeas.core.socialnetwork.mock.OrganizationControllerMock;
import org.silverpeas.core.socialnetwork.model.ExternalAccount;
import org.silverpeas.core.socialnetwork.model.SocialNetworkID;
import org.silverpeas.core.test.WarBuilder4LibCore;
import org.silverpeas.core.test.rule.DbSetupRule;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class SocialNetworkServiceTest {

  @Inject
  OrganizationControllerMock organisationController;

  @Inject
  SocialNetworkService service;

  public static final Operation TABLES_CREATION =
      Operations.sql("CREATE TABLE IF NOT EXISTS sb_sn_externalaccount (" +
          "profileId varchar(100) NOT NULL ," +
          "networkId varchar(10) NOT NULL," +
          "silverpeasUserId varchar(50) NULL)");
  public static final Operation CLEAN_UP = Operations.deleteAllFrom("sb_sn_externalaccount");

  public static final Operation INSERT_DATA = Operations.insertInto("sb_sn_externalaccount")
      .columns("profileId", "networkId", "silverpeasUserId")
      .values("1234", "LINKEDIN", "11")
      .build();

  @Rule
  public DbSetupRule dbSetupRule = DbSetupRule.createTablesFrom("")
      .loadInitialDataSetFrom(TABLES_CREATION, CLEAN_UP, INSERT_DATA);

  @Deployment
  public static Archive<?> createTestArchive() {
    return WarBuilder4LibCore.onWarForTestClass(SocialNetworkServiceTest.class)
        .addAdministrationFeatures()
        .addMavenDependencies("org.springframework.social:spring-social-linkedin",
            "org.springframework.social:spring-social-facebook").testFocusedOn((warBuilder) -> {
          warBuilder.addPackages(true, "org.silverpeas.util.exception");
          warBuilder.addClasses(OrganizationControllerMock.class);
          warBuilder.testFocusedOn(
              war -> war.addPackages(true, "org.silverpeas.core.socialnetwork.service")
                  .addPackages(true, "org.silverpeas.core.socialnetwork.qualifiers")
                  .addPackages(true, "org.silverpeas.core.socialnetwork.connectors")
                  .addPackages(true, "org.silverpeas.core.socialnetwork.dao")
                  .addPackages(true, "org.silverpeas.core.socialnetwork.model"));
          warBuilder.addPackages(true, "org.silverpeas.core.i18n");
          warBuilder
              .addAsResource("org/silverpeas/social/settings/socialNetworkSettings.properties");
        }).build();
  }


  @Test
  public void testReadByPrimaryKeyUnexistingUser() throws Exception {
    Transaction.performInOne(() -> {
      ExternalAccount account = service.getExternalAccount(SocialNetworkID.LINKEDIN, "1233");
      assertThat(account, nullValue());
      return null;
    });
  }

  @Test
  @Transactional
  public void testReadByPrimaryKeyValidUser() throws Exception {
    ExternalAccount account = service.getExternalAccount(SocialNetworkID.LINKEDIN, "1234");
    assertThat(account.getSilverpeasUserId(), is("11"));
    assertThat(account.getNetworkId(), is(SocialNetworkID.LINKEDIN));
  }

  @Test
  @Transactional
  public void testCreateExternalAccount() throws Exception {
    Transaction.performInOne(() -> {
      service.createExternalAccount(SocialNetworkID.FACEBOOK, "13", "1345");
      return null;
    });
    ExternalAccount account = service.getExternalAccount(SocialNetworkID.FACEBOOK, "1345");
    assertThat(account, is(notNullValue()));
    assertThat(account.getSilverpeasUserId(), is("13"));
  }

}