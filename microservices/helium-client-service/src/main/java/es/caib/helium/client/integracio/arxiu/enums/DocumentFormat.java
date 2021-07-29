package es.caib.helium.client.integracio.arxiu.enums;

public enum DocumentFormat {

    GML("GML"),
    WFS("WFS"),
    WMS("WMS"),
    GZIP("GZIP"),
    ZIP("ZIP"),
    AVI("AVI"),
    MP4A("MPEG-4 MP4 media"),
    CSV("Comma Separated Values"),
    HTML("HTML"),
    CSS("CSS"),
    JPEG("JPEG"),
    MHTML("MHTML"),
    OASIS12("ISO/IEC 26300:2006 OASIS 1.2"),
    SOXML("Strict Open XML"),
    PDF("PDF"),
    PDFA("PDF/A"),
    PNG("PNG"),
    RTF("RTF"),
    SVG("SVG"),
    TIFF("TIFF"),
    TXT("TXT"),
    XHTML("XHTML"),
    MP3("MP3. MPEG-1 Audio Layer 3"),
    OGG("OGG-Vorbis"),
    MP4V("MPEG-4 MP4 vídeo"),
    WEBM("WebM"),
    CSIG("csig"),
    XSIG("xsig"),
    XML("xml");

    private String str;

    private DocumentFormat(String str) {
        this.str = str;
    }

    public static DocumentFormat toEnum(String str) {
        if (str != null) {
            DocumentFormat[] var4;
            int var3 = (var4 = values()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
                DocumentFormat valor = var4[var2];
                if (valor.toString().equals(str)) {
                    return valor;
                }
            }
        }

        return null;
    }

    public String toString() {
        return this.str;
    }
}
