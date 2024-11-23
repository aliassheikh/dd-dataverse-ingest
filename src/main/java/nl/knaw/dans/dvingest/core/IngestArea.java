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

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nl.knaw.dans.dvingest.api.ConversionResultDto;
import nl.knaw.dans.dvingest.api.ConversionResultDto.StatusEnum;
import nl.knaw.dans.dvingest.api.ConvertDansBagCommandDto;
import nl.knaw.dans.dvingest.api.ImportCommandDto;
import nl.knaw.dans.dvingest.api.ImportJobStatusDto;
import nl.knaw.dans.dvingest.core.service.DansBagMappingService;
import nl.knaw.dans.dvingest.core.service.DataverseService;
import nl.knaw.dans.dvingest.core.service.UtilityServices;
import nl.knaw.dans.dvingest.core.service.YamlService;
import nl.knaw.dans.dvingest.core.service.YamlServiceImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Slf4j
@Builder
public class IngestArea {
    @NonNull
    private final ExecutorService executorService;
    @NonNull
    private final DataverseService dataverseService;
    @NonNull
    private final UtilityServices utilityServices;
    @NonNull
    private final DansBagMappingService dansBagMappingService;
    @NonNull
    private final Path inbox;
    @NonNull
    private final Path outbox;

    private final Map<String, ImportJob> importJobs = new ConcurrentHashMap<>();
    private final YamlService yamlService = new YamlServiceImpl(); // Does not need to be configurable

    private IngestArea(ExecutorService executorService, DataverseService dataverseService, UtilityServices utilityServices,  DansBagMappingService dansBagMappingService, Path inbox, Path outbox) {
        try {
            this.executorService = executorService;
            this.dataverseService = dataverseService;
            this.utilityServices = utilityServices;
            this.dansBagMappingService = dansBagMappingService;
            this.inbox = inbox.toAbsolutePath().toRealPath();
            this.outbox = outbox.toAbsolutePath().toRealPath();
        }
        catch (IOException e) {
            throw new IllegalStateException("Failed to create ingest area", e);
        }
    }

    public void submit(ImportCommandDto importCommand) {
        log.debug("Received import command: {}", importCommand);
        if (importJobs.containsKey(importCommand.getPath())) {
            throw new IllegalArgumentException("Already submitted " + importCommand.getPath());
        }
        validatePath(importCommand.getPath());
        log.debug("Path validation successful");
        var importJob = createImportJob(importCommand);
        log.debug("Created import job: {}", importJob);
        importJobs.put(importCommand.getPath(), importJob);
        log.debug("Submitted import job");
        executorService.submit(importJob);
    }

    public ConversionResultDto convertDansBag(ConvertDansBagCommandDto convertDansBagCommand) {
        var result = new ConversionResultDto().status(StatusEnum.SUCCESS);
        // TODO: implement, throw IllegalArgumentException if input is invalid and RuntimeException if conversion fails
        return result;
    }

    public List<ImportJobStatusDto> getStatus(String path) {
        if (path == null) {
            return importJobs.values().stream().map(ImportJob::getStatus).toList();
        }
        else {
            if (importJobs.get(path) == null) {
                throw new IllegalArgumentException("No job found for path: " + path);
            }
            return List.of(importJobs.get(path).getStatus());
        }
    }

    private ImportJob createImportJob(ImportCommandDto importCommand) {
        Path relativePath;
        if (importCommand.getSingleObject()) {
            relativePath = inbox.relativize(Path.of(importCommand.getPath()).getParent());
        }
        else {
            relativePath = inbox.relativize(Path.of(importCommand.getPath()));
        }
        return ImportJob.builder()
            .importCommand(importCommand)
            .outputDir(outbox.resolve(relativePath))
            .dataverseService(dataverseService)
            .utilityServices(utilityServices)
            .yamlService(yamlService)
            .dansBagMappingService(dansBagMappingService)
            .build();
    }

    private void validatePath(String path) {
        var pathObj = Path.of(path);
        checkPathIsAbsolute(pathObj);
        checkPathInInbox(pathObj);
    }

    private void checkPathIsAbsolute(Path path) {
        if (!path.isAbsolute()) {
            throw new IllegalArgumentException("Path must be absolute: " + path);
        }
    }

    private void checkPathInInbox(Path path) {
        if (!path.startsWith(inbox)) {
            throw new IllegalArgumentException("Path must be in inbox: " + path);
        }
    }
}
