package net.conselldemallorca.helium.v3.core.service;

import java.io.File;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import net.conselldemallorca.helium.core.helper.MonitorIntegracioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Avis;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.AppInfo;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.ContextInfo;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.DetallSalut;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.EstatSalut;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.EstatSalutEnum;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.IntegracioApp;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.IntegracioInfo;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.IntegracioPeticions;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.IntegracioSalut;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.Manual;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.MissatgeSalut;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.SalutInfo;
import net.conselldemallorca.helium.v3.core.api.service.SalutService;
import net.conselldemallorca.helium.v3.core.repository.AvisRepository;

@Service
public class SalutServiceImpl implements SalutService {

	private static final int MAX_CONNECTION_RETRY = 3;
	
//	@Autowired
//	private PluginHelper pluginHelper;
	
	@Resource
	private AvisRepository avisRepository;
	@Resource
	private MonitorIntegracioHelper monitorIntegracioHelper;
	private Date lastCheckout;
	
	
	@Override
	public List<IntegracioInfo> getIntegracions() {
		// Per ara es retorna un llistat fix de les diferents integracions
		return Lists.newArrayList(
				new IntegracioInfo(IntegracioApp.ARX.name(), IntegracioApp.ARX.getNom()),
				new IntegracioInfo(IntegracioApp.AFI.name(), IntegracioApp.AFI.getNom()),
				new IntegracioInfo(IntegracioApp.VFI.name(), IntegracioApp.VFI.getNom()),
				new IntegracioInfo(IntegracioApp.NOT.name(), IntegracioApp.NOT.getNom()),
				new IntegracioInfo(IntegracioApp.PBL.name(), IntegracioApp.PBL.getNom()),
				new IntegracioInfo(IntegracioApp.PFI.name(), IntegracioApp.PFI.getNom()),
				new IntegracioInfo(IntegracioApp.REG.name(), IntegracioApp.REG.getNom()),
				new IntegracioInfo(IntegracioApp.RSC.name(), IntegracioApp.RSC.getNom()),
				new IntegracioInfo(IntegracioApp.DIS.name(), IntegracioApp.DIS.getNom()));
	}

	@Override
	public List<AppInfo> getSubsistemes() {
		List<AppInfo> subsitemes = new ArrayList<AppInfo>();
		subsitemes.add(AppInfo.builder().codi("AWE").nom("Alta web").build());
		return subsitemes;
	}

	@Override
	public List<ContextInfo> getContexts(String baseUrl) {
		return Lists.newArrayList(
			ContextInfo.builder()
				.codi("BACK")
				.nom("Backoffice")
				.path(baseUrl + "/helium")
				.manuals(Lists.newArrayList(
						Manual.builder().nom("Manual d'usuari").path("https://github.com/GovernIB/helium/blob/helium-3.3/doc/pdf/Helium_manual_usuari.pdf").build(),
						Manual.builder().nom("Manual de disseny").path("https://github.com/GovernIB/helium/blob/helium-3.3/doc/pdf/manual_disseny.pdf").build())
						)
				.build(),
			ContextInfo.builder()
				.codi("EXT")
				.nom("API externa")
				.path(baseUrl + "/helium/rest")
				.api(baseUrl + "/helium/rest")
				.build()
		);
	}

	@Override
	public SalutInfo checkSalut(String versio, String performanceUrl) {
		EstatSalut estatSalut = checkEstatSalut(performanceUrl);		// Estat
		EstatSalut salutDatabase = checkDatabase();						// Base de dades
		List<IntegracioSalut> integracions = checkIntegracions();		// Integracions
		List<DetallSalut> altres = checkAltres();						// Altres
		List<MissatgeSalut> missatges = checkMissatges();				// Missatges
		EstatSalutEnum estatGlobalSubsistemes = EstatSalutEnum.UP;

		if (EstatSalutEnum.UP.equals(estatSalut.getEstat()) && !EstatSalutEnum.UP.equals(estatGlobalSubsistemes)) {
			estatSalut = EstatSalut.builder()
				.estat(EstatSalutEnum.UP)
				.latencia(estatSalut.getLatencia())
				.build();
		}

		return SalutInfo.builder()
				.codi("HEL")
				.versio(versio)
				.data(new Date())
				.estat(estatSalut)
				.bd(salutDatabase)
				.integracions(integracions)
				.subsistemes(null)
				.altres(altres)
				.missatges(missatges)
				.build();
	}
	
	
	
