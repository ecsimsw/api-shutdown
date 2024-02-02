package shutdown.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Predicate;

public class ShutDownConditions {

    private final String[] conditionOnProfiles;
    private final String[] conditionOnProperties;
    private final Class<?>[] conditionOnBean;
    private final Class<?>[] conditionOnMissingBean;
    private final boolean force;

    public ShutDownConditions(String[] conditionOnProfiles, String[] conditionOnProperties, Class<?>[] conditionOnBean, Class<?>[] conditionOnMissingBean, boolean force) {
        if (conditionOnProfiles.length != 0 && conditionOnProperties.length != 0 && conditionOnBean.length != 0 && conditionOnMissingBean.length != 0) {
            throw new ShutDownException("Only one of 'conditionOnXXX' can be specified.");
        }
        this.conditionOnProfiles = conditionOnProfiles;
        this.conditionOnProperties = conditionOnProperties;
        this.conditionOnBean = conditionOnBean;
        this.conditionOnMissingBean = conditionOnMissingBean;
        this.force = force;
    }

    public static ShutDownConditions of(ShutDown shutDownInfo) {
        return new ShutDownConditions(
            shutDownInfo.conditionOnActiveProfile(),
            shutDownInfo.conditionOnProperties(),
            shutDownInfo.conditionOnBean(),
            shutDownInfo.conditionOnMissingBean(),
            shutDownInfo.force()
        );
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutDownConditions.class);

    public boolean isCondition(Predicate<String> hasProfile, Predicate<String> hasProperty, Predicate<Class<?>> hasBean) {
        Arrays.stream(conditionOnMissingBean)
            .forEach(it -> LOGGER.info(it.getName()));

        if (force) {
            return true;
        }
        if (conditionOnProfiles.length != 0) {
            return Arrays.stream(conditionOnProfiles).allMatch(hasProfile);
        }
        if (conditionOnProperties.length != 0) {
            return Arrays.stream(conditionOnProperties).allMatch(hasProperty);
        }
        if (conditionOnBean.length != 0) {
            return Arrays.stream(conditionOnBean).allMatch(hasBean);
        }
        if (conditionOnMissingBean.length != 0) {
            return true;
        }
        throw new ShutDownException("You must select either conditionOnBean or conditionOnMissingBean. Or set force to true");
    }
}
