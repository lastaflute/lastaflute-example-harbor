/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.docksidestage.app.logic.startup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.dbflute.util.Srl;

/**
 * @author jflute
 */
public class StartupLogic {

    public void fromHarbor(File projectDir, String domain, String serviceName) {
        String packageName = buildPackageName(domain);
        new NewProjectCreator("harbor", projectDir, original -> {
            String filtered = original;
            filtered = replace(filtered, buildProjectDirPureName(projectDir), Srl.initUncap(serviceName)); // e.g. lastaflute-example-harbor
            filtered = replace(filtered, "lastaflute-example-harbor", Srl.initUncap(serviceName)); // just in case
            filtered = replace(filtered, "maihamadb", Srl.initUncap(serviceName) + (!serviceName.endsWith("db") ? "db" : ""));
            filtered = replace(filtered, "org/docksidestage", replace(packageName, ".", "/")); // for file path
            filtered = replace(filtered, "docksidestage.org", domain);
            filtered = replace(filtered, "org.docksidestage", packageName);
            filtered = replace(filtered, "Harbor", Srl.initCap(serviceName));
            filtered = replace(filtered, "harbor", Srl.initUncap(serviceName));
            filtered = replace(filtered, "new JettyBoot(8090, ", "new JettyBoot(9001, ");
            filtered = replace(filtered, "new TomcatBoot(8090, ", "new TomcatBoot(9001, ");
            return filtered;
        }).newProject();
    }

    protected String buildPackageName(String domain) { // e.g. docksidestage.org to org.docksidestage
        List<String> elementList = new ArrayList<String>(Arrays.asList(domain.split("\\.")));
        Collections.reverse(elementList);
        String pkgName = elementList.stream().reduce((left, right) -> left + "." + right).get();
        return pkgName.replace("-", ""); // e.g. org.dockside-stage to org.docksidestage
    }

    private String buildProjectDirPureName(File projectDir) { // e.g. /sea/mystic => mystic
        try {
            return Srl.substringLastRear(projectDir.getCanonicalPath(), "/"); // thanks oreilly
        } catch (IOException e) {
            throw new IllegalStateException("Failed to get canonical path: " + projectDir);
        }
    }

    protected String replace(String str, String fromStr, String toStr) {
        return Srl.replace(str, fromStr, toStr);
    }
}
