package org.silverpeas.core.index.search.model;

import org.silverpeas.core.contribution.contentcontainer.content.GlobalSilverContent;
import org.silverpeas.core.i18n.AbstractI18NBean;
import org.silverpeas.core.util.StringUtil;
import org.silverpeas.core.util.file.FileServerUtils;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Nicolas Eysseric
 */
public class SearchResult extends AbstractI18NBean {

  private String keywords;
  private LocalDate creationDate;
  private String creatorId;
  private LocalDate lastUpdateDate;
  private String lastUpdaterId;

  private String id;
  private String componentId;
  private String type;

  private float score;
  private String serverName;
  private String thumbnailURL;
  private String attachmentFilename;
  private boolean externalResult = false;
  private boolean alias = false;

  /**
   * list of XML form fields used to sort results
   */
  private Map<String, String> sortableXMLFormFields = null;
  private Map<String, String> formFieldsForFacets;

  private SearchResult(MatchingIndexEntry mie) {
    setName(mie.getTitle());
    setDescription(mie.getPreView());
    this.keywords = mie.getKeywords(null);

    this.creationDate = getLocalDate(mie.getCreationDate());
    this.lastUpdateDate = getLocalDate(mie.getLastModificationDate());

    this.creatorId = mie.getCreationUser();
    this.lastUpdaterId = mie.getLastModificationUser();

    this.id = mie.getObjectId();
    this.componentId = mie.getComponent();
    this.type = mie.getObjectType();

    this.score = mie.getScore();
    this.serverName = mie.getServerName();
    attachmentFilename = mie.getFilename();
    externalResult = mie.isExternalResult();
    alias = mie.isAlias();

    // add sortable fields from XML form
    sortableXMLFormFields = mie.getSortableXMLFormFields();
    formFieldsForFacets = mie.getXMLFormFieldsForFacets();

    Iterator<String> languages = mie.getLanguages();
    while (languages.hasNext()) {
      String language = languages.next();
      SearchResultTranslation translation =
          new SearchResultTranslation(language, mie.getTitle(language), mie.getPreview(language));
      addTranslation(translation);
    }

    if (mie.getThumbnail() != null) {
      if (mie.getThumbnail().startsWith("/")) {
        // case of a thumbnail picked up in a gallery
        thumbnailURL = mie.getThumbnail();
      } else {
        // case of an uploaded image
        thumbnailURL = FileServerUtils.getUrl(mie.getComponent(),
            mie.getThumbnail(), mie.getThumbnailMimeType(), mie.getThumbnailDirectory());
      }
    }
  }

  private SearchResult(GlobalSilverContent gsc) {
    setName(gsc.getName());
    setDescription(gsc.getDescription());

    this.creationDate = getLocalDate(gsc.getCreationDate());
    this.creatorId = gsc.getUserId();

    this.id = gsc.getId();
    this.componentId = gsc.getInstanceId();
    this.type = gsc.getType();

    for (String language : gsc.getTranslations().keySet()) {
      SearchResultTranslation translation =
          new SearchResultTranslation(language, gsc.getName(language), gsc.getDescription(language));
      addTranslation(translation);
    }

    thumbnailURL = gsc.getThumbnailURL();
  }

  public static SearchResult fromIndexEntry(MatchingIndexEntry mie) {
    return new SearchResult(mie);
  }

  public static SearchResult fromGlobalSilverContent(GlobalSilverContent gsc) {
    return new SearchResult(gsc);
  }

  private LocalDate getLocalDate(String date) {
    if (StringUtil.isDefined(date)) {
      return LocalDate.parse(date.replaceAll("/", "-"));
    }
    return null;
  }

  public String getKeywords() {
    return keywords;
  }

  public LocalDate getCreationDate() {
    return creationDate;
  }

  public String getCreatorId() {
    return creatorId;
  }

  public LocalDate getLastUpdateDate() {
    return lastUpdateDate;
  }

  public String getLastUpdaterId() {
    return lastUpdaterId;
  }

  public String getId() {
    return id;
  }

  public String getInstanceId() {
    return componentId;
  }

  public String getType() {
    return type;
  }

  public float getScore() {
    return score;
  }

  public String getServerName() {
    return serverName;
  }

  public String getThumbnailURL() {
    return thumbnailURL;
  }

  public void setThumbnailURL(String url) {
    thumbnailURL = url;
  }

  public String getAttachmentFilename() {
    return attachmentFilename;
  }

  /**
   * gets the list of Sortable fields if the content is a form XML
   * @return the sortableXMLFormFields
   */
  public Map<String, String> getSortableXMLFormFields() {
    return sortableXMLFormFields;
  }

  public Map<String, String> getFormFieldsForFacets() {
    return formFieldsForFacets;
  }

  public boolean isExternalResult() {
    return externalResult;
  }

  public boolean isAlias() {
    return alias;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final SearchResult that = (SearchResult) o;

    if (id != null ? !id.equals(that.id) : that.id != null) {
      return false;
    }
    return componentId != null ? componentId.equals(that.componentId) : that.componentId == null;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (componentId != null ? componentId.hashCode() : 0);
    return result;
  }
}
