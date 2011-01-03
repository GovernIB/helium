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
import org.bouncycastle.util.encoders.Base64;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;


/**
 * Implementació de test del plugin de signatura. Aquest plugin
 * només extreu les dades del certificat sense comprovar la seva
 * validesa. Només té utilitat en entorns de proves.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class SignaturaPluginTest implements SignaturaPlugin {

	@SuppressWarnings("unchecked")
	public InfoSignatura verificarSignatura(
			byte[] document,
			byte[] signatura) throws SignaturaPluginException {
		try {
			InfoSignatura resposta = new InfoSignatura(signatura);
			X509Certificate[] certificats = null;
			byte[] pkcs7Bytes = Base64.decode(signatura);
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
				resposta.setInfoCertificat(getInfoCertificat(certificats[0]));
				resposta.setValida(true);
			}
			return resposta;
		} catch (Exception ex) {
			throw new SignaturaPluginException("Error en la validació de la signatura", ex);
		}
	}

	@SuppressWarnings("unchecked")
	public List<InfoSignatura> verificarSignatura(
			byte[] documentsignat) throws SignaturaPluginException {
		try {
			List<InfoSignatura> resposta = new ArrayList<InfoSignatura>();
			PdfReader reader = new PdfReader(new ByteArrayInputStream(Base64.decode(documentsignat)));
			AcroFields af = reader.getAcroFields();
			ArrayList<String> names = af.getSignatureNames();
			for (String name: names) {
				/*System.out.println("Signature name: " + name);
				System.out.println("Signature covers whole document: " + af.signatureCoversWholeDocument(name));
				System.out.println("Document revision: " + af.getRevision(name) + " of " + af.getTotalRevisions());*/
				PdfPKCS7 pk = af.verifySignature(name);
				Certificate pkc[] = pk.getCertificates();
				for (Certificate cert: pkc) {
					if (cert instanceof X509Certificate) {
						InfoCertificat ic = getInfoCertificat((X509Certificate)cert);
						if (ic != null) {
							InfoSignatura is = new InfoSignatura(documentsignat);
							is.setInfoCertificat(ic);
							is.setValida(true);
							resposta.add(is);
						}
					}
				}
			}
			return resposta;
		} catch (Exception ex) {
			throw new SignaturaPluginException("Error en la validació de l'arxiu", ex);
		}
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
	@SuppressWarnings("unchecked")
	private InfoCertificat getInfoCertificat(X509Certificate cert) throws Exception {
		ASN1InputStream asn1is = new ASN1InputStream(cert.getEncoded());
		org.bouncycastle.asn1.DERObject obj = asn1is.readObject();
		/*byte[] value = cert.getExtensionValue(X509Extensions.BasicConstraints.toString());
		BasicConstraints basicConstraints = new BasicConstraints(cert.getBasicConstraints());
		if (basicConstraints.isCA())
			return null;*/
		InfoCertificat resposta = new InfoCertificat();
		X509CertificateStructure certificate = new X509CertificateStructure((ASN1Sequence)obj);
		X509Name name = certificate.getSubject();
		resposta.setPersonaFisica(true);
		Vector oids = name.getOIDs();
		Vector values = name.getValues();
		for (int i = 0; i < oids.size(); i++) {
			if (oids.get(i).equals(X509Name.CN)) {
				processName(values.get(i).toString(), resposta);
			} else if (oids.get(i).equals(X509Name.SURNAME)) {
				resposta.setSurname(values.get(i).toString());
			} else if (oids.get(i).equals(X509Name.GIVENNAME)) {
				resposta.setGivenName(values.get(i).toString());
			} else if (oids.get(i).equals(X509Name.SN)) {
				resposta.setNif(values.get(i).toString());
			} else if (oids.get(i).equals(OID_NIF_RESPONSABLE)) {
				resposta.setNifResponsable(values.get(i).toString());
				resposta.setPersonaFisica(false);
			} else if (oids.get(i).equals(X509Name.EmailAddress)) {
				resposta.setEmail(values.get(i).toString());
			} else if (oids.get(i).equals(X509Name.C)) {
				resposta.setPais(values.get(i).toString());
			} else if (oids.get(i).equals(X509Name.O)) {
				resposta.setOrganitzacio(values.get(i).toString());
			} else if (oids.get(i).equals(X509Name.OU)) {
				resposta.setDepartament(values.get(i).toString());
			} else if (oids.get(i).equals(X509Name.T)) {
				resposta.setCarrec(values.get(i).toString());
		    }
		}
		return resposta;
	}
	private void processName(String cn, InfoCertificat infoCertificat) {
		if (cn != null && cn.startsWith("NOMBRE ")) {
			int i = cn.indexOf(" - ");
			if (i > 0 && cn.substring(i).startsWith(" - NIF ")) {
				infoCertificat.setNif(cn.substring(i + 7));
				infoCertificat.setFullName(cn.substring(7, i));
				infoCertificat.setPersonaFisica(true);
			}
		} else if (cn != null && cn.startsWith("ENTIDAD ")) {
			infoCertificat.setPersonaFisica(false);
			int i = cn.indexOf(" - ");
			if (i > 0 && cn.substring(i).startsWith(" - CIF ")) {
				int j = cn.indexOf(" - ", i + 7);
				int k = cn.indexOf(" - NIF ", i + 7);
				if (j > 0 && k > 0) {
					infoCertificat.setNif(cn.substring(i + 8, j));
					infoCertificat.setFullName(cn.substring(7, i));
					infoCertificat.setNifResponsable(cn.substring(k + 7));
				}
			}
		} else {
			infoCertificat.setFullName(cn);
		}
	}

	private static final DERObjectIdentifier OID_NIF_RESPONSABLE = new DERObjectIdentifier("1.3.6.1.4.1.18838.1.1");

}
