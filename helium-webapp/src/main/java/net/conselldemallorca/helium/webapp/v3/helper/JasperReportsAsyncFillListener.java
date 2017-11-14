package net.conselldemallorca.helium.webapp.v3.helper;

import net.conselldemallorca.helium.webapp.v3.helper.InformeHelper.Estat;
import net.conselldemallorca.helium.webapp.v3.helper.InformeHelper.InformeInfo;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.AsynchronousFilllListener;

/** Classe Listener per a actualitzar l'estat de la generació de l'informe durant la generació asíncrona.
 * 
 * @author Limit Tecnologies <Limit Tecnologies>
 *
 */
public class JasperReportsAsyncFillListener implements AsynchronousFilllListener{

	private InformeInfo informeInfo;
	private JasperReportsHelper jasperReportsHelper;
		
	public JasperReportsAsyncFillListener(
			InformeInfo info,
			JasperReportsHelper jasperReportsHelper) {
		this.informeInfo = info;
		this.jasperReportsHelper = jasperReportsHelper;
	}
	
	@Override
	public void reportFinished(JasperPrint jasperPrint) {
		jasperReportsHelper.setJasperPrint(informeInfo, jasperPrint);
		informeInfo.setEstat(Estat.FINALITZAT);
		notificarFinalitzat();
	}

	@Override
	public void reportCancelled() {
		informeInfo.setEstat(Estat.CANCELLAT);
		notificarFinalitzat();
	}

	@Override
	public void reportFillError(Throwable t) {
		informeInfo.setEstat(Estat.ERROR);
		informeInfo.setError(t);
		notificarFinalitzat();
	}

	public void esperarFinalitzar() {
		synchronized(informeInfo) {
			try {
				informeInfo.wait();
			} catch (InterruptedException e) {}
		}
	}
	
	private void notificarFinalitzat() {
		jasperReportsHelper.notificarFinalitzat(this.informeInfo);
	}

}
