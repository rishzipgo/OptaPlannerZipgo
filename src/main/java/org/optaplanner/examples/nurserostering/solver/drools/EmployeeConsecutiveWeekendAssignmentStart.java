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

package org.optaplanner.examples.nurserostering.solver.drools;

import java.io.Serializable;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;

public class EmployeeConsecutiveWeekendAssignmentStart implements Comparable<EmployeeConsecutiveWeekendAssignmentStart>,
        Serializable {

    private Employee employee;
    private int sundayIndex;

    public EmployeeConsecutiveWeekendAssignmentStart(Employee employee, int sundayIndex) {
        this.employee = employee;
        this.sundayIndex = sundayIndex;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getSundayIndex() {
        return sundayIndex;
    }

    public void setSundayIndex(int sundayIndex) {
        this.sundayIndex = sundayIndex;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof EmployeeConsecutiveWeekendAssignmentStart) {
            EmployeeConsecutiveWeekendAssignmentStart other = (EmployeeConsecutiveWeekendAssignmentStart) o;
            return new EqualsBuilder()
                    .append(employee, other.employee)
                    .append(sundayIndex, other.sundayIndex)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(employee)
                .append(sundayIndex)
                .toHashCode();
    }

    public int compareTo(EmployeeConsecutiveWeekendAssignmentStart other) {
        return new CompareToBuilder()
                .append(employee, other.employee)
                .append(sundayIndex, other.sundayIndex)
                .toComparison();
    }

    @Override
    public String toString() {
        return employee + " weekend " + sundayIndex + " - ...";
    }

    public Contract getContract() {
        return employee.getContract();
    }

}
