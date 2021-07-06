/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;

import java.io.ByteArrayInputStream;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.SignedData;
import org.bouncycastle.asn1.x509.X509CertificateStructure;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;

import es.caib.helium.logic.util.GlobalProperties;


/**
 * Implementació de test del plugin de signatura. Aquest plugin
 * només extreu les dades del certificat sense comprovar la seva
 * validesa. Només té utilitat en entorns de proves.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SignaturaPluginTest implements SignaturaPlugin {

	public RespostaValidacioSignatura verificarSignatura(
			byte[] document,
			byte[] signatura,
			boolean obtenirDadesCertificat) throws SignaturaPluginException {
		RespostaValidacioSignatura resposta = new RespostaValidacioSignatura();
		resposta.setEstat(RespostaValidacioSignatura.ESTAT_OK);
		if (obtenirDadesCertificat) {
			try {
				if (isSignaturaFileAttached()) {
					resposta.setDadesCertificat(
							obtenirDadesCertificatPdf(signatura));
				} else {
					resposta.setDadesCertificat(
							obtenirDadesCertificatNoPdf(signatura));
				}
			} catch (Exception ex) {
				logger.error("Error en la validació de la signatura", ex);
			}
		}
		return resposta;
	}



	@SuppressWarnings("unchecked")
	private List<DadesCertificat> obtenirDadesCertificatPdf(
			byte[] signatura) throws Exception {
		PdfReader reader = new PdfReader(new ByteArrayInputStream(signatura));
		AcroFields af = reader.getAcroFields();
		ArrayList<String> names = af.getSignatureNames();
		for (String name: names) {
			PdfPKCS7 pk = af.verifySignature(name);
			Certificate pkc[] = pk.getCertificates();
			List<DadesCertificat> dadesCertificats = new ArrayList<DadesCertificat>();
			for (Certificate cert: pkc) {
				if (cert instanceof X509Certificate) {
					int basicConstraints = ((X509Certificate)cert).getBasicConstraints();
					// Només afegeix els certificats que no son de CA
					if (basicConstraints == -1)
						dadesCertificats.add(getDadesCertificat((X509Certificate)cert));
				}
			}
			return dadesCertificats;
		}
		return null;
	}
	@SuppressWarnings({ "unchecked", "resource" })
	private List<DadesCertificat> obtenirDadesCertificatNoPdf(
			byte[] signatura) throws Exception {
		/*
		X509Certificate[] certificats = null;
		byte[] pkcs7Bytes = signatura;
		ASN1InputStream asn1is = new ASN1InputStream(new ByteArrayInputStream(pkcs7Bytes));
		ContentInfo pkcs7Info = ContentInfo.getInstance(asn1is.readObject());
		SignedData signedData = SignedData.getInstance(pkcs7Info.getContent());
		ASN1Set signerInfos = signedData.getSignerInfos();
		int numSignatures = signerInfos.size();
		if (numSignatures > 0) {
			afegirProveidorBouncyCastle();
			CMSSignedData cmsSignedData = new CMSSignedData(pkcs7Bytes);
			SignerInformationStore signers = cmsSignedData.getSignerInfos();
			CertStore certStore = cmsSignedData.getCertificatesAndCRLs("Collection", "BC");
			List<X509Certificate> certs = new ArrayList<X509Certificate>();
			for (SignerInformation signer: (Collection<SignerInformation>)signers.getSigners()) {
				for (Certificate cert: certStore.getCertificates(signer.getSID())) {
					if (cert instanceof X509Certificate)
						certs.add((X509Certificate)cert);
				}
			}
			certificats = certs.toArray(new X509Certificate[certs.size()]);
			if (certificats.length != 1)
				throw new SignaturaPluginException("Aquesta signatura conté més d'un certificat");
			//resposta.setInfoCertificat(getInfoCertificat(certificats[0]));
			List<DadesCertificat> dadesCertificats = new ArrayList<DadesCertificat>();
			dadesCertificats.add(getDadesCertificat(certificats[0]));
			return dadesCertificats;
		}
		*/
		return null;
	}

	private void afegirProveidorBouncyCastle() {
		Provider[] proveidors = Security.getProviders();
		boolean existeix = false;
        for (int i = 0; i < proveidors.length; i++) {
        	if (proveidors[i].getName().equalsIgnoreCase("BC")) {
        		existeix = true;
        		break;
        	}
        }
		if (!existeix)
			Security.addProvider(new BouncyCastleProvider());
    }
	@SuppressWarnings({ "rawtypes", "resource" })
	private DadesCertificat getDadesCertificat(X509Certificate cert) throws Exception {
//		ASN1InputStream asn1is = new ASN1InputStream(cert.getEncoded());
//		org.bouncycastle.asn1.DERObject obj = asn1is.readObject();
//		/*byte[] value = cert.getExtensionValue(X509Extensions.BasicConstraints.toString());
//		BasicConstraints basicConstraints = new BasicConstraints(cert.getBasicConstraints());
//		if (basicConstraints.isCA())
//			return null;*/
//		DadesCertificat resposta = new DadesCertificat();
//		X509CertificateStructure certificate = new X509CertificateStructure((ASN1Sequence)obj);
//		X509Name name = certificate.getSubject();
//		Vector oids = name.getOIDs();
//		Vector values = name.getValues();
//		for (int i = 0; i < oids.size(); i++) {
//			if (oids.get(i).equals(X509Name.CN)) {
//				processName(values.get(i).toString(), resposta);
//			} else if (oids.get(i).equals(X509Name.SURNAME)) {
//				resposta.setApellidosResponsable(values.get(i).toString());
//			} else if (oids.get(i).equals(X509Name.GIVENNAME)) {
//				resposta.setNombreResponsable(values.get(i).toString());
//			} else if (oids.get(i).equals(X509Name.SN)) {
//				resposta.setNifCif(values.get(i).toString());
//				resposta.setNifResponsable(values.get(i).toString());
//			} else if (oids.get(i).equals(OID_NIF_RESPONSABLE)) {
//				resposta.setNifResponsable(values.get(i).toString());
//			} else if (oids.get(i).equals(X509Name.EmailAddress)) {
//				resposta.setEmail(values.get(i).toString());
//			} else if (oids.get(i).equals(X509Name.C)) {
//				//resposta.setPais(values.get(i).toString());
//			} else if (oids.get(i).equals(X509Name.O)) {
//				resposta.setRazonSocial(values.get(i).toString());
//			} else if (oids.get(i).equals(X509Name.OU)) {
//				//resposta.setDepartament(values.get(i).toString());
//			} else if (oids.get(i).equals(X509Name.T)) {
//				//resposta.setCarrec(values.get(i).toString());
//		    }
//		}
//		return resposta;
		return null;
	}
	private void processName(String cn, DadesCertificat dadesCertificat) {
		if (cn != null && cn.startsWith("NOMBRE ")) {
			int i = cn.indexOf(" - ");
			if (i > 0 && cn.substring(i).startsWith(" - NIF ")) {
				dadesCertificat.setNifResponsable(cn.substring(i + 7));
				dadesCertificat.setNombreCompletoResponsable(cn.substring(7, i));
			}
		} else if (cn != null && cn.startsWith("ENTIDAD ")) {
			int i = cn.indexOf(" - ");
			if (i > 0 && cn.substring(i).startsWith(" - CIF ")) {
				int j = cn.indexOf(" - ", i + 7);
				int k = cn.indexOf(" - NIF ", i + 7);
				if (j > 0 && k > 0) {
					dadesCertificat.setNifCif(cn.substring(i + 8, j));
					dadesCertificat.setNombreCompletoResponsable(cn.substring(7, i));
					dadesCertificat.setNifResponsable(cn.substring(k + 7));
				}
			}
		} else {
			dadesCertificat.setNombreCompletoResponsable(cn);
		}
	}

	private boolean isSignaturaFileAttached() {
		return "true".equalsIgnoreCase((String)GlobalProperties.getInstance().get("app.signatura.plugin.file.attached"));
	}

	private static final DERObjectIdentifier OID_NIF_RESPONSABLE = new DERObjectIdentifier("1.3.6.1.4.1.18838.1.1");

	private static final Log logger = LogFactory.getLog(SignaturaPluginTest.class);

}
