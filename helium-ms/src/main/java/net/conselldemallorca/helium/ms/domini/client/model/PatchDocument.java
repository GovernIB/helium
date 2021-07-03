/*
 * HeliumMS Dominis API
 * Microservei de Dominis de HeliumMS
 *
 * OpenAPI spec version: 1.0.0
 * Contact: limit@limit.es
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package net.conselldemallorca.helium.ms.domini.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Document amb format JSONPatch (definit al RFC 6902)
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-04-15T16:51:41.639+02:00[Europe/Paris]")
public class PatchDocument {
	/**
	 * Operació a realitzar
	 */
	public enum OpEnum {
		ADD("add"), REMOVE("remove"), REPLACE("replace"), MOVE("move"), COPY("copy"), TEST("test");

		private String value;

		OpEnum(String value) {
			this.value = value;
		}

		@JsonValue
		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}

		@JsonCreator
		public static OpEnum fromValue(String text) {
			for (OpEnum b : OpEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}

	}

	@JsonProperty("op")
	private OpEnum op = null;

	@JsonProperty("path")
	private String path = null;

	@JsonProperty("value")
	private Object value = null;

	@JsonProperty("from")
	private String from = null;

	public PatchDocument op(OpEnum op) {
		this.op = op;
		return this;
	}

	/**
	 * Operació a realitzar
	 * 
	 * @return op
	 **/
	public OpEnum getOp() {
		return op;
	}

	public void setOp(OpEnum op) {
		this.op = op;
	}

	public PatchDocument path(String path) {
		this.path = path;
		return this;
	}

	/**
	 * Una ruta de l&#x27;objecte a modificar
	 * 
	 * @return path
	 **/
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public PatchDocument value(Object value) {
		this.value = value;
		return this;
	}

	/**
	 * El valor a utilitzar en la operació.
	 * 
	 * @return value
	 **/
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public PatchDocument from(String from) {
		this.from = from;
		return this;
	}

	/**
	 * UUna ruta de l&#x27;objecte a modificar
	 * 
	 * @return from
	 **/
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PatchDocument patchDocument = (PatchDocument) o;
		return Objects.equals(this.op, patchDocument.op) && Objects.equals(this.path, patchDocument.path)
				&& Objects.equals(this.value, patchDocument.value) && Objects.equals(this.from, patchDocument.from);
	}

	@Override
	public int hashCode() {
		return Objects.hash(op, path, value, from);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class PatchDocument {\n");

		sb.append("    op: ").append(toIndentedString(op)).append("\n");
		sb.append("    path: ").append(toIndentedString(path)).append("\n");
		sb.append("    value: ").append(toIndentedString(value)).append("\n");
		sb.append("    from: ").append(toIndentedString(from)).append("\n");
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