package shutdown.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public class ShutDownAnnotated {

    private final String[] conditionOnProfiles;
    private final Class<?>[] conditionOnBean;
    private final Class<?>[] conditionOnMissingBean;
    private final boolean force;
    private final String message;
    private final HttpStatus status;
    private final String contentType;

    public ShutDownAnnotated(String[] conditionOnProfiles, Class<?>[] conditionOnBean, Class<?>[] conditionOnMissingBean, boolean force, String message, HttpStatus status, String contentType) {
        if (conditionOnProfiles.length != 0 && conditionOnBean.length != 0 && conditionOnMissingBean.length != 0) {
            throw new ShutDownException("Only one of 'conditionOnXXX' can be specified.");
        }
        this.conditionOnProfiles = conditionOnProfiles;
        this.conditionOnBean = conditionOnBean;
        this.conditionOnMissingBean = conditionOnMissingBean;
        this.force = force;
        this.message = message;
        this.status = status;
        this.contentType = contentType;
    }

    public static ShutDownAnnotated of(Class<?> controllerType) {
        var shutDownInfo = controllerType.getAnnotation(ShutDown.class);
        return new ShutDownAnnotated(
            shutDownInfo.conditionOnActiveProfile(),
            shutDownInfo.conditionOnBean(),
            shutDownInfo.conditionOnMissingBean(),
            shutDownInfo.force(),
            shutDownInfo.message(),
            shutDownInfo.status(),
            shutDownInfo.contentType()
        );
    }

    public boolean isCondition(String[] activeProfiles, BeanFactory beanFactory) {
        if (force) {
            return true;
        }
        if(conditionOnProfiles.length != 0) {
            return Arrays.equals(conditionOnProfiles, activeProfiles);
        }
        if (conditionOnBean.length != 0) {
            return Arrays.stream(conditionOnBean).allMatch(it -> hasBeanInFactory(beanFactory, it));
        }
        if (conditionOnMissingBean.length != 0) {
            return Arrays.stream(conditionOnMissingBean).noneMatch(it -> hasBeanInFactory(beanFactory, it));
        }
        throw new ShutDownException("You must select either conditionOnBean or conditionOnMissingBean. Or set force to true");
    }

    private static boolean hasBeanInFactory(BeanFactory beanFactory, Class<?> beanType) {
        try {
            beanFactory.getBean(beanType);
            return true;
        } catch (BeansException e) {
            return false;
        }
    }

    public String message() {
        return message;
    }

    public HttpStatus status() {
        return status;
    }

    public String contentType() {
        return contentType;
    }
}
