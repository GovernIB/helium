package net.conselldemallorca.helium.ms.domini.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DominiPagedList
 */
public class DominiPagedList extends PagedResponse {
  @JsonProperty("content")
  private DominiList content = null;

  public DominiPagedList content(DominiList content) {
    this.content = content;
    return this;
  }

   /**
   * Get content
   * @return content
  **/
  public DominiList getContent() {
    return content;
  }

  public void setContent(DominiList content) {
    this.content = content;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DominiPagedList dominiPagedList = (DominiPagedList) o;
    return Objects.equals(this.content, dominiPagedList.content) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DominiPagedList {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
