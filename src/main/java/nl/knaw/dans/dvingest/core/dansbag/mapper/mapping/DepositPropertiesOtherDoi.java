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
package nl.knaw.dans.dvingest.core.dansbag.mapper.mapping;

import nl.knaw.dans.dvingest.core.dansbag.mapper.builder.CompoundFieldGenerator;

import static nl.knaw.dans.dvingest.core.dansbag.mapper.DepositDatasetFieldNames.OTHER_ID_AGENCY;
import static nl.knaw.dans.dvingest.core.dansbag.mapper.DepositDatasetFieldNames.OTHER_ID_VALUE;

public class DepositPropertiesOtherDoi extends Base {

    public static CompoundFieldGenerator<String> toOtherIdValue = (builder, value) -> {
        builder.addSubfield(OTHER_ID_AGENCY, value.replaceAll(":.*",""));
        builder.addSubfield(OTHER_ID_VALUE, value.replaceAll("^[^:]*:",""));
    };
}
