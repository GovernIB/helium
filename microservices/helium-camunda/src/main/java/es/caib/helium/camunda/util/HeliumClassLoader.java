package es.caib.helium.camunda.util;

import es.caib.helium.camunda.service.DeploymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ClassLoadingException;
import org.camunda.bpm.engine.impl.util.ClassLoaderUtil;
import org.camunda.bpm.engine.impl.util.ReflectUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class HeliumClassLoader extends ClassLoader {

    @Override
    public Class findClass(String name) throws ClassNotFoundException {
        Class<?> clazz = null;
        Throwable throwable = null;
        byte[] b = null;

        try {
            b = loadClassFromLoadableDir(name);
        } catch (Throwable ex) {
            throwable = ex;
        }

        if (b != null && b.length > 0) {
            clazz = defineClass(name, b, 0, b.length);
        }

        if (clazz == null) {
            clazz = loadClassFromContext(name);
        }

        return clazz;
    }

    private byte[] loadClassFromLoadableDir(String className) throws ClassNotFoundException {
        try {
            Path loadablePath = Paths.get(DeploymentServiceImpl.DEPLOYMENT_PATH, DeploymentServiceImpl.LOADABLE_DIR);
            Path path = Paths.get(loadablePath.toString(), className.replace('.', File.separatorChar) + ".class");
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.debug("No s'ha pogut obtenir la classe " + className, e);
            throw new ClassNotFoundException("No s'ha pogut obtenir la classe " + className + "al directori 'loadable'", e);
        }
    }

    private Class<?> loadClassFromContext(String className) {
        Class<?> clazz = null;
        ClassLoader localClassloader;
        Throwable throwable = null;
        try {
            localClassloader = ClassLoaderUtil.getContextClassloader();
            if (localClassloader != null) {
                log.debug("Attempting to load class '{}' with {}: {}", className, "current thread context classloader", localClassloader);
                clazz = Class.forName(className, true, localClassloader);
            }
        } catch (Throwable ex) {
            if (throwable == null) {
                throwable = ex;
            }
        }

        if (clazz == null) {
            try {
                localClassloader = ClassLoaderUtil.getClassloader(ReflectUtil.class);
                log.debug("Attempting to load class '{}' with {}: {}", className, "local classloader", localClassloader);
                clazz = Class.forName(className, true, localClassloader);
            } catch (Throwable ex) {
                if (throwable == null) {
                    throwable = ex;
                }
            }
        }

        if (clazz == null) {
            throw new ClassLoadingException("No s'ha pogut obtenir la classe " + className + "al Custom classloader", throwable);
        } else {
            return clazz;
        }
    }
}
