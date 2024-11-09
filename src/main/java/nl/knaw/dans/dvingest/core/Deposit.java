/*
 * Copyright (C) 2024 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.dvingest.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nl.knaw.dans.lib.dataverse.model.dataset.Dataset;
import nl.knaw.dans.lib.dataverse.model.dataset.DatasetVersion;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Deposit {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @NonNull
    private Path location;

    public UUID getId() {
        return UUID.fromString(location.getFileName().toString());
    }

    public String getDatasetMetadata() throws IOException {
        return FileUtils.readFileToString(location.resolve("dataset.json").toFile(), "UTF-8");
    }

    public Path getFilesDir() {
        return location.resolve("files");
    }

    public void moveTo(Path targetDir) throws IOException {
        Files.move(location, targetDir.resolve(location.getFileName()));
        location = targetDir.resolve(location.getFileName());
    }
}
