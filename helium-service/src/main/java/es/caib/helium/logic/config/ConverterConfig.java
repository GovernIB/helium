package es.caib.helium.logic.config;

import es.caib.helium.logic.util.GlobalPropertiesImpl;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.core.util.AssertUtils;
import org.jodconverter.remote.RemoteConverter;
import org.jodconverter.remote.office.RemoteOfficeManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterConfig {

//    @Value("${es.caib.helium.conversio.openoffice.host}")
//    private String url;
//    @Value("${es.caib.helium.conversio.connection.timeout:30000}")
//    private long connectTimeout;
//    @Value("${es.caib.helium.conversio.socket.timeout:60000}")
//    private long socketTimeout;
//    @Value("${es.caib.helium.conversio.pool.size:1}")
//    private int poolSize = 1;
//    @Value("${es.caib.helium.conversio.openoffice.working.directory}")
//    private String workingDir;
//    @Value("${es.caib.helium.conversio.queue.timeout:30000}")
//    private long taskQueueTimeout = 30000L;
//    @Value("${es.caib.helium.conversio.execution.timeout:120000}")
//    private long taskExecutionTimeout = 120000L;


    private OfficeManager createOfficeManager() {
        AssertUtils.notNull(getOfficeUrl(), "urlConnection is required");
        RemoteOfficeManager.Builder builder = RemoteOfficeManager.builder()
                                .urlConnection(getOfficeUrl())
                                .connectTimeout(getConnectionTimeout())
                                .socketTimeout(getSocketTimeout())
                                .poolSize(getPoolSize())
//                                .workingDir(getWorkingDir())
                                .taskQueueTimeout(getQueueTimeout())
                                .taskExecutionTimeout(getExecutionTimeout());
        if (getWorkingDir() != null && !getWorkingDir().isBlank()) {
            builder.workingDir(getWorkingDir());
        }
//        if (this.properties.getSsl() != null) {
//            builder.sslConfig(this.properties.getSsl().sslConfig());
//        }

        return builder.build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean(name = {"remoteOfficeManager"})
    OfficeManager remoteOfficeManager() {
        return this.createOfficeManager();
    }

    @Bean
    @ConditionalOnMissingBean(name = {"remoteDocumentConverter"})
    @ConditionalOnBean(name = {"remoteOfficeManager"})
    DocumentConverter remoteDocumentConverter(OfficeManager remoteOfficeManager) {
        return RemoteConverter.make(remoteOfficeManager);
    }

    private String getOfficeUrl() {
        return GlobalPropertiesImpl.getInstance().getProperty("es.caib.helium.conversio.openoffice.host");
    }
    private Long getConnectionTimeout() {
        String timeout = GlobalPropertiesImpl.getInstance().getProperty("es.caib.helium.conversio.connection.timeout");
        try {
            return Long.valueOf(timeout);
        } catch (Exception e) {
            return 30000L;
        }
    }
    private Long getSocketTimeout() {
        String timeout = GlobalPropertiesImpl.getInstance().getProperty("es.caib.helium.conversio.socket.timeout");
        try {
            return Long.valueOf(timeout);
        } catch (Exception e) {
            return 60000L;
        }
    }
    private int getPoolSize() {
        String poolSize = GlobalPropertiesImpl.getInstance().getProperty("es.caib.helium.conversio.pool.size");
        try {
            return Integer.valueOf(poolSize);
        } catch (Exception e) {
            return 1;
        }
    }
    private String getWorkingDir() {
        return GlobalPropertiesImpl.getInstance().getProperty("es.caib.helium.conversio.openoffice.working.directory");
    }
    private Long getQueueTimeout() {
        String timeout = GlobalPropertiesImpl.getInstance().getProperty("es.caib.helium.conversio.queue.timeout");
        try {
            return Long.valueOf(timeout);
        } catch (Exception e) {
            return 30000L;
        }
    }
    private Long getExecutionTimeout() {
        String timeout = GlobalPropertiesImpl.getInstance().getProperty("es.caib.helium.conversio.execution.timeout");
        try {
            return Long.valueOf(timeout);
        } catch (Exception e) {
            return 120000L;
        }
    }

}