	private EstatSalut checkEstatSalut(String performanceUrl) {

		long start = System.currentTimeMillis();
		EstatSalutEnum estat = EstatSalutEnum.UP;
		//String response = null;
		for (int i = 1; i <= MAX_CONNECTION_RETRY; i++) {
			try {
				// restTemplate.getForObject(performanceUrl, String.class);
				break;
			} catch (Exception e) {
				if (i == MAX_CONNECTION_RETRY) {
					estat = EstatSalutEnum.DOWN; // After 3 connection failed attempts
				}
			}
		}
		long end = System.currentTimeMillis();
		Long latency = end - start;
		return EstatSalut.builder()
				.estat(estat)
				.latencia(latency.intValue())
				.build();
	}

	private EstatSalut checkDatabase() {
		long start = System.currentTimeMillis();
		try {
			Context initContext = new InitialContext();
			String dataSourceJndi = "java:/es.caib.helium.db";
			if (isDesplegamentTomcat())
				dataSourceJndi = "java:/comp/env/jdbc/HeliumDS";
			DataSource ds = (DataSource)initContext.lookup(dataSourceJndi);
			
			JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
			jdbcTemplate.execute("SELECT 1 AS x FROM DUAL");
			long end = System.currentTimeMillis();
			Long latency = end - start;
			return EstatSalut.builder()
					.estat(EstatSalutEnum.UP)
					.latencia(latency.intValue())
					.build();
		} catch (Exception e) {
			long end = System.currentTimeMillis();
			Long latency = end - start;
			return EstatSalut.builder()
					.estat(EstatSalutEnum.DOWN)
					.latencia(latency.intValue())
					.build();
		}
	}
	
	private boolean isDesplegamentTomcat() {
		String desplegamentTomcat = GlobalProperties.getInstance().getProperty("app.domini.desplegament.tomcat");
		return "true".equalsIgnoreCase(desplegamentTomcat);
	}

