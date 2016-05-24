package io.damo.kspec.robolectric;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.SdkEnvironment;
import org.robolectric.internal.dependency.DependencyResolver;
import org.robolectric.manifest.AndroidManifest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class RobolectricHelper extends RobolectricGradleTestRunner {

    public RobolectricHelper(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        // do not validate, we are not really using this as a runner.
    }

    private static class MethodAndFrameworkMethod {
        final Method method;
        final FrameworkMethod frameworkMethod;

        private MethodAndFrameworkMethod(Method method, FrameworkMethod frameworkMethod) {
            this.method = method;
            this.frameworkMethod = frameworkMethod;
        }
    }

    private final List<MethodAndFrameworkMethod> methods = new ArrayList<>();


    @Override
    public AndroidManifest getAppManifest(Config config) {
        return super.getAppManifest(config);
    }

    @Override
    public DependencyResolver getJarResolver() {
        return super.getJarResolver();
    }

    @Override
    public int pickSdkVersion(Config config, AndroidManifest manifest) {
        return super.pickSdkVersion(config, manifest);
    }

    @Override
    public void configureShadows(SdkEnvironment sdkEnvironment, Config config) {
        super.configureShadows(sdkEnvironment, config);
    }

    @Override
    public Statement methodBlock(FrameworkMethod method, Config config, AndroidManifest appManifest, SdkEnvironment sdkEnvironment) {
        return super.methodBlock(method, config, appManifest, sdkEnvironment);
    }

    @Override
    protected Method getBootstrappedMethod(Class bootstrappedTestClass, FrameworkMethod frameworkMethod) throws NoSuchMethodException {
        //noinspection unchecked
        Method method = bootstrappedTestClass.getMethod("getRoot");
        methods.add(new MethodAndFrameworkMethod(method, frameworkMethod));
        return method;
    }

    @Override
    protected FrameworkMethod getBootstrappedFrameworkMethod(Method bootstrappedMethod) {
        for (MethodAndFrameworkMethod method : methods) {
            if (method.method == bootstrappedMethod) {
                return method.frameworkMethod;
            }

        }
        throw new RuntimeException("Could not create framework method for given method $bootstrappedMethod.");
    }

    @Override
    protected HelperTestRunner getHelperTestRunner(Class bootstrappedTestClass) {
        try {
            return new HelperTestRunner(bootstrappedTestClass) {

                @Override
                protected void collectInitializationErrors(List<Throwable> errors) {
                    // do not validate
                }

                @Override
                public Statement methodBlock(FrameworkMethod method) {
                    final SpecLeafRunnerFrameworkMethod specLeafMethod = (SpecLeafRunnerFrameworkMethod) method;

                    return new Statement() {
                        @Override
                        public void evaluate() throws Throwable {
                            RunNotifier notifier = new RunNotifier();
                            specLeafMethod.runner.run(notifier);
                        }
                    };
                }
            };
        } catch (InitializationError initializationError) {
            throw new RuntimeException(initializationError);
        }
    }
}
