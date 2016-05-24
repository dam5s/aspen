package io.damo.kspec.robolectric;

import io.damo.kspec.*;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.notification.RunNotifier;

public class RobolectricSpecBranchRunner<T extends SpecTree> extends SpecBranchRunner<T> {

    private RobolectricSpecTreeRunner<T> rootRunner;

    public RobolectricSpecBranchRunner(
        RobolectricSpecTreeRunner<T> rootRunner,
        Class<T> specTreeClass,
        SpecBranch specBranch,
        boolean runFocusedOnly
    ) {
        super(specTreeClass, specBranch, runFocusedOnly);
        this.rootRunner = rootRunner;
    }

    @Override
    protected void runChild(@NotNull SpecTreeNodeRunner child, @NotNull RunNotifier notifier) {
        if (child instanceof SpecLeafRunner) {
            this.runLeaf((SpecLeafRunner) child);
        }

        super.runChild(child, notifier);
    }

    private void runLeaf(SpecLeafRunner runner) {
        SpecLeafRunnerFrameworkMethod method = new SpecLeafRunnerFrameworkMethod(runner, rootRunner.testMethod);

        try {
            rootRunner.helper.methodBlock(
                method,
                rootRunner.config,
                rootRunner.appManifest,
                rootRunner.sdkEnv
            ).evaluate();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    @Override
    public SpecBranchRunner<T> buildBranchRunner(@NotNull SpecBranch branch) {
        return new RobolectricSpecBranchRunner<>(rootRunner, getSpecTreeClass(), branch, getRunFocusedOnly());
    }
}
