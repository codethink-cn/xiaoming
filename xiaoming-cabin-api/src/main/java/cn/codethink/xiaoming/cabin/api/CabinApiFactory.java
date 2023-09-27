/*
 * Copyright 2023 CodeThink Technologies and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.codethink.xiaoming.cabin.api;

import cn.codethink.xiaoming.cabin.annotation.CabinInternalApi;
import com.google.common.base.Preconditions;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

/**
 * <h1>Cabin Api Factory</h1>
 *
 * <p>Cabin api factory is the factory of {@link CabinApi}. </p>
 *
 * <p>In implementation, it is an container of the unique instance of it. </p>
 *
 * @author Chuanwise
 */
@CabinInternalApi
public final class CabinApiFactory {
    private CabinApiFactory() {
        throw new UnsupportedOperationException("No cn.codethink.xiaoming.cabin.api.CabinApiFactory instances for you!");
    }

    /**
     * The global and unique instance of {@link CabinApi}.
     */
    private static volatile CabinApi cabinApi;

    /**
     * Get the global and unique instance of {@link CabinApi}.
     *
     * @return instance of {@link CabinApi}
     * @exception NoSuchElementException {@link CabinApi} is not present
     */
    public static CabinApi getCabinApi() {

        // check if cabinApi is present
        // if not, try to load it by Java SPI
        if (cabinApi == null) {
            synchronized (CabinApiFactory.class) {
                if (cabinApi == null) {

                    final ServiceLoader<CabinApi> serviceLoader = ServiceLoader.load(CabinApi.class);
                    final Iterator<CabinApi> iterator = serviceLoader.iterator();

                    if (iterator.hasNext()) {
                        cabinApi = iterator.next();
                    } else {
                        throw new NoSuchElementException("Cabin api is not present! " +
                                "Make sure module 'xiaoming-cabin' is a runtime dependency. " +
                                "In the majority of situations it can be found automatically. " +
                                "If it didn't, use 'CabinApiFactory.setCabinApi(new CabinApiImpl())' to fix it manually. ");
                    }
                }
            }
        }

        return cabinApi;
    }

    /**
     * Set the cabinApi instance manually.
     *
     * @param cabinApi instance of {@link CabinApi}
     * @exception NullPointerException cabinApi is null
     */
    public static void setCabinApi(CabinApi cabinApi) {
        Preconditions.checkNotNull(cabinApi, "Cabin api is null!");

        CabinApiFactory.cabinApi = cabinApi;
    }
}
