package net.conselldemallorca.helium.core.model.exception;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;

@Aspect
public class IncidentThrowsAdviceHelper {
	
	@Resource private AplicacioService aplicacioService;
	private static ThreadLocal<DadesAdvice> dadesAdviceThreadLocal = new ThreadLocal<DadesAdvice>();

	public static DadesAdvice getDadesAdvice() {
		return dadesAdviceThreadLocal.get();
	}
	public static void initDadesAdvice(Signature signature) {
		DadesAdvice dadesAdvice = dadesAdviceThreadLocal.get();
		if (dadesAdvice == null) 
			dadesAdvice = new DadesAdvice();
		if (dadesAdvice.getSignature() == null) {
			dadesAdvice.setSignature(signature);
			dadesAdviceThreadLocal.set(dadesAdvice);
		}
	}
	public static void addDadesAdvicePortasignatures(Integer idPortasignatures) {
		DadesAdvice dadesAdvice = dadesAdviceThreadLocal.get();
		dadesAdvice.addIdsPortasignatures(idPortasignatures);
		dadesAdviceThreadLocal.set(dadesAdvice);
	}
	public static void clearDadesAdvice(Signature signature) {
		DadesAdvice dadesAdvice = dadesAdviceThreadLocal.get();
		if (dadesAdvice.getSignature().equals(signature)) 
			dadesAdviceThreadLocal.set(new DadesAdvice());
	}

	public void before(JoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		initDadesAdvice(signature);
	}
	
	/**
	 * Called between the throw and the catch
	 */
	public void afterThrowing(JoinPoint joinPoint, Throwable e) {
		Signature signature = joinPoint.getSignature();
		String classOriginal  = signature.getDeclaringTypeName()!=null?signature.getDeclaringTypeName():"";
		String metodeOriginal = signature.getName()!=null?signature.getName():"";
		if (classOriginal!=null && (classOriginal.contains("ExpedientService") || classOriginal.contains("TascaService"))) {
			if (!getDadesAdvice().getIdsPortasignatures().isEmpty()) {
				for (Integer documentId : getDadesAdvice().getIdsPortasignatures()) {
					Jbpm3HeliumBridge.getInstanceService().portasignaturesEliminar(documentId);
				}
			}
			clearDadesAdvice(signature);
		}
		String params = "";
		if (joinPoint.getArgs()!=null && joinPoint.getArgs().length>0) {
			for (Object arg: joinPoint.getArgs()) {
				if (arg!=null) {
					params += arg.toString()+", ";
				} else {
					params += "null, ";
				}
			}
			params = params.substring(0, params.length()-2);
		}
		aplicacioService.excepcioSave(classOriginal+"."+metodeOriginal, params, e);
	}

	public void after(JoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		clearDadesAdvice(signature);
	}
	
	static class DadesAdvice {
		Signature signature;
		List<Integer> idsPortasignatures = new ArrayList<Integer>();
		
		public DadesAdvice() {
			idsPortasignatures = new ArrayList<Integer>();
		}
		public Signature getSignature() {
			return signature;
		}
		public void setSignature(Signature signature) {
			this.signature = signature;
		}
		public List<Integer> getIdsPortasignatures() {
			return idsPortasignatures;
		}
		public void setIdsPortasignatures(List<Integer> idsPortasignatures) {
			this.idsPortasignatures = idsPortasignatures;
		}
		public void addIdsPortasignatures(Integer idPortasignatures) {
			this.idsPortasignatures.add(idPortasignatures);
		}
	}
}
