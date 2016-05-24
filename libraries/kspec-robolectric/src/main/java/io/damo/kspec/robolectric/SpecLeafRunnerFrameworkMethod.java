package io.damo.kspec.robolectric;

import io.damo.kspec.SpecLeafRunner;
import org.junit.runners.model.FrameworkMethod;

import java.lang.reflect.Method;

class SpecLeafRunnerFrameworkMethod extends FrameworkMethod {

    public final SpecLeafRunner runner;

    public SpecLeafRunnerFrameworkMethod(SpecLeafRunner runner, Method method) {
        super(method);
        this.runner = runner;
    }

    @Override
    public String getName() {
        return runner.getSpecLeaf().getTestName();
    }
}
