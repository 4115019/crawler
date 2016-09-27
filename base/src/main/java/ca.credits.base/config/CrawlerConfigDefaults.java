/*
 * Copyright (c) 2014 AsyncHttpClient Project. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package ca.credits.base.config;

public final class CrawlerConfigDefaults {

    private CrawlerConfigDefaults() {
    }

    public static final String CONFIG_ROOT = "crawler.";

    public static boolean defaultDistribute() {
        return CrawlerConfigHelper.getAsyncHttpClientConfig().getBoolean(CONFIG_ROOT + "distribute");
    }

    public static boolean defaultUseProxy(){
        return CrawlerConfigHelper.getAsyncHttpClientConfig().getBoolean(CONFIG_ROOT + "useProxy");
    }

    public static long defaultHTTPTimeout(){
        return CrawlerConfigHelper.getAsyncHttpClientConfig().getLong(CONFIG_ROOT + "httpTimeOut");
    }
}
