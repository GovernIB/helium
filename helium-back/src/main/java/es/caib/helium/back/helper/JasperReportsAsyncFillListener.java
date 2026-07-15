package es.caib.helium.back.helper;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.AsynchronousFilllListener;

public class JasperReportsAsyncFillListener implements AsynchronousFilllListener {
	private InformeHelper.InformeInfo informeInfo;
	private JasperReportsHelper jasperReportsHelper;

	public JasperReportsAsyncFillListener(
		InformeHelper.InformeInfo info,
		JasperReportsHelper jasperReportsHelper) {
		this.informeInfo = info;
		this.jasperReportsHelper = jasperReportsHelper;
	}

	@Override
	public void reportFinished(JasperPrint jasperPrint) {
		jasperReportsHelper.setJasperPrint(informeInfo, jasperPrint);
		informeInfo.setEstat(InformeHelper.Estat.FINALITZAT);
		notificarFinalitzat();
	}

	@Override
	public void reportCancelled() {
		informeInfo.setEstat(InformeHelper.Estat.CANCELLAT);
		notificarFinalitzat();
	}

	@Override
	public void reportFillError(Throwable t) {
		informeInfo.setEstat(InformeHelper.Estat.ERROR);
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
