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

package cn.codethink.xiaoming.api;

import cn.codethink.xiaoming.annotation.BotInternalApi;
import com.google.common.base.Preconditions;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

/**
 * <h1>Bot Api Factory</h1>
 *
 * <p>Bot api factory is the factory of {@link BotApi}. </p>
 *
 * <p>In implementation, it is an container of the unique instance of it. </p>
 *
 * @author Chuanwise
 */
@BotInternalApi
public final class BotApiFactory {
    private BotApiFactory() {
        throw new UnsupportedOperationException("No cn.codethink.xiaoming.bot.api.BotApiFactory instances for you!");
    }

    /**
     * the global and unique instance of {@link BotApi}.
     */
    private static volatile BotApi botApi;

    /**
     * get the global and unique instance of {@link BotApi}.
     *
     * @return instance of {@link BotApi}
     * @exception NoSuchElementException {@link BotApi} is not present
     */
    public static BotApi getBotApi() {

        // check if botApi is present
        // if not, try to load it by Java SPI
        if (botApi == null) {
            synchronized (BotApiFactory.class) {
                if (botApi == null) {

                    final ServiceLoader<BotApi> serviceLoader = ServiceLoader.load(BotApi.class);
                    final Iterator<BotApi> iterator = serviceLoader.iterator();

                    if (iterator.hasNext()) {
                        botApi = iterator.next();
                    } else {
                        throw new NoSuchElementException("BotApi is not present! " +
                                "Make sure module 'xiaoming-bot-core' is a runtime dependency. " +
                                "If it is, use 'BotApiFactory.setBotApi(new BotApiImpl())' to fix it. ");
                    }
                }
            }
        }

        return botApi;
    }

    /**
     * set the botApi instance manually.
     *
     * @param botApi instance of {@link BotApi}
     * @exception NullPointerException botApi is null
     */
    public static void setBotApi(BotApi botApi) {
        Preconditions.checkNotNull(botApi, "Bot api is null!");

        BotApiFactory.botApi = botApi;
    }
}
