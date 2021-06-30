package es.caib.helium.base.service;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.caib.helium.base.domain.Exemple;
import es.caib.helium.base.mapper.ExempleMapper;
import es.caib.helium.base.model.ExempleDto;
import es.caib.helium.base.model.PagedList;
import es.caib.helium.base.repository.ExempleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BaseServiceImpl implements BaseService {

//    private static final String CACHE_BASE = "BaseCache";
//    private static final String CACHE_KEY_SEPARATOR = "#";

//    private final ExempleRepository exempleRepository;
//    private final ExempleMapper exempleMapper;

//    // DISTRIBUTED CACHE
//    private final HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(createConfig());
//
//    public Config createConfig() {
//        Config config = new Config();
//        config.addMapConfig(mapConfig());
//        return config;
//    }
//
//    private MapConfig mapConfig() {
//        MapConfig mapConfig = new MapConfig(CACHE_BASE);
//        mapConfig.setTimeToLiveSeconds(360);
//        mapConfig.setMaxIdleSeconds(20);
//        return mapConfig;
//    }
//
//    public Base put(String key, Base base) {
//        IMap<String, Base> map = hazelcastInstance.getMap(CACHE_BASE);
//        return map.putIfAbsent(key, base);
//    }
//
//    public Base get(String key) {
//        IMap<String, Base> map = hazelcastInstance.getMap(CACHE_BASE);
//        return map.get(key);
//    }
//
//    public void evict(String key) {
//        IMap<String, Base> map = hazelcastInstance.getMap(CACHE_BASE);
//        map.evict(key);
//    }

//    @Override
//    @Transactional
//    public ExempleDto createBase(ExempleDto baseDto) {
//
//        log.debug("Creant nou Base (Base=" + baseDto + ")");
////        return exempleMapper.entityToDto(exempleRepository.save(exempleMapper.dtoToEntity(baseDto)));
//        var exemple = exempleMapper.dtoToEntity(baseDto);
//        exemple = new Exemple();
//        exemple.setCodi("foo");
//        exemple.setNom("bar");
//        var saved = exempleRepository.save(exemple);
//        return exempleMapper.entityToDto(saved);
//
//    }
//
//    @Override
//    @Transactional
//    public void updateBase(
//            Long baseId,
//            ExempleDto baseDto) {
//
//        log.debug("Modificant el Base existent (" +
//                "baseId=" + baseId + ", " +
//                "Base =" + baseDto + ")");
//
//        Optional<Exemple> baseOptional = exempleRepository.findById(baseId);
//
//        baseOptional.ifPresentOrElse(
//                Base -> {
//                    Base.setCodi(baseDto.getCodi());
//                    Base.setNom(baseDto.getNom());
//                    exempleRepository.save(Base);
//                }, () -> {
//                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + baseId);
//                }
//        );
//    }
//
//    @Override
//    @Transactional
//    public void delete(Long baseId) {
//
//        log.debug("Esborrant el Base (baseId=" + baseId +  ")");
//        Optional<Exemple> baseOptional = exempleRepository.findById(baseId);
//        baseOptional.ifPresentOrElse(
//                exempleRepository::delete,
//                () -> {
//                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + baseId);
//                }
//        );
//
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public ExempleDto getById(Long baseId) {
//
//        log.debug("Obtenint base per id: " + baseId);
//        Optional<Exemple> baseOptional = exempleRepository.findById(baseId);
//
//        if (baseOptional.isPresent()) {
//            log.debug("Trobat base amb id: " + baseId);
//            return exempleMapper.entityToDto(baseOptional.get());
//        } else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + baseId);
//        }
//
//    }
//
//    @Override
//    public PagedList<ExempleDto> listBases(
//            String filtreRsql,
//            Pageable pageable,
//            Sort sort) {
//
//        Specification<Exemple> spec = null;
////        Specification<Base> spec = BaseSpecifications.basesList(...);
//
//        return ServiceHelper.getDtoPage(
//                exempleRepository,
//                spec,
//                filtreRsql,
//                pageable,
//                sort,
//                ExempleDto.class,
//                exempleMapper);
//
//    }

}
