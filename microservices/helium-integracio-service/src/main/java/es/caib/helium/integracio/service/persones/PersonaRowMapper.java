package es.caib.helium.integracio.service.persones;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;

import es.caib.helium.integracio.domini.persones.Persona;
import es.caib.helium.integracio.enums.persones.Sexe;

public class PersonaRowMapper implements RowMapper {

	private Environment env;
	
	public PersonaRowMapper() {
		
	}

	public PersonaRowMapper(Environment env) {
		super();
		this.env = env;
	}
	
	public Persona mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		if (nomLlinatgesSeparat()) {
			String codi = rs.getString(1);
			String nom = rs.getString(2);
			String llinatges = rs.getString(3);
			String dni = rs.getString(4);
			String email = rs.getString(5);
			var persona = new Persona(codi,
									nom,
									llinatges,
									email,
									Utilitats.sexePerNom(nom));
			persona.setDni(dni);
			return persona;
		} else {
			String codi = rs.getString(1);
			String nomSencer = rs.getString(2);
			String dni = rs.getString(3);
			String email = rs.getString(4);
			var persona = new Persona(codi,
									nomSencer,
									email,
									Utilitats.sexePerNom(nomSencer));
			persona.setDni(dni);
			return persona;
		}
	}
	
	private boolean nomLlinatgesSeparat() {
		String junt = env.getProperty("es.caib.helium.integracio.persones.jdbc.nom.llinatges.junt");
		return junt == null || junt.equalsIgnoreCase("false");
	}

}
