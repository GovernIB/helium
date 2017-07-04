package net.conselldemallorca.helium.webapp.v3.config;

import java.io.PrintStream;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Classe per inicialitzar la aplicació d'helium en algus aspectes. */
public class InicialitzacioListener implements ServletContextListener {


	private PrintStream out;
	private PrintStream err;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// Redirigeix la sortida stdout cap als logs
		this.out = System.out;
		System.setOut(new SystemOutPrintStream(System.out));
		// Redirigeix la sortida stderr cap als logs
		this.err = System.err;
		SystemOutPrintStream soupErr = new SystemOutPrintStream(System.out);
		soupErr.setError(true);
		System.setErr(soupErr);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.setOut(out);
		System.setErr(err);
	}

	/** Classe PrintStream per redirigir el system.out cap als logs. */
	static class SystemOutPrintStream extends PrintStream {

		private static final Log logger = LogFactory.getLog(SystemOutPrintStream.class);
		/** Indica si el log és error o info. */
		private boolean error;

		public boolean isError() {
			return error;
		}
		public void setError(boolean error) {
			this.error = error;
		}

		public SystemOutPrintStream(PrintStream original) {
			super(original);
		}

		/** Sobreescriu els println i ho redirigeix cap al log. */
		@Override
		public void println(String line) {
			if (error)
				logger.error(line);
			else
				logger.info(line);
		}
	}

}
