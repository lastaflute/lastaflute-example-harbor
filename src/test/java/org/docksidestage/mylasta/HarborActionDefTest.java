/*
 * Copyright 2014-2015 the original author or authors.
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
package org.docksidestage.mylasta;

import java.io.File;
import java.lang.reflect.Modifier;

import org.dbflute.utflute.core.document.DocumentGenerator;
import org.docksidestage.unit.UnitHarborTestCase;

/**
 * @author jflute
 */
public class HarborActionDefTest extends UnitHarborTestCase {

    public void test_definition() throws Exception {
        // ## Arrange ##
        String appWebPkg = ".app.web.";
        String actionSuffix = "Action";

        // ## Act ##
        policeStoryOfJavaClassChase((srcFile, clazz) -> {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) { // e.g. BaseAction
                return;
            }
            final String className = clazz.getName();
            if (className.contains(appWebPkg) && className.endsWith(actionSuffix)) {
                // ## Assert ##
                markHere("exists");
                assertActionDefinition(srcFile, clazz);
            }
        });
        assertMarked("exists");
    }

    protected void assertActionDefinition(File srcFile, Class<?> clazz) {
        getComponent(clazz); // expect no exception
    }

    public void test_document() throws Exception {
        DocumentGenerator documentGenerator = new DocumentGenerator();
        documentGenerator.saveLastaDocMeta();
    }
}
