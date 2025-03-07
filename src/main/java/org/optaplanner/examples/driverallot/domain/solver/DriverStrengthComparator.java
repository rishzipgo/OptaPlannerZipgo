/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package org.optaplanner.examples.driverallot.domain.solver;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.driverallot.domain.Driver;

public class DriverStrengthComparator implements Comparator<Driver>, Serializable {

    public int compare(Driver a, Driver b) {
    	//if(a !=null && b!= null)
    		return new CompareToBuilder()
                .append(b.getRank(), a.getRank()) // Descending
                .toComparison();
    	/*else if(a != null)
    		return -1;
    	else if(b != null)
    		return 1;
    	else
    		return 0;*/
    }

}
