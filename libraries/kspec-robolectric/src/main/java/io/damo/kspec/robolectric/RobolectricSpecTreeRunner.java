package io.damo.kspec.robolectric;

import io.damo.kspec.*;
import org.jetbrains.annotations.NotNull;
import org.junit.runners.model.InitializationError;
import org.robolectric.annotation.Config;
import org.robolectric.internal.InstrumentingClassLoaderFactory;
import org.robolectric.internal.SdkConfig;
import org.robolectric.internal.SdkEnvironment;
import org.robolectric.manifest.AndroidManifest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;

public class RobolectricSpecTreeRunner<T extends SpecTree> extends SpecTreeRunner<T> {

    /**
     * This is a Hack to make RobolectricTestRunner happy. We do not run test Methods.
     * This is made open in order to let the runner inspect annotations
     * on the given method or its class.
     */
    final Method testMethod;

    final RobolectricHelper helper;
    final Config config;
    final AndroidManifest appManifest;
    final InstrumentingClassLoaderFactory instrumentingClassLoaderFactory;
    final SdkEnvironment sdkEnv;

    public RobolectricSpecTreeRunner(@NotNull Class<T> specTreeClass) throws InitializationError {
        super(specTreeClass);

        testMethod = Spec.class.getMethods()[0];

        helper = new RobolectricHelper(specTreeClass);
        config = helper.getConfig(testMethod);

        appManifest = helper.getAppManifest(config);
        instrumentingClassLoaderFactory = new InstrumentingClassLoaderFactory(
            helper.createClassLoaderConfig(config),
            helper.getJarResolver()
        );

        sdkEnv = instrumentingClassLoaderFactory.getSdkEnvironment(new SdkConfig(
            helper.pickSdkVersion(config, appManifest)
        ));
    }

    @NotNull
    @Override
    public List<SpecTreeNodeRunner> buildChildren() {
        try {
            Class<?> roboSpecTreeClass = sdkEnv.bootstrappedClass(getSpecTreeClass());
            Class<?> roboSpecBranchClass = sdkEnv.bootstrappedClass(SpecBranch.class);
            Class<?> roboSpecBranchRunnerClass = sdkEnv.bootstrappedClass(RobolectricSpecBranchRunner.class);
//            Class<?> roboSpecTreeRunnerClass = sdkEnv.bootstrappedClass(RobolectricSpecTreeRunner.class);
//            Class<?> roboBooleanClass = sdkEnv.bootstrappedClass(Boolean.class);

            Constructor<?> roboSpecBranchRunnerClassConstructor = roboSpecBranchRunnerClass.getConstructors()[0];

            Method readSpecBodyMethod = roboSpecTreeClass.getMethod("readSpecBody");
            Method getRootMethod = roboSpecTreeClass.getMethod("getRoot");
            Method isFocusedMethod = roboSpecBranchClass.getMethod("isFocused");

            Object roboSpecTree = roboSpecTreeClass.newInstance();

            readSpecBodyMethod.invoke(roboSpecTree);

            Object root = getRootMethod.invoke(roboSpecTree);
            Object runFocusedOnly = isFocusedMethod.invoke(root);

            SpecTreeNodeRunner roboBranchRunner = (SpecTreeNodeRunner) roboSpecBranchRunnerClassConstructor.newInstance(
                this,
                roboSpecTreeClass,
                root,
                runFocusedOnly
            );

            return asList(roboBranchRunner);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
