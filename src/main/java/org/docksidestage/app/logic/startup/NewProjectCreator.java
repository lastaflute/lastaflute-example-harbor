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

import org.dbflute.helper.filesystem.FileHierarchyTracer;
import org.dbflute.helper.filesystem.FileHierarchyTracingHandler;
import org.dbflute.helper.filesystem.FileTextIO;
import org.dbflute.helper.filesystem.FileTextLineFilter;
import org.dbflute.util.Srl;
import org.dbflute.util.Srl.ScopeInfo;
import org.lastaflute.di.util.LdiFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jflute
 */
public class NewProjectCreator {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Logger logger = LoggerFactory.getLogger(NewProjectCreator.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String appName;
    protected final File projectDir;
    protected final ServiceNameFilter serviceNameFilter;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public NewProjectCreator(String appName, File projectDir, ServiceNameFilter serviceNameFilter) {
        this.appName = appName;
        this.projectDir = projectDir;
        this.serviceNameFilter = serviceNameFilter;
    }

    public static interface ServiceNameFilter {
        String filter(String original);
    }

    // ===================================================================================
    //                                                                         New Project
    //                                                                         ===========
    public void newProject() {
        new FileHierarchyTracer().trace(projectDir, new FileHierarchyTracingHandler() {

            @Override
            public boolean isTargetFileOrDir(File currentFile) {
                return determineTarget(currentFile);
            }

            @Override
            public void handleFile(File currentFile) throws IOException {
                migrateFile(currentFile);
            }
        });
    }

    // -----------------------------------------------------
    //                              from isTargetFileOrDir()
    //                              ------------------------
    protected boolean determineTarget(File currentFile) {
        final String canonicalPath;
        try {
            canonicalPath = currentFile.getCanonicalPath().replace("\\", "/");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to get canonical path: " + currentFile);
        }
        if (isAppResource(canonicalPath) && !isAppMigrated(canonicalPath)) { // e.g. app.product
            return false;
        }
        if (isWebInfViewResource(canonicalPath) && !isViewMigrated(canonicalPath)) { // e.g. /view/product
            return false;
        }
        if (isMyLastaOnlyExample(canonicalPath) // e.g. /mylasta/mail/
                || isResourcesOnlyExample(canonicalPath) // e.g. /resources/mail/
                || isStartUpTool(canonicalPath) // e.g. this
                || isDemoTestResource(canonicalPath) // e.g. .gitignore for DemoTest
                || isTestDbResource(canonicalPath) // e.g. H2 database
                || isTestClientResource(canonicalPath) // e.g. lidoisle
                || isDBFluteClientLog(canonicalPath) // e.g. dbflute.log
                || isH2DatabaseDDL(canonicalPath) // e.g. ...80-comment.sql
                || isErdImage(canonicalPath) // e.g. maihamadb.png
                || isOssText(canonicalPath) // e.g. LICENSE
                || isGitDir(canonicalPath) // e.g. .git
                || isBuildDir(canonicalPath) // e.g. target
        ) {
            return false;
        }
        return true;
    }

    // -----------------------------------------------------
    //                                     from handleFile()
    //                                     -----------------
    protected void migrateFile(File currentFile) throws IOException {
        final String canonicalPath = currentFile.getCanonicalPath().replace("\\", "/");
        final String baseDir = filterServiceName(Srl.substringLastFront(canonicalPath, "/"));
        final String pureFile = filterServiceName(Srl.substringLastRear(canonicalPath, "/"));
        final String outputFile = baseDir + "/" + pureFile;
        if (isPlainMigration(baseDir, pureFile)) { // e.g. binary
            mkdirs(baseDir);
            copyFile(currentFile, new File(outputFile));
        } else {
            final FileTextIO textIO = new FileTextIO().encodeAsUTF8();
            final String filtered;
            if (canonicalPath.endsWith("additionalForeignKeyMap.dfprop")) {
                filtered = textIO.readFilteringLine(canonicalPath, createAdditionalForeignKeyFilter());
            } else if (canonicalPath.endsWith("classificationDefinitionMap.dfprop")) {
                filtered = textIO.readFilteringLine(canonicalPath, createClassificationDefinitionFilter());
            } else if (canonicalPath.endsWith("classificationDeploymentMap.dfprop")) {
                filtered = textIO.readFilteringLine(canonicalPath, createClassificationDeploymentFilter());
            } else if (canonicalPath.endsWith("basicInfoMap.dfprop")) {
                filtered = textIO.readFilteringLine(canonicalPath, createBasicInfoMapFilter());
            } else if (canonicalPath.endsWith("databaseInfoMap.dfprop")) {
                filtered = textIO.readFilteringLine(canonicalPath, createDatabaseInfoMapFilter());
            } else if (canonicalPath.endsWith("databaseInfoMap+.dfprop")) {
                filtered = textIO.readFilteringLine(canonicalPath, createDatabaseInfoMapFilter());
            } else if (canonicalPath.endsWith("documentMap.dfprop")) {
                filtered = textIO.readFilteringLine(canonicalPath, createDocumentMapFilter());
            } else if (canonicalPath.endsWith("lastafluteMap.dfprop")) {
                filtered = textIO.readFilteringLine(canonicalPath, createLastaFluteMapFilter());
            } else if (canonicalPath.endsWith("_env.properties")) {
                filtered = textIO.readFilteringLine(canonicalPath, createEnvPropertiesFilter());
            } else if (canonicalPath.endsWith("pom.xml")) {
                filtered = textIO.readFilteringLine(canonicalPath, createPomXmlFilter());
            } else if (canonicalPath.endsWith("HarborFwAssistantDirector.java")) {
                filtered = textIO.readFilteringLine(canonicalPath, createFwAssistantDirectorFilter());
            } else {
                filtered = textIO.readFilteringLine(canonicalPath, line -> filterServiceName(line));
            }
            if (filtered == null) { // just in case
                String msg = "Filtered string was null or mpety: path=" + canonicalPath + ", filtered=" + filtered;
                throw new IllegalStateException(msg);
            }
            writeFile(textIO, outputFile, filtered);
            adjustReplaceSchemaDdl(canonicalPath, baseDir, outputFile);
        }
    }

    // ===================================================================================
    //                                                                       Create Filter
    //                                                                       =============
    // -----------------------------------------------------
    //                                  Configuration Filter
    //                                  --------------------
    protected FileTextLineFilter createAdditionalForeignKeyFilter() {
        return new FileTextLineFilter() {
            private boolean skipped;

            @Override
            public String filter(String line) {
                if (line.startsWith("map:{")) {
                    skipped = true;
                    return line;
                } else if (line.startsWith("}")) {
                    skipped = false;
                    return line;
                } else {
                    if (skipped) {
                        return null;
                    }
                    return filterServiceName(line);
                }
            }
        };
    }

    protected FileTextLineFilter createClassificationDefinitionFilter() {
        return new FileTextLineFilter() {
            private boolean skipped;

            @Override
            public String filter(String line) {
                if (line.startsWith("    ; ServiceRank = ")) { // Flg and MemberStatus only
                    skipped = true;
                    return null;
                } else if (line.startsWith("}")) {
                    skipped = false;
                    return line;
                } else {
                    if (skipped) {
                        return null;
                    }
                    return filterServiceName(line);
                }
            }
        };
    }

    protected FileTextLineFilter createClassificationDeploymentFilter() {
        return line -> {
            if (line.startsWith("    ; PURCHASE_PAYMENT = ")) {
                return null;
            }
            return filterServiceName(line);
        };
    }

    protected FileTextLineFilter createBasicInfoMapFilter() {
        return line -> {
            if (line.trim().startsWith("#")) { // to keep comment
                return line;
            }
            return filterServiceName(filterJdbcSettings(line));
        };
    }

    protected FileTextLineFilter createDatabaseInfoMapFilter() {
        return line -> filterServiceName(filterJdbcSettings(line));
    }

    protected FileTextLineFilter createDocumentMapFilter() {
        return line -> filterServiceName(filterJdbcSettings(line));
    }

    protected FileTextLineFilter createLastaFluteMapFilter() {
        return new FileTextLineFilter() {
            private boolean appMap = false;
            private boolean currentApp = false;

            @Override
            public String filter(String line) {
                if (line.trim().startsWith("#")) {
                    return filterServiceName(line);
                }
                if (line.contains("; appMap = map:{")) {
                    appMap = true;
                    return filterServiceName(line);
                }
                if (appMap) {
                    if (line.startsWith("    }")) {
                        appMap = false;
                        return filterServiceName(line);
                    }
                    if (line.contains("; " + appName + " = map:{")) {
                        currentApp = true;
                    }
                    if (currentApp) {
                        if (line.startsWith("        }")) {
                            currentApp = false;
                        }
                        return filterServiceName(line);
                    } else {
                        return null;
                    }
                } else {
                    return filterServiceName(line);
                }
            }
        };
    }

    // -----------------------------------------------------
    //                                  Configuration Filter
    //                                  --------------------
    protected FileTextLineFilter createEnvPropertiesFilter() {
        return line -> filterServiceName(filterJdbcSettings(line));
    }

    protected FileTextLineFilter createPomXmlFilter() {
        return line -> filterServiceName(filterJdbcSettings(line));
    }

    protected FileTextLineFilter createFwAssistantDirectorFilter() {
        return new FileTextLineFilter() {
            @Override
            public String filter(String line) {
                if (line.trim().startsWith("direction.directCors")) {
                    return null;
                }
                if (line.trim().endsWith("CorsHook;")) {
                    return null;
                }
                return filterServiceName(line);
            }
        };
    }

    // ===================================================================================
    //                                                                       Common Filter
    //                                                                       =============
    protected String filterJdbcSettings(String line) {
        // e.g. basicInfoMap.dfprop, databaseInfoMap.dfprop, _env.properties
        final String toDatabase = "; database = mysql";
        final String toDriver = "com.mysql.jdbc.Driver";
        final String toUrl = "jdbc:mysql://localhost:3306/maihamadb";
        final String toSyncUrl = "jdbc:mysql://localhost:3306/maihamasyncdb";
        line = Srl.replace(line, "; database = h2", toDatabase);
        line = Srl.replace(line, "org.h2.Driver", toDriver);
        line = Srl.replace(line, "jdbc:h2:file:../etc/testdb/maihamadb", toUrl);
        line = Srl.replace(line, "jdbc:h2:file:$classes(org.docksidestage.dbflute.allcommon.DBCurrent.class)/../../etc/testdb/maihamadb",
                toUrl);
        line = Srl.replace(line, "jdbc:h2:file:../etc/testdb/maihamasyncdb", toSyncUrl);

        // pom.xml
        final String toProperty = "<mysql.jdbc.version>5.1.36</mysql.jdbc.version>";
        final String toGroupId = "<groupId>mysql</groupId>";
        final String artifactId = "<artifactId>mysql-connector-java</artifactId>";
        final String toVersion = "<version>${mysql.jdbc.version}</version>";
        final ScopeInfo h2version = Srl.extractScopeFirst(line, "<h2.jdbc.version>", "</h2.jdbc.version>");
        if (h2version != null) {
            final String version = h2version.getContent();
            line = Srl.replace(line, "<h2.jdbc.version>" + version + "</h2.jdbc.version>", toProperty);
        }
        line = Srl.replace(line, "<groupId>com.h2database</groupId>", toGroupId);
        line = Srl.replace(line, "<artifactId>h2</artifactId>", artifactId);
        line = Srl.replace(line, "<version>${h2.jdbc.version}</version>", toVersion);
        return line;
    }

    protected String filterServiceName(String str) {
        return serviceNameFilter.filter(str);
    }

    // ===================================================================================
    //                                                                       ReplaceSchema
    //                                                                       =============
    protected void adjustReplaceSchemaDdl(String canonicalPath, String baseDir, String outputFile) throws IOException {
        final String basicDdl = "replace-schema-10-basic.sql";
        final String syncdbDdl = "replace-schema-99-syncdb.sql";
        if (Srl.endsWith(canonicalPath, "/" + basicDdl, "/" + syncdbDdl)) {
            final String systemDdl = "replace-schema-00-system.sql";
            final File srcFile = prepareStartupMySqlFile(systemDdl);
            final File destFile = new File(baseDir + "/" + systemDdl);
            copyFile(srcFile, destFile);
        }
        if (Srl.endsWith(canonicalPath, "/" + basicDdl)) {
            final File srcFile = prepareStartupMySqlFile(basicDdl);
            final File destFile = new File(baseDir + "/" + basicDdl);
            copyFile(srcFile, destFile); // override
        }
        if (Srl.endsWith(canonicalPath, "/" + syncdbDdl)) {
            final File srcFile = prepareStartupMySqlFile(basicDdl); // same as basic DDL
            final File destFile = new File(outputFile);
            copyFile(srcFile, destFile); // override
        }
    }

    protected File prepareStartupMySqlFile(String systemDdl) throws IOException {
        return new File(projectDir.getCanonicalPath() + "/etc/startup/mysql-" + systemDdl);
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    protected boolean isAppResource(String canonicalPath) {
        return canonicalPath.contains("/org/docksidestage/app/");
    }

    protected boolean isAppMigrated(String canonicalPath) {
        return Srl.endsWith(canonicalPath, "/app/web", "/app/logic") //
                || Srl.containsAny(canonicalPath //
                        , "/app/web/RootAction", "/app/web/base", "/app/web/signin", "/app/web/mypage" // web
                        , "/app/logic/context", "/app/logic/i18n" // logic
                );
    }

    protected boolean isWebInfViewResource(String canonicalPath) {
        return canonicalPath.contains("/WEB-INF/view/");
    }

    protected boolean isViewMigrated(String canonicalPath) {
        return Srl.containsAny(canonicalPath, "/view/common", "/view/error", "/view/signin", "/view/mypage");
    }

    protected boolean isPlainMigration(String baseDir, String pureFile) {
        return pureFile.endsWith(".xls") || pureFile.endsWith(".jar") // binary
                || baseDir.contains("/dbflute-1.x/") // no need to filter
                || baseDir.contains(".settings") // also
                || baseDir.contains("/etc/eclipse") // also
                || baseDir.contains("/etc/mysql") // also
        ;
    }

    // -----------------------------------------------------
    //                                          Not Migrated
    //                                          ------------
    protected boolean isMyLastaOnlyExample(String canonicalPath) {
        return Srl.containsAny(canonicalPath, "/mylasta/mail/");
    }

    protected boolean isResourcesOnlyExample(String canonicalPath) {
        return Srl.containsAny(canonicalPath, "/resources/mail/");
    }

    protected boolean isStartUpTool(String canonicalPath) {
        return Srl.containsAny(canonicalPath, "/etc/startup", "/org/docksidestage/startup");
    }

    protected boolean isDemoTestResource(String canonicalPath) {
        return Srl.containsAny(canonicalPath, "/org/docksidestage/DemoTest", "/org/docksidestage/.gitignore");
    }

    protected boolean isTestDbResource(String canonicalPath) {
        return Srl.containsAny(canonicalPath, "/etc/testdb", "/etc/.gitignore");
    }

    protected boolean isTestClientResource(String canonicalPath) {
        return Srl.containsAny(canonicalPath, "/lidoisle");
    }

    protected boolean isDBFluteClientLog(String canonicalPath) {
        return Srl.containsAny(canonicalPath, "/log/dbflute.log", "/log/velocity.log");
    }

    protected boolean isH2DatabaseDDL(String canonicalPath) {
        return Srl.containsAny(canonicalPath, "/replace-schema-80-comment.sql");
    }

    protected boolean isErdImage(String canonicalPath) {
        return Srl.containsAny(canonicalPath, "/maihamadb.png");
    }

    protected boolean isOssText(String canonicalPath) {
        return Srl.containsAny(canonicalPath, "/README.md"); // LICENSE, NOTICE are migrated just in case
    }

    protected boolean isGitDir(String canonicalPath) {
        return Srl.containsAny(canonicalPath, "/.git/");
    }

    protected boolean isBuildDir(String canonicalPath) {
        return Srl.containsAny(canonicalPath, "/target/");
    }

    // ===================================================================================
    //                                                                     Physical Helper
    //                                                                     ===============
    protected void mkdirs(String dirPath) {
        final File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    protected void copyFile(File currentFile, File outputFile) throws IOException {
        logger.debug("...Copying to {}", bulidDisplayPath(outputFile.getCanonicalPath()));
        LdiFileUtil.copy(currentFile, outputFile);
    }

    protected void writeFile(FileTextIO textIO, String outputFile, String filtered) throws IOException {
        logger.debug("...Writing to {}", bulidDisplayPath(outputFile));
        textIO.write(outputFile, filtered);
    }

    protected String bulidDisplayPath(String outputFile) throws IOException {
        final File parentFile = projectDir.getParentFile();
        return Srl.substringFirstRear(outputFile, parentFile.getCanonicalPath());
    }
}
