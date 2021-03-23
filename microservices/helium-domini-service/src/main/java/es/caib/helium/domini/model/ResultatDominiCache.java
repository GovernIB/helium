package es.caib.helium.domini.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.time.LocalDateTime;

@Validated
@Data
@ToString
public class ResultatDominiCache implements Serializable {

    private LocalDateTime dataCreacio;
    private ResultatDomini resultatDomini;


    public ResultatDominiCache(ResultatDomini resultatDomini) {
        this.resultatDomini = resultatDomini;
        this.dataCreacio = LocalDateTime.now();
    }
}
