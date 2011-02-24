/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura.applet;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.UIManager;

import netscape.javascript.JSObject;

import org.jdesktop.layout.GroupLayout;



/**
 * Applet per a la signatura de documents digitals
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public abstract class SignaturaApplet extends Applet {

	private JPanel jPanel = new JPanel();
	private JButton jButtonSignar;
	private JComboBox jComboBoxCertificats;
	private JLabel jLabelCertificat;
	private JLabel jLabelContrasenya;
	private JPasswordField jPasswordField;

	private PropertyResourceBundle missatges;



	public void init() {
		String lang = getParameter("locale");
		try {
			if (lang != null)
				missatges = (PropertyResourceBundle)ResourceBundle.getBundle("net.conselldemallorca.helium.integracio.plugins.signatura.applet.missatges_signatura", new Locale(lang));
			else
				missatges = (PropertyResourceBundle)ResourceBundle.getBundle("net.conselldemallorca.helium.integracio.plugins.signatura.applet.missatges_signatura");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		initComponents();
		initSignature();
	}



	private void initComponents() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {}
		jComboBoxCertificats = new JComboBox();
		jLabelCertificat = new JLabel();
		jLabelContrasenya = new JLabel();
		jPasswordField = new JPasswordField();
		jButtonSignar = new JButton();

		jComboBoxCertificats.setModel(
				new DefaultComboBoxModel(
						new String[] {missatges.getString("msg.info.carregant")}));
		jComboBoxCertificats.setEnabled(false);

		jLabelCertificat.setText(missatges.getString("label.certificat"));
		
		jLabelContrasenya.setText(missatges.getString("label.contrasenya"));
		jPasswordField.setEnabled(false);

		jButtonSignar.setText(missatges.getString("accio.signar"));
		jButtonSignar.setEnabled(false);

        GroupLayout layout = new GroupLayout(jPanel);
        jPanel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .addContainerGap()
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(jLabelContrasenya)
                                .add(jLabelCertificat))
                            .add(18, 18, 18)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 194, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jComboBoxCertificats, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 194, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(jButtonSignar))
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .addContainerGap()
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabelCertificat)
                        .add(jComboBoxCertificats, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabelContrasenya)
                        .add(jPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jButtonSignar)
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        jButtonSignar.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {jButtonSignarActionPerformed(e);}
		});
        add(jPanel, BorderLayout.CENTER);
        String bgColor = getParameter("bgColor");
        if (bgColor == null) {
        	setBackground(Color.WHITE);
        	jPanel.setBackground(Color.WHITE);
        } else {
        	setBackground(new Color(Integer.valueOf(bgColor, 16).intValue(), true));
        	jPanel.setBackground(new Color(Integer.valueOf(bgColor, 16).intValue()));
        }
    }

	private void initSignature() {
		try {
			initSigner();
			String[] certList = null;
			try {
				certList = getCertList(getSignaturaParams());
			} catch (Exception ex) {
				missatgeError("msg.error.certlist");
				ex.printStackTrace(); 
				return;
		    }
			if (certList != null) {
				jComboBoxCertificats.setModel(
						new DefaultComboBoxModel(certList));
			} else {
				jComboBoxCertificats.setModel(
						new DefaultComboBoxModel(new String[] {}));
			}
			jComboBoxCertificats.setEnabled(true);
			jButtonSignar.setEnabled(true);
			jPasswordField.setEnabled(true);
		} catch (NecessitaActualitzarException ex) {
			ex.printStackTrace();
			missatgeError("msg.error.upgrade", ex.getLocalizedMessage());
			getAppletContext().showDocument(ex.getUrl());
		}
	}



	private void jButtonSignarActionPerformed(ActionEvent evt) {
		if (confirm("msg.configrm.firmar")) {
			String certName = (String)jComboBoxCertificats.getSelectedItem();
			if (certName != null) {
				try {
					HttpURLConnection conn = (HttpURLConnection)new URL(getSourceUrl()).openConnection();
					conn.connect();
					String filename = getFileNameFromHttpUtlConnection(conn);
					InputStream connInputStream = conn.getInputStream();
					if (connInputStream != null) {
						setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						int nRead;
						byte[] tmp = new byte[1024];
						while ((nRead = connInputStream.read(tmp, 0, tmp.length)) != -1) {
							buffer.write(tmp, 0, nRead);
						}
						buffer.flush();
						if (signarDocument(buffer.toByteArray(), filename, certName)) {
							JSObject win = JSObject.getWindow(this);
							win.eval("location.reload()");
						}
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				} catch (Exception ex) {
					missatgeError("msg.error.download", ex.getLocalizedMessage());
				}
			} else {
				missatgeError("msg.error.nosel");
			}
		}
	}

	private void missatgeError(String missatge) {
		missatgeError(missatge, null);
	}
	private void missatgeError(String missatge, String afegit) {
		String text = missatges.getString(missatge) + ((afegit != null) ? ": " + afegit : "");
		JOptionPane.showMessageDialog(
				jPanel,
				text,
				missatges.getString("msg.titol"),
				JOptionPane.ERROR_MESSAGE);
	}
	private boolean confirm(String missatge) {
		String text = missatges.getString(missatge);
		return JOptionPane.showConfirmDialog(
				jPanel,
				text,
				missatges.getString("msg.titol"),
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	private boolean signarDocument(byte[] inputDocument, String filename, String certName) {
		RespostaSignatura resposta = new RespostaSignatura();
		resposta.setToken(getParameter("token"));
		try {
			Object signatura = sign(
					inputDocument,
					certName,
					new String(jPasswordField.getPassword()),
					getSignaturaParams());
			resposta.setSignatura(signatura);
			resposta.setArxiuNom(filename);
			return enviarDocumentSignat(resposta);
		} catch (ContrasenyaIncorrectaException ex) {
			missatgeError("msg.error.contrasenya");
		} catch (SignaturaException ex) {
			missatgeError("msg.error.firma", ex.getLocalizedMessage());
		}
		return false;
	}

	private boolean enviarDocumentSignat(RespostaSignatura resposta) {
		String TWOHYPHENS = "--";
		String NEWLINE = "\r\n";
		try {
			HttpURLConnection conn = (HttpURLConnection)new URL(getTargetUrl()).openConnection();
			conn.setDoInput(true);          
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setDefaultUseCaches(false);
			String boundary = "------" + Long.toString(System.currentTimeMillis(), 16);
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(TWOHYPHENS + boundary + NEWLINE);
			out.writeBytes("Content-Disposition: form-data; name=\"data\"; filename=\"data.bin\"" + NEWLINE);
			out.writeBytes("Content-Type: application/octet-stream" + NEWLINE + NEWLINE);
			ObjectOutputStream oout = new ObjectOutputStream(conn.getOutputStream());
			oout.writeObject(resposta);
			out.writeBytes(NEWLINE);
			out.writeBytes(TWOHYPHENS + boundary + TWOHYPHENS + NEWLINE);
			out.flush();
			out.close();
			conn.getInputStream().close();
			return true;
		} catch (Exception ex) {
			missatgeError("msg.error.upload", ex.getLocalizedMessage());
			return false;
		}
	}

	private String getSourceUrl() throws Exception {
		return getBaseUrl() +
				"/document/arxiuPerSignar.html?token=" +
				URLEncoder.encode(getParameter("token"), "UTF-8");
	}
	private String getTargetUrl() {
		return getBaseUrl() + "/signatura/signarAmbTokenCaib.html";
	}
	private String getBaseUrl() {
		return getParameter("baseUrl");
	}
	private String getSignaturaParams() {
		String signaturaParams = getParameter("signaturaParams");
		return signaturaParams;
	}

	private String getFileNameFromHttpUtlConnection(HttpURLConnection conn) {
		String filenameToken = "filename=\"";
		String contentType = conn.getContentType();
		String contentDisposition = conn.getHeaderField("Content-Disposition");
		if (contentDisposition != null) {
			int filenameIndex = contentDisposition.indexOf(filenameToken);
			if (filenameIndex != -1)
				return contentDisposition.substring(
						filenameIndex + filenameToken.length(),
						contentDisposition.indexOf("\"", filenameIndex + filenameToken.length()));
		}
		if (contentType.contains("pdf") || contentType.contains("PDF"))
			return "unknown.pdf";
		else
			return "unknown.bin";
	}

	public abstract void initSigner() throws NecessitaActualitzarException;
	public abstract String[] getCertList(String params) throws ObtencioCertificatsException;
	public abstract Object sign(
			byte[] inputDocument,
			String certName,
			String password,
			String params) throws SignaturaException, ContrasenyaIncorrectaException;
	

	private static final long serialVersionUID = 1L;

}
