/**
 * Copyright (C) 2006-2018 Talend Inc. - www.talend.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.talend.sdk.component.tools;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static org.apache.ziplock.JarLocation.jarLocation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.talend.sdk.component.junit.base.junit5.TemporaryFolder;
import org.talend.sdk.component.junit.base.junit5.WithTemporaryFolder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@WithTemporaryFolder
class AsciidocDocumentationGeneratorTest {

    @Test
    void generateAdoc(final TemporaryFolder temporaryFolder, final TestInfo info) throws IOException {
        final File output = new File(temporaryFolder.getRoot(), info.getTestMethod().get().getName() + ".asciidoc");
        new AsciidocDocumentationGenerator(
                new File[] { copyBinaries("org.talend.test.valid", temporaryFolder.getRoot(),
                        info.getTestMethod().get().getName()) },
                output, null, 2, null, null, null, null, log, findWorkDir(), "1.0").run();
        assertTrue(output.exists());
        try (final BufferedReader reader = new BufferedReader(new FileReader(output))) {
            assertEquals(
                    "== my\n" + "\n" + "super my component\n" + "\n" + "=== Configuration\n" + "\n"
                            + "[cols=\"e,d,m,a\",options=\"header\"]\n" + "|===\n"
                            + "|Path|Description|Default Value|Enabled If\n"
                            + "|configuration|configuration configuration|-|Always enabled\n"
                            + "|configuration.input|the input value|-|Always enabled\n"
                            + "|configuration.nested|it is nested|-|Always enabled\n"
                            + "|configuration.nested.user|the user to log in|unknown|Always enabled\n" + "|===\n",
                    reader.lines().collect(joining("\n")));
        }
    }

    @Test
    void generateAdocWithConditions(final TemporaryFolder temporaryFolder, final TestInfo info) throws IOException {
        final File output = new File(temporaryFolder.getRoot(), info.getTestMethod().get().getName() + ".asciidoc");
        new AsciidocDocumentationGenerator(
                new File[] { copyBinaries("org.talend.test.activeif", temporaryFolder.getRoot(),
                        info.getTestMethod().get().getName()) },
                output, null, 2, null, null, null, null, log, findWorkDir(), "1.0").run();
        assertTrue(output.exists());
        try (final BufferedReader reader = new BufferedReader(new FileReader(output))) {
            assertEquals("== activeif\n" + "\n" + "=== Configuration\n" + "\n"
                    + "[cols=\"e,d,m,a\",options=\"header\"]\n" + "|===\n"
                    + "|Path|Description|Default Value|Enabled If\n"
                    + "|configuration|configuration configuration|-|Always enabled\n"
                    + "|configuration.advanced|advanced configuration|false|Always enabled\n"
                    + "|configuration.advancedOption|advancedOption configuration|-|All of the following conditions are met:\n"
                    + "\n" + "- `advanced` is equal to `false`\n" + "- `query` is empty\n" + "\n"
                    + "|configuration.query|query configuration|-|All of the following conditions are met:\n" + "\n"
                    + "- `toggle` is equal to `true`\n" + "- `type` is equal to `mysql` or `oracle`\n" + "\n"
                    + "|configuration.toggle|toggle configuration|false|Always enabled\n"
                    + "|configuration.token|token configuration|-|`toggle` is equal to `true`\n"
                    + "|configuration.type|type configuration|-|Always enabled\n" + "|===\n",
                    reader.lines().collect(joining("\n")));
        }
    }

    @Test
    void generateHtmlPdf(final TemporaryFolder temporaryFolder, final TestInfo info) throws IOException {
        final String testMethod = info.getTestMethod().get().getName();
        final File output = new File(temporaryFolder.getRoot(), testMethod + ".asciidoc");
        final File outputHtml = new File(temporaryFolder.getRoot(), testMethod + ".html");
        final File outputPdf = new File(temporaryFolder.getRoot(), testMethod + ".pdf");
        new AsciidocDocumentationGenerator(
                new File[] { copyBinaries("org.talend.test.valid", temporaryFolder.getRoot(),
                        info.getTestMethod().get().getName()) },
                output, "SuperTitle", 2, new HashMap<String, String>() {

                    {
                        put("html", outputHtml.getAbsolutePath());
                        put("pdf", outputPdf.getAbsolutePath());
                    }
                }, null, null, null, log, findWorkDir(), "1.0").run();
        assertTrue(outputHtml.exists());
        assertTrue(outputPdf.exists());
        try (final BufferedReader reader = new BufferedReader(new FileReader(outputHtml))) {
            assertEquals("<!DOCTYPE html>", reader.lines().limit(1).findFirst().get());
        }
    }

    private File findWorkDir() {
        return new File(jarLocation(AsciidocDocumentationGeneratorTest.class).getParentFile(),
                getClass().getSimpleName() + "_workdir");
    }

    private File copyBinaries(final String pck, final File tmp, final String name) {
        final String pckPath = pck.replace('.', '/');
        final File root = new File(jarLocation(getClass()), pckPath);
        final File scannable = new File(tmp, getClass().getName() + "_" + name);
        final File classDir = new File(scannable, pckPath);
        classDir.mkdirs();
        ofNullable(root.listFiles())
                .map(Stream::of)
                .orElseGet(Stream::empty)
                .filter(c -> c.getName().endsWith(".class"))
                .forEach(c -> {
                    try {
                        Files.copy(c.toPath(), new File(classDir, c.getName()).toPath());
                    } catch (final IOException e) {
                        fail("cant create test plugin");
                    }
                });
        return scannable;
    }
}
