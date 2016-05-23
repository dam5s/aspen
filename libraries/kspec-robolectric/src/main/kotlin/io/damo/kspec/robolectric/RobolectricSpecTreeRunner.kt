package io.damo.kspec.robolectric

import io.damo.kspec.SpecTree
import io.damo.kspec.SpecTreeRunner

open class RobolectricSpecTreeRunner<T: SpecTree>(specificationClass: Class<T>) : SpecTreeRunner<T>(specificationClass) {

}
