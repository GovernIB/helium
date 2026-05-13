package es.caib.helium.logic.service;

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

import es.caib.comanda.model.server.monitoring.*;
import es.caib.comanda.ms.salut.helper.IntegracioApp;
import es.caib.comanda.ms.salut.helper.SalutHelper;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import es.caib.comanda.ms.salut.helper.MonitorHelper;
import es.caib.helium.commons.dto.AvisNivellEnumDto;
import es.caib.helium.commons.dto.IntegracioAccioDto;
import es.caib.helium.commons.dto.IntegracioAccioEstatEnumDto;
import es.caib.helium.commons.dto.IntegracioAccioTipusEnumDto;
import es.caib.helium.commons.utils.GlobalProperties;
import es.caib.helium.logic.intf.service.SalutService;
import es.caib.helium.logic.intf.util.DatesUtils;
import es.caib.helium.persistence.entity.Avis;
import es.caib.helium.persistence.repository.AvisRepository;
import es.caib.helium.logic.helper.MonitorIntegracioHelper;

@Service
public class SalutServiceImpl implements SalutService {

	private static final int MAX_CONNECTION_RETRY = 3;

	private String baseUrl = GlobalProperties.getInstance().getProperty("app.base.url");

	@Resource
	private AvisRepository avisRepository;
	@Resource
	private MonitorIntegracioHelper monitorIntegracioHelper;
	private Date lastCheckout;

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

	public List<SubsistemaInfo> getSubsistemes() {
		return Lists.newArrayList(new SubsistemaInfo("AWE", "Alta web"));
	}

	public List<ContextInfo> getContexts() {
		return Lists.newArrayList(
			new ContextInfo()
				.codi("BACK")
				.nom("Backoffice")
				.path(baseUrl + "/helium")
				.manuals(Lists.newArrayList(
					new Manual().nom("Manual d'usuari").path("https://github.com/GovernIB/helium/blob/helium-3.3/doc/pdf/Helium_manual_usuari.pdf"),
					new Manual().nom("Manual de disseny").path("https://github.com/GovernIB/helium/blob/helium-3.3/doc/pdf/manual_disseny.pdf"))
				),
			new ContextInfo()
				.codi("EXT")
				.nom("API externa")
				.path(baseUrl + "/helium/rest")
				.api(baseUrl + "/helium/rest")
		);
	}

	@Override
	public SalutInfo checkSalut(String versio, String performanceUrl) {
		EstatSalut estatSalut = checkEstatSalut(performanceUrl);		// Estat
		EstatSalut salutDatabase = checkDatabase();						// Base de dades
		List<IntegracioSalut> integracions = checkIntegracions();		// Integracions
		//List<DetallSalut> altres = checkAltres();						// Altres
		List<MissatgeSalut> missatges = checkMissatges();				// Missatges
		//EstatSalutEnum estatGlobalSubsistemes = EstatSalutEnum.UP;

		if (EstatSalutEnum.UP.equals(estatSalut.getEstat())) {
			estatSalut = new EstatSalut()
				.estat(EstatSalutEnum.UP)
				.latencia(estatSalut.getLatencia());
		}

		return new SalutInfo()
				.codi("HEL")
				.versio(versio)
				.data(DatesUtils.toOffsetDateTime(lastCheckout))
				.estatGlobal(estatSalut)
				.estatBaseDeDades(salutDatabase)
				.integracions(integracions)
				.subsistemes(null)
				.informacioSistema(MonitorHelper.getInfoSistema())
				.missatges(missatges);
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
		long latency = end - start;
		return new EstatSalut()
				.estat(estat)
				.latencia(Long.valueOf(latency).intValue());
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
			long latency = end - start;
			return new EstatSalut()
					.estat(EstatSalutEnum.UP)
					.latencia(Long.valueOf(latency).intValue());
		} catch (Exception e) {
			long end = System.currentTimeMillis();
			long latency = end - start;
			return new EstatSalut()
					.estat(EstatSalutEnum.DOWN)
					.latencia(Long.valueOf(latency).intValue());
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
			int latencia = 0;
			EstatSalutEnum estat = EstatSalutEnum.UNKNOWN;

			long totalOk = 0;
			long totalError = 0;
			long totalTempsMig = 0;
			Long peticionsOkUltimPeriode = 0L;
			Long peticionsErrorUltimPeriode = 0L;
			List<Long> tempsMigUltimPeriode = new ArrayList<Long>();

			for(IntegracioAccioDto request : requests) {
				if(request.getEntornId() == null)
					continue;

				String entornIdStr = request.getIntegracioCodi();
				IntegracioPeticions entornIntegracions = peticionsPerEntorn.get(entornIdStr);
				if(entornIntegracions == null) {
					entornIntegracions = new IntegracioPeticions()
											.totalOk(0L)
											.totalError(0L)
											.totalTempsMig(0)
											.peticionsOkUltimPeriode(0L)
											.peticionsErrorUltimPeriode(0L)
											.tempsMigUltimPeriode(0);
					peticionsPerEntorn.put(entornIdStr, entornIntegracions);
				}

				// Si entornIdStr no existeix s'afegeix amb un List<Long>
				totalPerEntorn.computeIfAbsent(entornIdStr, k -> new ArrayList<Long>());

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
				new IntegracioSalut()
					.codi(integracio.getCodi())
					.latencia(latencia)
					.estat(estat)
					.peticions(new IntegracioPeticions()
							.totalError(totalError)
							.totalOk(totalOk)
							.totalTempsMig(Long.valueOf(requests.isEmpty()? 0 : totalTempsMig / requests.size()).intValue())
							.peticionsErrorUltimPeriode(peticionsErrorUltimPeriode)
							.peticionsOkUltimPeriode(peticionsOkUltimPeriode)
							.tempsMigUltimPeriode(calculaMitga(tempsMigUltimPeriode).intValue())
							.endpoint(getIntegracioEndpoint(integracio.getCodi())))
					);
		}
		lastCheckout = new Date();
		return integracions;
	}

	private Long calculaMitga(List<Long> nums) {
		if(nums == null || nums.isEmpty()) return 0L;
		long total = 0L;
		for(Long num : nums) {
			total += num;
		}
		return total / nums.size();
	}

	public InformacioSistema checkInformacioSistema() {
		try {
			return MonitorHelper.getInfoSistema();
		} catch (Exception e) {
			logger.error("No s'ha pogut obtenir informació del sistema", e);
			return null;
		}
	}

	public List<MissatgeSalut> checkMissatges() {
		List<MissatgeSalut> missatges = new ArrayList<MissatgeSalut>();
		try {
			List<Avis> avisos = avisRepository.findActive(DateUtils.truncate(new Date(), Calendar.DATE));
			if (avisos != null && !avisos.isEmpty()) {
				for(Avis avis : avisos) {
					missatges.add(new MissatgeSalut()
							.missatge(avis.getMissatge())
							.data(DatesUtils.toOffsetDateTime(avis.getDataInici()))
							.nivell(toSalutNivell(avis.getAvisNivell())));
				}
			}
			return missatges;
		} catch (Exception e) {
			return null;
		}
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

	private SalutNivell toSalutNivell(AvisNivellEnumDto avisNivell) {
		switch(avisNivell) {
		case INFO:
			return SalutNivell.INFO;
		case WARNING:
			return SalutNivell.WARN;
		case ERROR:
			return SalutNivell.ERROR;
		}
		return null;
	}

	private static final Log logger = LogFactory.getLog(SalutServiceImpl.class);
}
