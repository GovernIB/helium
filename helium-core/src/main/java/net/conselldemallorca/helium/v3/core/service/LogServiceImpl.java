package net.conselldemallorca.helium.v3.core.service;

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

import com.sun.jersey.core.util.Base64;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.ApiException;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.FitxerContingut;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.FitxerInfo;
import net.conselldemallorca.helium.v3.core.api.service.LogService;

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
			return new FitxerContingut(
					new String(Base64.encode(Files.readAllBytes(fitxer.toPath()))),
					Files.probeContentType(fitxer.toPath()),
					fitxer.getName(), 
					attr.size(), 
					df.format(new Date(attr.creationTime().toMillis())), 
					df.format(new Date(attr.lastModifiedTime().toMillis())));
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
			
			return new FitxerContingut(
					new String(Base64.encode(sb.reverse().toString().getBytes())),
					Files.probeContentType(fitxer.toPath()),
					fitxer.getName(), 
					fileLength, 
					df.format(new Date(attr.creationTime().toMillis())), 
					df.format(new Date(attr.lastModifiedTime().toMillis())));
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
						new FitxerInfo(
							f.getName(),
							attr.size(),
							df.format(new Date(attr.creationTime().toMillis())),
							df.format(new Date(attr.lastModifiedTime().toMillis()))
						));
				}
			}
			return continguts;
		} catch (IOException e) {
			throw new ApiException(500, e.getMessage());
		}
	}

}
