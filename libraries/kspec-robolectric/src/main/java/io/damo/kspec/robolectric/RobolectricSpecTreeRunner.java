package io.damo.kspec.robolectric;

import io.damo.kspec.*;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.robolectric.DefaultTestLifecycle;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.TestLifecycle;
import org.robolectric.annotation.Config;
import org.robolectric.internal.*;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.ResourceLoader;
import org.robolectric.util.ReflectionHelpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RobolectricSpecTreeRunner<T extends SpecTree> extends Runner {

    /**
     * This is a Hack to make RobolectricTestRunner happy. We do not run test Methods.
     * This is made open in order to let the runner inspect annotations
     * on the given method or its class.
     */
    final Method testMethod;

    final RobolectricHelper helper;
    final Config config;
    final AndroidManifest appManifest;
    final InstrumentingClassLoaderFactory classLoaderFactory;
    final SdkEnvironment sdkEnv;

    private final Object actualRunner;
    private final Method getDescriptionMethod;
    private final Method runMethod;
    private final ClassLoader roboClassLoader;

    public RobolectricSpecTreeRunner(Class<T> specTreeClass) throws InitializationError, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        testMethod = Spec.class.getMethods()[0];

        helper = new RobolectricHelper(specTreeClass);
        config = helper.getConfig(testMethod);

        appManifest = helper.getAppManifest(config);
        classLoaderFactory = new InstrumentingClassLoaderFactory(
            helper.createClassLoaderConfig(config),
            helper.getJarResolver()
        );

        sdkEnv = classLoaderFactory.getSdkEnvironment(new SdkConfig(
            helper.pickSdkVersion(config, appManifest)
        ));
        roboClassLoader = sdkEnv.getRobolectricClassLoader();

        Class<?> actualRunnerClass = sdkEnv.bootstrappedClass(ActualRunner.class);
        Constructor<?> actualConstructor = actualRunnerClass.getConstructors()[0];
        actualRunner = actualConstructor.newInstance(specTreeClass);
        getDescriptionMethod = actualRunnerClass.getMethod("getDescription");
        runMethod = actualRunnerClass.getMethod("run");
    }

    private TestLifecycle testLifecycle;
    private ParallelUniverseInterface parallelUniverse;

    public class Before {

        public void run() {
            helper.configureShadows(sdkEnv, config);

            Thread.currentThread().setContextClassLoader(roboClassLoader);
            parallelUniverse = buildParallelUniverse();
            testLifecycle = buildTestLifeCycle();
            parallelUniverse.resetStaticState(config);
            parallelUniverse.setSdkConfig(sdkEnv.getSdkConfig());

            int sdkVersion = helper.pickSdkVersion(config, appManifest);
//            ReflectionHelpers.setStaticField(sdkEnv.bootstrappedClass(Build.VERSION.class), "SDK_INT", sdkVersion);

            ResourceLoader systemResourceLoader = sdkEnv.getSystemResourceLoader(helper.getJarResolver());
            setUpApplicationState(bootstrappedMethod, parallelUniverse, systemResourceLoader, appManifest, config);
            testLifecycle.beforeTest(bootstrappedMethod);
        }
    }

    public class After {
        public void run() {

        }
    }

    private ParallelUniverseInterface buildParallelUniverse() {
        try {
            Class roboParallelUniverseClass = roboClassLoader.loadClass(ParallelUniverse.class.getName());
            Class roboInterfaceClass = roboParallelUniverseClass.asSubclass(ParallelUniverseInterface.class);
            Constructor constructor = roboInterfaceClass.getConstructor(new Class[]{RobolectricTestRunner.class});

            return (ParallelUniverseInterface) constructor.newInstance(new Object[]{helper});

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private TestLifecycle buildTestLifeCycle() {
        try {
            Class<?> roboTestLifeCycleClass = roboClassLoader.loadClass(DefaultTestLifecycle.class.getName());
            return (TestLifecycle) roboTestLifeCycleClass.newInstance();

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Description getDescription() {
        try {
            return (Description) getDescriptionMethod.invoke(actualRunner);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            runMethod.invoke(actualRunner);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static class ActualRunner<T extends SpecTree> extends SpecTreeRunner<T> {

        public ActualRunner(@NotNull Class<T> specTreeClass) throws InitializationError {
            super(specTreeClass);
        }

        @NotNull
        @Override
        public SpecBranchRunner<T> buildRootBranchRunner(@NotNull SpecBranch root, boolean runFocusedOnly) {
            return new RobolectricSpecBranchRunner<>(this, getSpecTreeClass(), root, runFocusedOnly);
        }
    }
}
