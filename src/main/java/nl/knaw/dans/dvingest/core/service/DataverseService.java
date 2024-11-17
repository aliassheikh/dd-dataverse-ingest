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
package nl.knaw.dans.dvingest.core.service;

import nl.knaw.dans.lib.dataverse.DataverseException;
import nl.knaw.dans.lib.dataverse.model.dataset.Dataset;
import nl.knaw.dans.lib.dataverse.model.dataset.DatasetVersion;
import nl.knaw.dans.lib.dataverse.model.dataset.FileList;
import nl.knaw.dans.lib.dataverse.model.dataset.UpdateType;
import nl.knaw.dans.lib.dataverse.model.file.FileMeta;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface DataverseService {

    String createDataset(Dataset datasetMetadata) throws DataverseException, IOException;

    FileList addFile(String persistentId, Path file, FileMeta fileMeta) throws DataverseException, IOException;

    void publishDataset(String persistentId, UpdateType updateType) throws DataverseException, IOException;

    void replaceFile(String targetDatasetPid, String pathInDataset, Path replacement, Map<String, FileMeta> filesInDataset) throws DataverseException, IOException;

    void deleteFile(String persistentId, String filepath, Map<String, FileMeta> filesInDataset) throws DataverseException, IOException;

    void waitForState(String persistentId, String state) throws DataverseException;

    void updateMetadata(String targetDatasetPid, DatasetVersion datasetMetadata) throws DataverseException, IOException;

    void updateFileMetadata(String pid, String pathInDataset, FileMeta newMeta, Map<String, FileMeta> filesInDataset) throws DataverseException, IOException;

    List<FileMeta> getFiles(String pid) throws IOException, DataverseException;
}
