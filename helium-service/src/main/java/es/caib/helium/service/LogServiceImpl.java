package es.caib.helium.service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.comanda.model.v1.log.FitxerContingut;
import es.caib.comanda.model.v1.log.FitxerInfo;
import es.caib.comanda.service.v1.avis.ApiException;
import es.caib.helium.commons.utils.GlobalProperties;
import es.caib.helium.logic.intf.service.LogService;

@Service
public class LogServiceImpl implements LogService {

	private String LOGS_LOCATION = GlobalProperties.getInstance().getProperty("app.comanda.logs.location");
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	@Override
	public FitxerContingut getFitxerByNom(String nomFitxer) throws ApiException {
		try {
			File fitxer = new File(LOGS_LOCATION, nomFitxer);
			if(!fitxer.exists()) 
				throw new ApiException(500, "No s'ha trobat el fitxer '" + nomFitxer + "'");
			if(fitxer.isDirectory()) 
				throw new ApiException(500, nomFitxer + " es una carpeta");
			
			BasicFileAttributes attr = Files.readAttributes(fitxer.toPath(), BasicFileAttributes.class);
			
			return FitxerContingut
					.builder()
					.contingut(Files.readAllBytes(fitxer.toPath()))
					.mimeType(Files.probeContentType(fitxer.toPath()))
					.nom(fitxer.getName())
					.mida(attr.size())
					.dataCreacio(df.format(new Date(attr.creationTime().toMillis())))
					.dataModificacio(df.format(new Date(attr.lastModifiedTime().toMillis())))
					.build();
		} catch (IOException e) {
			throw new ApiException(500, e.getMessage());
		}
	}

	@Override
	public FitxerContingut llegitUltimesLinies(String nomFitxer, Long nLinies)
			throws ApiException {
		try {
			File fitxer = new File(LOGS_LOCATION, nomFitxer);
			if(!fitxer.exists()) 
				throw new ApiException(500, "No s'ha trobat el fitxer '" + nomFitxer + "'");
			if(fitxer.isDirectory()) 
				throw new ApiException(500, nomFitxer + " es una carpeta");
			
			BasicFileAttributes attr = Files.readAttributes(fitxer.toPath(), BasicFileAttributes.class);
			
			RandomAccessFile raf = new RandomAccessFile(fitxer, "r");
			
			long fileLength = raf.length();
			long pos = fileLength - 1; // apuntam al darrer byte del fitxer
			
			StringBuilder sb = new StringBuilder();
			
			long linesFound = 0;
			while(nLinies >= linesFound) {
				if(pos < 0) // Hem llegit tot el fitxer
					break;
				// Comprovam si el caracter al que apuntam es un salt de linea
				raf.seek(pos);
				char caracter = (char) raf.read();
				if(caracter == '\n')
					linesFound++;
				
				if(nLinies < linesFound)
					break;
				
				sb.append(caracter); // Afegim el caracter al resultat
				pos--; // Apuntam a la posició del seguent caracter
			}
			
			raf.close();
			
			return FitxerContingut
				.builder()
				.contingut(sb.reverse().toString().getBytes())
				.mimeType(Files.probeContentType(fitxer.toPath()))
				.nom(fitxer.getName())
				.mida(attr.size())
				.dataCreacio(df.format(new Date(attr.creationTime().toMillis())))
				.dataModificacio(df.format(new Date(attr.lastModifiedTime().toMillis())))
				.build();
		} catch (IOException e) {
			throw new ApiException(500, e.getMessage());
		}
	}

	@Override
	public List<FitxerInfo> llistarFitxers() throws ApiException {
		try {
			File directori = new File(LOGS_LOCATION);
			if(!directori.exists()) 
				throw new ApiException(500, "No s'ha trobat la capeta '" + LOGS_LOCATION + "'");
			if(!directori.isDirectory()) 
				throw new ApiException(500, LOGS_LOCATION + " no es una carpeta");
			
			List<FitxerInfo> continguts = new ArrayList<FitxerInfo>();
			for(File f : directori.listFiles()) {
				if(f.isFile()) {
					BasicFileAttributes attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
					continguts.add(
						FitxerInfo
							.builder()
							.nom(f.getName())
							.mida(attr.size())
							.mimeType(Files.probeContentType(f.toPath()))
							.dataCreacio(df.format(new Date(attr.creationTime().toMillis())))
							.dataModificacio(df.format(new Date(attr.lastModifiedTime().toMillis())))
							.build());
				}
			}
			return continguts;
		} catch (IOException e) {
			throw new ApiException(500, e.getMessage());
		}
	}

}
