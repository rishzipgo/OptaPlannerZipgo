/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.examples.investment.domain;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.investment.domain.util.InvestmentNumericUtil;

@XStreamAlias("Region")
public class Region extends AbstractPersistable {

    private String name;
    private Long quantityMillisMaximum; // In milli's (so multiplied by 1000)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getQuantityMillisMaximum() {
        return quantityMillisMaximum;
    }

    public void setQuantityMillisMaximum(Long quantityMillisMaximum) {
        this.quantityMillisMaximum = quantityMillisMaximum;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getQuantityMaximumLabel() {
        return InvestmentNumericUtil.formatMillisAsPercentage(quantityMillisMaximum);
    }

    @Override
    public String toString() {
        return name;
    }

}
