package es.caib.helium.base.service;

import es.caib.helium.base.domain.Base;
import es.caib.helium.base.model.BaseDto;
import es.caib.helium.base.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BaseServiceImpl implements BaseService {

//    private static final String CACHE_BASE = "BaseCache";
//    private static final String CACHE_KEY_SEPARATOR = "#";

    private final es.caib.helium.base.repository.BaseRepository baseRepository;
    private final es.caib.helium.base.mapper.BaseMapper baseMapper;

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

    @Override
    @Transactional
    public BaseDto createBase(BaseDto baseDto) {

        log.debug("Creant nou Base (Base=" + baseDto + ")");
        return baseMapper.entityToDto(baseRepository.save(baseMapper.dtoToEntity(baseDto)));

    }

    @Override
    @Transactional
    public void updateBase(
            Long baseId,
            BaseDto baseDto) {

        log.debug("Modificant el Base existent (" +
                "baseId=" + baseId + ", " +
                "Base =" + baseDto + ")");

        Optional<Base> baseOptional = baseRepository.findById(baseId);

        baseOptional.ifPresentOrElse(
                Base -> {
                    Base.setCodi(baseDto.getCodi());
                    Base.setNom(baseDto.getNom());
                    baseRepository.save(Base);
                }, () -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + baseId);
                }
        );
    }

    @Override
    @Transactional
    public void delete(Long baseId) {

        log.debug("Esborrant el Base (baseId=" + baseId +  ")");
        Optional<Base> baseOptional = baseRepository.findById(baseId);
        baseOptional.ifPresentOrElse(
                baseRepository::delete,
                () -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + baseId);
                }
        );

    }

    @Override
    @Transactional(readOnly = true)
    public BaseDto getById(Long baseId) {

        log.debug("Obtenint base per id: " + baseId);
        Optional<Base> baseOptional = baseRepository.findById(baseId);

        if (baseOptional.isPresent()) {
            log.debug("Trobat base amb id: " + baseId);
            return baseMapper.entityToDto(baseOptional.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Id: " + baseId);
        }

    }

    @Override
    public PagedList<BaseDto> listBases(
            String filtreRsql,
            Pageable pageable,
            Sort sort) {

        Specification<Base> spec = null;
//        Specification<Base> spec = BaseSpecifications.basesList(...);

        return ServiceHelper.getDtoPage(
                baseRepository,
                spec,
                filtreRsql,
                pageable,
                sort,
                BaseDto.class,
                baseMapper);

    }

}