	private List<IntegracioSalut> checkIntegracions() {
		List<IntegracioSalut> integracions = new ArrayList<IntegracioSalut>();
		for(IntegracioInfo integracio : getIntegracions()) {
			Map<String, IntegracioPeticions> peticionsPerEntorn = new HashMap<String, IntegracioPeticions>();
			Map<String, List<Long>> totalPerEntorn = new HashMap<String, List<Long>>();
			
			List<IntegracioAccioDto> requests = monitorIntegracioHelper.findAccionsByIntegracioCodi(getMonitorIntegracioHelper(integracio.getCodi()));
			Integer latencia = 0;
			EstatSalutEnum estat = EstatSalutEnum.UNKNOWN;
			
			int totalOk = 0;
			int totalError = 0;
			long totalTempsMig = 0;
			Long peticionsOkUltimPeriode = 0l;
			Long peticionsErrorUltimPeriode = 0l;
			List<Long> tempsMigUltimPeriode = new ArrayList<Long>();
			
			for(IntegracioAccioDto request : requests) {
				Long entornId = request.getEntornId();
				String entornIdStr = entornId != null? entornId.toString() : "";
				IntegracioPeticions entornIntegracions = peticionsPerEntorn.get(entornIdStr);
				if(entornIntegracions == null) {
					entornIntegracions = IntegracioPeticions.builder()
											.totalOk(0l)
											.totalError(0l)
											.totalTempsMig(0)
											.peticionsOkUltimPeriode(0l)
											.peticionsErrorUltimPeriode(0l)
											.tempsMigUltimPeriode(0)
											.build();
					peticionsPerEntorn.put(entornIdStr, entornIntegracions);
				}
				if(totalPerEntorn.get(entornIdStr) == null) {
					totalPerEntorn.put(entornIdStr, new ArrayList<Long>());
				}
				
				totalTempsMig += request.getTempsResposta();
				
				entornIntegracions.setTotalTempsMig(entornIntegracions.getTotalTempsMig() + Long.valueOf(request.getTempsResposta()).intValue());
				
				boolean isInPeriode = lastCheckout == null || request.getData().after(lastCheckout);
				
				if(isInPeriode) {
					tempsMigUltimPeriode.add(request.getTempsResposta());
				}
				
				if(request.getTipus() == IntegracioAccioTipusEnumDto.ENVIAMENT) {
					latencia = Long.valueOf(request.getTempsResposta()).intValue();
					if(request.getEstat() == IntegracioAccioEstatEnumDto.OK) {
						estat = EstatSalutEnum.UP;
						totalOk++;
						entornIntegracions.setTotalOk(entornIntegracions.getTotalOk()+1);
						if(isInPeriode) {
							entornIntegracions.setPeticionsOkUltimPeriode(entornIntegracions.getPeticionsOkUltimPeriode()+1);
							peticionsOkUltimPeriode++;
						}
					} else {
						// TODO: S'ha de comprovar a la darrera crida s'ha caigut el sistema extern
						estat = EstatSalutEnum.DOWN;
						totalError++;
						entornIntegracions.setTotalError(entornIntegracions.getTotalError()+1);
						if(isInPeriode) {
							entornIntegracions.setPeticionsErrorUltimPeriode(entornIntegracions.getPeticionsErrorUltimPeriode() + 1);
							peticionsErrorUltimPeriode++;
						}
					}
				}
			}
			
			for(String key : peticionsPerEntorn.keySet()) {
				IntegracioPeticions integracioEntorn = peticionsPerEntorn.get(key);
				integracioEntorn.setTempsMigUltimPeriode(calculaMitga(totalPerEntorn.get(key)).intValue());
			}
			
			integracions.add(
				IntegracioSalut
					.builder()
					.codi(integracio.getCodi())
					.latencia(latencia)
					.estat(estat)
					.peticions(IntegracioPeticions
							.builder()
							.peticionsPerEntorn(peticionsPerEntorn)
							.totalError(totalError)
							.totalOk(totalOk)
							.totalTempsMig(Long.valueOf(requests.isEmpty()? 0 : totalTempsMig / requests.size()).intValue())
							.peticionsErrorUltimPeriode(peticionsErrorUltimPeriode)
							.peticionsOkUltimPeriode(peticionsOkUltimPeriode)
							.tempsMigUltimPeriode(calculaMitga(tempsMigUltimPeriode).intValue())
							.endpoint(getIntegracioEndpoint(integracio.getCodi()))
							.build())
					.build());
		}
		lastCheckout = new Date();
		return integracions;
	}
	
	private Long calculaMitga(List<Long> nums) {
		if(nums == null || nums.isEmpty()) return 0l;
		long total = 0l;
		for(Long num : nums) {
			total += num;
		}
		return total / nums.size();
	}

