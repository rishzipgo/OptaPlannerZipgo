/*
 * Copyright 2011 JBoss Inc
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

package org.optaplanner.examples.machinereassignment.solver.drools;

import java.io.Serializable;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrMachineCapacity;
import org.optaplanner.examples.machinereassignment.domain.MrResource;

public class MrMachineUsage implements Serializable, Comparable<MrMachineUsage> {

    private MrMachineCapacity machineCapacity;
    private long usage;

    public MrMachineUsage(MrMachineCapacity machineCapacity, long usage) {
        this.machineCapacity = machineCapacity;
        this.usage = usage;
    }

    public MrMachineCapacity getMachineCapacity() {
        return machineCapacity;
    }

    public long getUsage() {
        return usage;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof MrMachineUsage) {
            MrMachineUsage other = (MrMachineUsage) o;
            return new EqualsBuilder()
                    .append(machineCapacity, other.machineCapacity)
                    .append(usage, other.usage)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(machineCapacity)
                .append(usage)
                .toHashCode();
    }

    /**
     * Used by the GUI to sort the {@link ConstraintMatch} list
     * by {@link ConstraintMatch#getJustificationList()}.
     * @param other never null
     * @return comparison
     */
    public int compareTo(MrMachineUsage other) {
        return new CompareToBuilder()
                .append(machineCapacity, other.machineCapacity)
                .append(usage, other.usage)
                .toComparison();
    }

    public MrMachine getMachine() {
        return machineCapacity.getMachine();
    }

    public MrResource getResource() {
        return machineCapacity.getResource();
    }

    public boolean isTransientlyConsumed() {
        return machineCapacity.getResource().isTransientlyConsumed();
    }

    public long getLoadCostWeight() {
        return machineCapacity.getResource().getLoadCostWeight();
    }

    public long getMaximumAvailable() {
        return machineCapacity.getMaximumCapacity() - usage;
    }

    public long getSafetyAvailable() {
        return machineCapacity.getSafetyCapacity() - usage;
    }

    @Override
    public String toString() {
        return getMachine() + "-" + getResource() + "=" + usage;
    }

}
