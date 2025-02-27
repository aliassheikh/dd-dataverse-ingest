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
package nl.knaw.dans.dvingest.core.dansbag;

import nl.knaw.dans.dvingest.core.TestDirFixture;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class LightweightBagInfoTest extends TestDirFixture {

    @Test
    public void reads_key_value_pairs_from_bag_info_file() throws Exception {
        // Given
        var bagInfoTxt = testDir.resolve("bag-info.txt");
        FileUtils.writeStringToFile(bagInfoTxt.toFile(), """
            Bagging-Date: 2021-06-01
            Bag-Size: 12345
            Payload-Oxum: 12345.6
            """, StandardCharsets.UTF_8);

        // When
        var bagInfo = new LightweightBagInfo(bagInfoTxt);

        // Then
        assertThat(bagInfo.get("Bagging-Date")).isEqualTo("2021-06-01");
        assertThat(bagInfo.get("Bag-Size")).isEqualTo("12345");
        assertThat(bagInfo.get("Payload-Oxum")).isEqualTo("12345.6");
    }

    @Test
    public void reads_Is_Version_Of_from_bag_info_file() throws Exception {
        // Given
        var bagInfoTxt = testDir.resolve("bag-info.txt");
        FileUtils.writeStringToFile(bagInfoTxt.toFile(), """
            Is-Version-Of: urn:nbn:nl:ui:13-4-5-6
            """, StandardCharsets.UTF_8);

        // When
        var bagInfo = new LightweightBagInfo(bagInfoTxt);

        // Then
        assertThat(bagInfo.get("Is-Version-Of")).isEqualTo("urn:nbn:nl:ui:13-4-5-6");
    }

}