	public List<DetallSalut> checkAltres() {
		OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
		// Nombre de cores (CPU)
		//int availableProcessors = osBean.getAvailableProcessors();
		String os = osBean.getName() + " " + osBean.getVersion() + " (" + osBean.getArch() + ")";
		
		try {
			String systemCpuLoad = "No disponible";
			String processCpuLoad = "No disponible";
		
			loadSigarNativeLibs();
			Sigar sigar = new Sigar();
			// Informació sobre CPU
			CpuPerc cpu = sigar.getCpuPerc();
			systemCpuLoad = CpuPerc.format(cpu.getCombined());
			processCpuLoad = CpuPerc.format(sigar.getProcCpu(sigar.getPid()).getPercent());
			// Informació sobre Memòria
			Mem memory = sigar.getMem();
			// Informació sobre Disc
			Long totalSpace = 0L;
			Long freeSpace = 0L;
			FileSystem[] fileSystems = sigar.getFileSystemList();
			for (FileSystem fs : fileSystems) {
				if (fs.getDirName().equals("/")) {
					FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
					totalSpace = usage.getTotal();
					freeSpace = usage.getFree();
					break;
				}
			}
			
			return Lists.newArrayList(
			DetallSalut.builder().codi("PRC").nom("Processadors").valor(String.valueOf(Runtime.getRuntime().availableProcessors())).build(),
			DetallSalut.builder().codi("SCPU").nom("Càrrega del sistema").valor(systemCpuLoad).build(),
			DetallSalut.builder().codi("PCPU").nom("Càrrega del procés").valor(processCpuLoad).build(),
			DetallSalut.builder().codi("MED").nom("Memòria disponible").valor(humanReadableByteCount(memory.getFree())).build(),
			DetallSalut.builder().codi("MET").nom("Memòria total").valor(humanReadableByteCount(memory.getTotal())).build(),
			DetallSalut.builder().codi("EDT").nom("Espai de disc total").valor(humanReadableByteCount(totalSpace)).build(),
			DetallSalut.builder().codi("EDL").nom("Espai de disc lliure").valor(humanReadableByteCount(freeSpace)).build(),
			DetallSalut.builder().codi("SO").nom("Sistema operatiu").valor(os).build());
		
		} catch (Exception e) {
			logger.error("No s'ha pogut obtenir informació del sistema utilitzant la llibreria Sigar", e);
		try {
			// Càrrega de la CPU (només per la implementació de Sun)
			if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
				com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
				String systemCpuLoad = sunOsBean.getSystemCpuLoad() * 100 + "%";
				String processCpuLoad = sunOsBean.getProcessCpuLoad() * 100 + "%";
				Long totalSpace = 0L;
				Long freeSpace = 0L;
				for (File root : File.listRoots()) {
				if (root.getTotalSpace() > totalSpace) {
					totalSpace = root.getTotalSpace();
					freeSpace = root.getFreeSpace();
				}
			}
		
		return Lists.newArrayList(
			DetallSalut.builder().codi("PRC").nom("Processadors").valor(String.valueOf(Runtime.getRuntime().availableProcessors())).build(),
			DetallSalut.builder().codi("CPU").nom("Càrrega del sistema").valor(systemCpuLoad).build(),
			DetallSalut.builder().codi("CPU").nom("Càrrega del procés").valor(processCpuLoad).build(),
			DetallSalut.builder().codi("MED").nom("Memòria disponible").valor((Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "Ilimitada" : humanReadableByteCount(Runtime.getRuntime().maxMemory()))).build(),
			DetallSalut.builder().codi("MET").nom("Memòria total").valor(humanReadableByteCount(Runtime.getRuntime().totalMemory())).build(),
			DetallSalut.builder().codi("EDT").nom("Espai de disc total").valor(humanReadableByteCount(totalSpace)).build(),
			DetallSalut.builder().codi("EDL").nom("Espai de disc lliure").valor(humanReadableByteCount(freeSpace)).build(),
			DetallSalut.builder().codi("SO").nom("Sistema operatiu").valor(os).build()
		);
			}
		} catch (Exception e2) {
			logger.error("Salut: No s'ha pogut obtenir informació del sistema amb la implementació de Sun", e2);
		}
			return null;
		}
	}

	public List<MissatgeSalut> checkMissatges() {
		List<MissatgeSalut> missatges = new ArrayList<MissatgeSalut>();
		try {
			List<Avis> avisos = avisRepository.findActive(DateUtils.truncate(new Date(), Calendar.DATE));
		if (avisos != null && !avisos.isEmpty()) {
			for(Avis avis : avisos) {
				missatges.add(MissatgeSalut
						.builder()
						.missatge(avis.getMissatge())
						.data(avis.getDataInici())
						.nivell(avis.getAvisNivell().name())
						.build());
			}
		}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String humanReadableByteCount(long bytes) {
		long unit = 1000;
		if (bytes < unit) {
			return bytes + " B";
		}
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		char pre = "kMGTPE".charAt(exp - 1);
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	public static void loadSigarNativeLibs() throws Exception {
		OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
		String osName = osBean.getName().toLowerCase();
		boolean osArch64 = osBean.getArch().contains("64");

		String nativeLibPath = "net/conselldemallorca/helium/core/sigar/natives/";

		if (osName.contains("win")) {
			nativeLibPath += osArch64 ? "windows/sigar-amd64-winnt.dll" : "windows/sigar-x86-winnt.dll";
		} else if (osName.contains("linux")) {
			nativeLibPath += osArch64 ? "linux/libsigar-amd64-linux.so" : "linux/libsigar-x86-linux.so";
		} else if (osName.contains("mac")) {
			nativeLibPath += osArch64 ? "macos/libsigar-universal64-macosx.dylib" : "macos/libsigar-universal-macosx.dylib";
		} else {
			throw new UnsupportedOperationException("OS not supported for Sigar natives: " + osName);
		}

		// Copia la llibreria nativa a un directori temporal
		InputStream in = SalutServiceImpl.class.getClassLoader().getResourceAsStream(nativeLibPath);
		if (in == null) {
			throw new RuntimeException("Failed to load native library from path: " + nativeLibPath);
		}

		File tempLib = File.createTempFile("sigar", nativeLibPath.substring(nativeLibPath.lastIndexOf('.')));
		Files.copy(in, tempLib.toPath(), StandardCopyOption.REPLACE_EXISTING);
		in.close();

		// Afegir el directori temporal al java.library.path
		System.load(tempLib.getAbsolutePath());
	}
	
	private String getMonitorIntegracioHelper(String codi) {
		if(codi == null) return null;
		switch(IntegracioApp.valueOf(codi)) {
		case ARX:
			return MonitorIntegracioHelper.INTCODI_ARXIU;
		case AFI:
			return MonitorIntegracioHelper.INTCODI_FIRMA;
		case VFI:
			return MonitorIntegracioHelper.INTCODI_FIRMA_SERV;
		case NOT:
			return MonitorIntegracioHelper.INTCODI_NOTIB;
		case PBL:
			return MonitorIntegracioHelper.INTCODI_PINBAL;
		case PFI:
			return MonitorIntegracioHelper.INTCODI_PFIRMA;
		case REG:
			return MonitorIntegracioHelper.INTCODI_REGISTRE;
		case RSC:
			return MonitorIntegracioHelper.INTCODI_PROCEDIMENT;
		case DIS:
			return MonitorIntegracioHelper.INTCODI_DISTRIBUCIO;
		default:
			return null;
		}
	}
	
	private String getIntegracioEndpoint(String codi) {
		if(codi == null) return null;
		switch(IntegracioApp.valueOf(codi)) {
		case ARX:
			return GlobalProperties.getInstance().getProperty("app.plugin.arxiu.caib.base.url");
		case AFI:
			return GlobalProperties.getInstance().getProperty("app.plugin.firma.portafib.plugins.signatureserver.portafib.api_passarela_url");
		case VFI:
			return GlobalProperties.getInstance().getProperty("app.plugins.validatesignature.afirmacxf.endpoint");
		case NOT:
			return GlobalProperties.getInstance().getProperty("app.notificacio.plugin.url");
		case PBL:
			return GlobalProperties.getInstance().getProperty("app.pinbal.plugin.url");
		case PFI:
			return GlobalProperties.getInstance().getProperty("app.plugin.passarelafirma.1.plugins.signatureweb.portafib.apifirmawebsimple.endpoint");
		case REG:
			return GlobalProperties.getInstance().getProperty("app.registre.plugin.ws.url");
		case RSC:
			return GlobalProperties.getInstance().getProperty("app.plugins.procediments.rolsac.service.url");
		case DIS:
			return GlobalProperties.getInstance().getProperty("net.conselldemallorca.helium.distribucio.backofficeIntegracio.ws.url");
		default:
			return null;
		}
	}
	
	private static final Log logger = LogFactory.getLog(SalutServiceImpl.class);
}
