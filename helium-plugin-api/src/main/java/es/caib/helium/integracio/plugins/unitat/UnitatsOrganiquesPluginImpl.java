package es.caib.helium.integracio.plugins.unitat;

import es.caib.helium.client.integracio.unitat.UnitatClientServiceImpl;
import es.caib.helium.integracio.plugins.SistemaExternException;

import java.util.List;

public class UnitatsOrganiquesPluginImpl implements UnitatsOrganiquesPlugin {

    private UnitatClientServiceImpl unitatClientService;

    @Override
    public List<UnitatOrganica> findAmbPare(String pareCodi) throws SistemaExternException {
        return null;
    }

    @Override
    public UnitatOrganica findAmbCodi(String codi) throws SistemaExternException {
        return null;
    }

    @Override
    public List<UnitatOrganica> cercaUnitats(String codi, String denominacio, Long nivellAdministracio, Long comunitatAutonoma, Boolean ambOficines, Boolean esUnitatArrel, Long provincia, String municipi) throws SistemaExternException {
        return null;
    }
}
