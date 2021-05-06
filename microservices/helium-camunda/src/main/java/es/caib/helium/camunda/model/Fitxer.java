package es.caib.helium.camunda.model;

import lombok.Builder;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Data
@Builder
public class Fitxer {

    String nom;
    byte[] contingut;

    public InputStream getInputStream() {
        return new ByteArrayInputStream(contingut);
    }

    public static class FitxerBuilder {
        public FitxerBuilder contingut(InputStream inputStream) throws IOException {
            this.contingut = inputStream.readAllBytes();
            return this;
        }
        public FitxerBuilder contingut(byte[] contingut) throws IOException {
            this.contingut = contingut;
            return this;
        }
    }
}
