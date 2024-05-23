/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.flyray.bsin.facade.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author Tijs Rademakers
 * @author Joram Barrez
 */

@Data
public class SequenceFlow extends FlowElement {

    protected String conditionExpression;
    protected String sourceRef;
    protected String targetRef;
    protected String skipExpression;

    // Actual flow elements that match the source and target ref
    // Set during process definition parsing
    @JsonIgnore
    protected FlowElement sourceFlowElement;

    @JsonIgnore
    protected FlowElement targetFlowElement;
}
